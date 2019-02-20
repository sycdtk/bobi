package db

import (
	"database/sql"
	"strconv"
	"strings"
	"sync"

	"github.com/sycdtk/bobi/config"
	"github.com/sycdtk/bobi/errtools"
	"github.com/sycdtk/bobi/logger"

	_ "github.com/go-sql-driver/mysql"
	_ "github.com/lib/pq"
	_ "github.com/mattn/go-sqlite3"
)

var onceDB sync.Once
var pool *DBPool

type DBPool struct {
	conns map[string]*sql.DB //多数据库连接池  db名称:数据库连接
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
	}
}
