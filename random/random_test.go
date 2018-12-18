package random

import (
	"testing"
)

func TestUniqueID(t *testing.T) {
	IDs := map[string]string{}

	for i := 0; i < 100000; i++ {
		IDs[UniqueID()] = "wolffy"
	}

	if len(IDs) != 100000 {
		t.Error(len(IDs), "存在相等值")
	} else {
		t.Log("未存在相等值")
	}
}
