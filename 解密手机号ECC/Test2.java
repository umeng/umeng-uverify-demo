public class Test2 {
    public static void main(String[] args) throws Exception {
        //每次请求获取AES加密的mobile和ECC加密的AES key
        String mobile = "xxxx";
        String aesEncryptKey = "xxxx";
        //ECC的私钥是控制台获取的
        String privateKeyStr = "xxxx";
        //先用ECC把aesEncryptKey解密，/Users/zihao_wang/Downloads/解密手机号ECC/ErniuUtil.java解出来的是AES key
        byte[] byteArr = ErniuUtil.privateDecrypt(ErniuUtil.base642Byte(aesEncryptKey),ErniuUtil.string2PrivateKey(privateKeyStr));
        String aesKey = ErniuUtil.byte2Base64(byteArr);
        //再用AES把mobile解密
        mobile = new String(ErniuUtil.decryptAES(ErniuUtil.base642Byte(mobile),ErniuUtil.loadKeyAES(aesKey)));
        System.out.println(mobile);
    }

}