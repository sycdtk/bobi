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
func (set *Set) String() string {
	s := ""
	for k, _ := range set.datas {
		s = s + "," + k
	}
	if len(s) > 0 {
		return strings.TrimPrefix(s, ",")
	}
	return s
}

//set载入数据字符串，字符串以","分割
func (set *Set) Load(str string) *Set {
	for _, data := range strings.Split(str, ",") {
		set.datas[data] = true
	}
	return set
}

//清空数据
func (set *Set) Clear() {
	set.datas = map[string]bool{}
}

//判断是否为空
func (set *Set) Empty() bool {
	if len(set.datas) > 0 {
		return false
	}
	return true
}

//返回数组
func (set *Set) Array() []string {
	a := []string{}
	for k, _ := range set.datas {
		a = append(a, k)
	}
	return a
}

func NewSet() *Set {
	return &Set{datas: map[string]bool{}}
}
