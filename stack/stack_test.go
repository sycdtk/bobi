package stack

import (
	"testing"
)

func TestStack(t *testing.T) {
	s := NewStack()
	Push(s, "aaa")
	Push(s, "b")
	Push(s, "ww")

	if Top(s) == "ww" {
		t.Log(Top(s), "ww")
	} else {
		t.Fatal(Top(s), "ww")
	}

	if v := Pop(s); v == "ww" {
		t.Log(v, "ww")
	} else {
		t.Fatal(v, "ww")
	}
	Push(s, "qq1")

	if Top(s) == "qq1" {
		t.Log(Top(s), "qq1")
	} else {
		t.Fatal(Top(s), "qq1")
	}

	Pop(s)
	Pop(s)
	if v := Pop(s); v == "aaa" {
		t.Log(v, "aaa")
	} else {
		t.Fatal(v, "aaa")
	}
}

func BenchmarkStack(b *testing.B) {
	for i := 0; i < b.N; i++ {
		s := NewStack()
		Push(s, "aaa")
		Push(s, "b")
		Push(s, "ww")
		Push(s, "qq")
		Pop(s)
		Pop(s)
		Push(s, "b")
		Push(s, "ww")
		Push(s, "b")
		Push(s, "ww")
		Push(s, "b")
		Push(s, "ww")
		Pop(s)
		Pop(s)
		Pop(s)
		Pop(s)
	}
}

func BenchmarkConcurrency(b *testing.B) {
	b.RunParallel(func(pb *testing.PB) {
		for pb.Next() {
			s := NewStack()
			Push(s, "aaa")
			Push(s, "b")
			Push(s, "ww")
			Push(s, "qq")
			Pop(s)
			Pop(s)
			Push(s, "b")
			Push(s, "ww")
			Push(s, "b")
			Push(s, "ww")
			Push(s, "b")
			Push(s, "ww")
			Pop(s)
			Pop(s)
			Pop(s)
			Pop(s)
		}
	})
}
