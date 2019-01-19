package db

import (
	"database/sql"
	"strings"

	"github.com/sycdtk/bobi/config"
	"github.com/sycdtk/bobi/errtools"

	_ "github.com/lib/pq"
	_ "github.com/mattn/go-sqlite3"
)

var once sync.Once
var Pool *DBPool

type DBPool struct {
	conns map[string]*sql.DB //多数据库连接池  db名称:数据库连接
}

func newDBPool() *DBPool {
	once.Do(func() {
		pool := map[string]*sql.DB{}
		Pool = &DBPool{conns: pool}
	})

	return Pool
}

//数据库初始化
func init() {

	newDBPool()

	dbNames := strings.Split(config.Read("db", "dbName"), ",")

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
		//默认数据库
		if config.Read(dbName, "default") == "true" {
			Pool.conns["default"] = db
		}
	}

}

// Query  "SELECT * FROM users"
func (p *DBPool) Query(querySql string, args ...interface{}) [][]sql.RawBytes {
	return QueryDB("default", querySql, args)
}

func (p *DBPool) QueryDB(dbName, querySql string, args ...interface{}) [][]sql.RawBytes {

	rows, err := p.conns[dbName].Query(querySql, args...)
	errtools.CheckErr(err, "SQL 查询失败:", querySql, args)

	defer rows.Close()

	cols, err := rows.Columns() // 获取列数
	errtools.CheckErr(err, "SQL 获取结果失败:", querySql, args)

	var results [][]sql.RawBytes

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

	return results
}
