package test

import (
	"fmt"
	"reflect"
)

type Aaa struct {
	ID       string `ft:"aaa";ft1:"bb"`
	Username string
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

	obj := &Aaa{ID: "111"}

	objType := indirect(reflect.TypeOf(obj))

	fmt.Println("-->", objType.Field(0).Tag.Get("ft"))
	fmt.Println("-->", objType.Field(0).Tag.Get("ft1"))

}
