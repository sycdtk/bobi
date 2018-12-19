package workflow

import (
	"testing"

	"github.com/sycdtk/bobi/random"
	. "github.com/sycdtk/bobi/workflow/model"
)

//顺序提交
func TestLoadAndRunSeq(t *testing.T) {

	engine := NewEngine()

	//流程定义解析、构建过程
	//1、建立流程定义
	pd := &ProcessDef{
		ID:         random.UniqueID(),
		Name:       "顺序流程测试",
		Version:    "0.1",
		Status:     ProcessEnableStatus,
		DefineJson: "",
	}

	//2、建立节点
	s0 := &Node{ID: random.UniqueID(), Name: "开始", Type: BeginNode}
	s1 := &Node{ID: random.UniqueID(), Name: "步骤1", Type: UserNode}
	s2 := &Node{ID: random.UniqueID(), Name: "步骤2", Type: UserNode}
	s3 := &Node{ID: random.UniqueID(), Name: "结束", Type: EndNode}

	//3、建立路径
	l1 := &Line{ID: random.UniqueID(), Name: "连接1", From: s0.ID, To: s1.ID}
	l2 := &Line{ID: random.UniqueID(), Name: "连接2", From: s1.ID, To: s2.ID}
	l3 := &Line{ID: random.UniqueID(), Name: "连接3", From: s2.ID, To: s3.ID}

	//4、指定流程定义入口节点
	pd.Entrance = s0.ID

	//5、流程定义存入引擎
	engine.setProcess(pd, []*Node{s0, s1, s2, s3}, []*Line{l1, l2, l3})

	//输出信息
	//	for _, p := range engine.nodes {
	//		for _, n := range p {
	//			t.Log("节点", n.ID, n.Name)
	//		}
	//	}

	//	for _, p := range engine.lines {
	//		for _, l := range p {
	//			t.Log("连线", l.ID, l.Name, l.From, l.To)
	//		}
	//	}

	//	for _, p := range engine.relations {
	//		for _, r := range p {
	//			t.Log(engine.getNode(pd.ID, r.ID), r.From, r.To)
	//		}
	//	}

	pi := engine.Start(pd.ID)
	engine.Submit(pi.ID)
	engine.Submit(pi.ID)
	engine.Submit(pi.ID)
	engine.Submit(pi.ID)
	engine.Submit(pi.ID) //冗余提交

}

//排他网关
func TestLoadAndRunExclusive(t *testing.T) {

	engine := NewEngine()

	//流程定义解析、构建过程
	//1、建立流程定义
	pd := &ProcessDef{
		ID:         random.UniqueID(),
		Name:       "排他网关流程测试",
		Version:    "0.1",
		Status:     ProcessEnableStatus,
		DefineJson: "",
	}

	//2、建立节点
	n0 := &Node{ID: random.UniqueID(), Name: "开始", Type: BeginNode}
	n1 := &Node{ID: random.UniqueID(), Name: "步骤1", Type: UserNode}
	n2 := &Node{ID: random.UniqueID(), Name: "排他网关1", Type: ExclusiveGateway}
	n3 := &Node{ID: random.UniqueID(), Name: "步骤3", Type: UserNode}
	n4 := &Node{ID: random.UniqueID(), Name: "步骤4", Type: UserNode}
	n5 := &Node{ID: random.UniqueID(), Name: "结束", Type: EndNode}

	//3、建立路径
	l1 := &Line{ID: random.UniqueID(), Name: "连接1", From: n0.ID, To: n1.ID}
	l2 := &Line{ID: random.UniqueID(), Name: "连接2", From: n1.ID, To: n2.ID}
	l3 := &Line{ID: random.UniqueID(), Name: "连接3x", From: n2.ID, To: n3.ID, Rule: "aa"}
	l4 := &Line{ID: random.UniqueID(), Name: "连接4x", From: n2.ID, To: n4.ID, Rule: "bb"}
	l5 := &Line{ID: random.UniqueID(), Name: "连接5", From: n3.ID, To: n5.ID}
	l6 := &Line{ID: random.UniqueID(), Name: "连接6", From: n4.ID, To: n5.ID}

	//4、指定流程定义入口节点
	pd.Entrance = n0.ID

	//5、流程定义存入引擎
	engine.setProcess(pd, []*Node{n0, n1, n2, n3, n4, n5}, []*Line{l1, l2, l3, l4, l5, l6})

	//输出信息
	//	for _, p := range engine.nodes {
	//		for _, n := range p {
	//			t.Log("节点", n.ID, n.Name)
	//		}
	//	}

	//	for _, p := range engine.lines {
	//		for _, l := range p {
	//			t.Log("连线", l.ID, l.Name, l.From, l.To)
	//		}
	//	}

	//	for _, p := range engine.relations {
	//		for _, r := range p {
	//			t.Log(engine.getNode(pd.ID, r.ID), r.From, r.To)
	//		}
	//	}

	pi := engine.Start(pd.ID)
	engine.Submit(pi.ID)
	engine.Submit(pi.ID)
	engine.Submit(pi.ID)
	engine.Submit(pi.ID)
	engine.Submit(pi.ID)
	engine.Submit(pi.ID)
	engine.Submit(pi.ID) //冗余提交

}
