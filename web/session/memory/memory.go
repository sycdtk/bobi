package memory

import (
	"sync"
	"time"

	"github.com/sycdtk/bobi/logger"
	"github.com/sycdtk/bobi/web/session"
)

//session 内存存储模型
type SessionMemStore struct {
	sessionID        string    //session ID
	lastAccessedTime time.Time //最后访问时间
	values           sync.Map  //session 数据
}

//设置session值
func (sms *SessionMemStore) Set(key, value interface{}) error {
	sms.values.Store(key, value)
	sms.lastAccessedTime = time.Now()
	return nil
}

//获取session值
func (sms *SessionMemStore) Get(key interface{}) interface{} {
	sms.lastAccessedTime = time.Now()
	if v, ok := sms.values.Load(key); ok {
		return v
	}
	return nil
}

//删除session值
func (sms *SessionMemStore) Del(key interface{}) error {
	sms.values.Delete(key)
	return nil
}

//获取session ID
func (sms *SessionMemStore) ID() string {
	return sms.sessionID
}

//更新时间
func (sms *SessionMemStore) Update() {
	sms.lastAccessedTime = time.Now()
}

//定义session内存维护接口
type MemProvider struct {
	sessions sync.Map //存储session
}

func NewMemProvider() session.SessionProvider {
	return &MemProvider{}
}

//创建session
func (mp *MemProvider) Init(sessionID string) (session session.Session, err error) {

	ss := &SessionMemStore{sessionID: sessionID, lastAccessedTime: time.Now()}
	mp.sessions.Store(sessionID, ss)

	logger.Debug("新建session ID：", ss.ID())

	return ss, nil
}

//检查session ID是否存在
func (mp *MemProvider) Check(sessionID string) bool {
	_, ok := mp.sessions.Load(sessionID)
	return ok
}

//读取session
func (mp *MemProvider) Read(sessionID string) (session session.Session, err error) {
	if ss, ok := mp.sessions.Load(sessionID); ok {
		sss := ss.(*SessionMemStore)
		logger.Debug("读取session ID：", sss.ID())
		return sss, nil
	}

	return mp.Init(sessionID)
}

func (mp *MemProvider) Update(sessionID string) {
	if ss, ok := mp.sessions.Load(sessionID); ok {
		sss := ss.(*SessionMemStore)
		sss.Update()
		logger.Debug("更新session ID：", sss.ID())
	}
}

//session 销毁
func (mp *MemProvider) Destroy(sessionID string) {
	if _, ok := mp.sessions.Load(sessionID); ok {
		mp.sessions.Delete(sessionID)
		logger.Debug("删除session ID：", sessionID)
	}
}

//gc 销毁过期session
func (mp *MemProvider) GC(maxLifeTime int64) {

	deleteSessionIDs := make(map[string]int)

	mp.sessions.Range(func(sessionIDObj, sessionObj interface{}) bool {
		sessionID := sessionIDObj.(string)
		session := sessionObj.(*SessionMemStore)
		if (session.lastAccessedTime.Unix() + maxLifeTime) < time.Now().Unix() {
			deleteSessionIDs[sessionID] = 0
		}
		return true
	})

	for sessionID, _ := range deleteSessionIDs {
		mp.sessions.Delete(sessionID)
		logger.Debug("删除session ID：", sessionID)
	}
}
