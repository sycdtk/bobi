package message

import (
	"encoding/json"
)

//1000 为成功
const SuccessCode = 1000
const SuccessMsg = "success"

//非1000 均为失败，默认2000
const FailedCode = 2000
const FailedMsg = "failed"

//返回数据对象
type message struct {
	Code int
	Msg  string
	Data interface{}
}

//空对象
type empty struct{}

func NewMessage(code int, msg string, data interface{}) []byte {
	//空对象处理
	if data == nil {
		data = &empty{}
	}
	msgObj := &message{Code: code, Msg: msg, Data: data}

	if datas, err := json.Marshal(msgObj); err != nil {
		return []byte{}
	} else {
		return datas
	}
}
