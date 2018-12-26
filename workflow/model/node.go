package model

import (
	"github.com/sycdtk/bobi/random"
)

//节点类型：
//网关说明：http://www.mossle.com/docs/activiti/index.html#bpmnCustomExtensions
//排他网关（也叫异或（XOR）网关，或更技术性的叫法 基于数据的排他网关）， 用来在流程中实现决策。
//  当流程执行到这个网关，所有外出顺序流都会被处理一遍。 其中条件解析为true的顺序流（或者没有设置条件，概念上在顺序流上定义了一个'true'）
//  会被选中，让流程继续运行。注意这里的外出顺序流 与BPMN 2.0通常的概念是不同的。通常情况下，所有条件结果为true的顺序流 都会被选中，
//  以并行方式执行，但排他网关只会选择一条顺序流执行。 就是说，虽然多个顺序流的条件结果为true，
//  那么XML中的第一个顺序流（也只有这一条）会被选中，并用来继续运行流程。 如果没有选中任何顺序流，会抛出一个异常。
//并行网关：网关也可以表示流程中的并行情况。最简单的并行网关是 并行网关，它允许将流程 分成多条分支，也可以把多条分支 汇聚到一起。 of execution.
//  并行网关的功能是基于进入和外出的顺序流的：
//    分支： 并行后的所有外出顺序流，为每个顺序流都创建一个并发分支。
//    汇聚： 所有到达并行网关，在此等待的进入分支， 直到所有进入顺序流的分支都到达以后， 流程就会通过汇聚网关。
//  注意，如果同一个并行网关有多个进入和多个外出顺序流， 它就同时具有分支和汇聚功能。 这时，网关会先汇聚所有进入的顺序流，然后再切分成多个并行分支。
//  与其他网关的主要区别是，并行网关不会解析条件。 即使顺序流中定义了条件，也会被忽略。
//包含网关:可以看做是排他网关和并行网关的结合体。 和排他网关一样，你可以在外出顺序流上定义条件，包含网关会解析它们。 但是主要的区别是包含网关可以选择多于一条顺序流，这和并行网关一样。
//  包含网关的功能是基于进入和外出顺序流的：
//    分支： 所有外出顺序流的条件都会被解析，结果为true的顺序流会以并行方式继续执行， 会为每个顺序流创建一个分支。
//    汇聚： 所有并行分支到达包含网关，会进入等待章台， 直到每个包含流程token的进入顺序流的分支都到达。 这是与并行网关的最大不同。换句话说，包含网关只会等待被选中执行了的进入顺序流。 在汇聚之后，流程会穿过包含网关继续执行。
//  注意，如果同一个包含节点拥有多个进入和外出顺序流， 它就会同时含有分支和汇聚功能。 这时，网关会先汇聚所有拥有流程token的进入顺序流， 再根据条件判断结果为true的外出顺序流，为它们生成多条并行分支。
const (
	BeginNode      = 1001 //节点类型：开始节点
	EndNode        = 1002 //节点类型：结束节点
	UserNode       = 1003 //节点类型：用户任务节点（默认类型）
	AutoNode       = 1004 //节点类型：自动任务节点
	SubProcessNode = 1005 //节点类型：子流程节点
	Exclusive      = 2001 //节点出入路径规则类型：排他（默认类型）
	Parallel       = 2002 //节点出入路径规则类型：并行
	Inclusive      = 2003 //节点出入路径规则类型：包含

)

//流程节点
type Node struct {
	ID   string
	Name string

	Type    int //节点类型
	InType  int //入节点规则类型
	OutType int //出节点规则类型

	From []*Relation //节点入向关系
	To   []*Relation //节点出向关系

	Tasks []string //节点任务 Task Id 集合
}

func NewNode(Name string, Type, InType, OutType int) *Node {
	return &Node{
		ID:      random.UniqueID(),
		Name:    Name,
		Type:    Type,
		InType:  InType,
		OutType: OutType,
		From:    []*Relation{},
		To:      []*Relation{},
	}
}

func (node *Node) NewNodeInst() *NodeInst {

	in, out := Exclusive, Exclusive

	if node.InType != 0 {
		in = node.InType
	}

	if node.OutType != 0 {
		out = node.OutType
	}

	return &NodeInst{
		ID:      random.UniqueID(),
		NodeID:  node.ID,
		Name:    node.Name,
		Type:    node.Type,
		InType:  in,
		OutType: out,
	}
}
