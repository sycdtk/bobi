package mapper

import (
	"testing"

	"github.com/sycdtk/bobi/logger"
	"github.com/sycdtk/bobi/orm/db"
)

type Aaa struct {
	ID       string
	Username string
}

func TestWrite(t *testing.T) {

	Register("orm", func() interface{} { return &Aaa{} })

	results := db.QueryDB("test2", "SELECT id,username FROM test1 ")

	dataList := Write(&Aaa{}, results, []string{"id", "username"})

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
		logger.Info(data.ID, data.Username)
	}

}

func TestCreate(t *testing.T) {
	Register("test", func() interface{} { return &Aaa{} })

	Create([]interface{}{&Aaa{ID: "x11", Username: "lirui"}}, []string{"id", "username"})

	Create([]interface{}{&Aaa{ID: "x12", Username: "lirui"}}, []string{"id", "username"})

	Create([]interface{}{&Aaa{ID: "x13", Username: "lirui"}}, []string{"id", "username"})

	Create([]interface{}{&Aaa{ID: "x11", Username: "lirui"}, &Aaa{ID: "x22", Username: "qingdao"}}, []string{"id", "username"})
}

func TestDelete(t *testing.T) {
	Register("test", func() interface{} { return &Aaa{} })

	Delete([]interface{}{&Aaa{ID: "x11", Username: "lirui"}}, []string{"id"})

	Delete([]interface{}{&Aaa{ID: "x11", Username: "qingdao"}}, []string{"username"})

	Delete([]interface{}{&Aaa{ID: "x11", Username: "lirui"}}, []string{"id", "username"})

	Delete([]interface{}{&Aaa{ID: "x11", Username: "lirui"}, &Aaa{ID: "x22", Username: "qingdao"}}, []string{"id", "username"})

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
}
