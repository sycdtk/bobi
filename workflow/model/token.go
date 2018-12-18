package model

//流程实例令牌
type Token struct {
	ID        string
	NodeInsts map[string]*NodeInst //当前运行节点实例集合
}
