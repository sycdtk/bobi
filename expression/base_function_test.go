package exprssion

import (
	"testing"
)

func TestBaseOper(t *testing.T) {

	if plu(9992.32352345, 23.23423) == 10015.55775345 {
		t.Log("plu(9992.32352345, 23.23423) == 10015.55775345")
	} else {
		t.Error("error:", "plu(9992.32352345, 23.23423) == 10015.55775345", plu(9992.32352345, 23.23423))
	}

	if sub(9992.32352345, 1111.00234) == 8881.32118345 {
		t.Log("sub(9992.32352345, 1111.00234) == 8881.32118345")
	} else {
		t.Error("error:", "sub(9992.32352345, 1111.00234) == 8881.32118345", sub(9992.32352345, 1111.00234))
	}

	if mul(2.3, 11.903) == 27.3769 {
		t.Log("mul(2.3, 11.903) == 27.3769")
	} else {
		t.Error("error:", "mul(2.3, 11.903) == 27.3769", mul(2.3, 11.903))
	}

	if div(2.3, 11.903) == 0.19322859783247917 {
		t.Log("mul(2.3, 11.903) == 0.19322859783247917")
	} else {
		t.Error("error:", "mul(2.3, 11.903) == 0.19322859783247917", div(2.3, 11.903))
	}

	if div(10, -3) == -3.3333333333333335 {
		t.Log("div(10, -3) == -3.3333333333333335")
	} else {
		t.Error("error:", "div(10, -3) == -3.3333333333333335", div(10, -3))
	}

	if div(10, 0) == -9999 {
		t.Log("div(10, 0) == -9999")
	} else {
		t.Error("error:", "div(10, 0) == -9999", div(10, 0))
	}

	if pow(10, 0) == 1 {
		t.Log("pow(10, 0) == 1")
	} else {
		t.Error("error:", "pow(10, 0) == 1", pow(10, 0))
	}

	if pow(10, 3) == 1000 {
		t.Log("pow(10, 3) == 1000")
	} else {
		t.Error("error:", "pow(10, 3) == 1000", pow(10, 3))
	}

	if pow(10, -2) == 0.01 {
		t.Log("pow(10, -2) == 0.01")
	} else {
		t.Error("error:", "pow(10, -2) == 0.01) == 1000", pow(10, -2))
	}

	if eq(10, -2) == false {
		t.Log("eq(10, -2) == false")
	} else {
		t.Error("error:", "eq(10, -2) == false", eq(10, -2))
	}

	if eq(3.3333, 3.3333) == true {
		t.Log("eq(3.3333, 3.3333) == true")
	} else {
		t.Error("error:", "eq(3.3333, 3.3333) == true", eq(3.3333, 3.3333))
	}

	if ne(10, -2) == true {
		t.Log("ne(10, -2) == true")
	} else {
		t.Error("error:", "ne(10, -2) == true", ne(10, -2))
	}

	if ne(3.3333, 3.3333) == false {
		t.Log("ne(3.3333, 3.3333) == false")
	} else {
		t.Error("error:", "ne(3.3333, 3.3333) == false", ne(3.3333, 3.3333))
	}

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

	if iF(true, 0.23, 1.990) == 0.23 {
		t.Log("iF(true,0.23,1.990)==0.23")
	} else {
		t.Error("error:", "iF(true,0.23,1.990)==0.23", iF(true, 0.23, 1.990))
	}

	if iF(false, 0.23, 1.990) == 1.99 {
		t.Log("iF(false, 0.23, 1.990) == 1.99")
	} else {
		t.Error("error:", "iF(false, 0.23, 1.990) == 1.99", iF(false, 0.23, 1.990))
	}

	if max([]float64{9.7803, -4.20399, 10.23, 89.00234, -990}) == 89.00234 {
		t.Log("max([]float64{9.7803,-4.20399,10.23,89.00234,-990}) == 89.00234")
	} else {
		t.Error("error:", "max([]float64{9.7803,-4.20399,10.23,89.00234,-990}) == 89.00234", max([]float64{9.7803, -4.20399, 10.23, 89.00234, -990}))
	}

	if min([]float64{9.7803, -4.20399, 10.23, 89.00234, -990}) == -990 {
		t.Log("min([]float64{9.7803,-4.20399,10.23,89.00234,-990}) == -990")
	} else {
		t.Error("error:", "min([]float64{9.7803,-4.20399,10.23,89.00234,-990}) == -990", min([]float64{9.7803, -4.20399, 10.23, 89.00234, -990}))
	}

	if max([]float64{}) == -9999 {
		t.Log("max([]float64{}) == -9999")
	} else {
		t.Error("error:", "max([]float64{}) == -9999", max([]float64{}))
	}

	if min([]float64{}) == -9999 {
		t.Log("min([]float64{}) == -9999")
	} else {
		t.Error("error:", "min([]float64{}) == -9999", min([]float64{}))
	}

	if and([]bool{false, true, false}) == false {
		t.Log("and([]bool{false, true, false}) == false")
	} else {
		t.Error("error:", "and([]bool{false, true, false}) == false", and([]bool{false, true, false}))
	}

	if and([]bool{true, true, true}) == true {
		t.Log("and([]bool{true, true, true}) == true")
	} else {
		t.Error("error:", "and([]bool{true, true, true}) == true", and([]bool{true, true, true}))
	}

	if or([]bool{false, true, false}) == true {
		t.Log("or([]bool{false, true, false}) == true")
	} else {
		t.Error("error:", "or([]bool{false, true, false}) == true", or([]bool{false, true, false}))
	}

	if or([]bool{true, false, true}) == true {
		t.Log("or([]bool{true, true, true}) == true")
	} else {
		t.Error("error:", "or([]bool{true, true, true}) == true", or([]bool{true, true, true}))
	}

	if not(true) == false {
		t.Log("not(true) == false")
	} else {
		t.Error("error:", "not(true) == false", not(true))
	}

	if badval(-9998) == false {
		t.Log(" badval(-9998) == false")
	} else {
		t.Error("error:", "badval(-9998) == false", badval(-9998))
	}

	if badval(-10000) == true {
		t.Log(" badval(-10000) == true")
	} else {
		t.Error("error:", "badval(-10000) == true", badval(-10000))
	}

	if log10(100) == 2 {
		t.Log("log10(100) == 2")
	} else {
		t.Error("error:", "log10(100) == 2", log10(100))
	}

	if log10(1) == 0 {
		t.Log("log10(1) == 0")
	} else {
		t.Error("error:", "log10(1) == 0", log10(1))
	}

	if log10(-1) == -9999 {
		t.Log("log10(-1) == -9999")
	} else {
		t.Error("error:", "log10(-1) == -9999", log10(-1))
	}

	if log10(0) == -9999 {
		t.Log("log10(0) == -9999")
	} else {
		t.Error("error:", "log10(0) == -9999", log10(0))
	}

	if log(1) == 0 {
		t.Log("log(1) == 0")
	} else {
		t.Error("error:", "log(1) == 0", log(1))
	}

	if log(0) == -9999 {
		t.Log("log(0) == -9999")
	} else {
		t.Error("error:", "log(0) == -9999", log(0))
	}

	if log(2) == 0.6931471805599453 {
		t.Log("log(2) == 0.6931471805599453")
	} else {
		t.Error("error:", "log(2) == 0.6931471805599453", log(2))
	}

	if exp(1) == 2.7182818284590455 {
		t.Log("exp(1) == 2.7182818284590455")
	} else {
		t.Error("error:", "exp(1) == 2.7182818284590455", exp(1))
	}

	if sqrt(1) == 1 {
		t.Log("sqrt(1) == 1")
	} else {
		t.Error("error:", "sqrt(1) == 1", sqrt(1))
	}

	if sqrt(0) == 0 {
		t.Log("sqrt(0) == 0")
	} else {
		t.Error("error:", "sqrt(0) == 0", sqrt(0))
	}

	if sqrt(9) == 3 {
		t.Log("sqrt(9) == 3")
	} else {
		t.Error("error:", "sqrt(9) == 3", sqrt(9))
	}

	if sqrt(100) == 10 {
		t.Log("sqrt(100) == 10")
	} else {
		t.Error("error:", "sqrt(100) == 10", sqrt(100))
	}

	if abs(-100) == 100 {
		t.Log("abs(-100) == 100")
	} else {
		t.Error("error:", "abs(-100) == 100", abs(-100))
	}

	if abs(0) == 0 {
		t.Log("abs(0) == 0")
	} else {
		t.Error("error:", "abs(0) == 0", abs(0))
	}

	if abs(100) == 100 {
		t.Log("abs(100) == 100")
	} else {
		t.Error("error:", "abs(100) == 100", abs(100))
	}

	if selectN(3, []float64{1, 2, 7, 5, 3}) == 7 {
		t.Log("selectN(3,[]float64{1, 2, 7, 5, 3}) == 7")
	} else {
		t.Error("error:", "selectN(3,[]float64{1, 2, 7, 5, 3}) == 7", selectN(3, []float64{1, 2, 7, 5, 3}))
	}

	if selectN(9, []float64{1, 2, 7, 5, 3}) == -9999 {
		t.Log("selectN(3,[]float64{1, 2, 7, 5, 3}) == -9999")
	} else {
		t.Error("error:", "selectN(3,[]float64{1, 2, 7, 5, 3}) == -9999", selectN(9, []float64{1, 2, 7, 5, 3}))
	}

	if selectN(-1, []float64{1, 2, 7, 5, 3}) == -9999 {
		t.Log("selectN(-1,[]float64{1, 2, 7, 5, 3}) == -9999")
	} else {
		t.Error("error:", "selectN(-1,[]float64{1, 2, 7, 5, 3}) == -9999", selectN(-1, []float64{1, 2, 7, 5, 3}))
	}

}
