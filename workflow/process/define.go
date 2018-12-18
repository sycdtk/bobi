package workflow

import (
	"fmt"
)

//节点实例
type NodeInstance struct {
	Id            string
	Name          string
	TaskInstances []*TaskInstance //节点任务实例
}

type TaskInstance struct {
	Id           string
	Name         string
	NodeInstance *NodeInstance //任务对应节点实例
}

//任务操作
func (ti *TaskInstance) Do() {
	fmt.Println(ti.Name)
}

//流程实例令牌
type Token struct {
	Id            string
	NodeInstances map[string]*NodeInstance //当前运行节点实例集合
}

//流程定义
type ProcessDefine struct {
	Id         string
	Name       string
	Version    int              //版本
	DefineJson string           //流程定义，json格式
	StartNode  *Node            //开始节点
	Nodes      map[string]*Node //流程定义node集合
}

//解析流程定义
func (pd *ProcessDefine) Parse() {
	//建立节点
	s0 := &Node{Id: "N0000", Name: "开始"}
	s1 := &Node{Id: "N0001", Name: "步骤1"}
	s2 := &Node{Id: "N0002", Name: "步骤2"}
	s3 := &Node{Id: "N0003", Name: "结束"}

	nodes := map[string]*Node{
		s0.Name: s0,
		s1.Name: s1,
		s2.Name: s2,
		s3.Name: s3,
	}

	//构建任务
	s0.Tasks = []*Task{&Task{Id: "T0000", Name: "开始-任务1", Node: s0}}
	s1.Tasks = []*Task{&Task{Id: "T0001", Name: "步骤1-任务1", Node: s1}}
	s2.Tasks = []*Task{&Task{Id: "T0002", Name: "步骤2-任务1", Node: s2}}
	s3.Tasks = []*Task{&Task{Id: "T0003", Name: "结束-任务1", Node: s3}}

	//建立路径
	l1 := &Transition{Id: "TR0001", Name: "连接1", FromNode: s0, ToNode: s1}
	l2 := &Transition{Id: "TR0002", Name: "连接2", FromNode: s1, ToNode: s2}
	l3 := &Transition{Id: "TR0003", Name: "连接3", FromNode: s2, ToNode: s3}

	s0.ToTransitions = []*Transition{l1}

	s1.FromTransitions = []*Transition{l1}
	s1.ToTransitions = []*Transition{l2}

	s2.FromTransitions = []*Transition{l2}
	s2.ToTransitions = []*Transition{l3}

	s3.FromTransitions = []*Transition{l3}

	pd.StartNode = s0
	pd.Nodes = nodes

}

//新建流程实例
func (pd *ProcessDefine) NewInstance() *ProcessInstance {

	//构建Node实例
	ni := &NodeInstance{Id: "NI0001", Name: "流程实例1-节点1"}

	//构建Task实例
	ti := &TaskInstance{Id: "TI0001", Name: "流程实例1-节点1-任务1", NodeInstance: ni}

	//构建关系
	ni.TaskInstances = []*TaskInstance{ti}

	//构建token
	t := &Token{Id: "TO0001"}
	t.NodeInstances = map[string]*NodeInstance{
		ni.Name: ni,
	}

	//构建实例
	pi := &ProcessInstance{Id: "PI0001", Name: "流程实例1", ProcessDefine: pd, Token: t}
	return pi
}

//流程实例
type ProcessInstance struct {
	Id            string
	Name          string
	ProcessDefine *ProcessDefine //流程定义
	Token         *Token
}
