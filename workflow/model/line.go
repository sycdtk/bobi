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
	Rule *Rule  //规则
}

func NewLine(Name, From, To, RuleStr string) *Line {
	return &Line{
		ID:   random.UniqueID(),
		Name: Name,
		From: From,
		To:   To,
		Rule: &Rule{
			ID:         random.UniqueID(),
			Name:       Name + "Rule",
			Expression: RuleStr,
		},
	}
}

func MakeList(datas []interface{}) []*Line {
	finalDatas := []*Line{}
	for _, data := range datas {
		if dataObj, ok := data.(*Line); ok {
			finalDatas = append(finalDatas, dataObj)
		}
	}
	return finalDatas
}
