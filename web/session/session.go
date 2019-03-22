package session

import (
	"crypto/rand"
	"encoding/base64"
	"io"
	"net/http"
	"net/url"
	"sync"
	"time"

	"github.com/sycdtk/bobi/errtools"
	"github.com/sycdtk/bobi/logger"
)

var once *sync.Once
var provides = make(map[string]SessionProvider) //session provider 集合

//session对象接口
type Session interface {
	Set(key, value interface{}) error //保存session值
	Get(key interface{}) interface{}  //获取session值
	Del(key interface{}) error        //删除session值
	ID() string                       //返回session Id
}

//session维护接口，用以实现内存、文件存储session的接口
type SessionProvider interface {
	Init(sessionId string) (Session, error) //session 初始化
	Read(sessionId string) (Session, error) //读取session,不存在返回nil
	Check(sessionId string) bool            //检查现有session是否存在
	Destroy(sessionId string)               //session 销毁
	GC(maxLifeTime int64)                   //session 垃圾回收，删除过期session
}

//session管理器
type SessionManager struct {
	cookieName  string          //cookie名称
	mLock       *sync.Mutex     //保证线程安全
	provider    SessionProvider //session维护接口
	maxLifeTime int64           //session生命周期,单位分钟
	gcCycle     int64           //回收时间,nanosecond
}

//创建session ID
func (sm *SessionManager) sessionID() string {
	b := make([]byte, 32)
	if _, err := io.ReadFull(rand.Reader, b); err != nil {
		return ""
	}
	return base64.URLEncoding.EncodeToString(b)
}

//记录或创建session
func (sm *SessionManager) Start(w http.ResponseWriter, r *http.Request) (session Session) {
	sm.mLock.Lock()
	defer sm.mLock.Unlock()

	cookie, err := r.Cookie(sm.cookieName)

	if r.Method == http.MethodPost && r.URL.Path == "/login" && (err != nil || cookie.Value == "") { //登录，cookie为空，新建session
		sessionID := sm.sessionID()

		session, _ = sm.provider.Init(sessionID)

		cookie := http.Cookie{Name: sm.cookieName, Value: url.QueryEscape(sessionID), Path: "/", HttpOnly: true, MaxAge: int(sm.maxLifeTime)}
		http.SetCookie(w, &cookie)
	} else if err != nil || cookie.Value == "" { //非登录页面，cookie为空，跳转登录页面
		expiration := time.Now()
		cookie := http.Cookie{Name: sm.cookieName, Expires: expiration, Path: "/", HttpOnly: true, MaxAge: -1}
		http.SetCookie(w, &cookie)
		logger.Debug("session ID 不存在，页面跳转至登录界面！")
		http.Redirect(w, r, "/login", http.StatusFound)
	} else { //cookie value值不为空

		sessionID, _ := url.QueryUnescape(cookie.Value)

		if sm.provider.Check(sessionID) { //session Id 存在则读取
			session, _ = sm.provider.Read(sessionID)
		} else { //session Id 不存在，则返回空,页面跳转致登录界面
			expiration := time.Now()
			cookie := http.Cookie{Name: sm.cookieName, Expires: expiration, Path: "/", HttpOnly: true, MaxAge: -1}
			http.SetCookie(w, &cookie)
			logger.Debug("session ID 不存在，页面跳转至登录界面！")
			http.Redirect(w, r, "/login", http.StatusFound)
		}

	}
	return
}

//session销毁
func (sm *SessionManager) Destroy(w http.ResponseWriter, r *http.Request) {
	cookie, err := r.Cookie(sm.cookieName)
	if err != nil || cookie.Value == "" {

	} else {
		sm.mLock.Lock()
		defer sm.mLock.Unlock()

		sm.provider.Destroy(cookie.Value)

		expiration := time.Now()

		cookie := http.Cookie{Name: sm.cookieName, Expires: expiration, Path: "/", HttpOnly: true, MaxAge: -1}
		http.SetCookie(w, &cookie)
	}
}

//session回收、销毁
func (sm *SessionManager) GC() {
	sm.mLock.Lock()
	defer sm.mLock.Unlock()
	sm.provider.GC(sm.maxLifeTime)
	time.AfterFunc(time.Duration(sm.gcCycle), func() { sm.GC() })
}

//session provider 注册
func Register(providerName string, provider SessionProvider) {
	if provider == nil {
		errtools.NewErr("provider 为 nil!")
	}
	if _, ok := provides[providerName]; ok {
		errtools.NewErr("provider 已注册！")
	}
	provides[providerName] = provider
}

//创建session管理器
func NewSessionManager(providerName, cookieName string, maxLifeTime int64, gcCycle int64) (*SessionManager, error) {
	provider, ok := provides[providerName]
	if !ok {
		return nil, errtools.NewErr("获取 session provider 维护接口失败！")
	}

	return &SessionManager{provider: provider, cookieName: cookieName, maxLifeTime: maxLifeTime, gcCycle: gcCycle, mLock: new(sync.Mutex)}, nil
}
