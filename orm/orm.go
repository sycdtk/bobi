package orm

import (
	"strings"

	"github.com/sycdtk/bobi/config"
	"github.com/sycdtk/bobi/logger"
	"github.com/sycdtk/bobi/orm/db"
	"github.com/sycdtk/bobi/orm/mapper"
)

func init() {
	dbInfos := TableExist()
	for dbName, needInit := range dbInfos {
		if needInit {
			logger.Info("DB", dbName, ":", "init table structure success")
		}
	}
}

//新增
func Create(objs []interface{}, dataCol []string) {
	mapper.Create(objs, dataCol)
}

func CreateDB(dbName string, objs []interface{}, dataCol []string) {
	mapper.CreateForDB(dbName, objs, dataCol)
}

//删除
func Delete(objs []interface{}, whereDataCol []string) {
	mapper.Delete(objs, whereDataCol)
}

func DeleteDB(dbName string, objs []interface{}, whereDataCol []string) {
	mapper.DeleteForDB(dbName, objs, whereDataCol)
}

//按ID删除
func DeleteByID(objs []interface{}) {
	mapper.DeleteByID(objs)
}

func DeleteByIDDB(dbName string, objs []interface{}) {
	mapper.DeleteByIDForDB(dbName, objs)
}

//更新
func Update(objs []interface{}, dataCol []string, whereDataCol []string) {
	mapper.Update(objs, dataCol, whereDataCol)
}

func UpdateDB(dbName string, objs []interface{}, dataCol []string, whereDataCol []string) {
	mapper.UpdateForDB(dbName, objs, dataCol, whereDataCol)
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

//返回数据库是否需要初始化
func TableExist() map[string]bool {

	dbInitMap := map[string]bool{}

	dbNames := strings.Split(config.Read("db", "dbName"), ",")

	for _, dbName := range dbNames {
		//数据库类型
		dbType := config.Read(dbName, "dbType")
		switch dbType {
		case "sqlite3":
			dbInitMap[dbName] = len(db.QueryDB(dbName, "select name as tablename from sqlite_master where type='table' and name = 'bobi'")) == 0
		case "postgres":
			dbInitMap[dbName] = len(db.QueryDB(dbName, "select relname as tablename from pg_class where relname = 'bobi'")) == 0
		case "mysql":
			dbSchema := config.Read(dbName, "dbSchema")
			dbInitMap[dbName] = len(db.QueryDB(dbName, "select table_name as tablename from information_schema.tables where table_schema=? and table_name ='bobi'", dbSchema)) == 0
		default:
			logger.Info("DB", ":", "No initialization")
		}
	}
	return dbInitMap
}
