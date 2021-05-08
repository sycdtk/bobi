package db

import (
	"database/sql"
	"strconv"
	"strings"
	"sync"

	"github.com/sycdtk/bobi/config"
	"github.com/sycdtk/bobi/errtools"
	"github.com/sycdtk/bobi/logger"
	"github.com/sycdtk/bobi/random"

	_ "github.com/go-sql-driver/mysql"
	_ "github.com/lib/pq"
	_ "github.com/mattn/go-sqlite3"
)

var onceDB sync.Once
var pool *DBPool

type DBPool struct {
	conns map[string]*sql.DB //多数据库连接池  db名称:数据库连接
}

//事务的单个执行语句及参数
type ExecSql struct {
	ESql string
	Args []interface{}
}

// 事务
type Transaction struct {
	ID       string
	DBName   string
	Tx       *sql.Tx
	Execsqls []*ExecSql
}

//数据库初始化
func init() {

	onceDB.Do(func() {
		pool = &DBPool{conns: map[string]*sql.DB{}}
	})

	dbNames := strings.Split(config.Read("db", "dbName"), ",")
	defaultDB := config.Read("db", "default")

	for _, dbName := range dbNames {
		//数据库类型
		dbType := config.Read(dbName, "dbType")

		connStr := config.Read(dbName, "conn")

		db, err := sql.Open(dbType, connStr)
		errtools.CheckErr(err, "Open database "+dbType+" "+dbName+" error")

		if maxOpenConnsStr := config.Read("postgres", "maxOpenConns"); len(maxOpenConnsStr) > 0 {
			maxIdleConns, err := strconv.Atoi(maxOpenConnsStr)
			errtools.CheckErr(err, "Transfer "+dbType+" "+dbName+" maxOpenConns error")
			db.SetMaxOpenConns(maxIdleConns)
		}

		if maxIdleConnsStr := config.Read("postgres", "maxIdleConns"); len(maxIdleConnsStr) > 0 {
			maxIdleConns, err := strconv.Atoi(maxIdleConnsStr)
			errtools.CheckErr(err, "Transfer "+dbType+" "+dbName+" maxIdleConns error")
			db.SetMaxIdleConns(maxIdleConns)
		}

		errtools.CheckErr(db.Ping(), "Database "+dbType+" "+dbName+" connection error")

		//加入多源数据库连接
		pool.conns[dbName] = db

		logger.Info("DB", dbName, ":", "conn success")

		//默认数据库
		if dbName == defaultDB {
			pool.conns["default"] = db
		}
	}
}

// Query  "SELECT * FROM users"
func Query(querySql string, args ...interface{}) [][]sql.RawBytes {
	return QueryDB("default", querySql, args...)
}

func QueryDB(dbName, querySql string, args ...interface{}) [][]sql.RawBytes {

	var results [][]sql.RawBytes

	if db, ok := pool.conns[dbName]; ok {
		rows, err := db.Query(querySql, args...)
		defer rows.Close()
		errtools.CheckErr(err, "SQL 查询失败:", querySql, args)

		cols, err := rows.Columns() // 获取列数
		errtools.CheckErr(err, "SQL 获取结果失败:", querySql, args)

		for rows.Next() {

			r := make([]interface{}, len(cols))
			rv := make([]sql.RawBytes, len(cols))

			for i := range rv {
				r[i] = &rv[i]
			}

			err = rows.Scan(r...)
			errtools.CheckErr(err, "SQL 结果解析失败:", querySql, args)

			results = append(results, rv)
		}

		logger.Debug("DB", dbName, ":", querySql, args)
	}

	return results
}

// Execute  "INSERT INTO users(name,age) values(?,?)"
// UPDATE  "UPDATE users SET age = ? WHERE id = ?"
// DELETE "DELETE FROM users WHERE id = ?"
// Create "CREATE TABLE(...)"
// Drop "DROP TABLE..."
func Execute(execSql string, args ...interface{}) {
	ExecuteDB("default", execSql, args...)
}

func ExecuteDB(dbName, execSql string, args ...interface{}) {

	if db, ok := pool.conns[dbName]; ok {
		stmt, err := db.Prepare(execSql)

		errtools.CheckErr(err, "SQL Prepare失败:", execSql, args)

		result, err := stmt.Exec(args...)

		errtools.CheckErr(err, "SQL 执行失败:", execSql, args)

		lastID, _ := result.LastInsertId()

		affectNum, _ := result.RowsAffected()

		logger.Debug("DB", dbName, ":", execSql, args, "，最后ID：", lastID, "，受影响行数：", affectNum)
	} else {
		logger.Info("DB", dbName, " not found")
	}
}

//开启事务
func BeginTransaction() *Transaction {
	return BeginTransactionDB("default")
}

//开启事务
func BeginTransactionDB(dbName string) *Transaction {
	if db, ok := pool.conns[dbName]; ok {
		tx, err := db.Begin()
		if err != nil {
			logger.Err("DB", dbName, ":", "开启事务失败", err)
			return nil
		} else {
			id := random.UniqueID()
			logger.Debug("DB", dbName, ":", "事务开始", id)
			return &Transaction{
				ID:       id,
				DBName:   dbName,
				Tx:       tx,
				Execsqls: []*ExecSql{},
			}
		}
	} else {
		logger.Err("DB", dbName, " not found")
		return nil
	}
}

// Execute  "INSERT INTO users(name,age) values(?,?)"
// UPDATE  "UPDATE users SET age = ? WHERE id = ?"
// DELETE "DELETE FROM users WHERE id = ?"
// Create "CREATE TABLE(...)"
// Drop "DROP TABLE..."
func (t *Transaction) Execute(execSql string, args ...interface{}) {
	t.Execsqls = append(t.Execsqls, &ExecSql{ESql: execSql, Args: args})
}

//事务结束提交
func (t *Transaction) EndTransaction() {

	var err error
	var stmt *sql.Stmt
	var result sql.Result

	defer func() {
		if err != nil {
			logger.Err("DB", t.DBName, ":", "事务执行失败", t.ID, err)
			t.Tx.Rollback()
		} else {
			t.Tx.Commit()
		}
	}()
	if len(t.Execsqls) > 0 {
		for _, esql := range t.Execsqls {
			stmt, err = t.Tx.Prepare(esql.ESql)
			if err != nil {
				return
			}

			result, err = stmt.Exec(esql.Args...)

			lastID, _ := result.LastInsertId()
			affectNum, _ := result.RowsAffected()

			logger.Debug("DB", t.DBName, ":", esql.ESql, esql.Args, "，最后ID：", lastID, "，受影响行数：", affectNum)
		}
	}
	logger.Debug("DB", t.DBName, ":", "事务结束", t.ID)
}
