package vuser

import (
	"github.com/sycdtk/bobi/logger"
	"github.com/sycdtk/bobi/orm"
)

func (user *User) Save() {
	cols := []string{"id", "name", "description", "gender", "username",
		"password", "email", "telephone", "mobile", "logining", "locked",
		"errornum", "status", "standbyuser"}
	orm.Create([]interface{}{user}, cols)

	if user.Groups != nil && !user.Groups.Empty() {
		for _, group := range user.Groups.Array() {
			orm.Create([]interface{}{NewUserGroup(user.ID, group)}, []string{"id", "userid", "groupid"})
		}
	}
}

func (user *User) Update() {
	cols := []string{"id", "name", "description", "gender", "username",
		"password", "email", "telephone", "mobile", "logining", "locked",
		"errornum", "status", "standbyuser"}
	orm.Update([]interface{}{user}, cols, []string{"id"})

	if user.Groups != nil && !user.Groups.Empty() {
		orm.Delete([]interface{}{NewUserGroup(user.ID, "")}, []string{"userid"})
		for _, group := range user.Groups.Array() {
			orm.Create([]interface{}{NewUserGroup(user.ID, group)}, []string{"userid", "groupid"})
		}
	}
}

func (user *User) Delete() {
	orm.DeleteByID([]interface{}{user})
}

func QueryByUsername(username string) *User {
	cols := []string{"id", "name", "description", "gender", "username",
		"password", "email", "telephone", "mobile", "logining", "locked",
		"errornum", "status", "standbyuser"}

	tn, _ := orm.TableObjExist(&User{})
	results := orm.Query(&User{},
		"select id,name,description,gender,username,password,email, "+
			"telephone,mobile,logining,locked,errornum,status,standbyuser "+
			" from "+tn+" where username=?", cols, username)
	if len(results) > 0 {
		if data, ok := results[0].(*User); ok {
			return data
		}
	}
	return nil
}

func QueryByID(ID string) *User {
	cols := []string{"id", "name", "description", "gender", "username",
		"password", "email", "telephone", "mobile", "logining", "locked",
		"errornum", "status", "standbyuser"}

	tn, _ := orm.TableObjExist(&User{})
	results := orm.Query(&User{},
		"select id,name,description,gender,username,password,email, "+
			"telephone,mobile,logining,locked,errornum,status,standbyuser "+
			" from "+tn+" where id=?", cols, ID)
	if len(results) > 0 {
		if data, ok := results[0].(*User); ok {
			return data
		}
	}
	return nil
}

func init() {
	orm.Register("vuser", func() interface{} { return &User{} })
	if tn, ok := orm.TableObjExist(&User{}); !ok {
		orm.Execute(`CREATE TABLE ` +
			tn +
			`(
				id TEXT,
				name TEXT,
				description TEXT,
				gender TEXT,
				username TEXT,
				password TEXT,
				email TEXT,
				telephone TEXT,
				mobile TEXT,
				logining INTEGER,
				locked INTEGER,
				errornum INTEGER,
				status INTEGER,
				standbyuser  TEXT
		    );`)
		logger.Info("create table:", tn)
	}

	orm.Register("vuser", func() interface{} { return &UserGroup{} })
	if tn, ok := orm.TableObjExist(&UserGroup{}); !ok {
		orm.Execute(`CREATE TABLE ` +
			tn +
			`(
				userid TEXT,
				groupid TEXT
		    );`)
		logger.Info("create table:", tn)
	}

	orm.Register("vuser", func() interface{} { return &UserOrganization{} })
	if tn, ok := orm.TableObjExist(&UserOrganization{}); !ok {
		orm.Execute(`CREATE TABLE ` +
			tn +
			`(
				userid TEXT,
				organizationid TEXT
		    );`)
		logger.Info("create table:", tn)
	}
}
