package exprssion

import (
	"testing"

	"github.com/sycdtk/bobi/set"
	//"github.com/sycdtk/bobi/stack"
)

func TestFunction(t *testing.T) {

	if StrToInt64("39") == 39 {
		t.Log("StrToInt64(\"39\") == 39")
	} else {
		t.Error("error:", "StrToInt64(\"39\") == 39", StrToInt64("39"))
	}

	if Int64ToStr(111) == "111" {
		t.Log("Int64ToStr(111) == \"111\"")
	} else {
		t.Error("error:", "Int64ToStr(111) == \"111\"", Int64ToStr(111))
	}

	if StrToFloat64("12.170101") == 12.170101 {
		t.Log("StrToFloat64(\"12.170101\") == 12.170101")
	} else {
		t.Error("error:", "StrToFloat64(\"12.170101\") == 12.170101", StrToFloat64("12.170101"))
	}

	if Float64ToStr(39.2342) == "39.2342" {
		t.Log("Float64ToStr(39.2342) == \"39.2342\"")
	} else {
		t.Error("error:", "Float64ToStr(39.2342) == \"39.2342\"", Float64ToStr(39.2342))
	}

	if StrToBool("true") == true {
		t.Log("StrToBool(\"true\") == true")
	} else {
		t.Error("error:", "StrToBool(\"true\") == true", StrToBool("true"))
	}

	if StrToBool("false") == false {
		t.Log("StrToBool(\"false\") == false")
	} else {
		t.Error("error:", "StrToBool(\"false\") == false", StrToBool("false"))
	}

	if StrToBool("aaa") == false {
		t.Log("StrToBool(\"aaa\") == false")
	} else {
		t.Error("error:", "StrToBool(\"aaa\") == false", StrToBool("aaa"))
	}

	if BoolToStr(true) == "true" {
		t.Log("BoolToStr(true) == \"true\"")
	} else {
		t.Error("error:", "BoolToStr(true) == \"true\"", BoolToStr(true))
	}

	if BoolToStr(false) == "false" {
		t.Log("BoolToStr(false) == \"false\"")
	} else {
		t.Error("error:", "BoolToStr(false) == \"false\"", BoolToStr(false))
	}

	s := set.NewSet()
	s.Add("aa")
	s.Add("bb")
	s.Add("cc")
	s.Del("bb")

	if len(SetToStr(s)) == 5 {
		t.Log("len(SetToStr(s)) == 5", SetToStr(s))
	} else {
		t.Error("error:", "len(SetToStr(s)) == 5", SetToStr(s))
	}

	s.Del("aa")
	s.Del("cc")
	if len(SetToStr(s)) == 0 {
		t.Log("len(SetToStr(s)) == 0", SetToStr(s))
	} else {
		t.Error("error:", "len(SetToStr(s)) == 0", SetToStr(s))
	}

	s.Add("aa")
	if SetToStr(s) == "aa" {
		t.Log("SetToStr(s) == \"aa\"", SetToStr(s))
	} else {
		t.Error("error:", "SetToStr(s) == \"aa\"", SetToStr(s))
	}

	if StrToSet("aaa").Size() == 1 {
		t.Log("StrToSet(\"aaa\").Size() == 1", StrToSet("aaa").ToString())
	} else {
		t.Error("error:", "StrToSet(\"aaa\").Size() == 1", StrToSet("aaa").ToString())
	}

	if StrToSet("aaa,bbb,ccc").Size() == 3 {
		t.Log("StrToSet(\"aaa,bbb,ccc\").Size() == 3", StrToSet("aaa,bbb,ccc").ToString())
	} else {
		t.Error("error:", "StrToSet(\"aaa,bbb,ccc\").Size() == 3", StrToSet("aaa,bbb,ccc").ToString())
	}

}

