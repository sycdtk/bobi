package model

//节点间关系对象
type Relation struct {
	NodeID string //被关联节点ID
	Rule   *Rule  //流转规则，默认没有时直接执行
}
