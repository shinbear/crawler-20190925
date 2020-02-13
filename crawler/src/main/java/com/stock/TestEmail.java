package com.stock;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestEmail {
	public static void main(String[] args) throws IOException {
		sendmail("aaa");
	}
    public static void sendmail(String sendTxt){
    	 SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    	 Date now = null;
    	 try {
			 now = dateformat.parse(dateformat.format(new Date()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	 
        EmailEntity email = new EmailEntity();
        email.setUserName("z_hao2019@163.com");
        email.setPassword("Zh197544");
        email.setHost("smtp.163.com");
        email.setPort(25);
        email.setFromAddress("z_hao2019@163.com");
        email.setToAddress("z_hao2019@163.com");
		if (now == null) {
			email.setSubject("股票交易更新提示!!!!");
		} else {
			email.setSubject(now + "   股票交易更新提示!!!!");
		}
        email.setContext(sendTxt);
        email.setContextType("text/html;charset=utf-8");
        boolean flag = EmailSend.EmailSendTest(email);
        System.err.println("邮件发送结果=="+flag);
    }
 
}