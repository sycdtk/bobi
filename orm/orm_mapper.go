package orm

import (
	"github.com/sycdtk/bobi/orm/mapper"
)

func init() {
	mapper.Register(func() interface{} { return &Aaa{} })
}
