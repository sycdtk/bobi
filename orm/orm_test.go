package orm

import (
	"testing"
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

func (aaa *Aaa) Save() {
	cols := []string{"id", "username", "birth", "sex", "age", "xxx", "yyy"}
	Create([]interface{}{aaa}, cols)
}

func (aaa *Aaa) Update() {
	Update([]interface{}{aaa}, []string{"username", "birth", "sex", "age", "xxx", "yyy"}, []string{"id"})

}

func (aaa *Aaa) Delete() {
	if len(aaa.ID) > 0 {
		DeleteByID([]interface{}{aaa})
	}
}

func GetByID(ID string) *Aaa {
	finalDatas := []*Aaa{}
	cols := []string{"id", "username", "birth", "sex", "age", "xxx", "yyy"}
	results := Query(&Aaa{}, "select id,username,birth,sex,age,xxx,yyy from bobi_test_aaa", cols)

	for _, result := range results {
		if data, ok := result.(*Aaa); ok {
			finalDatas = append(finalDatas, data)
		}
	}

	if len(finalDatas) > 0 {
		return finalDatas[0]
	}
	return nil
}

func TestOrm(t *testing.T) {
	Register("test", func() interface{} { return &Aaa{} })

	if TableExist("bobi_test_aaa") {
		Execute("DROP TABLE bobi_test_aaa;")
	}
	Execute(`CREATE TABLE
		    bobi_test_aaa
		    (
				id TEXT,
				username TEXT,
				birth TEXT,
				sex INTEGER,
				age INTEGER,
				xxx REAL,
				yyy REAL
		    );`)

	a := &Aaa{ID: "10001", Username: "guanyu", Birth: "2019-02-25 00:32:00", Sex: true, Age: 15, XXX: 1.0021, YYY: 3.2213}
	a.Save()

	b := GetByID("10001")

	if b.ID == "10001" {
		if b.Username == "guanyu" {
			t.Log("Write function success")
		} else {
			t.Error("Write data num error")
		}

		if b.Birth == "2019-02-25 00:32:00" {
			t.Log("Write function success")
		} else {
			t.Error("Write data num error")
		}

		if b.Sex == true {
			t.Log("Write function success")
		} else {
			t.Error("Write data num error")
		}

		if b.Age == 15 {
			t.Log("Write function success")
		} else {
			t.Error("Write data num error")
		}

		if b.XXX == 1.0021 {
			t.Log("Write function success")
		} else {
			t.Error("Write data num error")
		}

		if b.YYY == 3.2213 {
			t.Log("Write function success")
		} else {
			t.Error("Write data num error")
		}
	}

	a.Username = "guanyu111"
	a.Update()

	b = GetByID("10001")

	if b.ID == "10001" {
		if b.Username == "guanyu111" {
			t.Log("Write function success")
		} else {
			t.Error("Write data num error")
		}

		if b.Birth == "2019-02-25 00:32:00" {
			t.Log("Write function success")
		} else {
			t.Error("Write data num error")
		}

		if b.Sex == true {
			t.Log("Write function success")
		} else {
			t.Error("Write data num error")
		}

		if b.Age == 15 {
			t.Log("Write function success")
		} else {
			t.Error("Write data num error")
		}

		if b.XXX == 1.0021 {
			t.Log("Write function success")
		} else {
			t.Error("Write data num error")
		}

		if b.YYY == 3.2213 {
			t.Log("Write function success")
		} else {
			t.Error("Write data num error")
		}
	}

	a.Delete()

	b = GetByID("10001")

	if b == nil {
		t.Log("Delete function success")
	} else {
		t.Error("Delete function error")
	}

}

func TestBaseOperation(t *testing.T) {

	Register("test", func() interface{} { return &Aaa{} })

	if TableExist("bobi_test_aaa") {
		Execute("DROP TABLE bobi_test_aaa;")
	}
	Execute(`CREATE TABLE
		    bobi_test_aaa
		    (
				id TEXT,
				username TEXT,
				birth TEXT,
				sex INTEGER,
				age INTEGER,
				xxx REAL,
				yyy REAL
		    );`)

	a := &Aaa{ID: "10001", Username: "guanyu", Birth: "2019-02-25 00:32:00", Sex: true, Age: 15, XXX: 1.0021, YYY: 3.2213}
	b := &Aaa{ID: "10002", Username: "zhangfei", Birth: "2019-02-26 00:32:00", Sex: true, Age: 18, XXX: 2.0021, YYY: 45.2213}
	c := &Aaa{ID: "10003", Username: "liubei", Birth: "2019-02-27 00:32:00", Sex: false, Age: 31, XXX: 3.0021, YYY: 66.2213}

	cols := []string{"id", "username", "birth", "sex", "age", "xxx", "yyy"}

	Create([]interface{}{a, b, c}, cols)

	finalDatas := []*Aaa{}
	results := Query(&Aaa{}, "select id,username,birth,sex,age,xxx,yyy from bobi_test_aaa", cols)

	for _, result := range results {
		if data, ok := result.(*Aaa); ok {
			finalDatas = append(finalDatas, data)
		}
	}

	for _, data := range finalDatas {
		if data.ID == "10001" {
			if data.Username == "guanyu" {
				t.Log("Write function success")
			} else {
				t.Error("Write data num error")
			}

			if data.Birth == "2019-02-25 00:32:00" {
				t.Log("Write function success")
			} else {
				t.Error("Write data num error")
			}

			if data.Sex == true {
				t.Log("Write function success")
			} else {
				t.Error("Write data num error")
			}

			if data.Age == 15 {
				t.Log("Write function success")
			} else {
				t.Error("Write data num error")
			}

			if data.XXX == 1.0021 {
				t.Log("Write function success")
			} else {
				t.Error("Write data num error")
			}

			if data.YYY == 3.2213 {
				t.Log("Write function success")
			} else {
				t.Error("Write data num error")
			}
		}

		if data.ID == "10002" {
			if data.Username == "zhangfei" {
				t.Log("Write function success")
			} else {
				t.Error("Write data num error")
			}

			if data.Birth == "2019-02-26 00:32:00" {
				t.Log("Write function success")
			} else {
				t.Error("Write data num error")
			}

			if data.Sex == true {
				t.Log("Write function success")
			} else {
				t.Error("Write data num error")
			}

			if data.Age == 18 {
				t.Log("Write function success")
			} else {
				t.Error("Write data num error")
			}

			if data.XXX == 2.0021 {
				t.Log("Write function success")
			} else {
				t.Error("Write data num error")
			}

			if data.YYY == 45.2213 {
				t.Log("Write function success")
			} else {
				t.Error("Write data num error")
			}
		}

		if data.ID == "10003" {
			if data.Username == "liubei" {
				t.Log("Write function success")
			} else {
				t.Error("Write data num error")
			}

			if data.Birth == "2019-02-27 00:32:00" {
				t.Log("Write function success")
			} else {
				t.Error("Write data num error")
			}

			if data.Sex == false {
				t.Log("Write function success")
			} else {
				t.Error("Write data num error")
			}

			if data.Age == 31 {
				t.Log("Write function success")
			} else {
				t.Error("Write data num error")
			}

			if data.XXX == 3.0021 {
				t.Log("Write function success")
			} else {
				t.Error("Write data num error")
			}

			if data.YYY == 66.2213 {
				t.Log("Write function success")
			} else {
				t.Error("Write data num error")
			}
		}
	}
}

func TestTableExist(t *testing.T) {

	if TableExist("bobi") {
		t.Log("table bobi exist")
	} else {
		t.Error("table bobi is not exist")
	}

	if !TableExist("bobo") {
		t.Log("table bobo is not exist")
	} else {
		t.Error("table bobo exist")
	}

	// ExecuteDB("test4", `CREATE TABLE
	//    bobi_test_bbb
	//    (
	// 	id TEXT,
	//        username TEXT,
	// 	birth TEXT,
	// 	sex INTEGER,
	// 	age INTEGER,
	// 	xxx REAL,
	// 	yyy REAL
	//    );`)

	if TableExistDB("test4", "bobi_test_bbb") {
		t.Log("db test4 : table bobi_test_bbb is not exist")
	} else {
		t.Error("db test4 : table bobi_test_bbb exist")
	}

	if !TableExistDB("test4", "bobi_test_qqq") {
		t.Log("db test4 : table bobi_test_qqq is not exist")
	} else {
		t.Error("db test4 : table bobi_test_qqq exist")
	}

}

func TestTransaction(t *testing.T) {

}
