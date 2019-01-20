package db

import (
	"database/sql"
	"strconv"
	"strings"
	"sync"

	"github.com/sycdtk/bobi/config"
	"github.com/sycdtk/bobi/errtools"
	"github.com/sycdtk/bobi/logger"

	_ "github.com/lib/pq"
	_ "github.com/mattn/go-sqlite3"
)

var onceDB sync.Once
var Pool *DBPool

type DBPool struct {
	conns map[string]*sql.DB //多数据库连接池  db名称:数据库连接
}

func newDBPool() *DBPool {
	onceDB.Do(func() {
		pool := map[string]*sql.DB{}
		Pool = &DBPool{conns: pool}
	})

	return Pool
}

//数据库初始化
func init() {

	newDBPool()

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
		Pool.conns[dbName] = db

		logger.Debug(dbName, "init success")

		//默认数据库
		if dbName == defaultDB {
			Pool.conns["default"] = db
		}
	}

}

// Query  "SELECT * FROM users"
func (p *DBPool) Query(querySql string, args ...interface{}) [][]sql.RawBytes {
	return p.QueryDB("default", querySql, args...)
}

func (p *DBPool) QueryDB(dbName, querySql string, args ...interface{}) [][]sql.RawBytes {

	var results [][]sql.RawBytes

	if db, ok := p.conns[dbName]; ok {
		rows, err := db.Query(querySql, args...)
		errtools.CheckErr(err, "SQL 查询失败:", querySql, args)

		defer rows.Close()

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

		logger.Debug("SQL 查询完成：", querySql, args)
	}

	return results
}

// Execute  "INSERT INTO users(name,age) values(?,?)"
// UPDATE  "UPDATE users SET age = ? WHERE id = ?"
// DELETE "DELETE FROM users WHERE id = ?"
// Create "CREATE TABLE(...)"
// Drop "DROP TABLE..."
func (p *DBPool) Execute(execSql string, args ...interface{}) {
	p.ExecuteDB("default", execSql, args...)
}

func (p *DBPool) ExecuteDB(dbName, execSql string, args ...interface{}) {

	if db, ok := p.conns[dbName]; ok {
		stmt, err := db.Prepare(execSql)

		errtools.CheckErr(err, "SQL Prepare失败:", execSql, args)

		result, err := stmt.Exec(args...)

		errtools.CheckErr(err, "SQL 执行失败:", execSql, args)

		lastID, _ := result.LastInsertId()

		affectNum, _ := result.RowsAffected()

		logger.Debug("SQL执行完成：", execSql, args, "，最后插入ID：", lastID, "，受影响行数：", affectNum)
	}
}
