package vuser

import (
	"github.com/sycdtk/bobi/random"
	"github.com/sycdtk/bobi/set"
)

type Group struct {
	ID          string
	Name        string
	Description string

	Users *set.Set //用户ID集合
}

func NewGroup(name, description string, users *set.Set) *Group {
	group := &Group{
		ID:          random.UniqueID(),
		Name:        name,
		Description: description,
		Users:       users,
	}

	Manager.groups[group.ID] = group

	if !users.Empty() {
		for _, userID := range users.Array() {
			if user, ok := Manager.users[userID]; ok {
				user.Groups.Add(group.ID)
			}
		}
	}

	return group
}