func TestFormula(t *testing.T) {

	kvMap := map[string]string{
		"data1": "aa,bb,cc,dd",
		"data2": "true,true,true",
		"data3": "110",
		"data4": "wolffy",
	}

	Reg("AND1", "@AND(true,false)")
	if Calc("AND1", kvMap) == false {
		t.Log("Calc(\"AND1\", kvMap) == false")
	} else {
		t.Error("Calc(\"AND1\", kvMap) == false", Calc("AND1", kvMap), kvMap)
	}

	Reg("AND2", "@AND(true,true,true)")
	if Calc("AND2", kvMap) == true {
		t.Log("Calc(\"AND2\", kvMap) == true")
	} else {
		t.Error("Calc(\"AND2\", kvMap) == true", Calc("AND2", kvMap), kvMap)
	}

	Reg("OR1", "@OR(true,false)")
	if Calc("OR1", kvMap) == true {
		t.Log("Calc(\"OR1\", kvMap) == true")
	} else {
		t.Error("Calc(\"OR1\", kvMap) == true", Calc("OR1", kvMap), kvMap)
	}

	Reg("OR2", "@OR(false,false)")
	if Calc("OR2", kvMap) == false {
		t.Log("Calc(\"OR2\", kvMap) == false")
	} else {
		t.Error("Calc(\"OR2\", kvMap) == false", Calc("OR2", kvMap), kvMap)
	}

	Reg("NOT1", "@NOT(false)")
	if Calc("NOT1", kvMap) == true {
		t.Log("Calc(\"NOT1\", kvMap) == true")
	} else {
		t.Error("Calc(\"NOT1\", kvMap) == true", Calc("NOT1", kvMap), kvMap)
	}

	Reg("NOT2", "@NOT(true)")
	if Calc("NOT2", kvMap) == false {
		t.Log("Calc(\"NOT2\", kvMap) == false")
	} else {
		t.Error("Calc(\"NOT2\", kvMap) == false", Calc("NOT2", kvMap), kvMap)
	}

	Reg("IN1", "@IN(aa,data1)")
	if Calc("IN1", kvMap) == true {
		t.Log("Calc(\"IN1\", kvMap) == true")
	} else {
		t.Error("Calc(\"IN1\", kvMap) == true", Calc("IN1", kvMap), kvMap)
	}

	Reg("IN2", "@IN(ff,data1)")
	if Calc("IN2", kvMap) == false {
		t.Log("Calc(\"IN2\", kvMap) == false")
	} else {
		t.Error("Calc(\"IN2\", kvMap) == false", Calc("IN2", kvMap), kvMap)
	}

	Reg("IN3", "@OR(@IN(aa,data1),@IN(ff,data1))")
	if Calc("IN3", kvMap) == true {
		t.Log("Calc(\"IN3\", kvMap) == true")
	} else {
		t.Error("Calc(\"IN3\", kvMap) == true", Calc("IN3", kvMap), kvMap)
	}

	Reg("IN4", "@AND(@IN(aa,data1),@IN(ff,data1))")
	if Calc("IN4", kvMap) == false {
		t.Log("Calc(\"IN4\", kvMap) == false")
	} else {
		t.Error("Calc(\"IN4\", kvMap) == false", Calc("IN4", kvMap), kvMap)
	}

	Reg("NIN1", "@NIN(aa,data1)")
	if Calc("NIN1", kvMap) == false {
		t.Log("Calc(\"NIN1\", kvMap) == false")
	} else {
		t.Error("Calc(\"NIN1\", kvMap) == false", Calc("NIN1", kvMap), kvMap)
	}

	Reg("NIN2", "@NIN(ff,data1)")
	if Calc("NIN2", kvMap) == true {
		t.Log("Calc(\"NIN2\", kvMap) == true")
	} else {
		t.Error("Calc(\"NIN2\", kvMap) == true", Calc("NIN2", kvMap), kvMap)
	}

	Reg("NIN3", "@AND(@NIN(ff,data1),@NIN(aa,data1)")
	if Calc("NIN3", kvMap) == false {
		t.Log("Calc(\"NIN3\", kvMap) == false")
	} else {
		t.Error("Calc(\"NIN3\", kvMap) == false", Calc("NIN3", kvMap), kvMap)
	}

	Reg("NIN4", "@OR(@NIN(ff,data1),@NIN(aa,data1))")
	if Calc("NIN4", kvMap) == true {
		t.Log("Calc(\"NIN4\", kvMap) == true")
	} else {
		t.Error("Calc(\"NIN4\", kvMap) == true", Calc("NIN4", kvMap), kvMap)
	}

	Reg("GT1", "@GT(111,22)")
	if Calc("GT1", kvMap) == true {
		t.Log("Calc(\"GT1\", kvMap) == true")
	} else {
		t.Error("Calc(\"GT1\", kvMap) == true", Calc("GT1", kvMap), kvMap)
	}

	Reg("GT2", "@GT(22,111)")
	if Calc("GT2", kvMap) == false {
		t.Log("Calc(\"GT2\", kvMap) == false")
	} else {
		t.Error("Calc(\"GT2\", kvMap) == false", Calc("GT2", kvMap), kvMap)
	}

	Reg("GE1", "@GE(22,111)")
	if Calc("GE1", kvMap) == false {
		t.Log("Calc(\"GE1\", kvMap) == false")
	} else {
		t.Error("Calc(\"GE1\", kvMap) == false", Calc("GE1", kvMap), kvMap)
	}

	Reg("GE2", "@GE(111,22)")
	if Calc("GE2", kvMap) == true {
		t.Log("Calc(\"GE2\", kvMap) == true")
	} else {
		t.Error("Calc(\"GE2\", kvMap) == true", Calc("GE2", kvMap), kvMap)
	}

	Reg("GE3", "@GE(111,111)")
	if Calc("GE3", kvMap) == true {
		t.Log("Calc(\"GE3\", kvMap) == true")
	} else {
		t.Error("Calc(\"GE3\", kvMap) == true", Calc("GE3", kvMap), kvMap)
	}

	Reg("GE4", "@GE(data3,111)")
	if Calc("GE4", kvMap) == false {
		t.Log("Calc(\"GE4\", kvMap) == false")
	} else {
		t.Error("Calc(\"GE4\", kvMap) == false", Calc("GE4", kvMap), kvMap)
	}

	Reg("LT1", "@LT(111,22)")
	if Calc("LT1", kvMap) == false {
		t.Log("Calc(\"LT1\", kvMap) == false")
	} else {
		t.Error("Calc(\"LT1\", kvMap) == false", Calc("LT1", kvMap), kvMap)
	}

	Reg("LT2", "@LT(22,111)")
	if Calc("LT2", kvMap) == true {
		t.Log("Calc(\"LT2\", kvMap) == true")
	} else {
		t.Error("Calc(\"LT2\", kvMap) == true", Calc("LT2", kvMap), kvMap)
	}

	Reg("LE1", "@LE(22,111)")
	if Calc("LE1", kvMap) == true {
		t.Log("Calc(\"LE1\", kvMap) == true")
	} else {
		t.Error("Calc(\"LE1\", kvMap) == true", Calc("LE1", kvMap), kvMap)
	}

	Reg("LE2", "@LE(111,22)")
	if Calc("LE2", kvMap) == false {
		t.Log("Calc(\"LE2\", kvMap) == false")
	} else {
		t.Error("Calc(\"LE2\", kvMap) == false", Calc("LE2", kvMap), kvMap)
	}

	Reg("LE3", "@LE(111,111)")
	if Calc("LE3", kvMap) == true {
		t.Log("Calc(\"LE3\", kvMap) == true")
	} else {
		t.Error("Calc(\"LE3\", kvMap) == true", Calc("LE3", kvMap), kvMap)
	}

	Reg("LE4", "@LE(data3,111)")
	if Calc("LE4", kvMap) == true {
		t.Log("Calc(\"LE4\", kvMap) == true")
	} else {
		t.Error("Calc(\"LE4\", kvMap) == true", Calc("LE4", kvMap), kvMap)
	}

	Reg("EQ1", "@EQ(data3,111)")
	if Calc("EQ1", kvMap) == false {
		t.Log("Calc(\"EQ1\", kvMap) == false")
	} else {
		t.Error("Calc(\"EQ1\", kvMap) == false", Calc("EQ1", kvMap), kvMap)
	}

	Reg("EQ2", "@EQ(110,data3)")
	if Calc("EQ2", kvMap) == true {
		t.Log("Calc(\"EQ2\", kvMap) == true")
	} else {
		t.Error("Calc(\"EQ2\", kvMap) == true", Calc("EQ2", kvMap), kvMap)
	}

	Reg("EQ3", "@EQ(wolffy,data4)")
	if Calc("EQ3", kvMap) == true {
		t.Log("Calc(\"EQ3\", kvMap) == true")
	} else {
		t.Error("Calc(\"EQ3\", kvMap) == true", Calc("EQ3", kvMap), kvMap)
	}

	Reg("EQ4", "@EQ(wolffy,wolffy1)")
	if Calc("EQ4", kvMap) == false {
		t.Log("Calc(\"EQ4\", kvMap) == false")
	} else {
		t.Error("Calc(\"EQ4\", kvMap) == false", Calc("EQ4", kvMap), kvMap)
	}

	Reg("NEQ1", "@NEQ(data3,111)")
	if Calc("NEQ1", kvMap) == true {
		t.Log("Calc(\"NEQ1\", kvMap) == true")
	} else {
		t.Error("Calc(\"NEQ1\", kvMap) == true", Calc("NEQ1", kvMap), kvMap)
	}

	Reg("NEQ2", "@NEQ(110,data3)")
	if Calc("NEQ2", kvMap) == false {
		t.Log("Calc(\"NEQ2\", kvMap) == false")
	} else {
		t.Error("Calc(\"NEQ2\", kvMap) == false", Calc("NEQ2", kvMap), kvMap)
	}

	Reg("NEQ3", "@NEQ(wolffy,data4)")
	if Calc("NEQ3", kvMap) == false {
		t.Log("Calc(\"NEQ3\", kvMap) == false")
	} else {
		t.Error("Calc(\"NEQ3\", kvMap) == false", Calc("NEQ3", kvMap), kvMap)
	}

	Reg("NEQ4", "@NEQ(wolffy,wolffy1)")
	if Calc("NEQ4", kvMap) == true {
		t.Log("Calc(\"NEQ4\", kvMap) == true")
	} else {
		t.Error("Calc(\"NEQ4\", kvMap) == true", Calc("NEQ4", kvMap), kvMap)
	}

	Reg("ALL1", "@OR(@EQ(wolffy,wolffy1),@IN(aaa,data1),@IN(ff,data1),@NIN(aa,data1),@AND(@EQ(wolffyx,data4),@NEQ(www,data4))")
	if Calc("ALL1", kvMap) == false {
		t.Log("Calc(\"ALL1\", kvMap) == false")
	} else {
		t.Error("Calc(\"ALL1\", kvMap) == false", Calc("ALL1", kvMap), kvMap)
	}

	Reg("ALL2", "@OR(@EQ(wolffy,wolffy1),@IN(aaa,data1),@IN(ff,data1),@NIN(aa,data1),@AND(@EQ(wolffy,data4),@NEQ(www,data4))")
	if Calc("ALL2", kvMap) == true {
		t.Log("Calc(\"ALL2\", kvMap) == true")
	} else {
		t.Error("Calc(\"ALL2\", kvMap) == true", Calc("ALL2", kvMap), kvMap)
	}

}

