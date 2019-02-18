package test

import (
	"fmt"
	"reflect"
)

type Aaa struct {
	ID       string
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

	obj := trans(&Aaa{ID: "111"})

	m1t := reflect.TypeOf(obj)
	nf := m1t.Elem().NumField() //获取字段数量
	fmt.Println(nf)
	for i := 0; i < nf; i++ {
		fmt.Println(m1t.Elem().Field(i).Name) //输出字段名称
	}

	fmt.Println("----------")

	m1v := reflect.ValueOf(obj)

	for i := 0; i < nf; i++ {
		fmt.Println(reflect.Indirect(m1v).FieldByName(indirect(m1t).Field(i).Name)) //获取字段值
	}

	a := &Aaa{ID: "x11", Username: "lirui"}

	b := make([]interface{}, 10)
	b = append(b, a)
}
