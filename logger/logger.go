package logger

import (
	"fmt"
	"log"
	"os"
	"sync"

	"github.com/sycdtk/gotools/config"
)

const (
	//日志级别
	lerror = 1 << 0
	ldebug = 1 << 1
	linfo  = 1 << 2

	DEBUG = "DEBUG"
	INFO  = "INFO"
	ERROR = "ERROR"
)

type Logger struct {
	*log.Logger
	level int //日志级别
	m     *sync.Mutex
}

var mylogger *Logger
var once sync.Once

func init() {
	NewLogger()
}

//设置日志级别
func SetLevel(lvl string) {
	if lvl == DEBUG {
		mylogger.level = ldebug
		mylogger.SetFlags(log.LstdFlags | log.Lshortfile) //设置输出格式
	} else if lvl == INFO {
		mylogger.level = linfo
		mylogger.SetFlags(log.LstdFlags) //设置输出格式
	} else if lvl == ERROR {
		mylogger.level = lerror
		mylogger.SetFlags(log.LstdFlags | log.Lshortfile) //设置输出格式
	}
}

//debug输出，包含info输出
func Debug(v ...interface{}) {
	if ldebug == mylogger.level&ldebug {
		mylogger.m.Lock()
		mylogger.Logger.SetPrefix("D: ")
		mylogger.Logger.Output(2, fmt.Sprintln(v))
		mylogger.Logger.SetPrefix("   ")
		mylogger.m.Unlock()
	}
}

//info输出
func Info(v ...interface{}) {
	if linfo == mylogger.level&linfo || ldebug == mylogger.level&ldebug {
		mylogger.m.Lock()
		mylogger.Logger.SetPrefix("I: ")
		mylogger.Logger.Output(2, fmt.Sprintln(v))
		mylogger.Logger.SetPrefix("   ")
		mylogger.m.Unlock()
	}
}

//error输出
func Err(v ...interface{}) {
	if linfo == mylogger.level&linfo || ldebug == mylogger.level&ldebug || lerror == mylogger.level&lerror {
		mylogger.m.Lock()
		mylogger.Logger.SetPrefix("E: ")
		mylogger.Logger.Output(2, fmt.Sprintln(v))
		mylogger.Logger.SetPrefix("   ")
		mylogger.m.Unlock()
	}
}

//error 输出调用文件路径为递归三层
func Err3(v ...interface{}) {
	if linfo == mylogger.level&linfo || ldebug == mylogger.level&ldebug || lerror == mylogger.level&lerror {
		mylogger.m.Lock()
		mylogger.Logger.SetPrefix("E: ")
		mylogger.Logger.Output(3, fmt.Sprintln(v))
		mylogger.Logger.SetPrefix("   ")
		mylogger.m.Unlock()
	}
}

func NewLogger() {

	//仅执行一次，单例
	once.Do(func() {

		filePath := config.Read("log", "path")
		logLevel := config.Read("log", "level")

		var logstd *log.Logger

		if len(filePath) != 0 { //日志文件

			file, err := os.Create(filePath)
			if err != nil {
				log.Panicln("创建日志失败!", err)
			}

			logstd = log.New(file, "", log.LstdFlags|log.Lshortfile) //构建默认log对象

		} else { //标准输出
			logstd = log.New(os.Stdout, "", log.LstdFlags|log.Lshortfile) //构建默认log对象
		}

		mylogger = &Logger{logstd, ldebug, new(sync.Mutex)} //默认级别

		if len(logLevel) > 0 {
			SetLevel(logLevel) //设置日志级别
		}
	})
}
