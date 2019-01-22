package orm

import (
	"github.com/sycdtk/bobi/orm/db"
	"github.com/sycdtk/bobi/orm/mapper"
)

func Query(obj interface{}, sql string, cols []string, args ...interface{}) []interface{} {
	return QueryDB("default", obj, sql, cols, args...)
}

func QueryDB(dbName string, obj interface{}, sql string, cols []string, args ...interface{}) []interface{} {
	results := db.QueryDB(dbName, sql, args...)
	dataList := mapper.Write(obj, results, cols)
	return dataList
}

func Execute(execSql string, args ...interface{}) {
	ExecuteDB("default", execSql, args...)
}

func ExecuteDB(dbName, execSql string, args ...interface{}) {
	db.ExecuteDB(dbName, execSql, args...)
}
