package test

import (
	"fmt"
	"regexp"
	"strings"
)

func TMain() {
	a := "/mofy/login/11111111111111/bbbb/ccc"
	b := "/mofy/login/{a}/{b}"
	parse(a, b)
}

func parseURL(a, b string) {
	data := map[string]string{}
	path := strings.Split(a, "/")
	uri := strings.Split(b, "/")
	if len(path) != len(uri) {
		fmt.Println("err")
	}
	for i, _ := range uri {
		if path[i] != uri[i] {
			params := regexp.MustCompile("{(.*)}").FindStringSubmatch(uri[i])
			if len(params) == 2 {
				data[params[1]] = path[i]
			}
		}
	}

	for k, v := range data {
		fmt.Println(k, v)
	}
}
