package mapper

import (
	"database/sql"
	"reflect"
	"strings"
	"sync"

	"github.com/sycdtk/bobi/logger"
)

const tablePrefix = "bobi"

var onceCache sync.Once
var structCache *StructCache //结构体字段小写与原Name映射关系缓存

//结构体映射缓存
type StructCache struct {
	//表名将以@tablename保存
	//objType.PkgPath()+"@"+objType.Name():@tablename name:table name
	//字段名已字段的小写字符保存
	//objType.PkgPath()+"@"+objType.Name():lower field name:fieldName
	cacheData map[string]map[string]string
	//objType.PkgPath()+"@"+objType.Name(): new Object function
	newFuncData map[string]func() interface{}
}

//获取缓存field原始名称
func (sc *StructCache) get(pathName, fieldName string) string {
	if data, ok := sc.cacheData[pathName]; ok {
		name, _ := data[fieldName]
		return name
	} else {
		return ""
	}
}

//检查是否在映射缓存
func (sc *StructCache) contains(pathName string) bool {
	_, ok := sc.cacheData[pathName]
	return ok
}

//增加字段名称、新建函数映射缓存
//modelName 模块名称缩写，用以设置表名中缀，bobi_wf_mapper_aaa,其中wf为模块名称：workflow
//newFn 实例新建函数
func Register(modelName string, newFn func() interface{}) {

	obj := newFn()

	objType, isIndirect := indirect(reflect.TypeOf(obj))
	objValue := reflect.ValueOf(obj)

	//新建函数加入缓存
	structCache.newFuncData[objType.PkgPath()+"@"+objType.Name()] = newFn

	//字段映射加入缓存
	data := map[string]string{}
	for i := 0; i < objType.NumField(); i++ {
		if isIndirect {
			if objValue.Elem().FieldByName(objType.Field(i).Name).CanSet() {
				data[strings.ToLower(objType.Field(i).Name)] = objType.Field(i).Name
			}
		} else {
			if objValue.FieldByName(objType.Field(i).Name).CanSet() {
				data[strings.ToLower(objType.Field(i).Name)] = objType.Field(i).Name
			}
		}
	}

	data["@tablename"] = tablePrefix + "_" + modelName + "_" + strings.ToLower(objType.Name())

	structCache.cacheData[objType.PkgPath()+"@"+objType.Name()] = data

	logger.Info("DB", ":", objType.PkgPath()+"@"+objType.Name(), "registered")
}

func init() {
	onceCache.Do(func() {
		structCache = &StructCache{
			cacheData:   map[string]map[string]string{},
			newFuncData: map[string]func() interface{}{},
		}
	})
}

//创建持久化对象
func Create(objs []interface{}, dataCol []string) {
	if len(objs) > 0 {

		objType, _ := indirect(reflect.TypeOf(objs[0]))
		pathName := objType.PkgPath() + "@" + objType.Name()
		//不在缓存则返回nil
		if structCache.contains(pathName) {

			//构建插入sql insert into
			createSQL := "insert into " + structCache.get(pathName, "@tablename") + " ("

			for _, colName := range dataCol {
				createSQL = createSQL + colName + ","
			}

			createSQL = strings.TrimSuffix(createSQL, ",") + ") values "

			for _, obj := range objs {
				niv := reflect.ValueOf(obj)
				createSQL = createSQL + "("
				for _, colName := range dataCol {
					c := structCache.get(pathName, colName)
					createSQL = createSQL + "'" + reflect.Indirect(niv).FieldByName(c).String() + "',"
				}
				createSQL = strings.TrimSuffix(createSQL, ",") + "),"
			}

			createSQL = strings.TrimSuffix(createSQL, ",") + ";"

			logger.Debug(createSQL)
		}
	}
}

//写入数据,传入结构体指针
func Write(obj interface{}, datas [][]sql.RawBytes, dataCol []string) []interface{} {

	objType, _ := indirect(reflect.TypeOf(obj))
	pathName := objType.PkgPath() + "@" + objType.Name()

	//不在缓存则返回nil
	if structCache.contains(pathName) {
		dataSet := []interface{}{}
		for _, data := range datas {
			ni := structCache.newFuncData[pathName]()
			niv := reflect.ValueOf(ni)
			for index, colName := range dataCol {
				reflect.Indirect(niv).FieldByName(structCache.get(pathName, colName)).SetString(string(data[index]))
			}
			dataSet = append(dataSet, ni)
		}
		return dataSet
	} else {
		return nil
	}
}

//判断指针还是引用对象
func indirect(t reflect.Type) (reflect.Type, bool) {
	if t.Kind() == reflect.Ptr {
		return t.Elem(), true
	}
	return t, false
}
