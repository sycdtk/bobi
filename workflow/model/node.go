package model

import (
	"github.com/sycdtk/bobi/random"
)

//节点类型：
const (
	Begin    = 1000 //开始节点
	End      = 2000 //结束节点
	UserNode = 3000 //用户任务节点（默认类型）
	AutoNode = 3001 //自动任务节点
)

//流程节点
type Node struct {
	ID   string
	Name string
	Type int //节点类型

	Tasks []string //节点任务 Task Id 集合
}

func (node *Node) NewInst() *NodeInst {
	return &NodeInst{
		ID:     random.UniqueID(),
		NodeID: node.ID,
		Name:   node.Name,
	}
}
