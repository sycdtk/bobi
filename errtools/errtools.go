package errtools

import (
	"errors"

	"github.com/sycdtk/bobi/logger"
)

//创建异常
func NewErr(errStr string) error {
	logger.Err3("异常：", errStr)
	return errors.New(errStr)
}

//检查错误
func CheckErr(err error, errStr ...interface{}) {
	if err != nil {
		logger.Err3("异常：", errStr, err)
		//os.Exit(1)
	}
}
