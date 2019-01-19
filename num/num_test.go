package num

import (
	"testing"
)

func TestNewMax(t *testing.T) {

	if NewMax([]int{1, 3, 5}) == int(6) {
		t.Log("NewMax([]int{1, 3, 5}) == 6")
	} else {
		t.Error("NewMax([]int{1, 3, 5}) == 6", NewMax([]int{1, 3, 5}))
	}

	if NewMax([]int{1, 1, 1, 1}) == 2 {
		t.Log("NewMax([]int{1, 1, 1, 1}) == 2")
	} else {
		t.Error("NewMax([]int{1, 1, 1, 1}) == 2", NewMax([]int{1, 1, 1, 1}))
	}

	//空值
	if NewMax([]int{}) == 1 {
		t.Log("NewMax([]int{}) == 1")
	} else {
		t.Error("NewMax([]int{}) == 1", NewMax([]int{}))
	}

	//负数
	if NewMax([]int{-1, -2, -3}) == 1 {
		t.Log("NewMax([]int{}) == 1")
	} else {
		t.Error("NewMax([]int{}) == 1", NewMax([]int{-1, -2, -3}))
	}
}
