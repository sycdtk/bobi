package model

import (
	"github.com/sycdtk/bobi/random"
)

//流程实例令牌，存放执行中的节点和节点实例
type Token struct {
	id        string
	nodeInsts map[string]*NodeInst //key nodeInstID 当前运行节点实例集合
	nodes     map[string]*NodeInst //key nodeInstID 当前运行节点实例集合
}

func NewToken(nodeInst *NodeInst) *Token {
	return &Token{
		id:        random.UniqueID(),
		nodeInsts: map[string]*NodeInst{nodeInst.ID: nodeInst},
		nodes:     map[string]*NodeInst{nodeInst.NodeID: nodeInst},
	}
}

//存入
func (token *Token) Save(nodeInst *NodeInst) {
	token.nodeInsts[nodeInst.ID] = nodeInst
	token.nodes[nodeInst.NodeID] = nodeInst
}

//移除
func (token *Token) Remove(nodeInst *NodeInst) {
	delete(token.nodeInsts, nodeInst.ID)
	delete(token.nodes, nodeInst.NodeID)
}

//通过节点实例ID查找节点实例
func (token *Token) FindByID(nodeInstID string) *NodeInst {
	if ni, ok := token.nodeInsts[nodeInstID]; ok {
		return ni
	}
	return nil
}

//通过节点实例对应的节点ID查找节点实例
func (token *Token) FindByNodeID(nodeID string) *NodeInst {
	if ni, ok := token.nodes[nodeID]; ok {
		return ni
	}
	return nil
}

func (token *Token) AllNodeInst() map[string]*NodeInst {
	return token.nodeInsts
}
