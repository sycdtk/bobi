package test

import (
	"fmt"
	"net"
	"reflect"
	"strconv"
	"strings"

	"github.com/sycdtk/bobi/logger"
	"github.com/sycdtk/bobi/set"
)

type Aaa struct {
	ID       string `ft:"aaa";ft1:"bb"`
	Username string
	Birth    string `ft:"date"`
	Sex      bool
	Age      int
	XXX      float64
	YYY      float32
	QQQ      []string
	OOO      []*Aaa
	WWW      *set.Set
	ZZZ      map[string]string
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

	obj := &Aaa{ID: "111", Username: "wolffy", Birth: "2019-01-01 00:00:00", Sex: true, Age: 30, XXX: 23.2222, YYY: 1.1111}

	relfectValue := reflect.ValueOf(obj)

	relfectType := reflect.TypeOf(obj)
	net.DefaultResolver

	dd := reflect.Indirect(rv).FieldByName("WWW")

	zzz := reflect.Indirect(rv).FieldByName("ZZZ")

	qqq := reflect.Indirect(rv).FieldByName("QQQ")

	id := reflect.Indirect(rv).FieldByName("ID")
	ooo := reflect.Indirect(rv).FieldByName("OOO")

	logger.Info(rv.Kind().String())
	logger.Info(rv.Type().String())
	logger.Info(dd.Kind().String())
	logger.Info(dd.Type().String())
	logger.Info(zzz.Kind().String())
	logger.Info(zzz.Type().String())
	logger.Info(qqq.Kind().String())
	logger.Info(qqq.Type().String())
	logger.Info(id.Kind().String())
	logger.Info(id.Type().String())
	logger.Info(ooo.Kind().String())
	logger.Info(ooo.Type().String())
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
