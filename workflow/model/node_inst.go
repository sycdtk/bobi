package model

//节点实例
type NodeInst struct {
	ID        string
	NodeID    string //流程定义节点ID
	Name      string
	TaskInsts []*TaskInst //节点任务实例

	ExecSeq string //节点实例执行顺序
}

func (ni *NodeInst) Exec(exec func()) {
	exec()
}
