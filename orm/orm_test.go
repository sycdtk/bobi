package orm

import (
	"testing"
)

type Aaa struct {
	ID       string `ft:"aaa" ft1:"bb"`
	Username string
	Birth    string `ft:"date"`
	Sex      bool
	Age      int
	XXX      float64
	YYY      float32
	QQQ      []string
}

func TestOrm(t *testing.T) {

	Register("test", func() interface{} { return &Aaa{} })

	Execute("DROP TABLE bobi_test_aaa;")
	Execute(`CREATE TABLE
    bobi_test_aaa
    (
		id TEXT,
        username TEXT,
		birth TEXT,
		sex INTEGER,
		age INTEGER,
		xxx REAL,
		yyy REAL
    );`)

	a := &Aaa{ID: "10001", Username: "guanyu", Birth: "2019-02-25 00:32:00", Sex: true, Age: 1, XXX: 1.0021, YYY: 3.2213}
	b := &Aaa{ID: "10002", Username: "zhangfei", Birth: "2019-02-26 00:32:00", Sex: true, Age: 1, XXX: 2.0021, YYY: 45.2213}
	c := &Aaa{ID: "10003", Username: "liubei", Birth: "2019-02-27 00:32:00", Sex: false, Age: 1, XXX: 3.0021, YYY: 66.2213}

	cols := []string{"id", "username", "birth", "sex", "age", "xxx", "yyy"}

	Create([]interface{}{a, b, c}, cols)

	finalDatas := []*Aaa{}
	results := Query(&Aaa{}, "select id,username,birth,age,sex,xxx,yyy from bobi_test_aaa", cols)

	for _, result := range results {
		if data, ok := result.(*Aaa); ok {
			finalDatas = append(finalDatas, data)
		}
	}

	for _, data := range finalDatas {
		t.Log(data.ID, data.Username, data.Sex, data.Age)
	}
}

// func TestTableExist(t *testing.T) {
// 	dbInitMap := TableExist()
// 	if dbInitMap["test2"] {
// 		t.Log("Need Init DB!")
// 	}
// }
