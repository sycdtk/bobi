package workflow

import (
	"sync"

	"github.com/sycdtk/bobi/expression"
	// "github.com/sycdtk/bobi/orm"

	. "github.com/sycdtk/bobi/workflow/model"
)

const (
	modelName = "wf"
)

var once sync.Once
var WFEngine *Engine

//流程引擎
//学习参考：http://www.mossle.com/docs/activiti/index.html#bpmnCustomExtensions
type Engine struct {
	//流程定义数据集合：流程定义ID
	processDefs map[string]*ProcessDef
	//节点数据集合：流程定义ID，节点ID
	nodes map[string]map[string]*Node

	//运行数据:流程实例集合
	prcessInsts map[string]*ProcessInst

	//引擎版本
	version string
}

//构建函数(单例模式)
func init() {
	once.Do(func() {
		//注册ORM对象
		//orm.Register(modelName, func() interface{} { return &Line{} })

		processDefs := map[string]*ProcessDef{}
		nodes := map[string]map[string]*Node{}
		prcessInsts := map[string]*ProcessInst{}
		version := "0.0.1"
		WFEngine = &Engine{processDefs: processDefs, nodes: nodes, prcessInsts: prcessInsts, version: version}
	})
}

//加载流程配置到引擎
func (engine *Engine) Load() {

}

//保存流程配置
func (engine *Engine) Save() {

}

//设置流程定义
func (engine *Engine) setProcess(processDef *ProcessDef, newNodes []*Node, newLines []*Line) {

	//1、存入流程定义
	engine.processDefs[processDef.ID] = processDef

	//2、存入节点
	//流程不存在，将创建流程节点集合
	if _, ok := engine.nodes[processDef.ID]; !ok {
		processNodes := make(map[string]*Node)
		engine.nodes[processDef.ID] = processNodes
	}

	for _, node := range newNodes {
		//无论连线是否存在，都做覆盖
		engine.nodes[processDef.ID][node.ID] = node
	}

	//3、构建节点关系,若连线两端的节点不在节点集合中将被忽略
	for _, line := range newLines {

		//注册表达式
		expression.Reg(line.Rule.ID, line.Rule.Expression)

		if node, ok := engine.nodes[processDef.ID][line.From]; ok {
			node.To = append(node.To, &Relation{NodeID: line.To, Rule: line.Rule})
		}

		if node, ok := engine.nodes[processDef.ID][line.To]; ok {
			node.From = append(node.From, &Relation{NodeID: line.From, Rule: line.Rule})
		}
	}

}

//获取节点
func (engine *Engine) getNode(processID string, nodeID string) *Node {
	if _, ok := engine.nodes[processID]; ok {
		return engine.nodes[processID][nodeID]
	}
	return nil
}

//获取引擎中的流程定义
func (engine *Engine) getProcessDef(processID string) *ProcessDef {
	if _, ok := engine.processDefs[processID]; ok {
		return engine.processDefs[processID]
	}
	return nil
}

//获取引擎中的流程实例
func (engine *Engine) getProcessInst(processInstID string) *ProcessInst {
	if _, ok := engine.prcessInsts[processInstID]; ok {
		return engine.prcessInsts[processInstID]
	}
	return nil
}

func (engine *Engine) setProcessInst(pi *ProcessInst) {
	engine.prcessInsts[pi.ID] = pi
}

func (engine *Engine) relationCheck(relation *Relation, data map[string]string) bool {
	return expression.Calc(relation.Rule.ID, data)

}

//流程引擎版本
func (engine *Engine) Version() string {
	return engine.version
}
