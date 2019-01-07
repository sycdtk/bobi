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

	//	if Calc("CAC", v1Map) == "10" {
	//		t.Log("Calc(\"CAC\",v1Map) == 10")
	//	} else {
	//		t.Error("error:", "Calc(\"CAC\",v1Map) == 10", Calc("CAC", v1Map))
	//	}

	//	var v2Map = map[string]string{
	//		"DEPACT": "1.45",
	//		"DEPSTA": "1",
	//		"IPNDEP": "10",
	//	}
	//	if Calc("CAC", v2Map) == "0" {
	//		t.Log("Calc(\"CAC\",v2Map) == 0")
	//	} else {
	//		t.Error("error:", "Calc(\"CAC\",v2Map) == 0", Calc("CAC", v2Map))
	//	}

	//	var nilMap = map[string]string{}

	//	Reg("EXP1", "@SUB(@PLU(1,@PLU(2,3)),@PLU(1,2))")
	//	if Calc("EXP1", nilMap) == "3" {
	//		t.Log("Calc(\"EXP1\",nilMap) == 3")
	//	} else {
	//		t.Error("error:", "Calc(\"EXP1\",nilMap) == 3", Calc("EXP1", nilMap))
	//	}

	//	Reg("EXP2x", "@PLU(2.2,-3)")
	//	t.Log(Calc("EXP2x", nilMap))

	//	Reg("EXP2", "@POW(@DIV(@SUB(10,3),2),@PLU(2.2,-3))")
	//	if Calc("EXP2", nilMap) == "0.3670671877495542" {
	//		t.Log("Calc(\"EXP2\",nilMap) == 0.3670671877495542")
	//	} else {
	//		t.Error("error:", "Calc(\"EXP2\",nilMap) == 0.3670671877495542", Calc("EXP2", nilMap))
	//	}

	//	Reg("EXP3", "@EQ(@DIV(@SUB(10,3),2),@PLU(2.2,-3))")
	//	if Calc("EXP3", nilMap) == "false" {
	//		t.Log("Calc(\"EXP3\",nilMap) == false")
	//	} else {
	//		t.Error("error:", "Calc(\"EXP3\",nilMap) == false", Calc("EXP3", nilMap))
	//	}

	//	Reg("EXP4", "@IF(@EQ(1,1),@MUL(@PLU(1,2),3),6)")
	//	if Calc("EXP4", nilMap) == "9" {
	//		t.Log("Calc(\"EXP4\",nilMap) == 9")
	//	} else {
	//		t.Error("error:", "Calc(\"EXP4\",nilMap) == 9", Calc("EXP4", nilMap))
	//	}

	//	Reg("EXP5", "@IF(@GT(1,3),@MUL(@PLU(1,2),3),@DIV(3,2))")
	//	if Calc("EXP5", nilMap) == "1.5" {
	//		t.Log("Calc(\"EXP5\",nilMap) == 1.5")
	//	} else {
	//		t.Error("error:", "Calc(\"EXP5\",nilMap) == 1.5", Calc("EXP5", nilMap))
	//	}

	//	Reg("EXP6", "@IF(@GT(1,3),@MUL(@PLU(1,2),3),@DIV(3,0))")
	//	if Calc("EXP6", nilMap) == "-9999" {
	//		t.Log("Calc(\"EXP6\",nilMap) == -9999")
	//	} else {
	//		t.Error("error:", "Calc(\"EXP6\",nilMap) == -9999", Calc("EXP6", nilMap))
	//	}

	//	Reg("EXP7x", "@OR(true,false,@AND(false,true,false))")
	//	t.Log(Calc("EXP7x", nilMap))

	//	Reg("EXP7", "@IF(@NOT(@OR(true,false,@AND(false,true,false)))1,2)")
	//	if Calc("EXP7", nilMap) == "2" {
	//		t.Log("Calc(\"EXP7\",nilMap) == 2")
	//	} else {
	//		t.Error("error:", "Calc(\"EXP7\",nilMap) == 2", Calc("EXP7", nilMap))
	//	}

	//	Reg("EXP8", "@MAX(2.33,-9,1.3456,@PLU(0.73,1.76))")
	//	if Calc("EXP8", nilMap) == "2.49" {
	//		t.Log("Calc(\"EXP8\",nilMap) == 2.49")
	//	} else {
	//		t.Error("error:", "Calc(\"EXP8\",nilMap) == 2.49", Calc("EXP8", nilMap))
	//	}

	//	Reg("EXP9", "@BADVAL(@SUB(1,10000))")
	//	if Calc("EXP9", nilMap) == "true" {
	//		t.Log("Calc(\"EXP9\",nilMap) == true")
	//	} else {
	//		t.Error("error:", "Calc(\"EXP9\",nilMap) == 2.49", Calc("EXP9", nilMap))
	//	}

	//	Reg("EXP10", "@LN(@SUB(1,0))")
	//	if Calc("EXP10", nilMap) == "0" {
	//		t.Log("Calc(\"EXP10\",nilMap) == 0")
	//	} else {
	//		t.Error("error:", "Calc(\"EXP10\",nilMap) == 0", Calc("EXP10", nilMap))
	//	}

	//	Reg("EXP11", "@LG(100)")
	//	if Calc("EXP11", nilMap) == "2" {
	//		t.Log("Calc(\"EXP11\",nilMap) == 2")
	//	} else {
	//		t.Error("error:", "Calc(\"EXP11\",nilMap) == 2", Calc("EXP11", nilMap))
	//	}

	//	Reg("EXP12", "@ABS(@SUB(9,13))")
	//	if Calc("EXP12", nilMap) == "4" {
	//		t.Log("Calc(\"EXP12\",nilMap) == 4")
	//	} else {
	//		t.Error("error:", "Calc(\"EXP12\",nilMap) == 4", Calc("EXP12", nilMap))
	//	}

	//	Reg("EXP13", "@SELECT(@SUB(12,9),@PLU(1,2),@DIV(1,3),@MUL(2.5,3))")
	//	if Calc("EXP13", nilMap) == "7.5" {
	//		t.Log("Calc(\"EXP13\",nilMap) == 7.5")
	//	} else {
	//		t.Error("error:", "Calc(\"EXP13\",nilMap) == 7.5", Calc("EXP13", nilMap))
	//	}

}

//func BenchmarkClac(b *testing.B) {
//	var nilMap = map[string]string{}
//	for i := 0; i < b.N; i++ {
//		Reg("EXP2", "@POW(@DIV(@SUB(10,3),2),@PLU(2.2,-3))")
//		if Calc("EXP2", nilMap) == "0.3670671877495542" {
//			//b.Log("Calc(\"EXP2\") == 0.3670671877495542")
//		} else {
//			//b.Error("error:", "Calc(\"EXP2\") == 0.3670671877495542", Calc("EXP2"))
//		}
//	}
//}

//func BenchmarkConcurrencyCalc(b *testing.B) {
//	Reg("EXP2", "@POW(@DIV(@SUB(10,3),2),@PLU(2.2,-3))")
//	var nilMap = map[string]string{}
//	b.RunParallel(func(pb *testing.PB) {
//		for pb.Next() {
//			if Calc("EXP2", nilMap) == "0.3670671877495542" {
//				//b.Log("Calc(\"EXP2\") == 0.3670671877495542")
//			} else {
//				//b.Error("error:", "Calc(\"EXP2\") == 0.3670671877495542", Calc("EXP2"))
//			}

//		}
//	})
//}
