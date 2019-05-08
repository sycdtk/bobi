package message

import (
	"testing"
)

type Aaa struct {
	Aa string
	Bb []string
}

func TestMessageJson(t *testing.T) {
	a := &Aaa{Aa: "name", Bb: []string{"xxx", "yyy", "zzzz"}}
	b := &Aaa{Aa: "xxxx", Bb: []string{"oooo", "pppp", "qqqq"}}

	m := NewMessage(SuccessCode, SuccessMsg, []*Aaa{a, b})
	t.Log(string(m))

	var xobj interface{}
	xobj = nil
	m = NewMessage(SuccessCode, SuccessMsg, xobj)
	t.Log(string(m))
}
