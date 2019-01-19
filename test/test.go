package test

import (
	"log"
	"strings"
)

func TMain() {
	s := "[asdfa[sdf]]"
	n1 := strings.Index(s, "[")
	n2 := strings.LastIndex(s, "]")

	log.Println(n1, n2, len(s))

}
