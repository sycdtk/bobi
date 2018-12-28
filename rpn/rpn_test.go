package rpn

import (
	"testing"
)

func TestParse(t *testing.T) {

	s := "@OR(@SEQ(a,b),@SEQ(c,d))"
	if Parse(s) == "| | a b @SEQ | c d @SEQ @OR" {
		t.Log(Parse(s))
	} else {
		t.Error("error:", Parse(s))
	}

}
