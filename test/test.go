package test

import (
	"fmt"
	"reflect"
	"strconv"
)

type Aaa struct {
	ID       string `ft:"aaa";ft1:"bb"`
	Username string
	Birth    string `ft:"date"`
	Sex      bool
	Age      int
	XXX      float64
	YYY      float32
}

func indirect(t reflect.Type) reflect.Type {
	if t.Kind() == reflect.Ptr {
		return t.Elem()
	}
	return t
}

func trans(data *Aaa) interface{} {
	return data
}

func TMain() {

	// obj := &Aaa{ID: "111", Username: "wolffy", Birth: "2019-01-01 00:00:00", Sex: true, Age: 30, XXX: 23.2222, YYY: 1.1111}

	objv := reflect.TypeOf(&Aaa{})

	fmt.Println(fieldType(objv.Elem().Field(0)))
	fmt.Println(fieldType(objv.Elem().Field(1)))
	fmt.Println(fieldType(objv.Elem().Field(2)))
	fmt.Println(fieldType(objv.Elem().Field(3)))
	fmt.Println(fieldType(objv.Elem().Field(4)))
	fmt.Println(fieldType(objv.Elem().Field(5)))
	fmt.Println(fieldType(objv.Elem().Field(6)))

	// fmt.Println(objv.Elem().Field(1).Type().String())
	// fmt.Println(objv.Elem().Field(2).Type().String())
	// fmt.Println(objv.Elem().Field(3).Type().String())
	// fmt.Println(objv.Elem().Field(4).Type().String())

	// objType := indirect(reflect.TypeOf(obj))

	// fmt.Println("-->", objType.Field(0).Tag.Get("ft"))
	// fmt.Println("-->", objType.Field(0).Tag.Get("ft1"))

}

func fieldType(data reflect.Value) string {
	result := ""
	switch data.Type().String() {
	case "string":
		result = data.String()
	case "int":
		result = strconv.FormatInt(data.Int(), 10)
	case "bool":
		result = strconv.FormatBool(data.Bool())
	case "float32":
		result = strconv.FormatFloat(data.Float(), 'f', -1, 32)
	case "float64":
		result = strconv.FormatFloat(data.Float(), 'f', -1, 64)
	default:
		result = data.String()
	}
	return result
}
