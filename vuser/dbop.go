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
}

func (user *User) Update() {
	cols := []string{"id", "name", "description", "gender", "username",
		"password", "email", "telephone", "mobile", "logining", "locked",
		"errornum", "status", "standbyuser"}
	orm.Update([]interface{}{user}, cols, []string{"id"})
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
}
