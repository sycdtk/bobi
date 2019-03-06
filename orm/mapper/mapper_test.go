package mapper

import (
	"testing"
	//"github.com/sycdtk/bobi/logger"
	"github.com/sycdtk/bobi/orm/db"
)

type Aaa struct {
	ID       string `ft:"aaa" ft1:"bb"`
	Username string
	Birth    string `ft:"date"`
	Sex      bool
	Age      int
	XXX      float64
	YYY      float32
	QQQ      []string
}

type Result struct {
	Count int
}

func TestRegister(t *testing.T) {
	Register("test", func() interface{} { return &Aaa{} })

	if structCache.contains("github.com/sycdtk/bobi/orm/mapper@Aaa") {
		t.Log("Test data bobi_test_aaa is register")
	} else {
		t.Error("Test data bobi_test_aaa is not register")
	}
}

func TestCreate(t *testing.T) {
	Register("test", func() interface{} { return &Aaa{} })
	Register("test", func() interface{} { return &Result{} })

	db.Execute("delete from bobi_test_aaa")

	a := &Aaa{ID: "111", Username: "huangzhong", Birth: "2019-01-01 00:00:00", Sex: true, Age: 30, XXX: 23.2222, YYY: 1.1111}
	b := &Aaa{ID: "222", Username: "machao", Birth: "2019-02-25 00:00:00", Sex: true, Age: 32, XXX: 44.392, YYY: 2.444}

	Create([]interface{}{a, b}, []string{"id", "username", "birth", "sex", "age", "xxx", "yyy"})

	Create([]interface{}{&Aaa{ID: "333", Username: "liubei"}}, []string{"id", "username"})

	Create([]interface{}{&Aaa{ID: "444", Username: "caocao"}}, []string{"id", "username"})

	Create([]interface{}{&Aaa{ID: "555", Username: "guanyu"}, &Aaa{ID: "777", Username: "sunquan"}}, []string{"id", "username"})

	Create([]interface{}{&Aaa{ID: "666", Username: "zhangfei", QQQ: []string{"aa", "bb"}}}, []string{"id", "username", "qqq"})

	results := db.Query("select count(id) from bobi_test_aaa")

	dataList := Write(&Result{}, results, []string{"count"})

	if data, ok := dataList[0].(*Result); ok {
		if data.Count == 6 {
			t.Log("Create function success")
		} else {
			t.Error("Create data num error")
		}
	} else {
		t.Error("Create data error")
	}

	db.Execute("delete from bobi_test_aaa")
}

func TestCreateDB(t *testing.T) {
	Register("test", func() interface{} { return &Aaa{} })
	Register("test", func() interface{} { return &Result{} })

	// db.ExecuteDB("test4", `CREATE TABLE
	//    bobi_test_aaa
	//    (
	// 	id TEXT,
	//        username TEXT,
	// 	birth TEXT,
	// 	sex INTEGER,
	// 	age INTEGER,
	// 	xxx REAL,
	// 	yyy REAL
	//    );`)

	db.ExecuteDB("test4", "delete from bobi_test_aaa")

	a := &Aaa{ID: "111", Username: "huangzhong", Birth: "2019-01-01 00:00:00", Sex: true, Age: 30, XXX: 23.2222, YYY: 1.1111}
	b := &Aaa{ID: "222", Username: "machao", Birth: "2019-02-25 00:00:00", Sex: true, Age: 32, XXX: 44.392, YYY: 2.444}

	CreateDB("test4", []interface{}{a, b}, []string{"id", "username", "birth", "sex", "age", "xxx", "yyy"})

	CreateDB("test4", []interface{}{&Aaa{ID: "333", Username: "liubei"}}, []string{"id", "username"})

	CreateDB("test4", []interface{}{&Aaa{ID: "444", Username: "caocao"}}, []string{"id", "username"})

	CreateDB("test4", []interface{}{&Aaa{ID: "555", Username: "guanyu"}, &Aaa{ID: "777", Username: "sunquan"}}, []string{"id", "username"})

	CreateDB("test4", []interface{}{&Aaa{ID: "666", Username: "zhangfei", QQQ: []string{"aa", "bb"}}}, []string{"id", "username", "qqq"})

	results := db.QueryDB("test4", "select count(id) from bobi_test_aaa")

	dataList := Write(&Result{}, results, []string{"count"})

	if data, ok := dataList[0].(*Result); ok {
		if data.Count == 6 {
			t.Log("CreateDB function success")
		} else {
			t.Error("CreateDB data num error")
		}
	} else {
		t.Error("CreateDB data error")
	}

	db.Execute("delete from bobi_test_aaa")
}

