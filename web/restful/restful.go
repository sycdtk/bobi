package restful

import (
	"net/http"
	"net/url"
	"strconv"
	"sync"

	"github.com/sycdtk/bobi/config"
	"github.com/sycdtk/bobi/logger"
	"github.com/sycdtk/bobi/web/message"
	"github.com/sycdtk/bobi/web/restful/mux"
	"github.com/sycdtk/bobi/web/session"
	"github.com/sycdtk/bobi/web/session/memory"
)

var once sync.Once
var restApi *RESTApi

//RESTful API
type RESTApi struct {
	baseName       string
	muxRouter      *mux.ServeMux
	sessionManager *session.SessionManager
}

//请求验证及请求类型判断
func (api *RESTApi) wapper(handler func(http.ResponseWriter, *http.Request, map[string]string) interface{}, auth bool) mux.HandlerFunc {
	return func(res http.ResponseWriter, req *http.Request, paramsMap map[string]string) {

		if req.ParseForm() != nil {
			res.WriteHeader(http.StatusBadRequest)
			res.Write(message.NewMessage(message.FailedCode, message.FailedMsg, nil))
			return
		}

		//权限认证
		//检查sessionid
		cookie, err := req.Cookie(api.baseName)
		if err != nil || cookie.Value == "" { //cookie中的session id不存在，无权限
			if auth { //需要鉴权
				res.WriteHeader(http.StatusUnauthorized)
				res.Write(message.NewMessage(message.FailedCode, message.FailedMsg, nil))
				return
			} else { //不需要鉴权,添加cookie
				cookie := api.sessionManager.Cookie()
				http.SetCookie(res, &cookie)
			}
		} else { //cookie存在
			sessionID, _ := url.QueryUnescape(cookie.Value)
			if sessionObj := api.sessionManager.Check(sessionID); sessionObj != nil { //session Id 存在则读取
				//session存在则更新
				sessionObj.Update()
			} else { //session Id 不存在，无权限
				if auth { //session不存在，且需要鉴权
					res.WriteHeader(http.StatusUnauthorized)
					res.Write(message.NewMessage(message.FailedCode, message.FailedMsg, nil))
					return
				} else { //session不存在，不需要鉴权
					cookie := api.sessionManager.Cookie()
					http.SetCookie(res, &cookie)
				}
			}
		}

		//请求类型解析
		var content interface{}

		//实际业务逻辑
		//Header 默认值设置
		res.Header().Add("Content-type", "application/json")

		content = handler(res, req, paramsMap)

		logger.Debug(content)

		//写返回数据
		res.Write(message.NewMessage(message.SuccessCode, message.SuccessMsg, content))
	}
}

func (api *RESTApi) handleFunc(pattern string, handleFunc func(http.ResponseWriter, *http.Request, map[string]string) interface{}, method string, auth bool) {
	api.muxRouter.HandleFunc(api.path(pattern), api.wapper(handleFunc, auth), method)
}

func (api *RESTApi) handle(pattern string, handler mux.Handler, method string) {
	api.muxRouter.Handle(api.path(pattern), handler, method)
}

func (api *RESTApi) handleRealPath(pattern string, handler mux.Handler, method string) {
	api.muxRouter.Handle(pattern, handler, method)
}

func (api *RESTApi) path(pattern string) string {
	prefix := api.baseName
	if prefix != "" {
		prefix = "/" + prefix
	}
	return prefix + pattern
}

func ListenAndServe(port string) {
	logger.Info("listen port : ", port)
	http.ListenAndServe(":"+port, restApi.muxRouter)
}

func HandleFunc(pattern string, handleFunc func(http.ResponseWriter, *http.Request, map[string]string) interface{}, method string, auth bool) {
	restApi.handleFunc(pattern, handleFunc, method, auth)
}

//http.Handler 转mux.Handler
type handlerTrans struct {
	handler http.Handler
}

func (ht *handlerTrans) ServeHTTP(res http.ResponseWriter, req *http.Request, paramsMap map[string]string) {
	ht.handler.ServeHTTP(res, req)
}

func handlerTransFunc(handler http.Handler) mux.Handler {
	return &handlerTrans{handler: handler}
}

func Handle(pattern string, handler http.Handler, method string) {
	restApi.handle(pattern, handlerTransFunc(http.StripPrefix(restApi.path(pattern), handler)), method)
}

func HandleRealPath(pattern string, handler http.Handler, method string) {
	restApi.handleRealPath(pattern, handlerTransFunc(http.StripPrefix(pattern, handler)), method)
}

//构建函数(单例模式)
func init() {
	once.Do(func() {
		//读取配置
		cycle, _ := strconv.ParseInt(config.Read("web", "cycle"), 10, 64)
		maxLifeTime, _ := strconv.ParseInt(config.Read("web", "maxLifeTime"), 10, 64)
		baseName := config.Read("web", "baseName")

		//初始化session管理器
		session.Register(baseName, memory.NewMemProvider())
		sessionManager, _ := session.NewSessionManager(baseName, maxLifeTime, cycle)

		//启动session过期回收协程
		go sessionManager.GC()

		//构建路由服务
		restApi = &RESTApi{baseName: config.Read("web", "baseName"), muxRouter: mux.NewServeMux(), sessionManager: sessionManager}
	})
}
