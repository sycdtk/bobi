package model

//节点间连线
type Line struct {
	ID   string
	Name string
	From string //来向节点Id
	To   string //去向节点Id
	Rule string //流转规则
}
