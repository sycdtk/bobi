package web

import (
	"github.com/sycdtk/bobi/config"
	"github.com/sycdtk/bobi/web/restful"
)

//然后在init函数中初始化
func init() {

	//启动监听服务
	restful.ListenAndServe(config.Read("web", "port"))
}
