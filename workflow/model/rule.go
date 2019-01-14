package model

//系统自带的逻辑表达式规则
const (
	rule_true  = "@OR(true)"
	rule_false = "@AND(false)"
)

//连线、节点规则
type Rule struct {
	ID         string
	Name       string //
	Expression string //规则表达式
}
