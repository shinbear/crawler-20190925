package com.stock;
 
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
 
/**
 * 验证邮箱
 * @Author zjt
 * @Date 2019年03月07 10:32
 */
public class VerifyEmail extends Authenticator {
    //账号
    private String userName;
    //密码
    private String password;
 
    public String getUserName() {
        return userName;
    }
 
    public void setUserName(String userName) {
        this.userName = userName;
    }
 
    public String getPassword() {
        return password;
    }
 
    public void setPassword(String password) {
        this.password = password;
    }
 
    //构造方法
    public VerifyEmail(){
        super();
    }
 
    public VerifyEmail(String userName, String password) {
        super();
        this.userName = userName;
        this.password = password;
    }
    protected PasswordAuthentication getPasswordAuthentication(){
 
        return new PasswordAuthentication(userName, password);
 
    }
}