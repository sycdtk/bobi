package model

type TaskInst struct {
	ID       string
	Name     string
	NodeInst *NodeInst //任务对应节点实例
}
