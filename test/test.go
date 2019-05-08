package test

import (
	"fmt"
	"sync"
)

type Aaa struct {
	Session sync.Map
}

func TMain() {
	a := 1
	switch a {
	case 1:
		fallthrough
	case 2:
		fmt.Println("2")
	case 3:
		fmt.Println("3")
	default:
		fmt.Println("default")
	}
}
