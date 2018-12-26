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
func (engine *Engine) Submit(processInstID, nodeInstID string) {
	pi := engine.getProcessInst(processInstID)
	logger.Info("-----\n" + nodeInstID)
	for _, ni := range pi.Token.NodeInsts {
		logger.Info(ni.Name)
	}

	//确认提交的动作节点处于token中
	if ni, ok := pi.Token.NodeInsts[nodeInstID]; ok {

		//判断节点类型
		switch ni.Type {

		case BeginNode: //开始节点,出路有且仅有一条
			//判断入向类型，开始节点无入向
			//判断出向类型
			if ni.OutType == Exclusive { //排他
				logger.Info(ni.Name, ni.OutType)
			} else if ni.OutType == Parallel { //并行
				logger.Info(ni.Name, ni.OutType)
			} else if ni.OutType == Inclusive { //包含
				logger.Info(ni.Name, ni.OutType)
			}

		case EndNode: //结束节点

		case UserNode: //用户任务节点（默认类型）

		case AutoNode: //自动任务节点

		case SubProcessNode: //子流程

		default:

		}

		//节点移动
		node := engine.getNode(pi.ProcessID, ni.NodeID)
		nextNode := engine.getNode(pi.ProcessID, node.To[0].NodeID)
		nextNodeInst := nextNode.NewNodeInst()
		delete(pi.Token.NodeInsts, nodeInstID)
		pi.Token.NodeInsts[nextNodeInst.ID] = nextNodeInst

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
