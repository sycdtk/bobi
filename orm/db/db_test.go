package db

import (
	"testing"
)

func TestExecute(t *testing.T) {
	Execute(`DROP TABLE bobi_test_aaa;`)

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

	Execute("insert into bobi_test_aaa (id,username,birth,sex,age,xxx,yyy) values ('111','liubei','2019-01-01 00:00:00','1','30','3.1215','4.222');")

	Execute("insert into bobi_test_aaa (id,username,birth,sex,age,xxx,yyy) values (?,?,?,?,?,?,?);", "222", "guanyu", "2018-07-23 00:00:00", true, 34, 234.1111, 33.222)

	Execute("insert into bobi_test_aaa (id,username,birth,sex,age,xxx,yyy) values ('333','zhangfei','2017-01-01 00:00:00','0','44','23.2222','1.1111'),('444','machao','2019-02-25 00:00:00','1','32','77.392','88.444');")

}

func TestExecuteDB(t *testing.T) {

	ExecuteDB("test4", `DROP TABLE bobi_test_aaa;`)

	ExecuteDB("test4", `CREATE TABLE
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

	ExecuteDB("test4", "insert into bobi_test_aaa (id,username,birth,sex,age,xxx,yyy) values ('111','liubei','2019-01-01 00:00:00','1','30','3.1215','4.222');")

	ExecuteDB("test4", "insert into bobi_test_aaa (id,username,birth,sex,age,xxx,yyy) values (?,?,?,?,?,?,?);", "222", "guanyu", "2018-07-23 00:00:00", true, 34, 234.1111, 33.222)

	ExecuteDB("test4", "insert into bobi_test_aaa (id,username,birth,sex,age,xxx,yyy) values ('333','zhangfei','2017-01-01 00:00:00','0','44','23.2222','1.1111'),('444','machao','2019-02-25 00:00:00','1','32','77.392','88.444');")

}

func TestQuery(t *testing.T) {
	Execute(`DROP TABLE bobi_test_aaa;`)

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

	Execute("insert into bobi_test_aaa (id,username,birth,sex,age,xxx,yyy) values ('111','liubei','2019-01-01 00:00:00','1','30','3.1215','4.222');")

	Execute("insert into bobi_test_aaa (id,username,birth,sex,age,xxx,yyy) values (?,?,?,?,?,?,?);", "222", "guanyu", "2018-07-23 00:00:00", true, 34, 234.1111, 33.222)

	Execute("insert into bobi_test_aaa (id,username,birth,sex,age,xxx,yyy) values ('333','zhangfei','2017-01-01 00:00:00','0','44','23.2222','1.1111'),('444','machao','2019-02-25 00:00:00','1','32','77.392','88.444');")

	results := Query("select id,username,birth,sex,xxx,yyy from bobi_test_aaa;")
	for _, row := range results {
		for _, col := range row {
			t.Log(string(col))
		}
		t.Log("==========")
	}

	if len(results) != 4 {
		t.Error("query result number error")
	}
}

func TestQueryDB(t *testing.T) {
	ExecuteDB("test4", `DROP TABLE bobi_test_aaa;`)

	ExecuteDB("test4", `CREATE TABLE
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

	ExecuteDB("test4", "insert into bobi_test_aaa (id,username,birth,sex,age,xxx,yyy) values ('111','liubei','2019-01-01 00:00:00','1','30','3.1215','4.222');")

	ExecuteDB("test4", "insert into bobi_test_aaa (id,username,birth,sex,age,xxx,yyy) values (?,?,?,?,?,?,?);", "222", "guanyu", "2018-07-23 00:00:00", true, 34, 234.1111, 33.222)

	ExecuteDB("test4", "insert into bobi_test_aaa (id,username,birth,sex,age,xxx,yyy) values ('333','zhangfei','2017-01-01 00:00:00','0','44','23.2222','1.1111'),('444','machao','2019-02-25 00:00:00','1','32','77.392','88.444');")

	results := QueryDB("test4", "select id,username,birth,sex,xxx,yyy from bobi_test_aaa;")
	for _, row := range results {
		for _, col := range row {
			t.Log(string(col))
		}
		t.Log("==========")
	}

	if len(results) != 4 {
		t.Error("query result number error")
	}
}

func TestDB(t *testing.T) {

	// results := Query("SELECT id,username FROM test ")

	// for _, row := range results {

	// 	for _, cell := range row {

	// 		if len(string(cell)) > 8 {
	// 		} else {
	// 			t.Log(string(cell))
	// 		}
	// 	}
	// 	t.Log("")
	// }

	// t.Log("----------")
	// Execute("insert into test(id,username) values(3,'liutao'),(4,'zhangfei')")

	// results = Query("SELECT id,username FROM test ")

	// for _, row := range results {

	// 	for _, cell := range row {

	// 		if len(string(cell)) > 8 {
	// 		} else {
	// 			t.Log(string(cell))
	// 		}
	// 	}
	// 	t.Log("")
	// }

	// t.Log("----------")

	// results := QueryDB("test2", "SELECT id,username FROM test1 ")

	// for _, row := range results {

	// 	for _, cell := range row {

	// 		if len(string(cell)) > 8 {
	// 		} else {
	// 			t.Log(string(cell))
	// 		}
	// 	}
	// 	t.Log("")
	// }

	// t.Log("----------")
	// ExecuteDB("test2", "insert into test1(id,username) values(3,'guojia'),(4,'liubei')")

	// //不支持多条删除
	// //ExecuteDB("test2", "delete from test1 where id=3;delete from test1 where id=4;")

	// results = QueryDB("test2", "SELECT id,username FROM test1 ")

	// for _, row := range results {

	// 	for _, cell := range row {

	// 		if len(string(cell)) > 8 {
	// 		} else {
	// 			t.Log(string(cell))
	// 		}
	// 	}
	// 	t.Log("")
	// }
}

func TestTransaction(t *testing.T) {
	tx := BeginTransaction()
	tx.Execute("insert into bobi_test_aaa (id,username,birth,sex,age,xxx,yyy) values ('999','liubei1','2019-01-01 00:00:00','1','30','3.1215','4.222');")
	tx.Execute("insert into bobi_test_aaa (id,username,birth,sex,age,xxx,yyy) values ('888','liubei2','2019-01-01 00:00:00','1','30','3.1215','4.222','ww');")
	tx.Execute("insert into bobi_test_aaa (id,username,birth,sex,age,xxx,yyy) values ('777','liubei3','2019-01-01 00:00:00','1','30','3.1215','4.222');")
	tx.EndTransaction()
}
