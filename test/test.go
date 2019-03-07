package test

import (
	"fmt"
	"reflect"
	"strconv"
	"strings"
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

	rv := reflect.ValueOf(&Aaa{})

	rvs := []*Aaa{&Aaa{}, &Aaa{}, &Aaa{}}

	fmt.Println(strings.HasPrefix(rv.Type().String(), "[]"))

	rvss := reflect.ValueOf(rvs)

	fmt.Println(strings.HasPrefix(rvss.Type().String(), "[]"))

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
