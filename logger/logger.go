package logger

import (
	"fmt"
	"log"
	"os"
	"sync"

	"github.com/sycdtk/bobi/config"
	"github.com/sycdtk/bobi/utils"
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

func init() {
	changeLogger(utils.DateStr())
	go utils.TimerZero(change)
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
		mylogger.Logger.Output(2, fmt.Sprintln(v...))
		mylogger.Logger.SetPrefix("   ")
		mylogger.m.Unlock()
	}
}

//info输出
func Info(v ...interface{}) {
	if linfo == mylogger.level&linfo || ldebug == mylogger.level&ldebug {
		mylogger.m.Lock()
		mylogger.Logger.SetPrefix("I: ")
		mylogger.Logger.Output(2, fmt.Sprintln(v...))
		mylogger.Logger.SetPrefix("   ")
		mylogger.m.Unlock()
	}
}

//info输出
func Println(v ...interface{}) {
	mylogger.m.Lock()
	flags := mylogger.Logger.Flags()
	mylogger.Logger.SetFlags(0)
	mylogger.Logger.SetPrefix("")
	mylogger.Logger.Output(2, fmt.Sprintln(v...))
	mylogger.Logger.SetPrefix("   ")
	mylogger.Logger.SetFlags(flags)
	mylogger.m.Unlock()
}

//error输出
func Err(v ...interface{}) {
	if linfo == mylogger.level&linfo || ldebug == mylogger.level&ldebug || lerror == mylogger.level&lerror {
		mylogger.m.Lock()
		mylogger.Logger.SetPrefix("E: ")
		mylogger.Logger.Output(2, fmt.Sprintln(v...))
		mylogger.Logger.SetPrefix("   ")
		mylogger.m.Unlock()
	}
}

//error 输出调用文件路径为递归三层
func Err3(v ...interface{}) {
	if linfo == mylogger.level&linfo || ldebug == mylogger.level&ldebug || lerror == mylogger.level&lerror {
		mylogger.m.Lock()
		mylogger.Logger.SetPrefix("E: ")
		mylogger.Logger.Output(3, fmt.Sprintln(v...))
		mylogger.Logger.SetPrefix("   ")
		mylogger.m.Unlock()
	}
}

func changeLogger(dateStr string) {
	//日志文件路径，可以是相对路径或绝对路径，为空时从控制台输出。默认为控制台输出
	path := config.Read("log", "path")

	//日志文件前缀，默认XXXX + dateStr + .log
	filePath := path + config.Read("log", "flieprefix") + dateStr + ".log"

	//日志级别，分为INFO、DEBUG、ERROR，三个级别，默认为INFO
	logLevel := config.Read("log", "level")

	var logstd *log.Logger

	if len(path) != 0 { //日志文件
		if !utils.FileExist(filePath) {
			//创建文件
			file, err := os.Create(filePath)
			if err != nil {
				log.Panicln(err)
			}
			logstd = log.New(file, "", log.LstdFlags|log.Lshortfile) //构建默认log对象

		} else {

			//文件存在追加
			file, err := os.OpenFile(filePath, os.O_RDWR|os.O_CREATE|os.O_APPEND, 0644)
			if err != nil {
				log.Panicln(err)
			}
			logstd = log.New(file, "", log.LstdFlags|log.Lshortfile) //构建默认log对象
		}
	} else { //标准输出
		logstd = log.New(os.Stdout, "", log.LstdFlags|log.Lshortfile) //构建默认log对象
	}

	mylogger = &Logger{logstd, ldebug, new(sync.Mutex)} //默认级别

	if len(logLevel) > 0 {
		SetLevel(logLevel) //设置日志级别
	} else {
		SetLevel("INFO") //未设置日志级别，默认INFO
	}

}

func change() {
	changeLogger(utils.DateStr())
}
