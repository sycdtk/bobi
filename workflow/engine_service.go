package workflow

import (
	"github.com/sycdtk/bobi/logger"

	"github.com/sycdtk/bobi/random"
	. "github.com/sycdtk/bobi/workflow/model"
)

//===================================================================================
//业务层
//启动流程,构建Process Inscense不做节点提交
func (engine *Engine) Start(processID string) *ProcessInst {

	//流程起始节点，节点创建实例
	pd := engine.getProcessDef(processID)
	sn := engine.getNode(pd.ID, pd.Entrance)
	ni := sn.NewInst()

	//起始节点实例加入token
	token := &Token{
		ID:        random.UniqueID(),
		NodeInsts: map[string]*NodeInst{ni.ID: ni},
	}

	pi := &ProcessInst{
		ID:        random.UniqueID(),
		Name:      pd.Name,
		ProcessID: pd.ID,
		Token:     token,
	}

	//返回流程实例
	engine.setProcessInst(pi)

	return pi
}

//提交流程实例：流程实例流转过程中，当前处理人员提交
func (engine *Engine) Submit(processInstID string) {
	pi := engine.getProcessInst(processInstID)

	tTokenMap := map[string]*NodeInst{}
	for _, ni := range pi.Token.NodeInsts {
		ni.Exec(func() {
			logger.Debug("执行", ni.Name)
		})

		delete(pi.Token.NodeInsts, ni.ID)

		nr := engine.getRelation(pi.ProcessID, ni.NodeID)

		for _, nID := range nr.To {
			newNode := engine.getNode(pi.ProcessID, nID)
			nni := newNode.NewInst()

			tTokenMap[nni.ID] = nni
		}
	}
	pi.Token.NodeInsts = tTokenMap
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
