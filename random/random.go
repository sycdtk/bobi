package random

import (
	"crypto/rand"
	"encoding/base64"
	"io"

	"github.com/sycdtk/bobi/md5"
)

//生成ID字串
func UniqueID() string {
	b := make([]byte, 48)

	if _, err := io.ReadFull(rand.Reader, b); err != nil {
		return ""
	}
	return md5.MD5(base64.URLEncoding.EncodeToString(b))
}
