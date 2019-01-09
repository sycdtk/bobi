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
	l1 := NewLine("连接1", n0.ID, n1.ID, "@EQ(aa,data1)")
	l2 := NewLine("连接2", n1.ID, n2.ID, "@IN(aa,data2)")
	l3 := NewLine("连接3", n2.ID, n3.ID, "@NIN(ww,data2)")

	//4、指定流程定义入口节点
	pd.Entrance = n0.ID

	//5、流程定义存入引擎
	engine.setProcess(pd, []*Node{n0, n1, n2, n3}, []*Line{l1, l2, l3})

	t.Log("节点信息: Begin\n")
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
			t.Log("\n")
		}
	}
	t.Log("节点信息: End ============================\n")
	t.Log("Token信息: Begin ============================")

	pi := engine.Start(pd.ID)
	pi.Data["data1"] = "aa"
	pi.Data["data2"] = "aa1,bb,cc,aa"

	//第一步 开始
	for _, ni := range pi.Token.AllNodeInst() {
		t.Log("Token Step1 :", ni.Name)
	}

	engine.Submit(pi.ID, n0.ID)

	for _, ni := range pi.Token.AllNodeInst() {
		t.Log("Token Step2 :", ni.Name)
	}
	t.Log("Token信息: End ============================")

	//第二步 步骤一
	t.Log("Token信息: Begin ============================")
	for _, ni := range pi.Token.AllNodeInst() {
		t.Log("Token Step2 :", ni.Name)
	}

	engine.Submit(pi.ID, n1.ID)

	for _, ni := range pi.Token.AllNodeInst() {
		t.Log("Token Step3 :", ni.Name)
	}
	t.Log("Token信息: End ============================")

	//第二步 步骤一 重复提交
	t.Log("Token信息: Begin ============================")
	for _, ni := range pi.Token.AllNodeInst() {
		t.Log("Token Step2 :", ni.Name)
	}

	engine.Submit(pi.ID, n1.ID)

	for _, ni := range pi.Token.AllNodeInst() {
		t.Log("Token Step3 :", ni.Name)
	}
	t.Log("Token信息: End ============================")

	//第三步 步骤二
	t.Log("Token信息: Begin ============================")
	for _, ni := range pi.Token.AllNodeInst() {
		t.Log("Token Step3 :", ni.Name)
	}

	engine.Submit(pi.ID, n2.ID)

	for _, ni := range pi.Token.AllNodeInst() {
		t.Log("Token Step4 :", ni.Name)
	}
	t.Log("Token信息: End ============================")

	//第三步 步骤二 重复提交
	t.Log("Token信息: Begin ============================")
	for _, ni := range pi.Token.AllNodeInst() {
		t.Log("Token Step3 :", ni.Name)
	}

	engine.Submit(pi.ID, n2.ID)

	for _, ni := range pi.Token.AllNodeInst() {
		t.Log("Token Step4 :", ni.Name)
	}
	t.Log("Token信息: End ============================")

	//第四步 结束
	t.Log("Token信息: Begin ============================")
	for _, ni := range pi.Token.AllNodeInst() {
		t.Log("Token Step4 :", ni.Name)
	}

	engine.Submit(pi.ID, n3.ID)

	for _, ni := range pi.Token.AllNodeInst() {
		t.Log("Token Step5 :", ni.Name)
	}
	t.Log("Token信息: End ============================")

	//第四步 结束
	t.Log("Token信息: Begin ============================")
	for _, ni := range pi.Token.AllNodeInst() {
		t.Log("Token Step4 :", ni.Name)
	}

	engine.Submit(pi.ID, n3.ID)

	for _, ni := range pi.Token.AllNodeInst() {
		t.Log("Token Step5 :", ni.Name)
	}
	t.Log("Token信息: End ============================")

}
