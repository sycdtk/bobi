package expression

import (
	"testing"

	"github.com/sycdtk/bobi/set"
)

func TestBaseOper(t *testing.T) {
	//and
	if and([]bool{true, false, true, true}) == false {
		t.Log("and([]bool{true, false, true, true}) == false")
	} else {
		t.Error("error:", "and([]bool{true, false, true, true}) == false", and([]bool{true, false, true, true}) == false)
	}

	if and([]bool{true, true}) == true {
		t.Log("and([]bool{true, true}) == true")
	} else {
		t.Error("error:", "and([]bool{true, true}) == true", and([]bool{true, true}) == true)
	}

	if and([]bool{true}) == true {
		t.Log("and([]bool{true}) == true")
	} else {
		t.Error("error:", "and([]bool{true}) == true", and([]bool{true}) == true)
	}

	if and([]bool{false}) == false {
		t.Log("and([]bool{false}) == false")
	} else {
		t.Error("error:", "and([]bool{false}) == false", and([]bool{false}) == false)
	}

	if and([]bool{}) == false {
		t.Log("and([]bool{}) == false")
	} else {
		t.Error("error:", "and([]bool{}) == false", and([]bool{}) == false)
	}

	//or
	if or([]bool{true, false, true, true}) == true {
		t.Log("or([]bool{true, false, true, true}) == false")
	} else {
		t.Error("error:", "or([]bool{true, false, true, true}) == false", or([]bool{true, false, true, true}) == false)
	}

	if or([]bool{true, true}) == true {
		t.Log("or([]bool{true, true}) == true")
	} else {
		t.Error("error:", "or([]bool{true, true}) == true", or([]bool{true, true}) == true)
	}

	if or([]bool{true}) == true {
		t.Log("or([]bool{true}) == true")
	} else {
		t.Error("error:", "or([]bool{true}) == true", or([]bool{true}) == true)
	}

	if or([]bool{false}) == false {
		t.Log("or([]bool{false}) == false")
	} else {
		t.Error("error:", "and([]bool{false}) == false", or([]bool{false}) == false)
	}

	if or([]bool{}) == false {
		t.Log("or([]bool{}) == false")
	} else {
		t.Error("error:", "and([]bool{}) == false", or([]bool{}) == false)
	}

	//not
	if not(true) == false {
		t.Log("not(true) == false")
	} else {
		t.Error("error:", "not(true) == false", not(true) == false)
	}

	if not(false) == true {
		t.Log("not(false) == true")
	} else {
		t.Error("error:", "not(false) == true", not(false) == true)
	}

	//in
	s := set.NewSet()
	s.Add("aaa")
	s.Add("bbb")
	s.Add("ccc")
	s.Add("ddd")

	if in("aaa", s) == true {
		t.Log("aaa,bbb,ccc,ddd  -->  in(\"aaa\", s) == true")
	} else {
		t.Error("error:", "aaa,bbb,ccc,ddd  -->  in(\"aaa\", s) == true", in("aaa", s) == true)
	}

	if in("fff", s) == false {
		t.Log("aaa,bbb,ccc,ddd  -->  in(\"fff\", s) == false")
	} else {
		t.Error("error:", "aaa,bbb,ccc,ddd  -->  in(\"fff\", s) == false", in("fff", s) == false)
	}

	//nin
	if nin("aaa", s) == false {
		t.Log("aaa,bbb,ccc,ddd  -->  nin(\"aaa\", s) == false")
	} else {
		t.Error("error:", "aaa,bbb,ccc,ddd  -->  nin(\"aaa\", s) == false", nin("aaa", s) == false)
	}

	if nin("fff", s) == true {
		t.Log("aaa,bbb,ccc,ddd  -->  nin(\"fff\", s) == true")
	} else {
		t.Error("error:", "aaa,bbb,ccc,ddd  -->  nin(\"fff\", s) == true", nin("fff", s) == true)
	}

	//eq
	if eq("aaa", "aab") == false {
		t.Log("eq(\"aaa\", \"aab\") == false")
	} else {
		t.Error("error:", "eq(\"aaa\", \"aab\") == false", eq("aaa", "aab") == false)
	}

	if eq("aaa", "aaa") == true {
		t.Log("eq(\"aaa\", \"aaa\") == true")
	} else {
		t.Error("error:", "eq(\"aaa\", \"aaa\") == true", eq("aaa", "aaa") == true)
	}

	//neq
	if neq("aaa", "aab") == true {
		t.Log("neq(\"aaa\", \"aab\") == true")
	} else {
		t.Error("error:", "neq(\"aaa\", \"aab\") == true", neq("aaa", "aab") == true)
	}

	if neq("aaa", "aaa") == false {
		t.Log("neq(\"aaa\", \"aaa\") == false")
	} else {
		t.Error("error:", "neq(\"aaa\", \"aaa\") == false", neq("aaa", "aaa") == false)
	}

	//gt
	if gt(10, -2) == true {
		t.Log("gt(10, -2) == true")
	} else {
		t.Error("error:", "gt(10, -2) == true", gt(10, -2))
	}

	if gt(0.23432, 1) == false {
		t.Log("gt(0.23432, 1) == false")
	} else {
		t.Error("error:", "gt(0.23432, 1) == false", gt(0.23432, 1))
	}

	//ge
	if ge(3.3333, 3.3333) == true {
		t.Log("ge(3.3333, 3.3333) == true")
	} else {
		t.Error("error:", "ge(3.3333, 3.3333) == true", ge(3.3333, 3.3333))
	}

	if ge(3.3332, 3.3333) == false {
		t.Log("ge(3.3332, 3.3333) == false")
	} else {
		t.Error("error:", "ge(3.3332, 3.3333) == false", ge(3.3332, 3.3333))
	}

	//lt
	if lt(10, -2) == false {
		t.Log("lt(10, -2) == false")
	} else {
		t.Error("error:", "lt(10, -2) == false", lt(10, -2))
	}

	if lt(0.23432, 1) == true {
		t.Log("lt(0.23432, 1) == true")
	} else {
		t.Error("error:", "lt(0.23432, 1) == true", lt(0.23432, 1))
	}

	//le
	if le(3.3333, 3.3333) == true {
		t.Log("le(3.3333, 3.3333) == true")
	} else {
		t.Error("error:", "le(3.3333, 3.3333) == true", le(3.3333, 3.3333))
	}

	if le(3.3332, 3.3331) == false {
		t.Log("le(3.3332, 3.3331) == false")
	} else {
		t.Error("error:", "le(3.3332, 3.3331) == false", le(3.3332, 3.3331))
	}

}
