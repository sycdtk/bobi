package vuser

import (
	"testing"

	"github.com/sycdtk/bobi/set"
)

func TestNewUserAndGroup(t *testing.T) {
	user := NewUser("管理员", "系统管理员", Male, "admin", "admin", "78306909@qq.com", "", "135XXXXXXXX", "", set.NewSet(), set.NewSet())

	users := set.NewSet().Load(user.ID)

	group := NewGroup("管理员组", "管理员分组", users)

	if Manager.users[user.ID].Groups.String() == group.ID {
		t.Log("Manager.users[user.ID].Groups.String() == group.ID")
	} else {
		t.Error("Manager.users[user.ID].Groups.String() == group.ID", Manager.users[user.ID].Groups.String(), group.ID)
	}

}
