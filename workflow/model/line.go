package model

import (
	"github.com/sycdtk/bobi/random"
)

//节点间连线
type Line struct {
	ID   string
	Name string
	From string //来向节点Id
	To   string //去向节点Id
	Rule string //规则
}

func NewLine(Name, From, To, Rule string) *Line {
	return &Line{
		ID:   random.UniqueID(),
		Name: Name,
		From: From,
		To:   To,
		Rule: Rule,
	}
}
