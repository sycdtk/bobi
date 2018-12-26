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
	n0 := NewNode("开始", BeginNode, Exclusive, Exclusive)
	n1 := NewNode("步骤1", UserNode, Exclusive, Exclusive)
	n2 := NewNode("步骤2", UserNode, Exclusive, Exclusive)
	n3 := NewNode("结束", EndNode, Exclusive, Exclusive)

	//3、建立关系
	l1 := NewLine("连接1", n0.ID, n1.ID, "")
	l2 := NewLine("连接2", n1.ID, n2.ID, "bb")
	l3 := NewLine("连接3", n2.ID, n3.ID, "")

	//4、指定流程定义入口节点
	pd.Entrance = n0.ID

	//5、流程定义存入引擎
	engine.setProcess(pd, []*Node{n0, n1, n2, n3}, []*Line{l1, l2, l3})

	//	输出信息
	for _, p := range engine.nodes {
		for _, n := range p {
			t.Log("节点", n.ID, n.Name)
			for _, r := range n.From {
				t.Log("+-From：", r.NodeID)
			}
			for _, r := range n.To {
				t.Log("+-To:", r.NodeID)
			}
			t.Log("--------------------------")
		}
	}

	pi := engine.Start(pd.ID)
	engine.Submit(pi.ID, n0.ID)
	engine.Submit(pi.ID, n1.ID)
	engine.Submit(pi.ID, n2.ID)
	engine.Submit(pi.ID, n3.ID)
	engine.Submit(pi.ID, n3.ID) //冗余提交

}
