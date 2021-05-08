package logger

import (
	"testing"
)

func TestNewLogger(t *testing.T) {

	t.Log("logger test start")
	Debug("a 1", "a 111")
	Debug("a 1")

	Info("a 2")
	Err("a 3")

	t.Log("--SetLevel(INFO)-----------------------------")
	SetLevel(INFO)

	Debug("b 1")
	Info("b 2")
	Err("b 3")

	t.Log("--SetLevel(ERROR)-----------------------------")
	SetLevel(ERROR)
	Debug("c 1")
	Info("c 2")
	Err("c 3")
	t.Log("logger test finish")

}
