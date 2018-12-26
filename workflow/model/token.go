package model

import (
	"github.com/sycdtk/bobi/random"
)

//流程实例令牌
type Token struct {
	ID        string
	NodeInsts map[string]*NodeInst //当前运行节点实例集合
}

func NewToken(node *NodeInst) *Token {
	return &Token{
		ID:        random.UniqueID(),
		NodeInsts: map[string]*NodeInst{node.ID: node},
	}
}
