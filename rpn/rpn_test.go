package rpn

import (
	"testing"
)

func TestParse(t *testing.T) {

	s := "@OR(@EQ(a,b),@EQ(c,d))"
	if Parse(s) == "| | a b @EQ | c d @EQ @OR" {
		t.Log(Parse(s))
	} else {
		t.Error("error:", Parse(s))
	}

	s = "@AND(@EQ(a,b),@EQ(c,d))"
	if Parse(s) == "| | a b @EQ | c d @EQ @AND" {
		t.Log(Parse(s))
	} else {
		t.Error("error:", Parse(s))
	}

	s = "@OR(@EQ(a,b),@EQ(c,d),@IN(e,f))"
	if Parse(s) == "| | a b @EQ | c d @EQ | e f @IN @OR" {
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
