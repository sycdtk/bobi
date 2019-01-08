package exprssion

import (
	"strconv"
	"strings"
	"sync"

	//"github.com/sycdtk/bobi/logger"
	"github.com/sycdtk/bobi/rpn"
	"github.com/sycdtk/bobi/set"
	"github.com/sycdtk/bobi/stack"
)

const (
	separator        = "|"
	whiteSpace       = " "
	dataSetSeparator = ","
)

var formulaMu = new(sync.Mutex)      //表达式集合写锁
var formulaMap = map[string]string{} //逆波兰表达式集合

//注册表达式
func Reg(name, formula string) {
	formulaMu.Lock()
	defer formulaMu.Unlock()

	//TODO 1、表达式正则检查
	formulaMap[name] = rpn.Parse(formula)

	//logger.Debug("表达式解析：", name, formula, rpn.Parse(formula))
}

//基于逆波兰结构表达式进行实际数据计算，返回结果字符串
//kvMap中集合类型，用英文逗号分隔，例如：a,b,c,d
func Calc(rpnExpName string, kvMap map[string]string) bool {

	if exp, ok := formulaMap[rpnExpName]; ok {

		calcExp := exp

		//实际数据带入模板
		for k, v := range kvMap {
			calcExp = strings.Replace(calcExp, k, v, -1)
		}

		//logger.Debug("带入模板后表达式：", calcExp)

		s := stack.NewStack()

		for _, op := range strings.Split(calcExp, whiteSpace) {
			switch op {
			case "@AND":
				stack.Push(s, AND(s))
			case "@OR":
				stack.Push(s, OR(s))
			case "@NOT":
				stack.Push(s, NOT(s))
			case "@IN":
				stack.Push(s, IN(s))
			case "@NIN":
				stack.Push(s, NIN(s))
			case "@GT":
				stack.Push(s, GT(s))
			case "@GE":
				stack.Push(s, GE(s))
			case "@LT":
				stack.Push(s, LT(s))
			case "@LE":
				stack.Push(s, LE(s))
			case "@EQ":
				stack.Push(s, EQ(s))
			case "@NEQ":
				stack.Push(s, NEQ(s))
			default:
				stack.Push(s, op)
			}
		}

		return StrToBool(stack.Pop(s))
	}
	return false
}

//逻辑运算AND
func AND(s *stack.Stack) string {

	args := reverseArgs(s)

	boolArgs := []bool{}

	for !stack.Empty(args) {
		boolArgs = append(boolArgs, StrToBool(stack.Pop(args)))
	}

	value := and(boolArgs)

	return BoolToStr(value)
}

//逻辑运算OR
func OR(s *stack.Stack) string {

	args := reverseArgs(s)

	boolArgs := []bool{}

	for !stack.Empty(args) {
		boolArgs = append(boolArgs, StrToBool(stack.Pop(args)))
	}

	value := or(boolArgs)

	return BoolToStr(value)
}

//逻辑运算NOT
func NOT(s *stack.Stack) string {

	args := reverseArgs(s)

	value := not(StrToBool(stack.Pop(args)))

	return BoolToStr(value)
}

//是否包含
func IN(s *stack.Stack) string {

	args := reverseArgs(s)

	value := in(stack.Pop(args), StrToSet(stack.Pop(args)))

	return BoolToStr(value)
}

//是否未包含
func NIN(s *stack.Stack) string {

	args := reverseArgs(s)

	value := nin(stack.Pop(args), StrToSet(stack.Pop(args)))

	return BoolToStr(value)
}

//是否相等
func EQ(s *stack.Stack) string {

	args := reverseArgs(s)

	value := eq(stack.Pop(args), stack.Pop(args))

	return BoolToStr(value)
}

//是否不相等
func NEQ(s *stack.Stack) string {

	args := reverseArgs(s)

	value := neq(stack.Pop(args), stack.Pop(args))

	return BoolToStr(value)
}

//大于
func GT(s *stack.Stack) string {

	args := reverseArgs(s)

	value := gt(StrToFloat64(stack.Pop(args)), StrToFloat64(stack.Pop(args)))

	return BoolToStr(value)
}

//大于等于
func GE(s *stack.Stack) string {

	args := reverseArgs(s)

	value := ge(StrToFloat64(stack.Pop(args)), StrToFloat64(stack.Pop(args)))

	return BoolToStr(value)
}

//小于
func LT(s *stack.Stack) string {

	args := reverseArgs(s)

	value := lt(StrToFloat64(stack.Pop(args)), StrToFloat64(stack.Pop(args)))

	return BoolToStr(value)
}

//小于等于
func LE(s *stack.Stack) string {

	args := reverseArgs(s)

	value := le(StrToFloat64(stack.Pop(args)), StrToFloat64(stack.Pop(args)))

	return BoolToStr(value)
}

//字符串转换为float64
func StrToFloat64(v string) float64 {
	vstf, _ := strconv.ParseFloat(v, 64)
	return vstf
}

//float64转换为字符串
func Float64ToStr(v float64) string {
	return strconv.FormatFloat(v, 'f', -1, 64)
}

//字符串转为bool，"true"返回true，其他返回false
func StrToBool(v string) bool {
	if v == "true" {
		return true
	}
	return false
}

//bool转为字符串
func BoolToStr(v bool) string {
	if v {
		return "true"
	}
	return "false"
}

//字符串转换为float64
func StrToInt64(v string) int64 {
	vsti, _ := strconv.ParseInt(v, 10, 64)
	return vsti
}

//float64转换为字符串
func Int64ToStr(v int64) string {
	return strconv.FormatInt(v, 10)
}

//字符串转为Set集合，字符串以英文,分隔
func StrToSet(v string) *set.Set {
	dataSet := set.NewSet()
	for _, s := range strings.Split(v, dataSetSeparator) {
		dataSet.Add(s)
	}
	return dataSet
}

//Set集合转为字符串，以英文,分隔
func SetToStr(v *set.Set) string {
	return v.ToString()
}

//参数反转
func reverseArgs(s *stack.Stack) *stack.Stack {
	args := stack.NewStack()

	for stack.Top(s) != separator {
		stack.Push(args, stack.Pop(s))
	}
	stack.Pop(s)

	return args
}
