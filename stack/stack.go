package stack

//简单栈对象
type Stack struct {
	datas []string
}

//压栈
func Push(s *Stack, v string) {
	s.datas = append(s.datas, v)
}

//出栈
func Pop(s *Stack) (r string) {
	if !Empty(s) {
		l := len(s.datas)
		r = s.datas[l-1]
		s.datas = append(s.datas[:l-1])
	}
	return
}

//栈是否为空
func Empty(s *Stack) bool {
	return len(s.datas) == 0
}

//返回栈顶元素
func Top(s *Stack) (r string) {
	if len(s.datas) > 0 {
		r = s.datas[len(s.datas)-1]
	}
	return
}

//新建栈
func NewStack() *Stack {
	return &Stack{}
}
