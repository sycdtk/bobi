package vuser

import (
	"sync"

	"github.com/sycdtk/bobi/random"
	"github.com/sycdtk/bobi/set"
)

var once sync.Once
var Manager *VUserManager

//虚拟用户，用以映射用户、用户组、组织机构
type VUser struct {
	ID          string
	Name        string
	Description string

	Users  *set.Set //用户ID集合
	Groups *set.Set //用户组ID集合

	Organizations map[string]*set.Set //组织机构ID集合:用户递归层级，1为层级用户
}

func NewVUser(name, description string) *VUser {
	vUser := &VUser{
		ID:            random.UniqueID(),
		Name:          name,
		Users:         set.NewSet(),
		Groups:        set.NewSet(),
		Organizations: map[string]*set.Set{},
	}

	Manager.vUsers[vUser.ID] = vUser

	return vUser
}

type VUserManager struct {
	vUsers map[string]*VUser

	users map[string]*User

	groups map[string]*Group

	organizations map[string]*Organization
}

func init() {
	once.Do(func() {
		vUsers := map[string]*VUser{}
		users := map[string]*User{}
		groups := map[string]*Group{}
		organizations := map[string]*Organization{}
		Manager = &VUserManager{vUsers: vUsers, users: users, groups: groups, organizations: organizations}
	})

	load()
}

//数据初始化
func load() {

}