func TestDelete(t *testing.T) {
	Register("test", func() interface{} { return &Aaa{} })
	Register("test", func() interface{} { return &Result{} })

	a := &Aaa{ID: "111", Username: "huangzhong", Birth: "2019-01-01 00:00:00", Sex: true, Age: 30, XXX: 23.2222, YYY: 1.1111}
	b := &Aaa{ID: "222", Username: "machao", Birth: "2019-02-25 00:00:00", Sex: true, Age: 32, XXX: 44.392, YYY: 2.444}
	Create([]interface{}{a, b}, []string{"id", "username", "birth", "sex", "age", "xxx", "yyy"})
	Create([]interface{}{&Aaa{ID: "333", Username: "liubei"}}, []string{"id", "username"})
	Create([]interface{}{&Aaa{ID: "444", Username: "caocao"}}, []string{"id", "username"})
	Create([]interface{}{&Aaa{ID: "555", Username: "guanyu"}, &Aaa{ID: "777", Username: "sunquan"}}, []string{"id", "username"})
	Create([]interface{}{&Aaa{ID: "666", Username: "zhangfei", QQQ: []string{"aa", "bb"}}}, []string{"id", "username", "qqq"})

	Delete([]interface{}{&Aaa{ID: "777", Username: "sunquan"}}, []string{"id"})

	Delete([]interface{}{&Aaa{ID: "222", Username: "machao"}}, []string{"username"})

	Delete([]interface{}{&Aaa{ID: "333", Username: "liubei"}}, []string{"id", "username"})

	Delete([]interface{}{&Aaa{ID: "444", Username: "caocao"}, &Aaa{ID: "555", Username: "guanyu"}}, []string{"id", "username"})

	Delete([]interface{}{&Aaa{ID: "111", Username: "huangzhong", Birth: "2019-01-01 00:00:00", Sex: true, Age: 30, XXX: 23.2222, YYY: 1.1111}}, []string{"id", "username", "birth", "sex", "age", "xxx", "yyy"})

	results := db.Query("select count(id) from bobi_test_aaa")

	dataList := Write(&Result{}, results, []string{"count"})

	if data, ok := dataList[0].(*Result); ok {
		if data.Count == 0 {
			t.Log("Delete function success")
		} else {
			t.Error("Delete data num error")
		}
	} else {
		t.Error("Delete data error")
	}
}

func TestDeleteDB(t *testing.T) {
	Register("test", func() interface{} { return &Aaa{} })
	Register("test", func() interface{} { return &Result{} })

	a := &Aaa{ID: "111", Username: "huangzhong", Birth: "2019-01-01 00:00:00", Sex: true, Age: 30, XXX: 23.2222, YYY: 1.1111}
	b := &Aaa{ID: "222", Username: "machao", Birth: "2019-02-25 00:00:00", Sex: true, Age: 32, XXX: 44.392, YYY: 2.444}
	CreateDB("test4", []interface{}{a, b}, []string{"id", "username", "birth", "sex", "age", "xxx", "yyy"})
	CreateDB("test4", []interface{}{&Aaa{ID: "333", Username: "liubei"}}, []string{"id", "username"})
	CreateDB("test4", []interface{}{&Aaa{ID: "444", Username: "caocao"}}, []string{"id", "username"})
	CreateDB("test4", []interface{}{&Aaa{ID: "555", Username: "guanyu"}, &Aaa{ID: "777", Username: "sunquan"}}, []string{"id", "username"})
	CreateDB("test4", []interface{}{&Aaa{ID: "666", Username: "zhangfei", QQQ: []string{"aa", "bb"}}}, []string{"id", "username", "qqq"})

	DeleteDB("test4", []interface{}{&Aaa{ID: "777", Username: "sunquan"}}, []string{"id"})

	DeleteDB("test4", []interface{}{&Aaa{ID: "222", Username: "machao"}}, []string{"username"})

	DeleteDB("test4", []interface{}{&Aaa{ID: "333", Username: "liubei"}}, []string{"id", "username"})

	DeleteDB("test4", []interface{}{&Aaa{ID: "444", Username: "caocao"}, &Aaa{ID: "555", Username: "guanyu"}}, []string{"id", "username"})

	DeleteDB("test4", []interface{}{&Aaa{ID: "111", Username: "huangzhong", Birth: "2019-01-01 00:00:00", Sex: true, Age: 30, XXX: 23.2222, YYY: 1.1111}}, []string{"id", "username", "birth", "sex", "age", "xxx", "yyy"})

	results := db.QueryDB("test4", "select count(id) from bobi_test_aaa")

	dataList := Write(&Result{}, results, []string{"count"})

	if data, ok := dataList[0].(*Result); ok {
		if data.Count == 0 {
			t.Log("DeleteDB function success")
		} else {
			t.Error("DeleteDB data num error")
		}
	} else {
		t.Error("DeleteDB data error")
	}
}

