package db

import (
	"testing"
)

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

	t.Log("----------")

	results := QueryDB("test2", "SELECT id,username FROM test1 ")

	for _, row := range results {

		for _, cell := range row {

			if len(string(cell)) > 8 {
			} else {
				t.Log(string(cell))
			}
		}
		t.Log("")
	}

	t.Log("----------")
	ExecuteDB("test2", "insert into test1(id,username) values(3,'guojia'),(4,'liubei')")

	//不支持多条删除
	//ExecuteDB("test2", "delete from test1 where id=3;delete from test1 where id=4;")

	results = QueryDB("test2", "SELECT id,username FROM test1 ")

	for _, row := range results {

		for _, cell := range row {

			if len(string(cell)) > 8 {
			} else {
				t.Log(string(cell))
			}
		}
		t.Log("")
	}
}
