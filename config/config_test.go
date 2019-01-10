package config

import (
	"testing"
)

func TestRead(t *testing.T) {

	if "werqwecxv" != Read("www", "aaa") {
		t.Fatal("aaa=werqwecxv")
	}

	if "twer etrew%%&%*" != Read("zzz", "vv") {
		t.Fatal("vv=twer etrew%%&%*")
	}

	if "c:/go" != Read("default", "path") {
		t.Fatal("path= c:/go")
	}

	if "1.44" != Read("default", "version") {
		t.Fatal("version = 1.44")
	}

	if "wrong" != Read("test", "something") {
		t.Fatal("something  = wrong  #注释1")
	}

	if "666" != Read("test", "num") {
		t.Fatal("num =	666")
	}

	if "" != Read("test", "fdfdfd") {
		t.Fatal("#fdfdfd = fdfdfd    注释整行")
	}

	if "refer" != Read("test", "refer") {
		t.Fatal("refer= refer       //注释3")
	}

	if "DSN=watch1" != Read("default", "dsnName") {
		t.Fatal("dsnName     = DSN=watch1")
	}

	if "wrong" != Read("test", "somet[hi]ng") {
		t.Fatal("wrong     = somet[hi]ng")
	}

	t.Log("success!")
}

func TestLoad(t *testing.T) {
	Load("config1.conf")
	if "11111111" != Read("www", "aaa") {
		t.Fatal("[www]aaa != 11111111")
	}

}
