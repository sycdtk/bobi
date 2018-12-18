package workflow

import (
	"sync"

	//"github.com/sycdtk/gotools/logger"

	//"github.com/sycdtk/bobi/random"
	. "github.com/sycdtk/bobi/workflow/model"
)

var once sync.Once

//流程引擎
type Engine struct {
	//流程定义数据集合：流程ID
	processDefs map[string]*ProcessDef
	//节点数据集合：流程ID，节点ID
	nodes map[string]map[string]*Node
	//连线数据集合：流程ID，连线ID
	lines map[string]map[string]*Line
	//节点上的连线：流程ID，节点ID
	relations map[string]map[string]*Relation

	//运行数据:流程实例集合
	prcessInsts map[string]*ProcessInst

	//引擎版本
	version string
}

//构建函数(单例模式)
func NewEngine() *Engine {
	var engine *Engine

	once.Do(func() {
		processDefs := map[string]*ProcessDef{}
		nodes := map[string]map[string]*Node{}
		lines := map[string]map[string]*Line{}
		relations := map[string]map[string]*Relation{}
		prcessInsts := map[string]*ProcessInst{}
		version := "0.0.1"
		engine = &Engine{processDefs: processDefs, nodes: nodes, lines: lines, relations: relations, prcessInsts: prcessInsts, version: version}
	})

	return engine
}

//维护层
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

	//3、存入连线
	//流程不存在，将创建流程连线集合
	if _, ok := engine.lines[processDef.ID]; !ok {
		processLines := make(map[string]*Line)
		engine.lines[processDef.ID] = processLines
	}

	//4、创建关系集合
	if _, ok := engine.relations[processDef.ID]; !ok {
		processRelations := make(map[string]*Relation)
		engine.relations[processDef.ID] = processRelations
	}

	for _, line := range newLines {
		//无论连线是否存在，都做覆盖
		engine.lines[processDef.ID][line.ID] = line

		//构建节点间关系
		if _, ok := engine.relations[processDef.ID][line.From]; !ok {
			engine.relations[processDef.ID][line.From] = &Relation{ID: line.From, To: []string{line.To}}
		} else {
			engine.relations[processDef.ID][line.From].To = append(engine.relations[processDef.ID][line.From].To, line.To)
		}

		if _, ok := engine.relations[processDef.ID][line.To]; !ok {
			engine.relations[processDef.ID][line.To] = &Relation{ID: line.To, From: []string{line.From}}
		} else {
			engine.relations[processDef.ID][line.To].From = append(engine.relations[processDef.ID][line.To].From, line.From)
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

//获取连线
func (engine *Engine) getLine(processID string, lineID string) *Line {
	if _, ok := engine.lines[processID]; ok {
		return engine.lines[processID][lineID]
	}
	return nil
}

//获取节点关系
func (engine *Engine) getRelation(processID string, nodeID string) *Relation {
	if _, ok := engine.relations[processID]; ok {
		return engine.relations[processID][nodeID]
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

//流程引擎版本
func (engine *Engine) Version() string {
	return engine.version
}
