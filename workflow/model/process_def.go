package model

const (
	ProcessDraftStatus   = 1000 //草稿状态，开发定义流程阶段，不会对外公布流程
	ProcessTestStatus    = 1001 //测试状态，流程对测试用户可见，不会对外公布流程
	ProcessEnableStatus  = 1002 //启用状态，流程对流程相关用户可见，对外公布流程
	ProcessPauseStatus   = 1003 //暂停状态，流程对流程相关用户可见，对外公布流程，但不可创建，数据可见
	ProcessDisableStatus = 1004 //停用状态，流程对流程相关用户不可见，对外公布流程，但不可创建，数据不可见
)

//流程定义
type ProcessDef struct {
	ID         string
	Name       string
	Type       string //流程类型
	Version    string //版本：每次启用为一个版本
	Status     int    //流程定义状态：草稿、启用、暂停
	DefineJson string //流程定义，json格式
	Entrance   string //开始节点Id
}
