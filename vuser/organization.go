package vuser

import (
	"github.com/sycdtk/bobi/set"
)

//用户组织结构
type Organization struct {
	ID          string
	Name        string
	Description string
	Level       int //组织层级
	SortNum     int //层级内机构顺序

	Leaders  *set.Set //机构领导ID集合
	Members  *set.Set //机构成员ID集合
	Managers *set.Set //机构管理员ID集合

	ParentOrg string   //父机构ID
	ChildOrg  *set.Set //子机构ID集合
}
