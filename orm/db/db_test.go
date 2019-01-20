package db

// import (
// 	"testing"
// )

// func TestDB(t *testing.T) {

// 	results := Pool.Query("SELECT id,username FROM test ")

// 	for _, row := range results {

// 		for _, cell := range row {

// 			if len(string(cell)) > 8 {
// 			} else {
// 				t.Log(string(cell))
// 			}
// 		}
// 		t.Log("")
// 	}

// 	t.Log("----------")
// 	Pool.Execute("insert into test(id,username) values(3,'liutao'),(4,'zhangfei')")

// 	results = Pool.Query("SELECT id,username FROM test ")

// 	for _, row := range results {

// 		for _, cell := range row {

// 			if len(string(cell)) > 8 {
// 			} else {
// 				t.Log(string(cell))
// 			}
// 		}
// 		t.Log("")
// 	}

// 	t.Log("----------")

// 	results = Pool.QueryDB("test2", "SELECT id,username FROM test1 ")

// 	for _, row := range results {

// 		for _, cell := range row {

// 			if len(string(cell)) > 8 {
// 			} else {
// 				t.Log(string(cell))
// 			}
// 		}
// 		t.Log("")
// 	}

// 	t.Log("----------")
// 	Pool.ExecuteDB("test2", "insert into test1(id,username) values(3,'guojia'),(4,'liubei')")

// 	results = Pool.QueryDB("test2", "SELECT id,username FROM test1 ")

// 	for _, row := range results {

// 		for _, cell := range row {

// 			if len(string(cell)) > 8 {
// 			} else {
// 				t.Log(string(cell))
// 			}
// 		}
// 		t.Log("")
// 	}
// }
