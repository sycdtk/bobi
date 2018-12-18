package model

//流程实例
type ProcessInst struct {
	ID         string
	ProcessID  string //流程定义ID
	Name       string
	ProcessDef *ProcessDef //流程定义
	Token      *Token
}
