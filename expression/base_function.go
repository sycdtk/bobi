package exprssion

import (
	"math"
)

/*
Built-in functions for the Formula Editor are described below. Some functions require a vector as input, although most will accept either vectors or scalars. Arguments labeled value can be either a vector or a scalar. Arguments labeled vector must be a vector. If there are multiple vectors among the parameters, the length of those vectors must be identical.
Square brackets, [ ], indicate the item is optional. Ellipses, . . ., indicate the item can be repeated.

All formula calculations are performed in double precision floating point. This is also true of the Boolean logic functions (@IF, @AND, @OR, etc.). They use the convention that zero means false and nonzero means true. The test for zero uses an equality check between two floating point numbers, so you should be careful about doing arithmetic that might introduce rounding if the result is to be used as a Boolean value.
If you want to enter a "bad value" in your formula, use a number less than -9998. Formula calculations can calculate a final value below –9999, as the legal range for values in the formula calculator is currently -1e20 to +1e20. (Note: For compatibility with other tools in the Aspen DMCplus package, the final result of the calculation will still be considered "bad" if it is less than -9998.) For details, see Testing formulas.
*/

const (
	badvalue = -9999 //坏值
)

//Natural logarithm of a positive real value
//@Log(value)
//返回传入数据的自然数对数的正实数
func log(v float64) float64 {
	value := math.Log(v)
	if math.IsInf(value, 0) || math.IsNaN(value) {
		return badvalue
	}
	return value
}

//Common logarithm of a positive real value
//@Log10(value)
//返回传入参数的以10为底的对数值
func log10(v float64) float64 {
	value := math.Log10(v)
	if math.IsInf(value, 0) || math.IsNaN(value) {
		return badvalue
	}
	return value
}

//Natural antilogarithm of a value
//@Exp(value)
//返回传入数据的以e为底的指数
func exp(v float64) float64 {
	value := math.Exp(v)
	if math.IsInf(value, 0) || math.IsNaN(value) {
		return badvalue
	}
	return value
}

//Square root of a positive real value
//@Sqrt(value)
//返回传入数据的平方根
func sqrt(v float64) float64 {
	value := math.Sqrt(v)
	if math.IsInf(value, 0) || math.IsNaN(value) {
		return badvalue
	}
	return value
}

//Absolute value of a value
//@Abs(value)
//返回传入数据的绝对值
func abs(v float64) float64 {
	value := math.Abs(v)
	if math.IsInf(value, 0) || math.IsNaN(value) {
		return badvalue
	}
	return value
}

//"true" if value1 is equal to value2
//若v1与v2相等，返回true，否则返回false
func eq(v1, v2 float64) bool {
	return v1 == v2
}

//"true" if value1 is not equal to value2
//若v1与v2不相等，返回true，否则返回false
func ne(v1, v2 float64) bool {
	return v1 != v2
}

//"true" if value1 is greater than value2
//若v1大于v2，返回true，否则返回false
func gt(v1, v2 float64) bool {
	return v1 > v2
}

//"true" if value1 is greater than or equal to value2
//若v1大于或等于v2，返回true，否则返回false
func ge(v1, v2 float64) bool {
	return v1 >= v2
}

//"true" if value1 is less than value2
//若v1小于v2，返回true，否则返回false
func lt(v1, v2 float64) bool {
	return v1 < v2
}

//"true" if value1 is less than or equal to value2
//若v1小于或等于v2，返回true，否则返回false
func le(v1, v2 float64) bool {
	return v1 <= v2
}

//"true" if any value in a list of values is true
//如果传输的参数中有任何一个参数值为true，则返回true，否则返回false
//若传入空集合，返回false
func or(vs []bool) bool {
	if len(vs) > 0 {
		for _, v := range vs {
			if v {
				return true
			}
		}
		return false
	}
	return false
}

//"true" if all values in a list of values are true
//传入的所有参数全部为true时，返回true，否则返回false
//若传入空集合，返回false
func and(vs []bool) bool {
	if len(vs) > 0 {
		r := true
		for _, v := range vs {
			r = r && v
		}
		return r
	}
	return false
}

//"true" if value is "false"
//取反，若传入值为true则返回false，传入值为false则返回true
func not(v bool) bool {
	return !v
}

//"true" if value is a BAD value
//If you want to enter a "bad value" in your formula, use a number less than -9998.
//若值小于-9998，则该值为“无效值”，返回true，否则返回false
func badval(v float64) bool {
	return v < -9998
}

