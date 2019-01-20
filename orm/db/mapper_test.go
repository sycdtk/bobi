package db

import (
	"testing"

	"github.com/sycdtk/bobi/logger"
)

type Aaa struct {
	ID       string
	Username string
}

func TestWrite(t *testing.T) {

	results := Pool.Query("SELECT id,username FROM test ")

	dataList := Write(func() interface{} { return &Aaa{} }, results, []string{"id", "username"})

	logger.Info("==>", len(dataList))

	for _, data := range dataList {
		if d, ok := data.(*Aaa); ok {
			logger.Info(d.ID, d.Username)
		}

	}

}
