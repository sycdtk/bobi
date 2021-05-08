package utils

import (
	"bytes"
	"crypto/aes"
	"crypto/cipher"
	"crypto/md5"
	"crypto/rand"
	"encoding/base64"
	"encoding/hex"
	"errors"
	"fmt"
	"io"
	"math/big"
	mrand "math/rand"
	"strconv"
	"time"
)

const (
	//数据加密盐16位
	SALT     = "mofy201807300130"
	BASE_NUM = 10000
)

func init() {
	mrand.Seed(time.Now().UnixNano())
}

//生成32位MD5
func MD5(text string) string {
	ctx := md5.New()
	ctx.Write([]byte(text))
	return hex.EncodeToString(ctx.Sum(nil))
}

//今天的日期yyyyMMdd
func DateStr() string {
	t := time.Now()
	return dateToStr("d", &t)
}

//明天的日期
func NextDateStr() string {
	t := time.Now().AddDate(0, 0, 1)
	return dateToStr("d", &t)
}

//今天的日期yyyy-MM-dd
func MDateStr() string {
	t := time.Now()
	return dateToStr("md", &t)
}

//明天的日期
func DiffMDateStr(years, months, days int) string {
	t := time.Now().AddDate(years, months, days)
	return dateToStr("md", &t)
}

//今天的日期时间
func DateTimeStr() string {
	t := time.Now()
	return dateToStr("sdt", &t)
}

func ParseDate(dt string) *time.Time {
	stamp, err := time.Parse("2006-01-02", dt)
	if err != nil {
		return nil
	} else {
		return &stamp
	}
}

func ParseDateTime(dt string) *time.Time {
	stamp, err := time.Parse("2006-01-02 15:04:05", dt)
	if err != nil {
		return nil
	} else {
		return &stamp
	}
}

func ParseDateTimePrecision(dt string) *time.Time {
	stamp, err := time.Parse("2006-01-02 15:04:05.999999999", dt)
	if err != nil {
		return nil
	} else {
		return &stamp
	}
}

//2006-01-02
func ParseMDateString(t *time.Time) string {
	return dateToStr("md", t)
}

//2006-01-02 15:04:05
func ParseDateString(t *time.Time) string {
	return dateToStr("md", t)
}

//2006-01-02 15:04:05.999999999
func ParseDatePrecisionString(t *time.Time) string {
	return dateToStr("sdtp", t)
}

func TimePrecision(t string) string {
	return t + ".999999999"
}

//生成当前时间字符串
//d:20060102
//md:2006-01-02
//dt:20060102150405
//sdt:2006-01-02 15:04:05
//sdtp:2006-01-02 15:04:05.999999999
func dateToStr(strType string, t *time.Time) string {
	switch strType {
	case "d":
		return t.Format("20060102")
	case "md":
		return t.Format("2006-01-02")
	case "dt":
		return t.Format("20060102150405")
	case "sdt":
		return t.Format("2006-01-02 15:04:05")
	case "sdtp":
		return t.Format("2006-01-02 15:04:05.999999999")
	default:
		return t.Format("2006-01-02 15:04:05")
	}
}

//跨日0点触发
func TimerZero(f func()) {
	for {
		now := time.Now()
		next := now.Add(time.Hour * 24)
		next = time.Date(next.Year(), next.Month(), next.Day(), 0, 0, 0, 0, next.Location())
		t := time.NewTimer(next.Sub(now))
		<-t.C
		go f()
	}
}

func pKCS5Padding(ciphertext []byte, blockSize int) []byte {
	padding := blockSize - len(ciphertext)%blockSize
	padtext := bytes.Repeat([]byte{byte(padding)}, padding)
	return append(ciphertext, padtext...)
}

func pKCS5UnPadding(origData []byte) []byte {
	length := len(origData)
	unpadding := int(origData[length-1])
	return origData[:(length - unpadding)]
}

//AES加密
//keyStr 16, 24, or 32 bytes to select
func AesEncrypt(origDataStr, keyStr string) (string, error) {

	origData := []byte(origDataStr)
	key := []byte(keyStr)

	block, err := aes.NewCipher(key)
	if err != nil {
		return "", err
	}

	blockSize := block.BlockSize()
	origData = pKCS5Padding(origData, blockSize)
	blockMode := cipher.NewCBCEncrypter(block, key[:blockSize])
	crypted := make([]byte, len(origData))
	blockMode.CryptBlocks(crypted, origData)
	return base64.StdEncoding.EncodeToString(crypted), nil
}

