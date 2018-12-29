# 流程引擎概述
## 1、说明
======
	此流程引擎参考了一些现有流程引擎的概念和定义，加入了一些自己理解功能和元素，不具有工作流的标准规范的通用性。

2、目的
======
	此流程引擎，仅作为mofy工程的流程引擎支持，会包含很多高耦合的开发实现，不考虑做成为一个支持各场景工作流的的通用框架。
	
3、流程定义
======

3、节点
======
	流程中节点与其他工作流的节点意义一致，用以指定该环节的任务相关的人员、
	
4、节点实例
======

5、任务
======

5、连线
======
6、连线实例
======
7、关联关系
======
8、规则
======
10、引擎功能
======


//两个节点单一方向上的连线，有且仅有一条
//节点支持指向自身




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



//规则包含Rule内部逻辑结果、连线关系逻辑结果、节点规则逻辑结果，最终节点逻辑结果决定了流程是否进行流转至下一步
//1、Rule内部逻辑包含传入数据比对、与非逻辑等
//2、连线关系逻辑、节点规则逻辑仅包含 AND、OR、NOT之间的与非逻辑
//逻辑关系：AND/OR/NOT
//字符串：等于、不等于、包含、不包含
//数值：大于、小于、等于、大于等于、小于等于、不等于