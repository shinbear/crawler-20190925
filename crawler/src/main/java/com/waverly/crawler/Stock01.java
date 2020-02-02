package com.waverly.crawler;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.WebElement;


public class Stock01 {
	
	public static void main(String[] args) throws IOException {
		System.out.println("用户的当前工作目录:"+System.getProperty("user.dir"));
		try {
			// Initialize the web browser
			System.getProperties().setProperty("webdriver.chrome.driver", "chromedriver.exe");
			ChromeOptions options = new ChromeOptions();
			options.addArguments("--lang=en");
			WebDriver webDriver = new ChromeDriver(options);
			webDriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			
			//access the joinquant
			String URL_search = "https://www.joinquant.com/post/20355?f=sharelist&m=list";
			webDriver.get(URL_search);
			webDriver.manage().window().maximize();
			
			//Waiting for element for 10 seconds
			WebDriverWait wait=new WebDriverWait(webDriver,10);        
			wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.linkText("点击登录")));
			
			//access the login dialog
			webDriver.findElement(By.linkText("点击登录")).click();		
			
			//login username & pwd
			WebElement loginInput = webDriver.findElement(By.cssSelector(".formPwdLogin"));
			List<WebElement> tb = loginInput.findElements(By.tagName("input"));
			tb.get(0).clear();
			Thread.sleep(1000);
			tb.get(0).sendKeys("13814005531");	
			Thread.sleep(1000);
			tb.get(1).clear();
			Thread.sleep(1000);
			tb.get(1).sendKeys("zh197544");
			loginInput.findElement(By.tagName("button")).click();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		String aa = "aa";
		
	}

}
