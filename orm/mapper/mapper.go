package mapper

import (
	"database/sql"
	"reflect"
	"strings"
	"sync"
	//"github.com/sycdtk/bobi/logger"
)

var onceCache sync.Once
var structCache *StructCache

//结构体映射缓存
type StructCache struct {
	cacheData map[string]map[string]string
}

//加入映射缓存
func (sc *StructCache) add(obj interface{}) {

	data := map[string]string{}

	objType, isIndirect := indirect(reflect.TypeOf(obj))
	objValue := reflect.ValueOf(obj)

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

	sc.cacheData[objType.PkgPath()+"@"+objType.Name()] = data
}

//获取缓存field原始名称
func (sc *StructCache) Get(pathName, fieldName string) string {
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

func init() {
	onceCache.Do(func() {
		structCache = &StructCache{cacheData: map[string]map[string]string{}}
	})
}

//写入数据,传入结构体指针
func Write(nf func() interface{}, datas [][]sql.RawBytes, dataCol []string) []interface{} {
	obj := nf()
	relObjType := reflect.TypeOf(obj)
	objType, _ := indirect(relObjType)
	pathName := objType.PkgPath() + "@" + objType.Name()

	//不在缓存则加入缓存中
	if !structCache.contains(pathName) {
		structCache.add(obj)
	}

	dataSet := []interface{}{}

	for _, data := range datas {
		ni := nf()
		niv := reflect.ValueOf(ni)
		for index, colName := range dataCol {
			reflect.Indirect(niv).FieldByName(structCache.Get(pathName, colName)).SetString(string(data[index]))
		}
		dataSet = append(dataSet, ni)
	}
	return dataSet
}

//判断指针还是引用对象
func indirect(t reflect.Type) (reflect.Type, bool) {
	if t.Kind() == reflect.Ptr {
		return t.Elem(), true
	}
	return t, false
}
