package com.stock;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
	public static ArrayList<String> currTrantd; 
	public static ArrayList<String> hisTrantd; 
	public static ArrayList<String> cuposTrantd; 
	public static ArrayList<String> hisposTrantd; 
	public static String currStr = "";
	public static String posStr = "";
	public static void main(String[] args) throws IOException {
		System.out.println("用户的当前工作目录:"+System.getProperty("user.dir"));	
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Boolean result = false;
		int count = 0;
		// Initialize the web browser
		System.getProperties().setProperty("webdriver.chrome.driver", "chromedriver.exe");
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--lang=en");
		WebDriver webDriver = new ChromeDriver(options);
		webDriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

		// access the joinquant
		String URL_search = "https://www.joinquant.com/post/20355?f=sharelist&m=list";
		webDriver.get(URL_search);
		webDriver.manage().window().maximize();
		
		while(!result) {
			try {
				Thread.sleep(5 * 1000); 				
				runStock(webDriver) ;
				count ++ ;
				System.out.println(sdf.format(new Date()) + "--循环执行第" + count + "次");
				if (count == 3) {
					result = true;
					break ;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}  
		}
	

		String aa = "aa";	
	}
	
	public static int runStock(WebDriver webDriver) throws IOException {
		try {
			webDriver.navigate().refresh();
			// Waiting for element for 10 seconds
			WebDriverWait wait = new WebDriverWait(webDriver, 10);
			wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//*[text()='交易详情']")));
			
			try {
				wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.linkText("点击登录")));
				// access the login dialog
				webDriver.findElement(By.linkText("点击登录")).click();

				// login username & pwd
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
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//Get the data
			wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#transaction-table")));
			WebElement trans = webDriver.findElement(By.cssSelector("#transaction-table"));
			WebElement tr = trans.findElement(By.cssSelector(".transaction_tr"));
			String aa = tr.getText();
			List<WebElement> trtd = tr.findElements(By.cssSelector("td"));
			for (WebElement tdd : trtd) {
				currStr = currStr + ";" + tdd.getText();
			}

			
			WebElement posi = webDriver.findElement(By.cssSelector("#position-table"));
			List<WebElement> tp = posi.findElements(By.cssSelector(".border_bo position_tr"));
			for (WebElement tpp : tp) {
				List<WebElement> td = tpp.findElements(By.cssSelector("td"));
				for (WebElement tdd : td) {
					posStr = posStr + ";" + tdd.getText();
				}
			}	
			Thread.sleep(3 * 1000); 
			return 1;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}

	}

}
