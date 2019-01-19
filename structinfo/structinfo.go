package structinfo

import (
	"reflect"
	"regexp"
	"strconv"
	"strings"
	"time"
)

//tag定义
type StructTag struct {
	Name  string
	Value string
}

//filed定义
type StructField struct {
	Name     string
	Type     string
	Value    interface{} //struct 对应字段值
	SQLValue string      //struct 对应字段值，转换为sql格式
	Tags     []*StructTag
}

func (sf StructField) Tag(name string) string {
	for _, tag := range sf.Tags {
		if name == tag.Name {
			return tag.Value
		}
	}
	return ""
}

//struct定义
type StructInfo struct {
	Pkg    string
	Name   string
	Fields []*StructField
}

//反射struct名称、属性、属性值、tag等
func NewStructInfo(stru interface{}) *StructInfo {

	st := typeIndirect(reflect.TypeOf(stru))

	//	sv := reflect.Indirect(reflect.ValueOf(stru))

	var sfs []*StructField

	reg := regexp.MustCompile(`(\w+):"(\w+)"`)

	for i := 0; i < st.NumField(); i++ {

		var sftgs []*StructTag

		tags := strings.Split(string(st.Field(i).Tag), " ") //tag 分组

		for _, tag := range tags {
			tkv := reg.FindStringSubmatch(tag)
			if len(tkv) == 3 {
				sftgs = append(sftgs, &StructTag{Name: tkv[1], Value: tkv[2]})
			}
		}

		sfs = append(sfs, &StructField{Name: st.Field(i).Name, Type: st.Field(i).Type.String(), Tags: sftgs})

	}

	return &StructInfo{Pkg: st.PkgPath(), Name: st.Name(), Fields: sfs}
}

func makeSQLValue(v reflect.Value) string {
	k := v.kind()
	switch k {
	case reflect.Bool:
		if v.Bool() {
			return "true"
		}
		return "false"
	case reflect.Int:
		fallthrough
	case reflect.Int:
		fallthrough
	case reflect.Int16:
		fallthrough
	case reflect.Int32:
		fallthrough
	case reflect.Int64:
		fallthrough
		return strconv.Itoa(v.Int())
	case reflect.Uint:
		fallthrough
	case reflect.Uint8:
		fallthrough
	case reflect.Uint16:
		fallthrough
	case reflect.Uint32:
		fallthrough
	case reflect.Uint64:
		fallthrough
		return strconv.Itoa(int(v.Uint()))
	//case reflect.Uintptr:
	case reflect.Float32:
		fallthrough
	case reflect.Float64:
		return strconv.FormatFloat(v.Float(), 'f', -1, 64)
	//case reflect.Complex64:
	//case reflect.Complex128:
	//case reflect.Array:
	//case reflect.Chan:
	//case reflect.Func:
	//	case reflect.Interface:
	//case reflect.Map:
	//case reflect.Ptr:
	//case reflect.Slice:
	case reflect.String:
		return v.String()
	case reflect.Struct:
		if v.Type() == time.Time {
			return time.Time(v)
		}

		//case reflect.UnsafePointer:

	}
}

//判断指针还是引用对象
func typeIndirect(t reflect.Type) reflect.Type {
	if t.Kind() == reflect.Ptr {
		return t.Elem()
	}
	return t
}