func TestDeleteByID(t *testing.T) {
	Register("test", func() interface{} { return &Aaa{} })
	Register("test", func() interface{} { return &Result{} })

	a := &Aaa{ID: "111", Username: "huangzhong", Birth: "2019-01-01 00:00:00", Sex: true, Age: 30, XXX: 23.2222, YYY: 1.1111}
	b := &Aaa{ID: "222", Username: "machao", Birth: "2019-02-25 00:00:00", Sex: true, Age: 32, XXX: 44.392, YYY: 2.444}
	Create([]interface{}{a, b}, []string{"id", "username", "birth", "sex", "age", "xxx", "yyy"})
	Create([]interface{}{&Aaa{ID: "333", Username: "liubei"}}, []string{"id", "username"})
	Create([]interface{}{&Aaa{ID: "444", Username: "caocao"}}, []string{"id", "username"})
	Create([]interface{}{&Aaa{ID: "555", Username: "guanyu"}, &Aaa{ID: "777", Username: "sunquan"}}, []string{"id", "username"})
	Create([]interface{}{&Aaa{ID: "666", Username: "zhangfei", QQQ: []string{"aa", "bb"}}}, []string{"id", "username", "qqq"})

	DeleteByID([]interface{}{&Aaa{ID: "111", Username: "huangzhong"}})

	DeleteByID([]interface{}{&Aaa{ID: "444"}})

	DeleteByID([]interface{}{&Aaa{ID: "666", Username: "zhangfei"}, &Aaa{ID: "777", Username: "sunquan"}})

	results := db.Query("select count(id) from bobi_test_aaa")

	dataList := Write(&Result{}, results, []string{"count"})

	if data, ok := dataList[0].(*Result); ok {
		if data.Count == 3 {
			t.Log("DeleteByID function success")
		} else {
			t.Error("DeleteByID data num error")
		}
	} else {
		t.Error("DeleteByID data error")
	}

	db.Execute("delete from bobi_test_aaa")

}

