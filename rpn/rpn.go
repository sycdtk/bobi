package rpn

import (
	"strings"

	"github.com/sycdtk/bobi/stack"
)

const (
	space            = " " //空格占位
	separator        = "|" //分隔符，区分函数逆波兰序列中不定参数函数的参数个数
	comma            = "," //逗号分隔符，分隔函数参数
	leftParenthesis  = "(" //左小括号，需要区分函数括号还是运算符括号
	rightParenthesis = ")" //右小括号，需要区分函数括号还是运算符括号
)

//逻辑关系：@AND、@OR、@NOT
//字符串：等于@EQ、不等于@NE、包含@IN("a",dataset)、不包含@NIN("a",dataset)
//数值：大于@GT、大于等于@GE、小于@LT、小于等于@LE、等于@EQ、不等于@NE
var functionNames = []string{
	"@AND", "@OR", "@NOT",
	"@IN", "@NIN",
	"@GT", "@GE", "@LT", "@LE", "@EQ", "@NE"} //函数名称

//表达式解析函数
//输入表达式串，输出逆波兰表达式结果
func Parse(exp string) (rpn string) {

	s := stack.NewStack()

	exp = prepare(exp) //表达式预处理，关键字拆解

	for _, foo := range strings.Fields(exp) { //处理函数部分

		if isFN(foo) { //是函数名则压栈
			stack.Push(s, foo)
			if len(rpn) > 0 {
				rpn += space
			}
			rpn += separator //增加函数参数长度截止标记
		} else {
			switch foo {
			case leftParenthesis: //"(" 左括号则压栈
				stack.Push(s, foo)
			case rightParenthesis: //")" 右括号则出栈，按函数方式处理
				for {
					fn := stack.Pop(s)
					if fn == leftParenthesis { //函数表达式时，忽略左括号，跳出循环
						rpn += space + stack.Pop(s)
						break
					} else {
						rpn += space + fn
					}
				}

			case comma: //","分隔符不做处理
			default: //追加参数
				if len(rpn) > 0 {
					rpn += space
				}
				rpn += foo
			}
		}
	}

	//	if empty(s){//为空栈则正常
	//	}

	return
}

//表达式预处理，对函数名称或符号名称关键字进行拆分
//例如输入：@EQ(SREIND,1)
//输出为：@EQ ( SREIND,1 )
func prepare(exp string) string {

	//处理函数名称
	for _, fn := range functionNames {
		exp = strings.Replace(exp, fn, space+fn+space, -1)
	}

	//处理左右括号及逗号
	for _, s := range []string{comma, leftParenthesis, rightParenthesis} {
		exp = strings.Replace(exp, s, space+s+space, -1)
	}

	return exp
}

//判断输入表达式是否为函数名称，是返回true，否则返回false
func isFN(fn string) bool {
	for _, functionName := range functionNames {
		if fn == functionName {
			return true
		}
	}
	return false
}

//判断输入表达式是否为左小括号，是返回true，否则返回false
func isLP(lp string) bool {
	return lp == leftParenthesis
}
