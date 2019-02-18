package orm

import (
	"testing"
)

type Aaa struct {
	ID       string
	Username string
}

func TestQuery(t *testing.T) {

	Register(func() interface{} { return &Aaa{} })

	finalData := []*Aaa{}
	list := Query(&Aaa{}, "select id,username from test1", []string{"id", "username"})
	for _, data := range list {
		if d, ok := data.(*Aaa); ok {
			finalData = append(finalData, d)
		}
	}

	for _, data := range finalData {
		t.Log(data.ID, data.Username)
	}
}

func TestTableExist(t *testing.T) {
	dbInitMap := TableExist()
	if dbInitMap["test2"] {
		t.Log("Need Init DB!")
	}

}