func TestDeleteByIDDB(t *testing.T) {
	Register("test", func() interface{} { return &Aaa{} })
	Register("test", func() interface{} { return &Result{} })

	a := &Aaa{ID: "111", Username: "huangzhong", Birth: "2019-01-01 00:00:00", Sex: true, Age: 30, XXX: 23.2222, YYY: 1.1111}
	b := &Aaa{ID: "222", Username: "machao", Birth: "2019-02-25 00:00:00", Sex: true, Age: 32, XXX: 44.392, YYY: 2.444}
	CreateDB("test4", []interface{}{a, b}, []string{"id", "username", "birth", "sex", "age", "xxx", "yyy"})
	CreateDB("test4", []interface{}{&Aaa{ID: "333", Username: "liubei"}}, []string{"id", "username"})
	CreateDB("test4", []interface{}{&Aaa{ID: "444", Username: "caocao"}}, []string{"id", "username"})
	CreateDB("test4", []interface{}{&Aaa{ID: "555", Username: "guanyu"}, &Aaa{ID: "777", Username: "sunquan"}}, []string{"id", "username"})
	CreateDB("test4", []interface{}{&Aaa{ID: "666", Username: "zhangfei", QQQ: []string{"aa", "bb"}}}, []string{"id", "username", "qqq"})

	DeleteByIDDB("test4", []interface{}{&Aaa{ID: "111", Username: "huangzhong"}})

	DeleteByIDDB("test4", []interface{}{&Aaa{ID: "444"}})

	DeleteByIDDB("test4", []interface{}{&Aaa{ID: "666", Username: "zhangfei"}, &Aaa{ID: "777", Username: "sunquan"}})

	results := db.QueryDB("test4", "select count(id) from bobi_test_aaa")

	dataList := Write(&Result{}, results, []string{"count"})

	if data, ok := dataList[0].(*Result); ok {
		if data.Count == 3 {
			t.Log("DeleteByIDDB function success")
		} else {
			t.Error("DeleteByIDDB data num error")
		}
	} else {
		t.Error("DeleteByIDDB data error")
	}

	db.ExecuteDB("test4", "delete from bobi_test_aaa")

}

func TestUpdate(t *testing.T) {
	Register("test", func() interface{} { return &Aaa{} })
	Register("test", func() interface{} { return &Result{} })

	a := &Aaa{ID: "111", Username: "huangzhong", Birth: "2019-01-01 00:00:00", Sex: true, Age: 30, XXX: 23.2222, YYY: 1.1111}
	b := &Aaa{ID: "222", Username: "machao", Birth: "2019-02-25 00:00:00", Sex: true, Age: 32, XXX: 44.392, YYY: 2.444}
	Create([]interface{}{a, b}, []string{"id", "username", "birth", "sex", "age", "xxx", "yyy"})
	Create([]interface{}{&Aaa{ID: "333", Username: "liubei"}}, []string{"id", "username"})
	Create([]interface{}{&Aaa{ID: "444", Username: "caocao"}}, []string{"id", "username"})
	Create([]interface{}{&Aaa{ID: "555", Username: "guanyu"}, &Aaa{ID: "777", Username: "sunquan"}}, []string{"id", "username"})
	Create([]interface{}{&Aaa{ID: "666", Username: "zhangfei", QQQ: []string{"aa", "bb"}}}, []string{"id", "username", "qqq"})

	Update([]interface{}{&Aaa{ID: "111", Username: "zhugeliang"}}, []string{"username"}, []string{"id"})

	results := db.Query("select count(id) from bobi_test_aaa where username='zhugeliang'")

	dataList := Write(&Result{}, results, []string{"count"})

	if data, ok := dataList[0].(*Result); ok {
		if data.Count == 1 {
			t.Log("Update function success")
		} else {
			t.Error("Update data num error")
		}
	} else {
		t.Error("Update data error")
	}

	Update([]interface{}{&Aaa{ID: "222", Username: "zhouyu"}, &Aaa{ID: "333", Username: "diaochan"}}, []string{"username"}, []string{"id"})

	results = db.Query("select count(id) from bobi_test_aaa where username='zhouyu' or username='diaochan'")

	dataList = Write(&Result{}, results, []string{"count"})

	if data, ok := dataList[0].(*Result); ok {
		if data.Count == 2 {
			t.Log("Update function success")
		} else {
			t.Error("Update data num error")
		}
	} else {
		t.Error("Update data error")
	}

	Update([]interface{}{&Aaa{ID: "777", Username: "guojia", Birth: "2018-01-01 00:00:00", Sex: false, Age: 20, XXX: 11.1111, YYY: 22.2222}}, []string{"id", "username", "birth", "sex", "age", "xxx", "yyy"}, []string{"id"})

	results = db.Query("select id,username,birth,sex,age,xxx,yyy from bobi_test_aaa where id='777'")

	dataList = Write(&Aaa{}, results, []string{"id", "username", "birth", "sex", "age", "xxx", "yyy"})

	if data, ok := dataList[0].(*Aaa); ok {
		if data.ID == "777" {
			t.Log("Update function success")
		} else {
			t.Error("Update data num error")
		}

		if data.Username == "guojia" {
			t.Log("Update function success")
		} else {
			t.Error("Update data num error")
		}

		if data.Birth == "2018-01-01 00:00:00" {
			t.Log("Update function success")
		} else {
			t.Error("Update data num error")
		}

		if data.Sex == false {
			t.Log("Update function success")
		} else {
			t.Error("Update data num error")
		}

		if data.Age == 20 {
			t.Log("Update function success")
		} else {
			t.Error("Update data num error")
		}

		if data.XXX == 11.1111 {
			t.Log("Update function success")
		} else {
			t.Error("Update data num error")
		}

		if data.YYY == 22.2222 {
			t.Log("Update function success")
		} else {
			t.Error("Update data num error")
		}
	} else {
		t.Error("Update data error")
	}

	db.Execute("delete from bobi_test_aaa")

}

