package logger

import (
	"testing"
)

func TestNewLogger(t *testing.T) {

	Debug("a 1")
	Info("a 2")
	Err("a 3")

	SetLevel(INFO)

	Debug("b 1")
	Info("b 2")
	Err("b 3")

	SetLevel(ERROR)
	Debug("c 1")
	Info("c 2")
	Err("c 3")

}
