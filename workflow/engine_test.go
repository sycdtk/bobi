package workflow

import (
	"strconv"
	//"strings"
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
	l1 := NewLine("连接1", n0.ID, n1.ID, "@EQ(aa,data1)")
	l2 := NewLine("连接2", n1.ID, n2.ID, "@IN(aa,data2)")
	l3 := NewLine("连接3", n2.ID, n3.ID, "@NIN(ww,data2)")

	//4、指定流程定义入口节点
	pd.Entrance = n0.ID

	//5、流程定义存入引擎
	engine.setProcess(pd, []*Node{n0, n1, n2, n3}, []*Line{l1, l2, l3})

	showDef(t, engine, pd)

	//启动流程
	pi := engine.Start(pd.ID)
	pi.Data["data1"] = "aa"
	pi.Data["data2"] = "aa1,bb,cc,aa"

	showSubmit(t, engine, pi, n0.ID, 1, "开始 -> 步骤1")

	showSubmit(t, engine, pi, n1.ID, 2, "步骤1 -> 步骤2")

	showSubmit(t, engine, pi, n1.ID, 3, "重复提交 步骤1 -> 步骤2")

	showSubmit(t, engine, pi, n2.ID, 4, "步骤2 -> 结束")

	showSubmit(t, engine, pi, n2.ID, 5, "重复提交 步骤2 -> 结束")

	showSubmit(t, engine, pi, n3.ID, 6, "结束 ->")

	showSubmit(t, engine, pi, n3.ID, 7, "重复提交 结束 ->")

}

//并行流程
func TestLoadAndRunParallel1(t *testing.T) {
	engine := NewEngine()

	//流程定义解析、构建过程
	//1、建立流程定义
	pd := &ProcessDef{
		ID:         random.UniqueID(),
		Name:       "并行流程测试1",
		Version:    "0.1",
		Status:     ProcessEnableStatus,
		DefineJson: "",
	}

	//2、建立节点
	n1 := NewNode("开始", BeginNode, Exclusive, Exclusive)
	n2 := NewNode("步骤1", UserNode, Exclusive, Parallel)
	n3 := NewNode("并行步骤A", UserNode, Exclusive, Exclusive)
	n4 := NewNode("并行步骤B", UserNode, Exclusive, Exclusive)
	n5 := NewNode("步骤2", UserNode, Parallel, Exclusive)
	n6 := NewNode("结束", EndNode, Exclusive, Exclusive)

	//3、建立关系
	l1 := NewLine("连接1", n1.ID, n2.ID, "@OR(true)")
	l2 := NewLine("连接2", n2.ID, n3.ID, "@OR(true)")
	l3 := NewLine("连接3", n2.ID, n4.ID, "@OR(true)")
	l4 := NewLine("连接4", n3.ID, n5.ID, "@OR(true)")
	l5 := NewLine("连接5", n4.ID, n5.ID, "@OR(true)")
	l6 := NewLine("连接6", n5.ID, n6.ID, "@OR(true)")

	//4、指定流程定义入口节点
	pd.Entrance = n1.ID

	//5、流程定义存入引擎
	engine.setProcess(pd, []*Node{n1, n2, n3, n4, n5, n6}, []*Line{l1, l2, l3, l4, l5, l6})

	showDef(t, engine, pd)

	//启动流程
	pi := engine.Start(pd.ID)

	showSubmit(t, engine, pi, n1.ID, 1, "开始 -> 步骤1")

	showSubmit(t, engine, pi, n2.ID, 2, "步骤1 -> 并行步骤A、并行步骤B")

	showSubmit(t, engine, pi, n2.ID, 3, "重复提交 步骤1 -> 并行步骤A、并行步骤B")

	showSubmit(t, engine, pi, n3.ID, 4, "并行步骤A -> 步骤2")

	showSubmit(t, engine, pi, n3.ID, 5, "重复提交 并行步骤A -> 步骤2")

	showSubmit(t, engine, pi, n4.ID, 6, "并行步骤B -> 步骤2")

	showSubmit(t, engine, pi, n4.ID, 7, "重复提交 并行步骤B -> 步骤2")

	showSubmit(t, engine, pi, n5.ID, 8, "步骤2 -> 结束")

	showSubmit(t, engine, pi, n6.ID, 9, "结束 -> ")

}