func TestUpdateDB(t *testing.T) {
	Register("test", func() interface{} { return &Aaa{} })
	Register("test", func() interface{} { return &Result{} })

	a := &Aaa{ID: "111", Username: "huangzhong", Birth: "2019-01-01 00:00:00", Sex: true, Age: 30, XXX: 23.2222, YYY: 1.1111}
	b := &Aaa{ID: "222", Username: "machao", Birth: "2019-02-25 00:00:00", Sex: true, Age: 32, XXX: 44.392, YYY: 2.444}
	CreateDB("test4", []interface{}{a, b}, []string{"id", "username", "birth", "sex", "age", "xxx", "yyy"})
	CreateDB("test4", []interface{}{&Aaa{ID: "333", Username: "liubei"}}, []string{"id", "username"})
	CreateDB("test4", []interface{}{&Aaa{ID: "444", Username: "caocao"}}, []string{"id", "username"})
	CreateDB("test4", []interface{}{&Aaa{ID: "555", Username: "guanyu"}, &Aaa{ID: "777", Username: "sunquan"}}, []string{"id", "username"})
	CreateDB("test4", []interface{}{&Aaa{ID: "666", Username: "zhangfei", QQQ: []string{"aa", "bb"}}}, []string{"id", "username", "qqq"})

	UpdateDB("test4", []interface{}{&Aaa{ID: "111", Username: "zhugeliang"}}, []string{"username"}, []string{"id"})

	results := db.QueryDB("test4", "select count(id) from bobi_test_aaa where username='zhugeliang'")

	dataList := Write(&Result{}, results, []string{"count"})

	if data, ok := dataList[0].(*Result); ok {
		if data.Count == 1 {
			t.Log("Update function success")
		} else {
			t.Error("Update data num error")
		}
	} else {
		t.Error("Update data error")
	}

	UpdateDB("test4", []interface{}{&Aaa{ID: "222", Username: "zhouyu"}, &Aaa{ID: "333", Username: "diaochan"}}, []string{"username"}, []string{"id"})

	results = db.QueryDB("test4", "select count(id) from bobi_test_aaa where username='zhouyu' or username='diaochan'")

	dataList = Write(&Result{}, results, []string{"count"})

	if data, ok := dataList[0].(*Result); ok {
		if data.Count == 2 {
			t.Log("Update function success")
		} else {
			t.Error("Update data num error")
		}
	} else {
		t.Error("Update data error")
	}

	UpdateDB("test4", []interface{}{&Aaa{ID: "777", Username: "guojia", Birth: "2018-01-01 00:00:00", Sex: false, Age: 20, XXX: 11.1111, YYY: 22.2222}}, []string{"id", "username", "birth", "sex", "age", "xxx", "yyy"}, []string{"id"})

	results = db.QueryDB("test4", "select id,username,birth,sex,age,xxx,yyy from bobi_test_aaa where id='777'")

	dataList = Write(&Aaa{}, results, []string{"id", "username", "birth", "sex", "age", "xxx", "yyy"})

	if data, ok := dataList[0].(*Aaa); ok {
		if data.ID == "777" {
			t.Log("Update function success")
		} else {
			t.Error("Update data num error")
		}

		if data.Username == "guojia" {
			t.Log("Update function success")
		} else {
			t.Error("Update data num error")
		}

		if data.Birth == "2018-01-01 00:00:00" {
			t.Log("Update function success")
		} else {
			t.Error("Update data num error")
		}

		if data.Sex == false {
			t.Log("Update function success")
		} else {
			t.Error("Update data num error")
		}

		if data.Age == 20 {
			t.Log("Update function success")
		} else {
			t.Error("Update data num error")
		}

		if data.XXX == 11.1111 {
			t.Log("Update function success")
		} else {
			t.Error("Update data num error")
		}

		if data.YYY == 22.2222 {
			t.Log("Update function success")
		} else {
			t.Error("Update data num error")
		}
	} else {
		t.Error("Update data error")
	}

	db.ExecuteDB("test4", "delete from bobi_test_aaa")

}

