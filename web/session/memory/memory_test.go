package memory

import (
	"crypto/rand"
	"encoding/base64"
	"io"
	"testing"
)

func TestMemProvider(t *testing.T) {
	memProvider := NewMemProvider()

	bs := make([]byte, 32)
	if _, err := io.ReadFull(rand.Reader, bs); err != nil {
		t.Error("create session ID fail!")
	}

	sessionID := base64.URLEncoding.EncodeToString(bs)
	memProvider.Init(sessionID)

	if memProvider.Check(sessionID) {
		t.Log("check session ID sucess!")
	} else {
		t.Error("check session ID fail!")
	}

	session, _ := memProvider.Read(sessionID)
	session.Set("name", "bobi")

	if "bobi" == session.Get("name") {
		t.Log("check session value sucess!")
	} else {
		t.Error("check session value fail!")
	}

	session.Set("name", "wolffy")
	if "wolffy" == session.Get("name") {
		t.Log("check session value sucess!")
	} else {
		t.Error("check session value fail!")
	}

	session.Del("name")
	if nil == session.Get("name") {
		t.Log("check session value sucess!")
	} else {
		t.Error("check session value fail!")
	}

	if sessionID == session.ID() {
		t.Log("check session value sucess!")
	} else {
		t.Error("check session value fail!")
	}

	memProvider.Destroy(sessionID)

	session1, _ := memProvider.Read(sessionID)

	if nil == session1.Get("name") {
		t.Log("check session value sucess!")
	} else {
		t.Error("check session value fail!")
	}

}

func BenchmarkMemProvider(b *testing.B) {

	memProvider := NewMemProvider()

	for i := 0; i < b.N; i++ {
		bs := make([]byte, 32)
		if _, err := io.ReadFull(rand.Reader, bs); err != nil {
			b.Error("create session ID fail!")
		}

		sessionID := base64.URLEncoding.EncodeToString(bs)
		memProvider.Init(sessionID)

		if memProvider.Check(sessionID) {
			b.Log("check session ID sucess!")
		} else {
			b.Error("check session ID fail!")
		}

		session, _ := memProvider.Read(sessionID)
		session.Set("name", "bobi")

		if "bobi" == session.Get("name") {
			b.Log("check session value sucess!")
		} else {
			b.Error("check session value fail!")
		}

		session.Set("name", "wolffy")
		if "wolffy" == session.Get("name") {
			b.Log("check session value sucess!")
		} else {
			b.Error("check session value fail!")
		}

		session.Del("name")
		if nil == session.Get("name") {
			b.Log("check session value sucess!")
		} else {
			b.Error("check session value fail!")
		}

		if sessionID == session.ID() {
			b.Log("check session value sucess!")
		} else {
			b.Error("check session value fail!")
		}

		memProvider.Destroy(sessionID)

		session1, _ := memProvider.Read(sessionID)

		if nil == session1.Get("name") {
			b.Log("check session value sucess!")
		} else {
			b.Error("check session value fail!")
		}
	}
}

func BenchmarkConcurrencyMemProvider(b *testing.B) {

	memProvider := NewMemProvider()

	b.RunParallel(func(pb *testing.PB) {
		for pb.Next() {
			bs := make([]byte, 32)
			if _, err := io.ReadFull(rand.Reader, bs); err != nil {
				b.Error("create session ID fail!")
			}

			sessionID := base64.URLEncoding.EncodeToString(bs)
			memProvider.Init(sessionID)

			if memProvider.Check(sessionID) {
				b.Log("check session ID sucess!")
			} else {
				b.Error("check session ID fail!")
			}

			session, _ := memProvider.Read(sessionID)
			session.Set("name", "bobi")

			if "bobi" == session.Get("name") {
				b.Log("check session value sucess!")
			} else {
				b.Error("check session value fail!")
			}

			session.Set("name", "wolffy")
			if "wolffy" == session.Get("name") {
				b.Log("check session value sucess!")
			} else {
				b.Error("check session value fail!")
			}

			session.Del("name")
			if nil == session.Get("name") {
				b.Log("check session value sucess!")
			} else {
				b.Error("check session value fail!")
			}

			if sessionID == session.ID() {
				b.Log("check session value sucess!")
			} else {
				b.Error("check session value fail!")
			}

			memProvider.Destroy(sessionID)

			session1, _ := memProvider.Read(sessionID)

			if nil == session1.Get("name") {
				b.Log("check session value sucess!")
			} else {
				b.Error("check session value fail!")
			}

		}
	})
}
