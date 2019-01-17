package model

import (
	"github.com/sycdtk/bobi/random"
	"github.com/sycdtk/bobi/set"
)

//流程实例
type ProcessInst struct {
	ID         string
	ProcessID  string //流程定义ID
	Name       string
	Status     *set.Set          //流程状态
	ProcessDef *ProcessDef       //流程定义
	Token      *Token            //执行中节点集
	Data       map[string]string // rule检查时的判断数据集合
}

func NewProcessInst(pd *ProcessDef, token *Token) *ProcessInst {
	return &ProcessInst{
		ID:        random.UniqueID(),
		Name:      pd.Name,
		Status:    set.NewSet(),
		ProcessID: pd.ID,
		Token:     token,
		Data:      map[string]string{},
	}
}
