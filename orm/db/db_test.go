package db

import (
	"testing"
)

func TestDB(t *testing.T) {

	results := Pool.Query("SELECT id,username FROM users")

	for _, row := range results {

		for _, cell := range row {

			if len(string(cell)) > 8 {
			} else {
				fmt.Println(string(cell))
			}
		}
		fmt.Println("")
	}
}
