package ddm

import (
	"testing"
)

type MyStruct struct {
	X int
	Y int
}

func TestDefine(t *testing.T) {
	RegisterDataDefined("person", [][2]string{{"name", "string"}, {"age", "float32"}, {"ms", "oo"}})

	//	p1 := NewDataModel("person")
	//	p1.Set("name", "lirui")
	//	p1.Set("age", float64(23))
	//	p1.Set("ms", &MyStruct{X: 1, Y: 10})

	//	t.Log("a", p1.GetString("name"))

	//	t.Log("b", p1.GetFloat64("age"))

	//	t.Log("c", p1.GetFloat64("yy"))

	//	if msx, ok := p1.Get("ms").(*MyStruct); ok {
	//		t.Log(msx.X, msx.Y)
	//	}

}

func BenchmarkDefine(b *testing.B) {

	RegisterDataDefined("person", [][2]string{{"name", "string"}, {"age", "float32"}, {"ms", "oo"}})

	for i := 0; i < b.N; i++ {
		p1 := NewDataModel("person")

		p1.Set("name", "lirui")
		p1.Set("name", "lirui")
		p1.Set("name", "lirui")
		p1.Set("name", "lirui")
		p1.Set("name", "lirui")
		p1.Set("name", "lirui")
		p1.Set("name", "lirui")
		p1.Set("age", float64(23))
		p1.Set("age", float64(39))
		p1.Set("age", float64(100))
		p1.Set("ms", &MyStruct{X: 1, Y: 10})

		p1.GetString("name")

		p1.GetFloat64("age")

		p1.GetFloat64("yy")

		//		Set(p1, "name", "lirui")
		//		Set(p1, "name", "lirui")
		//		Set(p1, "name", "lirui")
		//		Set(p1, "name", "lirui")
		//		Set(p1, "name", "lirui")
		//		Set(p1, "name", "lirui")
		//		Set(p1, "name", "lirui")
		//		Set(p1, "age", float64(23))
		//		Set(p1, "age", float64(39))
		//		Set(p1, "age", float64(100))
		//		Set(p1, "ms", &MyStruct{X: 1, Y: 10})

		//		GetString(p1, "name")

		//		GetFloat64(p1, "age")

		//		GetFloat64(p1, "yy")

	}
}

func BenchmarkDefineP(b *testing.B) {
	RegisterDataDefined("person", [][2]string{{"name", "string"}, {"age", "float32"}, {"ms", "oo"}})

	b.RunParallel(func(pb *testing.PB) {
		for pb.Next() {
			p1 := NewDataModel("person")
			p1.Set("name", "lirui")
			p1.Set("name", "lirui")
			p1.Set("name", "lirui")
			p1.Set("name", "lirui")
			p1.Set("name", "lirui")
			p1.Set("name", "lirui")
			p1.Set("name", "lirui")
			p1.Set("age", float64(23))
			p1.Set("age", float64(39))
			p1.Set("age", float64(100))
			p1.Set("ms", &MyStruct{X: 1, Y: 10})

			p1.GetString("name")

			p1.GetFloat64("age")

			p1.GetFloat64("yy")

			//			Set(p1, "name", "lirui")
			//			Set(p1, "name", "lirui")
			//			Set(p1, "name", "lirui")
			//			Set(p1, "name", "lirui")
			//			Set(p1, "name", "lirui")
			//			Set(p1, "name", "lirui")
			//			Set(p1, "name", "lirui")
			//			Set(p1, "age", float64(23))
			//			Set(p1, "age", float64(39))
			//			Set(p1, "age", float64(100))
			//			Set(p1, "ms", &MyStruct{X: 1, Y: 10})

			//			GetString(p1, "name")

			//			GetFloat64(p1, "age")

			//			GetFloat64(p1, "yy")

		}
	})
}