//分支流程
func TestLoadAndRunParallel2(t *testing.T) {
	engine := NewEngine()

	//流程定义解析、构建过程
	//1、建立流程定义
	pd := &ProcessDef{
		ID:         random.UniqueID(),
		Name:       "分支流程测试1",
		Version:    "0.1",
		Status:     ProcessEnableStatus,
		DefineJson: "",
	}

	//2、建立节点
	n1 := NewNode("开始", BeginNode, Exclusive, Exclusive)
	n2 := NewNode("步骤1", UserNode, Exclusive, Exclusive)
	n3 := NewNode("并行步骤A", UserNode, Exclusive, Exclusive)
	n4 := NewNode("并行步骤B", UserNode, Exclusive, Exclusive)
	n5 := NewNode("步骤2", UserNode, Exclusive, Exclusive)
	n6 := NewNode("结束", EndNode, Exclusive, Exclusive)

	//3、建立关系
	l1 := NewLine("连接1", n1.ID, n2.ID, "@OR(true)")
	l2 := NewLine("连接2", n2.ID, n3.ID, "@EQ(aa,data1)")
	l3 := NewLine("连接3", n2.ID, n4.ID, "@NEQ(aa,data1)")
	l4 := NewLine("连接4", n3.ID, n5.ID, "@OR(true)")
	l5 := NewLine("连接5", n4.ID, n5.ID, "@OR(true)")
	l6 := NewLine("连接6", n5.ID, n6.ID, "@OR(true)")

	//4、指定流程定义入口节点
	pd.Entrance = n1.ID

	//5、流程定义存入引擎
	engine.setProcess(pd, []*Node{n1, n2, n3, n4, n5, n6}, []*Line{l1, l2, l3, l4, l5, l6})

	showDef(t, engine, pd)

	//启动流程
	pi := engine.Start(pd.ID)

	pi.Data["data1"] = "cc"

	showSubmit(t, engine, pi, n1.ID, 1, "开始 -> 步骤1")

	showSubmit(t, engine, pi, n2.ID, 2, "步骤1 -> 并行步骤A、并行步骤B")

	showSubmit(t, engine, pi, n2.ID, 3, "重复提交 步骤1 -> 并行步骤A、并行步骤B")

	showSubmit(t, engine, pi, n3.ID, 4, "并行步骤A -> 步骤2")

	showSubmit(t, engine, pi, n3.ID, 5, "重复提交 并行步骤A -> 步骤2")

	showSubmit(t, engine, pi, n4.ID, 6, "并行步骤B -> 步骤2")

	showSubmit(t, engine, pi, n4.ID, 7, "重复提交 并行步骤B -> 步骤2")

	showSubmit(t, engine, pi, n5.ID, 8, "步骤2 -> 结束")

	showSubmit(t, engine, pi, n6.ID, 9, "结束 -> ")

}

//分支流程
func TestLoadAndRunParallel3(t *testing.T) {
	engine := NewEngine()

	//流程定义解析、构建过程
	//1、建立流程定义
	pd := &ProcessDef{
		ID:         random.UniqueID(),
		Name:       "分支流程测试1",
		Version:    "0.1",
		Status:     ProcessEnableStatus,
		DefineJson: "",
	}

	//2、建立节点
	n1 := NewNode("开始", BeginNode, Exclusive, Exclusive)
	n2 := NewNode("步骤1", UserNode, Exclusive, Exclusive)
	n3 := NewNode("并行步骤A", UserNode, Exclusive, Exclusive)
	n4 := NewNode("并行步骤B", UserNode, Exclusive, Exclusive)
	n5 := NewNode("步骤2", UserNode, Exclusive, Exclusive)
	n6 := NewNode("结束", EndNode, Exclusive, Exclusive)

	//3、建立关系
	l1 := NewLine("连接1", n1.ID, n2.ID, "@OR(true)")
	l2 := NewLine("连接2", n2.ID, n3.ID, "@EQ(aa,data1)")
	l3 := NewLine("连接3", n2.ID, n4.ID, "@NEQ(aa,data1)")
	l4 := NewLine("连接4", n3.ID, n5.ID, "@OR(true)")
	l5 := NewLine("连接5", n4.ID, n5.ID, "@OR(true)")
	l6 := NewLine("连接6", n5.ID, n6.ID, "@OR(true)")

	//4、指定流程定义入口节点
	pd.Entrance = n1.ID

	//5、流程定义存入引擎
	engine.setProcess(pd, []*Node{n1, n2, n3, n4, n5, n6}, []*Line{l1, l2, l3, l4, l5, l6})

	showDef(t, engine, pd)

	//启动流程
	pi := engine.Start(pd.ID)

	pi.Data["data1"] = "cc"

	showSubmit(t, engine, pi, n1.ID, 1, "开始 -> 步骤1")

	showSubmit(t, engine, pi, n2.ID, 2, "步骤1 -> 并行步骤A、并行步骤B")

	showSubmit(t, engine, pi, n2.ID, 3, "重复提交 步骤1 -> 并行步骤A、并行步骤B")

	showSubmit(t, engine, pi, n3.ID, 4, "并行步骤A -> 步骤2")

	showSubmit(t, engine, pi, n3.ID, 5, "重复提交 并行步骤A -> 步骤2")

	showSubmit(t, engine, pi, n4.ID, 6, "并行步骤B -> 步骤2")

	showSubmit(t, engine, pi, n4.ID, 7, "重复提交 并行步骤B -> 步骤2")

	showSubmit(t, engine, pi, n5.ID, 8, "步骤2 -> 结束")

	showSubmit(t, engine, pi, n6.ID, 9, "结束 -> ")

}

func showDef(t *testing.T, engine *Engine, pd *ProcessDef) {
	t.Log("==============================流程定义：" + pd.Name + "\n")
	//	输出信息
	if p, ok := engine.nodes[pd.ID]; ok {
		for _, n := range p {
			t.Log("节点", n.ID, n.Name)
			for _, r := range n.From {
				t.Log("+--From：", r.NodeID)
			}
			for _, r := range n.To {
				t.Log("+--To:", r.NodeID)
			}
			t.Log("\n")
		}
	}

	t.Log("\n")
}

func showSubmit(t *testing.T, engine *Engine, pi *ProcessInst, nodeId string, num int, name string) {
	t.Log("==============================第" + strconv.Itoa(num) + "提交:" + name)
	for _, ni := range pi.Token.AllNodeInst() {
		t.Log(ni.Name)
	}
	t.Log("------------------------------")
	engine.Submit(pi.ID, nodeId)
	for _, ni := range pi.Token.AllNodeInst() {
		t.Log(ni.Name)
	}
	t.Log("\n")
}
