package workflow

import (
	"log"
	"testing"
)

func ProcessDefineTest(t testing.T) {
	pd := &ProcessDefine{}
	pi := pd.NewInstance()
	for k, _ := range pi.Token.NodeInstances {
		log.Println(k)
	}
}
