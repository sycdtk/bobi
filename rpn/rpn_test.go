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

	s = "@AND(@SEQ(a,b),@SEQ(c,d))"
	if Parse(s) == "| | a b @SEQ | c d @SEQ @AND" {
		t.Log(Parse(s))
	} else {
		t.Error("error:", Parse(s))
	}

	s = "@OR(@SEQ(a,b),@SEQ(c,d),@IN(e,f))"
	if Parse(s) == "| | a b @SEQ | c d @SEQ | e f @IN @OR" {
		t.Log(Parse(s))
	} else {
		t.Error("error:", Parse(s))
	}

	s = "@OR(@AND(@IN(a,b),@IN(c,d)),@NOT(@EQ(e,f)))"
	if Parse(s) == "| | | a b @IN | c d @IN @AND | | e f @EQ @NOT @OR" {
		t.Log(Parse(s))
	} else {
		t.Error("error:", Parse(s))
	}

}
