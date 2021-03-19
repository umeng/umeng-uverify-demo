package com.aliyun.api.gateway.demo.util;

public class Test2 {

    public static void main(String[] args) throws Exception {
        //每次请求获取AES加密的mobile和ECC加密的AES key
        String mobile = "xxxx";
        String aesEncryptKey = "xxxx";
        String privateKeyStr = "xxxx";

        //先用ECC把aesEncryptKey解密，解出来的是AES key
        byte[] aesKey = ErniuUtil.privateDecryptRSA(aesEncryptKey, privateKeyStr);
        //再用AES把mobile解密
        mobile = new String(ErniuUtil.decryptAES(ErniuUtil.base642Byte(mobile), ErniuUtil.loadKeyAES(aesKey)));
        System.out.println(mobile);
    }

}