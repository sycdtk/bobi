package model

//节点任务
type Task struct {
	ID    string
	Name  string
	Level int    //任务优先级：高级别先执行
	Node  string //任务对应节点 Id
}
