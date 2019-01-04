package exprssion

import (
	"github.com/sycdtk/bobi/set"
)

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

//"true" if value is "false"
//取反，若传入值为true则返回false，传入值为false则返回true
func not(v bool) bool {
	return !v
}

//是否包含：字符串是否包含在set中
//若包含，则返回true，若不包含返回false
func in(str string, dataSet *set.Set) bool {
	return dataSet.Contains(str)
}

//是否 未 包含：字符串是否 未 包含在set中
//若包含，则返回false，若不包含返回true
func nin(str string, dataSet *set.Set) bool {
	return !dataSet.Contains(str)
}

//"true" if value1 is equal to value2
//若v1与v2相等，返回true，否则返回false
func eq(v1, v2 string) bool {
	return v1 == v2
}

//"true" if value1 is not equal to value2
//若v1与v2不相等，返回true，否则返回false
func neq(v1, v2 string) bool {
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
