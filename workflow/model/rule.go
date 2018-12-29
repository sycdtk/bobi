package model

//连线、节点规则
type Rule struct {
	ID         string
	Name       string //
	Expression string //规则表达式
}

//规则结果确认
func (rule *Rule) Validation() bool {

}

//连线规则结果确认
func LineValidation() bool {

}

//节点上存在多条连线
func NodeValidation() bool {

}
