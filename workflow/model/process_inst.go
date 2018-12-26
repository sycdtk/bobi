package model

import (
	"github.com/sycdtk/bobi/random"
)

//流程实例
type ProcessInst struct {
	ID         string
	ProcessID  string //流程定义ID
	Name       string
	ProcessDef *ProcessDef //流程定义
	Token      *Token
}

func NewProcessInst(pd *ProcessDef, token *Token) *ProcessInst {
	return &ProcessInst{
		ID:        random.UniqueID(),
		Name:      pd.Name,
		ProcessID: pd.ID,
		Token:     token,
	}
}
