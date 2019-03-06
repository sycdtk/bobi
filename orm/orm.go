package orm

import (
	"github.com/sycdtk/bobi/config"
	"github.com/sycdtk/bobi/logger"
	"github.com/sycdtk/bobi/orm/db"
	"github.com/sycdtk/bobi/orm/mapper"
)

//新增
func Create(objs []interface{}, dataCol []string) {
	mapper.Create(objs, dataCol)
}

func CreateDB(dbName string, objs []interface{}, dataCol []string) {
	mapper.CreateDB(dbName, objs, dataCol)
}

//删除
func Delete(objs []interface{}, whereDataCol []string) {
	mapper.Delete(objs, whereDataCol)
}

func DeleteDB(dbName string, objs []interface{}, whereDataCol []string) {
	mapper.DeleteDB(dbName, objs, whereDataCol)
}

//按ID删除
func DeleteByID(objs []interface{}) {
	mapper.DeleteByID(objs)
}

func DeleteByIDDB(dbName string, objs []interface{}) {
	mapper.DeleteByIDDB(dbName, objs)
}

//更新
func Update(objs []interface{}, dataCol []string, whereDataCol []string) {
	mapper.Update(objs, dataCol, whereDataCol)
}

func UpdateDB(dbName string, objs []interface{}, dataCol []string, whereDataCol []string) {
	mapper.UpdateDB(dbName, objs, dataCol, whereDataCol)
}

//查询
func Query(obj interface{}, sql string, cols []string, args ...interface{}) []interface{} {
	return QueryDB("default", obj, sql, cols, args...)
}

func QueryDB(dbName string, obj interface{}, sql string, cols []string, args ...interface{}) []interface{} {
	results := db.QueryDB(dbName, sql, args...)
	dataList := mapper.Write(obj, results, cols)
	return dataList
}

//直接执行sql
func Execute(execSql string, args ...interface{}) {
	ExecuteDB("default", execSql, args...)
}

func ExecuteDB(dbName, execSql string, args ...interface{}) {
	db.ExecuteDB(dbName, execSql, args...)
}

func Register(modelName string, newFn func() interface{}) {
	mapper.Register(modelName, newFn)
}

//获取传入对象判断数据库中表名是否存在
func TableObjExist(obj interface{}) (string, bool) {
	tn := mapper.TableName(obj)
	return tn, TableExistDB("default", tn)
}

//判断数据库中表是否存在
func TableExist(tableName string) bool {
	return TableExistDB("default", tableName)
}

//返回数据库是否需要初始化
func TableExistDB(dbName, tableName string) bool {

	var dbType string
	var isExist int
	defaultDB := config.Read("db", "default")

	if dbName == "default" {
		dbType = config.Read(defaultDB, "dbType")
	} else {
		dbType = config.Read(dbName, "dbType")
	}

	switch dbType {
	case "sqlite3":
		data := db.QueryDB(dbName, "select name as tablename from sqlite_master where type='table' and name = ?", tableName)
		isExist = len(data)
	case "postgres":
		isExist = len(db.QueryDB(dbName, "select relname as tablename from pg_class where relname = ?", tableName))
	case "mysql":
		dbSchema := config.Read(dbName, "dbSchema")
		isExist = len(db.QueryDB(dbName, "select table_name as tablename from information_schema.tables where table_schema=? and table_name =?", dbSchema, tableName))
	default:
		logger.Info("DB", ":", "No support database")
	}

	return isExist > 0
}