func BenchmarkClac(b *testing.B) {
	kvMap := map[string]string{
		"data1": "aa,bb,cc,dd",
		"data2": "true,true,true",
		"data3": "110",
		"data4": "wolffy",
	}

	Reg("ALL1", "@OR(@EQ(wolffy,wolffy1),@IN(aaa,data1),@IN(ff,data1),@NIN(aa,data1),@AND(@EQ(wolffyx,data4),@NEQ(www,data4))")
	Reg("ALL2", "@OR(@EQ(wolffy,wolffy1),@IN(aaa,data1),@IN(ff,data1),@NIN(aa,data1),@AND(@EQ(wolffy,data4),@NEQ(www,data4))")

	for i := 0; i < b.N; i++ {
		if Calc("ALL1", kvMap) == false {
			b.Log("Calc(\"ALL1\", kvMap) == false")
		} else {
			b.Error("Calc(\"ALL1\", kvMap) == false", Calc("ALL1", kvMap), kvMap)
		}

		if Calc("ALL2", kvMap) == true {
			b.Log("Calc(\"ALL2\", kvMap) == true")
		} else {
			b.Error("Calc(\"ALL2\", kvMap) == true", Calc("ALL2", kvMap), kvMap)
		}
	}
}

func BenchmarkConcurrencyCalc(b *testing.B) {

	Reg("ALL1", "@OR(@EQ(wolffy,wolffy1),@IN(aaa,data1),@IN(ff,data1),@NIN(aa,data1),@AND(@EQ(wolffyx,data4),@NEQ(www,data4))")
	Reg("ALL2", "@OR(@EQ(wolffy,wolffy1),@IN(aaa,data1),@IN(ff,data1),@NIN(aa,data1),@AND(@EQ(wolffy,data4),@NEQ(www,data4))")

	kvMap := map[string]string{
		"data1": "aa,bb,cc,dd",
		"data2": "true,true,true",
		"data3": "110",
		"data4": "wolffy",
	}

	b.RunParallel(func(pb *testing.PB) {
		for pb.Next() {
			if Calc("ALL1", kvMap) == false {
				b.Log("Calc(\"ALL1\", kvMap) == false")
			} else {
				b.Error("Calc(\"ALL1\", kvMap) == false", Calc("ALL1", kvMap), kvMap)
			}

			if Calc("ALL2", kvMap) == true {
				b.Log("Calc(\"ALL2\", kvMap) == true")
			} else {
				b.Error("Calc(\"ALL2\", kvMap) == true", Calc("ALL2", kvMap), kvMap)
			}

		}
	})
}
