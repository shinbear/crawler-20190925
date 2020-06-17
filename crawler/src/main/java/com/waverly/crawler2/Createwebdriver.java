package com.waverly.crawler2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

public class Createwebdriver {
	public static String URL = "";
	public static String current_url = "";

	public static WebDriver getwebdriver() throws Exception {
		// Acess the WOS page
		URL = "https://www.lib.umd.edu/dbfinder/id/UMD04150";
		// Initialize chrome drive in Seleuium
		System.getProperties().setProperty("webdriver.chrome.driver", "chromedriver.exe");
		// modify the download path
		DesiredCapabilities caps = setDownloadsPath();

		ChromeOptions options = new ChromeOptions();
		options.addArguments("--lang=zh-cn");

		WebDriver webDriver = new ChromeDriver(options);
		// WebDriver webDriver = new ChromeDriver(caps);
		webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		// lanunch the webdriver
		webDriver.get(URL);
		// Input the query condition
		Thread.sleep(3000);
		ArrayList<String> tabs;
		tabs = new ArrayList<String>(webDriver.getWindowHandles());
		if (tabs.size() > 1) {
			for (int a = tabs.size(); a > 1; a--) {
				webDriver.switchTo().window(tabs.get(a - 1));
				Thread.sleep(500);
				webDriver.close();
			}
			webDriver.switchTo().window(tabs.get(0));
		}
		tabs = null;
		webDriver.findElement(By.linkText("Web of Science Core Collection")).click();
		Thread.sleep(3000);
		tabs = new ArrayList<String>(webDriver.getWindowHandles());
		webDriver.switchTo().window(tabs.get(0));
		webDriver.close();
		tabs = new ArrayList<String>(webDriver.getWindowHandles());
		Thread.sleep(3000);
		webDriver.switchTo().window(tabs.get(0));

		WebElement searchElement = webDriver.findElement(By.cssSelector("ul.searchtype-nav"));
		searchElement.findElements(By.cssSelector(".searchtype-sub-nav__list-item")).get(3).click();

		// Get the serarch page URL
		current_url = webDriver.getCurrentUrl();
		return webDriver;

	}

	public static DesiredCapabilities setDownloadsPath() {
		String downloadsPath = "E:\\jobs";
		HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
		chromePrefs.put("download.default_directory", downloadsPath);
		ChromeOptions options = new ChromeOptions();
		options.setExperimentalOption("prefs", chromePrefs);
		DesiredCapabilities caps = new DesiredCapabilities();
		caps.setCapability(ChromeOptions.CAPABILITY, options);
		return caps;
	}

}
