package com.stock;
 
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.*;
 
/**
 * @Author zjt
 * @Date 2019年03月07 10:38
 */
public class EmailSend {
    public static boolean EmailSendTest(EmailEntity emailEntity){
        try {
            //配置文件
            Properties properties = new Properties();
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.host", emailEntity.getHost());
            properties.put("mail.smtp.port", 25);
            properties.put("mail.smtp.starrttls.enable", "true");
            //创建会话
            VerifyEmail verifyEmail = new VerifyEmail(emailEntity.getUserName(), emailEntity.getPassword());
            Session mailSession = Session.getInstance(properties, verifyEmail);
            mailSession.setDebug(true);
            //创建信息对象
            Message message = new MimeMessage(mailSession);
            InternetAddress from = new InternetAddress(emailEntity.getFromAddress());
            InternetAddress to = new InternetAddress(emailEntity.getToAddress());
            //设置邮件信息的来源
            message.setFrom(from);
            //设置邮件的接收者
            message.setRecipient(MimeMessage.RecipientType.TO, to);
            message.setSubject(emailEntity.getSubject());
            //设置邮件发送日期
            message.setSentDate(new Date());
            //设置邮件内容
            message.setContent(emailEntity.getContext() , emailEntity.getContextType());
            message.saveChanges();
            //发送邮件
            Transport transport = mailSession.getTransport("smtp");
            transport.connect(emailEntity.getHost(), emailEntity.getUserName(), emailEntity.getPassword());
            System.out.println("发送:" + transport);
            transport.sendMessage(message, message.getAllRecipients());
            System.out.println("success");
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("fial...");
            return false;
 
        }
    }
}