//AES解密
//keyStr 16, 24, or 32 bytes to select
func AesDecrypt(cryptedDecodeStr, keyStr string) (string, error) {

	cryptedStr, err := base64.StdEncoding.DecodeString(cryptedDecodeStr)
	if err != nil {
		return "", err
	}

	crypted := []byte(cryptedStr)
	key := []byte(keyStr)

	block, err := aes.NewCipher(key)
	if err != nil {
		return "", err
	}

	blockSize := block.BlockSize()
	blockMode := cipher.NewCBCDecrypter(block, key[:blockSize])
	origData := make([]byte, len(crypted))
	blockMode.CryptBlocks(origData, crypted)
	origData = pKCS5UnPadding(origData)
	return string(origData[:]), nil
}

//数据解密
func Decrypt(val, id string) (string, error) {
	return AesDecrypt(val, MD5(id+SALT))
}

//数据加密
func Encrypt(val, id string) (string, error) {
	return AesEncrypt(val, MD5(id+SALT))
}

//生成ID字串
func UniqueID() string {
	b := make([]byte, 48)

	if _, err := io.ReadFull(rand.Reader, b); err != nil {
		return ""
	}
	return MD5(base64.URLEncoding.EncodeToString(b))
}

func VersionInfo(productName, version string) string {
	nullLen := 10
	versionStr := " Version:"

	infoLen := len(productName) + len(versionStr) + len(version)
	lineLen := infoLen + 2*nullLen + 2

	infos := ""
	//line1
	for i := 0; i < lineLen; i++ {
		infos = infos + "#"
	}

	//line 2
	infos = infos + "\n#"
	for i := 0; i < nullLen; i++ {
		infos = infos + " "
	}
	infos = infos + productName + versionStr + version
	for i := 0; i < nullLen; i++ {
		infos = infos + " "
	}
	infos = infos + "#\n"

	//line 3
	for i := 0; i < lineLen; i++ {
		infos = infos + "#"
	}

	return infos
}

func Judge() bool {
	basedata := map[int][]int{
		0: []int{0, 5000},
		1: []int{5000, BASE_NUM},
	}

	r := Probabilities(basedata)
	return r == 0
}

//成功率：0-100
func RateOfSuccess(rate int) bool {
	if rate < 0 || rate > 100 {
		return false
	}

	if rate == 0 {
		return false
	}

	if rate == 100 {
		return true
	}
	basedata := map[int][]int{
		0: []int{0, rate * 100},
		1: []int{rate * 100, BASE_NUM},
	}

	r := Probabilities(basedata)
	return r == 0
}

//概率数据预处理
func ProbDataPreProc(basedata map[int]int) (map[int][]int, error) {

	data := map[int][]int{}

	//检查数据，概率最小粒度0.01%
	sum := 0
	for k, _ := range basedata {
		sum += k
	}

	if sum > BASE_NUM {
		return nil, errors.New("out of range")
	}

	//数据处理
	pValue := 0
	for k, v := range basedata {
		data[v] = []int{pValue, k + pValue}
		pValue = k + pValue
	}

	//数据补全
	if sum < BASE_NUM {
		data[0] = []int{pValue, BASE_NUM}
	}

	return data, nil

}

//概率生成
func Probabilities(basedata map[int][]int) int {

	r := mrand.Intn(BASE_NUM)

	code := 0

	for k, v := range basedata {
		if r >= v[0] && r < v[1] {
			code = k
			break
		}
	}

	return code
}

//向上对半取整
func Ceil(i int) int {
	if i <= 0 {
		return 0
	}
	if i%2 == 0 {
		return i / 2
	} else {
		return (i + 1) / 2
	}

}

//[0, rangeNum) 范围的真随机数
func RandInt(rangeNum int64) int64 {
	result, _ := rand.Int(rand.Reader, big.NewInt(rangeNum))
	return result.Int64()
}

//浮点格式保留两位小数
func Decimal(value float64) float64 {
	value, _ = strconv.ParseFloat(fmt.Sprintf("%.2f", value), 64)
	return value
}
