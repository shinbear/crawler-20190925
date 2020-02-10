package com.stock;

import java.io.IOException;

public class TestEmail {
	public static void main(String[] args) throws IOException {
		sendmail("aaa");
	}
    public static void sendmail(String sendTxt){
        EmailEntity email = new EmailEntity();
        email.setUserName("z_hao2019@163.com");
        email.setPassword("Zh197544");
        email.setHost("smtp.163.com");
        email.setPort(25);
        email.setFromAddress("z_hao2019@163.com");
        email.setToAddress("z_hao2019@163.com");
        email.setSubject("股票交易更新提示!!!!");
        email.setContext("sendTxt");
        email.setContextType("text/html;charset=utf-8");
        boolean flag = EmailSend.EmailSendTest(email);
        System.err.println("邮件发送结果=="+flag);
    }
 
}