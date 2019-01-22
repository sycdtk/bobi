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

	Register(func() interface{} { return &Aaa{} })

	results := db.QueryDB("test2", "SELECT id,username FROM test1 ")

	dataList := Write(&Aaa{}, results, []string{"id", "username"})

	logger.Info("==>", len(dataList))

	for _, data := range dataList {
		if d, ok := data.(*Aaa); ok {
			logger.Info(d.ID, d.Username)
		}
	}

	results = db.QueryDB("test2", "SELECT id,username FROM test1 ")
	dataList = Write(&Aaa{}, results, []string{"id", "username"})

	logger.Info("==>", len(dataList))

	for _, data := range dataList {
		if d, ok := data.(*Aaa); ok {
			logger.Info(d.ID, d.Username)
		}

	}

}