//小数点后位数截取，采取四舍五入规则
func round(f float64, n int) float64 {
	pow10_n := math.Pow10(n)
	return math.Trunc((f+0.5/pow10_n)*pow10_n) / pow10_n
}

//TODO 待理解补充
//Piece-wise linear interpolation: (x,y) pairs define function
//@Interp(value,x1, y1, x2, y2[, x3, y3…] )
//传入参数是成对的(x,y)
func interp() {
}

//TODO
//Interpolate over bad values in a vector
//@InterpBad(vector)
func interpbad() {

}

//TODO
//Central average of a vector. nsamples must be odd
//@Cav(vector, nsamples)
func cav() {

}

//TODO
//Exponential filter: out(1) = in(1),
//@EFilt(vector, factor)
//out(i) = out(i-1) * factor + in(i) * (1 - factor)
func efilt() {

}

//TODO
//Difference a vector: out(1) = 0,
//@Diff(vector)
//out(i) = in(i) - in(i-1)
func diff() {

}

//TODO
//Integrate a vector: out(1) = in(1) + bias,
//@Integ(vector, bias)
//out(i) = in(i) + out(i-1)
func integ() {

}

//TODO
//Shift a vector. Negative nsamples means shift earlier.
//@Shift(vector, nsamples)
func shift() {

}

//TODO
//Mark samples outside range vallow and valhigh as BAD
//@VaLim(vector, vallow, valhigh )
func valim() {

}

//TODO
//Mark sample ranges as BAD. nfirst and nlast are sample numbers
//@MkBad(vector, nfirst1, nlast1[, nfirst2, nlast2…])
func mkbad() {

}

//TODO
//Set values less than valclip to valnew
//@LoClp(vector, valclip, valnew)
func loclp() {

}

//TODO
//Set values greater than valclip to valnew
//@HiClp(vector, valclip, valnew)
func hiclp() {

}

//Maximum of a set of values
//@Max(value1[, value2, value3 …])
//返回集合中的最大值,若传入空集合，返回badvalue
func max(vs []float64) float64 {

	if len(vs) > 0 {
		r := vs[0]
		for _, v := range vs {
			if r < v {
				r = v
			}
		}
		return r
	}
	return badvalue
}

//Minimum of a set of values
//@Min(value1[, value2, value3 …])
//返回集合中的最小值，,若传入空集合，返回badvalue
func min(vs []float64) float64 {
	if len(vs) > 0 {
		r := vs[0]
		for _, v := range vs {
			if r > v {
				r = v
			}
		}
		return r
	}
	return badvalue
}

//value1 if test is "true" or value2 if test is "false"
//@IF(test, value1, value2)
//判断输入值test为true，返回value1，否则返回value2
func iF(t bool, v1, v2 float64) float64 {
	if t {
		return v1
	} else {
		return v2
	}
}

//TODO
//Pressure Compensated Temperature
//@PCT2(Temp<degF>, Pres<psig>, Rpres<psig>, B, C, Bias<degF>, LogInd<1=ln, 2=log10>)
func pct2() {

}

//TODO
//Petroleum Fraction Pressure Compensated Temperature
//@PFPCT(Temp<degF>, Pres<psig>, StdP<psig>, WatK)
func pepct() {

}

//TODO
//Refinery Pressure Compensated Temperature
//@REFPCT(Temp<degF>, Pres<psig>, StdP<psig>, Bias<degF>)
func refpct() {

}

//Returns Vn based on value of N
//@SELECT(N, V1, V2, V3, …)x
//返回集合中的第N个值
func selectN(n int64, vs []float64) float64 {
	if n > 0 && n <= int64(len(vs)) {
		return vs[n-1]
	}
	return badvalue
}

//TODO
//Returns range indicator In if X <= Ln
//@MAPRANGE(X, L1, I1, L2, I2, …, Ln, In, In+1)
func maprange() {

}

//TODO
//Cubic spline (based on change greater than `threshold')
//@Spline(vector, threshold)
func spline() {

}

//返回两个值相加
func plu(v1, v2 float64) float64 {
	return v1 + v2
}

//返回两个值相减
func sub(v1, v2 float64) float64 {
	return v1 - v2
}

//返回两个值相乘
func mul(v1, v2 float64) float64 {
	return v1 * v2
}

//返回两个值相除,除数为0时，返回badvalue：-9999
func div(v1, v2 float64) float64 {
	if v2 != 0 {
		return v1 / v2
	}
	return badvalue
}

//返回指数运算结果
func pow(b, e float64) float64 {
	return math.Pow(b, e)
}
