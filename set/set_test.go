package set

import (
	"testing"
)

func TestSet(t *testing.T) {
	s := NewSet()

	s.Add("aaa")
	s.Add("bbb")

	if s.Size() == 2 {
		t.Log("length=2")
	} else {
		t.Fatal("Error : length!=2")
	}

	s.Add("ccc")
	s.Add("ddd")
	s.Add("eee")

	if s.Contains("ddd") {
		t.Log("Contains ddd")
	} else {
		t.Fatal("Error : Contains ddd")
	}

	s.Del("eee")
	if s.Contains("eee") {
		t.Fatal("Error : Contains eee")
	} else {
		t.Log("Contains eee")
	}
}

func BenchmarkStack(b *testing.B) {
	for i := 0; i < b.N; i++ {
		s := NewSet()
		s.Add("aaa")
		s.Add("b")
		s.Add("ww")
		s.Add("qq")
		s.Add("aaa")
		s.Del("ww")
		s.Del("qq")
		s.Add("qq")
		s.Add("aaa")

	}
}

func BenchmarkConcurrency(b *testing.B) {
	b.RunParallel(func(pb *testing.PB) {
		for pb.Next() {
			s := NewSet()
			s.Add("aaa")
			s.Add("b")
			s.Add("ww")
			s.Add("qq")
			s.Add("aaa")
			s.Del("ww")
			s.Del("qq")
			s.Add("qq")
			s.Add("aaa")
		}
	})
}
