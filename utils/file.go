package utils

import (
	"bufio"
	"io/ioutil"
	"mime/multipart"
	"os"
	"path"
	"strings"
)

var sep string

func init() {
	sep = string(os.PathSeparator)
}

//检查文件是否存在
func FileExist(filename string) bool {
	_, err := os.Stat(filename)
	return err == nil || os.IsExist(err)
}

//获取文件大小
func FileSize(f multipart.File) (int, error) {
	content, err := ioutil.ReadAll(f)
	return len(content), err
}

//获取文件扩展名
func FileExt(fileName string) string {
	return path.Ext(fileName)
}

//检查文件访问权限
func FilePerm(src string) bool {
	_, err := os.Stat(src)
	return os.IsPermission(err)
}

//路径结尾没有路径分隔符，则补全
func PathAddSuffix(p string) string {
	if !strings.HasSuffix(p, sep) {
		p = p + sep
	}
	return p
}

//去除路径末尾分隔符
func PathRemoveSuffix(p string) string {
	if p != "./" && strings.HasSuffix(p, sep) {
		p = p[0 : len(p)-1]
	}
	return p
}

//创建目录
func MkDir(src string) error {
	err := os.MkdirAll(src, os.ModePerm)
	if err != nil {
		return err
	}
	return nil
}

//写文件
func FileBufferWrite(fileName string, data []byte) error {
	fileHandle, err := os.OpenFile(fileName, os.O_WRONLY|os.O_CREATE, 0666)
	if err != nil {
		return err
	}
	defer fileHandle.Close()
	// NewWriter 默认缓冲区大小是 4096
	// 需要使用自定义缓冲区的writer 使用 NewWriterSize()方法
	buf := bufio.NewWriter(fileHandle)
	// 字节写入
	buf.Write(data)
	// 将缓冲中的数据写入
	err = buf.Flush()
	if err != nil {
		return err
	}
	return nil
}
