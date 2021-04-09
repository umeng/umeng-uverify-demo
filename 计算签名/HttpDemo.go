package main

import (
	"crypto/hmac"
	"crypto/md5"
	"crypto/sha256"
	"encoding/base64"
	"fmt"
	"io/ioutil"
	"net/http"
	"strings"
	"time"
)

func generate_md5(content string) string {
	b := md5.Sum([]byte(content))
	return base64.StdEncoding.EncodeToString(b[:])
}

func generate_sign(method string, req *http.Request, aliyunAppSecret string, uri string) string {
	var sign strings.Builder
	sign.WriteString(method + "\n")
	sign.WriteString(req.Header.Get("Accept") + "\n")
	sign.WriteString(req.Header.Get("Content-MD5") + "\n")
	sign.WriteString(req.Header.Get("Content-Type") + "\n")
	sign.WriteString(req.Header.Get("Date") + "\n")
	sign.WriteString("X-Ca-Key:" + req.Header.Get("X-Ca-Key") + "\n")
	sign.WriteString("X-Ca-Nonce:" + req.Header.Get("X-Ca-Nonce") + "\n")
	sign.WriteString("X-Ca-Stage:" + req.Header.Get("X-Ca-Stage") + "\n")
	sign.WriteString("X-Ca-Timestamp:" + req.Header.Get("X-Ca-Timestamp") + "\n")
	sign.WriteString("X-Ca-Version:" + req.Header.Get("X-Ca-Version") + "\n")
	sign.WriteString(uri)
	key := []byte(aliyunAppSecret)
	h := hmac.New(sha256.New, key)
	h.Write([]byte(sign.String()))
	return base64.StdEncoding.EncodeToString(h.Sum(nil))
}

func main() {
	// 初始化参数
	umengAppkey := "123"
	aliyunAppKey := "20000000"
	aliyunAppSecret := "xxxxxxxx"
	uri := "/api/v1/mobile/info?appkey=" + umengAppkey
	url := "https://verify5.market.alicloudapi.com" + uri
	httpBody := `{"token":"1234"}`
	method := "POST"
	// 初始化http请求
	payload := strings.NewReader(httpBody)
	client := &http.Client{}
	req, err := http.NewRequest(method, url, payload)
	if err != nil {
		fmt.Println(err)
		return
	}
	// 设置header
	req.Header.Add("Accept", "application/json")
	req.Header.Add("Content-MD5", generate_md5(httpBody))
	req.Header.Add("Content-Type", "application/json; charset=UTF-8")
	req.Header.Add("Date", fmt.Sprint(time.Now()))
	req.Header.Add("X-Ca-Key", aliyunAppKey)
	req.Header.Add("X-Ca-Nonce", fmt.Sprintf("%x", md5.Sum([]byte("my_salt"+fmt.Sprint(time.Now().UnixNano())))))
	req.Header.Add("X-Ca-Stage", "RELEASE")
	req.Header.Add("X-Ca-Timestamp", fmt.Sprint(time.Now().UnixNano()/1000000))
	req.Header.Add("X-Ca-Version", "1")
	req.Header.Add("X-Ca-Signature-Headers", "X-Ca-Key, X-Ca-Nonce, X-Ca-Stage, X-Ca-Timestamp, X-Ca-Version")
	req.Header.Add("X-Ca-Signature", generate_sign(method, req, aliyunAppSecret, uri))
	// 发起请求
	res, err := client.Do(req)
	if err != nil {
		fmt.Println(err)
		return
	}
	defer res.Body.Close()
	body, err := ioutil.ReadAll(res.Body)
	if err != nil {
		fmt.Println(err)
		return
	}
	fmt.Println(res.Header)
	fmt.Println(string(body))
}
