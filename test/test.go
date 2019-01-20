package test

import (
	"fmt"
	"reflect"
)

type Aaa struct {
	ID       string
	Username string
}

func indirect(t reflect.Type) (reflect.Type, bool) {
	if t.Kind() == reflect.Ptr {
		return t.Elem(), true
	}
	return t, false
}

func TMain() {
	var obj interface{}
	t, v := indirect(reflect.TypeOf(&Aaa{}))
	if v {
		obj = reflect.New(t).Elem().Interface()
	} else {
		obj = reflect.New(t).Interface()
	}

	vv := reflect.ValueOf(obj)
	vv.Elem().FieldByName("ID").SetString("1234")

	fmt.Println(obj.(Aaa).ID)
	fmt.Println(obj.(Aaa).Username)
}
