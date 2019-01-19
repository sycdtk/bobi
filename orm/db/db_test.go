package db

import (
	"testing"
)

func TestDB(t *testing.T) {

	results := Pool.Query("SELECT id,username FROM test ")

	for _, row := range results {

		for _, cell := range row {

			if len(string(cell)) > 8 {
			} else {
				t.Log(string(cell))
			}
		}
		t.Log("")
	}

	results = Pool.QueryDB("test2", "SELECT id,username FROM test1 ")

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
