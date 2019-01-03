package model

import (
	"github.com/sycdtk/bobi/random"
)

const (
	BeginNode      = 1001 //节点类型：开始节点
	EndNode        = 1002 //节点类型：结束节点
	UserNode       = 1003 //节点类型：用户任务节点（默认类型）
	AutoNode       = 1004 //节点类型：自动任务节点
	SubProcessNode = 1005 //节点类型：子流程节点
	Exclusive      = 2001 //节点出入路径规则类型：排他（默认类型）
	Parallel       = 2002 //节点出入路径规则类型：并行
	Inclusive      = 2003 //节点出入路径规则类型：包含

)

//流程节点
type Node struct {
	ID   string
	Name string

	Type    int //节点类型
	InType  int //入节点规则类型
	OutType int //出节点规则类型

	From []*Relation //节点入向关系
	To   []*Relation //节点出向关系

	Tasks []string //节点任务 Task Id 集合
}

func NewNode(Name string, Type, InType, OutType int) *Node {
	return &Node{
		ID:      random.UniqueID(),
		Name:    Name,
		Type:    Type,
		InType:  InType,
		OutType: OutType,
		From:    []*Relation{},
		To:      []*Relation{},
	}
}

func (node *Node) NewNodeInst() *NodeInst {

	in, out := Exclusive, Exclusive

	if node.InType != 0 {
		in = node.InType
	}

	if node.OutType != 0 {
		out = node.OutType
	}

	return &NodeInst{
		ID:      random.UniqueID(),
		NodeID:  node.ID,
		Name:    node.Name,
		Type:    node.Type,
		InType:  in,
		OutType: out,
	}
}
