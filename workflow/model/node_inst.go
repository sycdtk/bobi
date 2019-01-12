package model

const (
	Ready   = 4001 //节点状态：创建完成后，就绪态
	Closed  = 4002 //节点状态：节点执行完成，关闭态
	Waiting = 4003 //节点状态：节点部分前置节点还没有全部执行完成，等待态
	Running = 4004 //节点状态：节点执行中或节点重开后，运行态
)

//节点实例
type NodeInst struct {
	ID        string
	NodeID    string //流程定义节点ID
	Name      string
	Status    int         //节点状态
	Type      int         //节点类型，与Node共用类型
	InType    int         //入节点规则类型
	OutType   int         //出节点规则类型
	TaskInsts []*TaskInst //节点任务实例

	ExecSeq string //节点实例执行顺序
}

func (ni *NodeInst) Exec(exec func(), ruleTag bool) {

	switch ni.Type {
	case BeginNode:
		exec()
	case EndNode:
		exec()
	case UserNode:
		exec()
	case AutoNode:
		exec()
	case SubProcessNode:
		exec()
	case Exclusive:
		exec()
	case Parallel:
		exec()
	case Inclusive:
		exec()
	default:
		exec()
	}

}