func TestWrite(t *testing.T) {

	Register("test", func() interface{} { return &Aaa{} })
	Register("test", func() interface{} { return &Result{} })

	a := &Aaa{ID: "111", Username: "huangzhong", Birth: "2019-01-01 00:00:00", Sex: false, Age: 30, XXX: 23.2222, YYY: 1.1111}
	b := &Aaa{ID: "222", Username: "machao", Birth: "2019-02-25 00:00:00", Sex: true, Age: 32, XXX: 44.392, YYY: 2.444}

	Create([]interface{}{a, b}, []string{"id", "username", "birth", "sex", "age", "xxx", "yyy"})

	results := db.Query("SELECT id,username,birth,age,sex,xxx,yyy FROM bobi_test_aaa ")

	dataList := Write(&Aaa{}, results, []string{"id", "username", "birth", "age", "sex", "xxx", "yyy"})

	if data, ok := dataList[0].(*Aaa); ok {
		if data.ID == "111" {
			t.Log("Write function success")
		} else {
			t.Error("Write data num error")
		}

		if data.Username == "huangzhong" {
			t.Log("Write function success")
		} else {
			t.Error("Write data num error")
		}

		if data.Birth == "2019-01-01 00:00:00" {
			t.Log("Write function success")
		} else {
			t.Error("Write data num error")
		}

		if data.Sex == false {
			t.Log("Write function success")
		} else {
			t.Error("Write data num error")
		}

		if data.Age == 30 {
			t.Log("Write function success")
		} else {
			t.Error("Write data num error")
		}

		if data.XXX == 23.2222 {
			t.Log("Write function success")
		} else {
			t.Error("Write data num error")
		}

		if data.YYY == 1.1111 {
			t.Log("Write function success")
		} else {
			t.Error("Write data num error")
		}
	} else {
		t.Error("Write data error")
	}

	if data, ok := dataList[1].(*Aaa); ok {
		if data.ID == "222" {
			t.Log("Write function success")
		} else {
			t.Error("Write data num error")
		}

		if data.Username == "machao" {
			t.Log("Write function success")
		} else {
			t.Error("Write data num error")
		}

		if data.Birth == "2019-02-25 00:00:00" {
			t.Log("Write function success")
		} else {
			t.Error("Write data num error")
		}

		if data.Sex == true {
			t.Log("Write function success")
		} else {
			t.Error("Write data num error")
		}

		if data.Age == 32 {
			t.Log("Write function success")
		} else {
			t.Error("Write data num error")
		}

		if data.XXX == 44.392 {
			t.Log("Write function success")
		} else {
			t.Error("Write data num error")
		}

		if data.YYY == 2.444 {
			t.Log("Write function success")
		} else {
			t.Error("Write data num error")
		}
	} else {
		t.Error("Write data error")
	}

	db.Execute("delete from bobi_test_aaa")

}

func TestTableName(t *testing.T) {
	Register("test", func() interface{} { return &Aaa{} })
	Register("test", func() interface{} { return &Result{} })

	if TableName(&Aaa{}) == "bobi_test_aaa" {
		t.Log("TableName Aaa is ok")
	} else {
		t.Error("TableName Aaa is error")
	}

	if TableName(&Result{}) == "bobi_test_result" {
		t.Log("TableName Result is ok")
	} else {
		t.Error("TableName Result is error")
	}
}
