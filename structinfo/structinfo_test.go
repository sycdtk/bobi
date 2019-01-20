package structinfo

import (
	"testing"
	"time"
)

type MyStruct struct {
	Id    int       `col:"ID" type:"int" long:"8" PK:"true"`
	Name  string    `col:"NAME" type:"string" long:"20" FK:"MyStruct.Id"`
	Birth time.Time `col:"BIRTH" type:"time"`
}

func TestNewStructInfo(t *testing.T) {
	si := NewStructInfo(&MyStruct{})

	t.Log(si.Pkg)
	t.Log(si.Name)
	for _, f := range si.Fields {
		t.Log(f.Name)
		t.Log(f.Type)
		for _, ft := range f.Tags {
			t.Log(ft.Name, ft.Value)
		}

		t.Log("       ", f.Tag("col"))
		t.Log(f.Tag("nncol"))

	}
	//	NewStructInfo(MyStruct{})
}
