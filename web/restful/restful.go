//借鉴参考自：https://github.com/dougblack/sleepy
package restful

import (
	"net/http"
	"sync"

	"github.com/sycdtk/bobi/config"
	"github.com/sycdtk/bobi/web/message"
)

var once sync.Once
var API *HttpApi

//请求验证及请求类型判断
func wapper(handler func(http.ResponseWriter, *http.Request) interface{}, method string, auth bool) http.HandlerFunc {
	return func(res http.ResponseWriter, req *http.Request) {

		if req.ParseForm() != nil {
			res.WriteHeader(http.StatusBadRequest)
			res.Write(message.NewMessage(message.FailedCode, message.FailedMsg, nil))
			return
		}

		//TODO 权限认证
		if auth {
			//TODO 检查sessionid
		}

		//TODO 请求类型解析
		var content interface{}

		if req.Method == method {
			//实际业务逻辑
			//Header 默认值设置
			res.Header().Add("Content-type", "application/json")

			content = handler(res, req)

		} else {
			res.WriteHeader(http.StatusMethodNotAllowed)
			res.Write(message.NewMessage(message.FailedCode, message.FailedMsg, nil))
			return
		}

		//TODO 写返回数据
		res.Write(message.NewMessage(message.SuccessCode, message.SuccessMsg, content))
	}
}

//RESTful API
type HttpApi struct {
	baseName string
	mux      *http.ServeMux
}

func (api *HttpApi) HandleFunc(pattern string, handleFunc func(http.ResponseWriter, *http.Request) interface{}, method string, auth bool) {
	api.mux.HandleFunc(api.path(pattern), wapper(handleFunc, method, auth))
}

func (api *HttpApi) Handle(pattern string, handler http.Handler) {
	api.mux.Handle(api.path(pattern), handler)
}

func (api *HttpApi) path(pattern string) string {
	prefix := api.baseName
	if prefix != "" {
		prefix = "/" + prefix
	}
	return prefix + pattern
}

func (api *HttpApi) ListenAndServe(port) {
	portString := fmt.Sprintf(":%d", port)
	return http.ListenAndServe(portString, api.mux)
}

//构建函数(单例模式)
func init() {
	once.Do(func() {
		API = &HttpApi{baseName: config.Read("web", "baseName"), mux: http.NewServeMux()}
	})
}
