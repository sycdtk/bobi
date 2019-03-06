package vuser

import (
	"testing"

	"github.com/sycdtk/bobi/set"
)

func TestUser(t *testing.T) {
	user := NewUser("wolffy", "test user", Male, "wolffy", "123456", "78306909@qq.com",
		"010-12345678", "13520040000", "", set.NewSet(), set.NewSet())
	//user.Save()
	user = QueryByUsername("wolffy")
	user.Telephone = "010-12345678"
	user.Update()
	user = QueryByID(user.ID)
	user.Name = "niky"
	user.Update()
	user = QueryByID(user.ID)
	user.Name = "niky"
	user.Update()
	user = QueryByID(user.ID)
	user.Name = "niky"
	user.Update()
	user = QueryByID(user.ID)
	user.Name = "niky"
	user.Update()
}

func BenchmarkUser(b *testing.B) {

	for i := 0; i < b.N; i++ {

		user := QueryByUsername("wolffy")

		if user != nil {
			//b.Log("user != nil")
		} else {
			b.Error("user == nil")
		}
	}
}

func BenchmarkConcurrencyUser(b *testing.B) {

	b.RunParallel(func(pb *testing.PB) {
		for pb.Next() {
			user := QueryByUsername("wolffy")

			if user != nil {
				//b.Log("user != nil")
			} else {
				b.Error("user == nil")
			}
		}
	})
}
