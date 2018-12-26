package model

//节点间关系对象
type Relation struct {
	NodeID string //被关联节点ID
	Rule   string //流转规则，默认没有时直接执行
}

func (rel *Relation) CheckRule() bool {
	if rel.Rule == "aa" {
		return true
	}
	return false
}
