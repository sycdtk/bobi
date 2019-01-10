package config

import (
	"bufio"
	"io"
	"log"
	"os"
	"strings"
)

var configName = "config.conf" //默认配置文件名称

var conf *config

//初始化
func init() {
	conf = &config{}
	_, err := os.Stat(configName)
	if err == nil || os.IsExist(err) {
		conf.initConfig(configName)
	}
}

func Load(configFile string) {
	configName = configFile
	conf.initConfig(configName)
}

type config struct {
	confMap map[string]string //配置信息
	strcet  string
	path    string
}

//读取配置文件，文件格式：
//[组名]
//属性1=值1
//属性2=值2
func (c *config) initConfig(path string) {

	c.confMap = make(map[string]string)

	if path != "" {
		conf.path = path
	}

	f, err := os.Open(conf.path)
	if err != nil {
		log.Panicln("读取配置文件失败！", err)
	}
	defer f.Close()

	r := bufio.NewReader(f)
	for {
		b, _, err := r.ReadLine()
		if err != nil {
			if err == io.EOF {
				break
			} else {
				log.Panicln("csv文件读取失败！", err)
				return
			}
		}

		s := strings.TrimSpace(string(b))
		//fmt.Println(s)
		if strings.Index(s, "#") == 0 {
			continue
		}

		//TODO:key中包含[] 会读取失败
		n1 := strings.Index(s, "[")
		n2 := strings.LastIndex(s, "]")
		if n1 > -1 && n2 > -1 && n2 > n1+1 {
			c.strcet = strings.TrimSpace(s[n1+1 : n2])
			continue
		}

		if len(c.strcet) == 0 {
			continue
		}
		index := strings.Index(s, "=")
		if index < 0 {
			continue
		}

		frist := strings.TrimSpace(s[:index])
		if len(frist) == 0 {
			continue
		}
		second := strings.TrimSpace(s[index+1:])

		pos := strings.Index(second, "\t#")
		if pos > -1 {
			second = second[0:pos]
		}

		pos = strings.Index(second, " #")
		if pos > -1 {
			second = second[0:pos]
		}

		pos = strings.Index(second, "\t//")
		if pos > -1 {
			second = second[0:pos]
		}

		pos = strings.Index(second, " //")
		if pos > -1 {
			second = second[0:pos]
		}

		if len(second) == 0 {
			continue
		}

		key := c.strcet + frist
		c.confMap[key] = strings.TrimSpace(second)
	}
}

func (c config) read(node, key string) string {
	key = node + key
	v, found := c.confMap[key]
	if !found {
		return ""
	}
	return v
}

//读取配置信息，node：组名，key：属性
func Read(node, key string) string {
	return conf.read(node, key)
}

//获取分组列表
func GetNode(node string) map[string]string {
	data := make(map[string]string)
	for key, value := range conf.confMap {
		if strings.HasPrefix(key, node) {
			data[key[len(node):len(key)]] = value
		}
	}
	return data
}
