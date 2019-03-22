package memory

import (
	"sync"
	"time"

	"github.com/sycdtk/bobi/logger"
	"github.com/sycdtk/bobi/web/session"
)

//session 内存存储模型
type SessionMemStore struct {
	sessionID        string                      //session ID
	lastAccessedTime time.Time                   //最后访问时间
	values           map[interface{}]interface{} //session 数据
	mLock            *sync.RWMutex               //保证线程安全
}

//设置session值
func (sms *SessionMemStore) Set(key, value interface{}) error {
	sms.mLock.Lock()
	defer sms.mLock.Unlock()

	sms.values[key] = value
	sms.lastAccessedTime = time.Now()

	return nil
}

//获取session值
func (sms *SessionMemStore) Get(key interface{}) interface{} {
	sms.mLock.RLock()
	defer sms.mLock.RUnlock()
	sms.lastAccessedTime = time.Now()

	if v, ok := sms.values[key]; ok {
		return v
	}
	return nil
}

//删除session值
func (sms *SessionMemStore) Del(key interface{}) error {
	sms.mLock.Lock()
	defer sms.mLock.Unlock()
	delete(sms.values, key)
	return nil
}

//获取session ID
func (sms *SessionMemStore) ID() string {
	return sms.sessionID
}

//定义session内存维护接口
type MemProvider struct {
	mLock    *sync.RWMutex               //保证线程安全
	sessions map[string]*SessionMemStore //存储session
}

func NewMemProvider() session.SessionProvider {
	return &MemProvider{sessions: make(map[string]*SessionMemStore), mLock: new(sync.RWMutex)}
}

//创建session
func (mp *MemProvider) Init(sessionID string) (session session.Session, err error) {
	mp.mLock.Lock()
	defer mp.mLock.Unlock()

	values := make(map[interface{}]interface{}, 0)
	ss := &SessionMemStore{sessionID: sessionID, lastAccessedTime: time.Now(), values: values, mLock: new(sync.RWMutex)}
	mp.sessions[sessionID] = ss

	logger.Debug("新建session ID：", ss.ID())

	return ss, nil
}

//检查session ID是否存在
func (mp *MemProvider) Check(sessionID string) bool {
	_, ok := mp.sessions[sessionID]
	return ok
}

//读取session
func (mp *MemProvider) Read(sessionID string) (session session.Session, err error) {
	if ss, ok := mp.sessions[sessionID]; ok {
		logger.Debug("读取session ID：", ss.ID())
		return ss, nil
	}

	return mp.Init(sessionID)
}

//session 销毁
func (mp *MemProvider) Destroy(sessionID string) {
	if _, ok := mp.sessions[sessionID]; ok {
		delete(mp.sessions, sessionID)
	}
}

//gc 销毁过期session
func (mp *MemProvider) GC(maxLifeTime int64) {
	mp.mLock.Lock()
	defer mp.mLock.Unlock()

	for sessionID, session := range mp.sessions {
		if (session.lastAccessedTime.Unix() + maxLifeTime) < time.Now().Unix() {
			delete(mp.sessions, sessionID)
			logger.Debug("删除session ID：", sessionID)
		}
	}
}
