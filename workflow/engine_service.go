package workflow

import (
	"github.com/sycdtk/gotools/logger"

	//	"github.com/sycdtk/bobi/logger"

	//"github.com/sycdtk/bobi/random"
	. "github.com/sycdtk/bobi/workflow/model"
)

//===================================================================================
//业务层
//启动流程,构建Process Inscense,但不做节点提交
func (engine *Engine) Start(processID string) *ProcessInst {

	//流程起始节点，节点创建实例
	pd := engine.getProcessDef(processID)
	sn := engine.getNode(pd.ID, pd.Entrance)
	ni := sn.NewNodeInst()

	//起始节点实例加入token
	pi := NewProcessInst(pd, NewToken(ni))

	engine.setProcessInst(pi)
	//返回流程实例
	return pi
}

//提交流程实例：流程实例流转过程中，当前处理人员提交
func (engine *Engine) Submit(processInstID, nodeID string) {
	pi := engine.getProcessInst(processInstID)

	if ni := pi.Token.FindByNodeID(nodeID); ni != nil {
		//判断节点类型
		switch ni.Type {

		case BeginNode:
			//判断出向类型，开始节点无入向
			if ni.OutType == Exclusive { //排他
				//节点移动
				node := engine.getNode(pi.ProcessID, ni.NodeID)
				if node != nil && len(node.To) > 0 {
					for _, relation := range node.To {
						//TODO 以下为测试逻辑，需要增加规则判断逻辑
						if relation.Rule == "" {
							nextNode := engine.getNode(pi.ProcessID, relation.NodeID)
							nextNodeInst := nextNode.NewNodeInst()
							//TODO 测试逻辑，需要处理并行的情况
							pi.Token.Remove(ni)
							pi.Token.Save(nextNodeInst)
							break
						}
					}
				}

			} else if ni.OutType == Parallel { //并行
				logger.Info(ni.Name, ni.OutType)
			} else if ni.OutType == Inclusive { //包含
				logger.Info(ni.Name, ni.OutType)
			}

		case EndNode: //结束节点
			//判断入向类型，结束节点无入向
			if ni.InType == Exclusive { //排他
				//节点移动
				node := engine.getNode(pi.ProcessID, ni.NodeID)
				if node != nil && len(node.To) > 0 {
					for _, relation := range node.To {
						//TODO 以下为测试逻辑，需要增加规则判断逻辑
						if relation.Rule == "" {
							nextNode := engine.getNode(pi.ProcessID, relation.NodeID)
							nextNodeInst := nextNode.NewNodeInst()
							//TODO 测试逻辑，需要处理并行的情况
							pi.Token.Remove(ni)
							pi.Token.Save(nextNodeInst)
							break
						}
					}
				}

			} else if ni.OutType == Parallel { //并行
				logger.Info(ni.Name, ni.OutType)
			} else if ni.OutType == Inclusive { //包含
				logger.Info(ni.Name, ni.OutType)
			}

		case UserNode: //用户任务节点（默认类型）
			//判断入向类型，开始节点无入向
			//判断出向类型
			if ni.OutType == Exclusive { //排他
				//节点移动
				node := engine.getNode(pi.ProcessID, ni.NodeID)
				if node != nil && len(node.To) > 0 {
					for _, relation := range node.To {
						//TODO 以下为测试逻辑，需要增加规则判断逻辑
						if relation.Rule == "" {
							nextNode := engine.getNode(pi.ProcessID, relation.NodeID)
							nextNodeInst := nextNode.NewNodeInst()
							//TODO 测试逻辑，需要处理并行的情况
							pi.Token.Remove(ni)
							pi.Token.Save(nextNodeInst)
							break
						}
					}
				}

			} else if ni.OutType == Parallel { //并行
				logger.Info(ni.Name, ni.OutType)
			} else if ni.OutType == Inclusive { //包含
				logger.Info(ni.Name, ni.OutType)
			}

		case AutoNode: //自动任务节点

		case SubProcessNode: //子流程

		default:

		}
	}

}

//转交流程实例：流程实例流转过程中，由当前节点的处理人员转交给其他人员
func (engine *Engine) HandOver() {

}

//撤回流程实例：流程实例流转过程中，由发起人员主动撤回
func (engine *Engine) Withdraw() {

}

//取消流程实例：流程实例流转过程中，由人员主动取消
func (engine *Engine) Cancel() {

}

//关闭流程实例：流程实例流转过程中，流程实例经过流转，正常关闭
func (engine *Engine) Finish() {

}

//关闭流程实例：流程未正常关闭，由特殊权限从中途主动关闭
func (engine *Engine) Close() {

}
