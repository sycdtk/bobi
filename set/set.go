package set

import (
	"strings"
)

type Set struct {
	datas map[string]bool
}

func (set *Set) Add(data string) {
	set.datas[data] = true
}

func (set *Set) Del(data string) {
	delete(set.datas, data)
}

func (set *Set) Contains(data string) bool {
	if _, ok := set.datas[data]; ok {
		return true
	}
	return false
}

func (set *Set) Size() int {
	return len(set.datas)
}

//返回字符串集合，以英文逗号分隔
func (set *Set) ToString() string {
	s := ""
	for k, _ := range set.datas {
		s = s + "," + k
	}
	if len(s) > 0 {
		return strings.TrimPrefix(s, ",")
	}
	return s
}

func NewSet() *Set {
	return &Set{datas: map[string]bool{}}
}
