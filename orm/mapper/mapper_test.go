package mapper

import (
	"testing"

	"github.com/sycdtk/bobi/logger"
	"github.com/sycdtk/bobi/orm/db"
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

func TestRegister(t *testing.T) {
	Register("orm", func() interface{} { return &Aaa{} })

}

func TestWrite(t *testing.T) {

	Register("orm", func() interface{} { return &Aaa{} })

	results := db.Query("SELECT id,username,birth,age FROM bobi_test_aaa ")

	dataList := Write(&Aaa{}, results, []string{"id", "username", "birth", "age"})

	resultList := func(dataList []interface{}) []*Aaa {
		resultList := []*Aaa{}
		for _, data := range dataList {
			if newObj, ok := data.(*Aaa); ok {
				resultList = append(resultList, newObj)
			}
		}
		return resultList
	}(dataList)

	logger.Info("==>", len(dataList))

	for _, data := range resultList {
		logger.Info(data.ID, data.Username, data.Birth, data.Sex, data.Age, data.XXX, data.YYY)
	}

}

func TestCreate(t *testing.T) {
	Register("test", func() interface{} { return &Aaa{} })

	a := &Aaa{ID: "111", Username: "wolffy", Birth: "2019-01-01 00:00:00", Sex: true, Age: 30, XXX: 23.2222, YYY: 1.1111}
	b := &Aaa{ID: "222", Username: "wolffy", Birth: "2019-01-01 00:00:00", Sex: true, Age: 30, XXX: 23.2222, YYY: 1.1111}

	Create([]interface{}{a, b}, []string{"id", "username", "birth", "sex", "age", "xxx", "yyy"})

	Create([]interface{}{&Aaa{ID: "x12", Username: "lirui"}}, []string{"id", "username"})

	Create([]interface{}{&Aaa{ID: "x13", Username: "lirui"}}, []string{"id", "username"})

	Create([]interface{}{&Aaa{ID: "x11", Username: "lirui"}, &Aaa{ID: "x22", Username: "qingdao"}}, []string{"id", "username"})

	Create([]interface{}{&Aaa{ID: "x13", Username: "lirui", QQQ: []string{"aa", "bb"}}}, []string{"id", "username", "qqq"})
}

func TestDelete(t *testing.T) {
	Register("test", func() interface{} { return &Aaa{} })

	Delete([]interface{}{&Aaa{ID: "x11", Username: "lirui"}}, []string{"id"})

	Delete([]interface{}{&Aaa{ID: "x11", Username: "qingdao"}}, []string{"username"})

	Delete([]interface{}{&Aaa{ID: "x11", Username: "lirui"}}, []string{"id", "username"})

	Delete([]interface{}{&Aaa{ID: "x11", Username: "lirui"}, &Aaa{ID: "x22", Username: "qingdao"}}, []string{"id", "username"})

	Delete([]interface{}{&Aaa{ID: "111", Username: "wolffy", Birth: "2019-01-01 00:00:00", Sex: true, Age: 30, XXX: 23.2222, YYY: 1.1111}}, []string{"id", "username", "birth", "sex", "age", "xxx", "yyy"})

}

func TestDeleteByID(t *testing.T) {
	Register("test", func() interface{} { return &Aaa{} })

	DeleteByID([]interface{}{&Aaa{ID: "x11", Username: "lirui"}})

	DeleteByID([]interface{}{&Aaa{ID: "x11", Username: "lirui"}})

	DeleteByID([]interface{}{&Aaa{ID: "x11", Username: "lirui"}, &Aaa{ID: "x22", Username: "qingdao"}})

}

func TestUpdate(t *testing.T) {
	Register("test", func() interface{} { return &Aaa{} })

	Update([]interface{}{&Aaa{ID: "x12", Username: "wolffy"}}, []string{"username"}, []string{"id"})

	Update([]interface{}{&Aaa{ID: "x12", Username: "wolffy"}, &Aaa{ID: "x13", Username: "qingdao"}}, []string{"username"}, []string{"id"})

	Update([]interface{}{&Aaa{ID: "111", Username: "wolffy", Birth: "2019-01-01 00:00:00", Sex: true, Age: 30, XXX: 23.2222, YYY: 1.1111}}, []string{"id", "username", "birth", "sex", "age", "xxx", "yyy"}, []string{"id"})

}
