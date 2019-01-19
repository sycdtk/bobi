package structinfo

import (
	"fmt"
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

	fmt.Println(si.Pkg)
	fmt.Println(si.Name)
	for _, f := range si.Fields {
		fmt.Println(f.Name)
		fmt.Println(f.Type)
		for _, ft := range f.Tags {
			fmt.Println(ft.Name, ft.Value)
		}

		fmt.Println("       ", f.Tag("col"))
		fmt.Println(f.Tag("nncol"))

	}
	//	NewStructInfo(MyStruct{})
}
