package workflow

import (
	"testing"

	"github.com/sycdtk/bobi/random"
	. "github.com/sycdtk/bobi/workflow/model"
)

func TestLoad(t *testing.T) {

	engine := NewEngine()

	t.Log("Load...")

	//流程定义解析、构建过程
	//1、建立流程定义
	pd := &ProcessDef{
		ID:         random.UniqueID(),
		Name:       "测试流程",
		Type:       "",
		Version:    "0.1",
		Status:     ProcessDraftStatus,
		DefineJson: "",
	}

	//2、建立节点
	s0 := &Node{ID: random.UniqueID(), Name: "开始"}
	s1 := &Node{ID: random.UniqueID(), Name: "步骤1"}
	s2 := &Node{ID: random.UniqueID(), Name: "步骤2"}
	s3 := &Node{ID: random.UniqueID(), Name: "结束"}

	//3、建立路径
	l1 := &Line{ID: random.UniqueID(), Name: "连接1", From: s0.ID, To: s1.ID}
	l2 := &Line{ID: random.UniqueID(), Name: "连接2", From: s1.ID, To: s2.ID}
	l3 := &Line{ID: random.UniqueID(), Name: "连接3", From: s2.ID, To: s3.ID}

	//4、指定流程定义入口节点
	pd.Entrance = s0.ID

	//5、流程定义存入引擎
	engine.setProcess(pd, []*Node{s0, s1, s2, s3}, []*Line{l1, l2, l3})

	//输出信息
	for _, p := range engine.nodes {
		for _, n := range p {
			t.Log("节点", n.ID, n.Name)
		}
	}

	for _, p := range engine.lines {
		for _, l := range p {
			t.Log("连线", l.ID, l.Name, l.From, l.To)
		}
	}

	for _, p := range engine.relations {
		for _, r := range p {
			t.Log(engine.getNode(pd.ID, r.ID), r.From, r.To)
		}
	}

	pi := engine.Start(pd.ID)
	engine.Submit(pi.ID)
	engine.Submit(pi.ID)
	engine.Submit(pi.ID)

}
