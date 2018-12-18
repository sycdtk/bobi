package model

//节点间关系对象
type Relation struct {
	ID   string   //节点ID
	From []string //来向节点ID
	To   []string //去向节点ID
}
