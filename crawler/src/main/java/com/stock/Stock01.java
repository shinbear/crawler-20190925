package com.stock;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
	public static ArrayList<String> currTrantd = new ArrayList<>(); 
	public static ArrayList<String> hisTrantd = new ArrayList<>(); 
	public static ArrayList<String> cuposTrantd= new ArrayList<>(); 
	public static ArrayList<String> hisposTrantd= new ArrayList<>(); 
	public static ArrayList<String> uniquevalues = new ArrayList<String>();
	public static String currStr = "";
	public static String posStr = "";
	public static void main(String[] args) throws IOException {
		System.out.println("用户的当前工作目录:"+System.getProperty("user.dir"));	
		SimpleDateFormat df = new SimpleDateFormat("HH:mm");
		SimpleDateFormat dayformat = new SimpleDateFormat("dd");
		Date now = null;
		Date beginTime = null;
		Date endTime = null;
		Date today = null;
		try {
			now = df.parse(df.format(new Date()));
			beginTime = df.parse("09:30");
			endTime = df.parse("19:13");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
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
		
		while (!result) {
			try {
				try {
					now = df.parse(df.format(new Date()));
					today = dayformat.parse(dayformat.format(new Date()));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (belongCalendar(now, beginTime, endTime)) {
					runStock(webDriver);
					System.out.println(df.format(new Date()) + "  ----OK");
					Thread.sleep(60 * 1000);
				} else {
					Thread.sleep(1 * 1000);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static int runStock(WebDriver webDriver) throws IOException {
		try {
			webDriver.navigate().refresh();
			// Waiting for element for 10 seconds
			WebDriverWait wait = new WebDriverWait(webDriver, 10);
			try {
				wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".backg")));
			} catch (Exception e1) {
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
					System.out.println("system error");
				}
			}

			wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".backg")));
			// Get the holding stocks
			try {
				WebElement posi = webDriver.findElement(By.cssSelector("#position-table"));
				List<WebElement> tp = posi.findElements(By.cssSelector(".position_tr"));
				for (WebElement tpp : tp) {
					List<WebElement> td = tpp.findElements(By.cssSelector("td"));
					for (WebElement tdd : td) {
						posStr = posStr + ";" + tdd.getText();
					}
					cuposTrantd.add(posStr);
					posStr = "";
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Thread.sleep(3 * 1000); 
			
			// Get the transaction data
			try {
				WebElement trans = webDriver.findElement(By.cssSelector("#transaction-table"));
				List<WebElement> tr = trans.findElements(By.cssSelector(".transaction_tr"));
				for (WebElement tpp : tr) {
					List<WebElement> td = tpp.findElements(By.cssSelector("td"));
					for (WebElement tdd : td) {
						currStr = currStr + ";" + tdd.getText();
					}
					currTrantd.add(currStr);
					currStr = "";
				}

				for (String finalval : currTrantd) {
					if (!hisTrantd.contains(finalval))
						// finalval is in currTrantd but not in
						// hisTrantd. Add it as unique.
						uniquevalues.add(finalval);
				}
				// assemble the mail content
				String mailContent = "";
				if (!uniquevalues.isEmpty()) {
					// Assemble the mail content
					for (String mailStr : uniquevalues) {
						mailContent = mailContent + "<br>" + mailStr;
					}
					System.out.println("The below transaction data----");
					System.out.println(uniquevalues + "\n");
					System.out.println("The below holding stocks -----");
					System.out.println(cuposTrantd + "\n");
					hisTrantd = (ArrayList<String>) currTrantd.clone();
					TestEmail.sendmail(mailContent);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

			cuposTrantd.clear();
			currTrantd.clear();
			uniquevalues.clear();
			return 1;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}

	}
	
	/**
	 * 判断时间是否在时间段内
	 * 
	 * @param nowTime
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	public static boolean belongCalendar(Date nowTime, Date beginTime,
			Date endTime) {
		Calendar date = Calendar.getInstance();
		date.setTime(nowTime);
 
		Calendar begin = Calendar.getInstance();
		begin.setTime(beginTime);
 
		Calendar end = Calendar.getInstance();
		end.setTime(endTime);
 
		if (date.after(begin) && date.before(end)) {
			return true;
		} else {
			return false;
			}
		}
	}
