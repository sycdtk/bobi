package web

import (
	"strconv"

	"github.com/sycdtk/bobi/config"
	"github.com/sycdtk/bobi/web/session"
	"github.com/sycdtk/bobi/web/session/memory"
)

var SessionManager *session.SessionManager

//然后在init函数中初始化
func init() {

	cycleStr := config.Read("web", "cycle")
	cycle, _ := strconv.ParseInt(cycleStr, 10, 64)

	maxLifeTimeStr := config.Read("web", "maxLifeTime")
	maxLifeTime, _ := strconv.ParseInt(maxLifeTimeStr, 10, 64)

	session.Register("memory", memory.NewMemProvider())
	SessionManager, _ = session.NewSessionManager("memory", "gosessionID", maxLifeTime, cycle)

	go SessionManager.GC()
}
