package com.waverly.crawler2;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import jxl.Cell;
import jxl.Sheet;

public class Runwebdriver {
	public static String URL = "";
	public static String current_url = "";

	private static ReadProgress dataProgress = new ReadProgress();
	private static int total = 0;
	private static int page = 0;
	private static int row = 0;
	private static int sim_row = 0;
	public static String auadQuery = "";
	public static WebDriver webDriverIn;
	// The content read from Excel
	public static String ID = "";
	public static String PID = "";
	public static String name = "";
	public static String lastname = "";
	public static String firstname = "";
	public static String midname = "";
	public static String phdu = "";
	public static String phdyr = "";
	public static String phd_country = "";
	public static String search_list = "";
	public static String time_from = "";
	public static String time_to = "";

	public static String author = "";
	public static String authorOrg = "";
	public static int rowid_Total = 0;
	public static int exceptionCode = 0;
	public static int clickCount = 0;
	public static int searchCount = 0;

	// List item String
	public static boolean isFirstPage = true;
	public static boolean isPatentPage = false;
	public static boolean isFirstSearch = true;
	public static String Result[] = new String[40];

	public static String tempLink = "";
	public static String tempTitle = "";

	// Restore the current download info
	public static int rowidIn = 1;
	public static Sheet sheetIn;
	// Restore the current number of records in a search query
	public static int detailRowidIn = 1;
	public static int startPageNumIn = 1;
	public static int runStatus = 0;
	public static int pageRowID = 1;
	public static PrintWriter writerIn;

	public static DesiredCapabilities setDownloadsPath() {
		String downloadsPath = "E:\\jobs";
		HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
		chromePrefs.put("download.default_directory", downloadsPath);
		ChromeOptions options = new ChromeOptions();
		options.setExperimentalOption("prefs", chromePrefs);
		DesiredCapabilities caps = new DesiredCapabilities();
		caps.setCapability(ChromeOptions.CAPABILITY, options);
		;
		return caps;
	}

	public static ArrayList<Object> runRecord(ArrayList<Object> recordlist) throws InterruptedException {
		// initial page data
		total = 0;
		page = 0;
		rowidIn = 0;
		sim_row = 0;

		// import the parameter
		sheetIn = (Sheet) recordlist.get(0);
		rowidIn = (Integer) recordlist.get(1);
		readExcel(sheetIn, rowidIn);
		detailRowidIn = (Integer) recordlist.get(2);

		// webDriverIn = (WebDriver) recordlist.get(3);
		startPageNumIn = (Integer) recordlist.get(4);
		runStatus = (Integer) recordlist.get(5);
		writerIn = (PrintWriter) recordlist.get(6);

		// Show the progress
		dataProgress.setVisible(true);
		Thread thread1 = new Thread(dataProgress);
		thread1.start();

		// get advanced page URL
		getaBlankPage();

		// Run search
		runSearch();
		webDriverIn.quit();

		// 异常退出杀死chromedriver和chrome浏览器！
		try {
			command("taskkill /F /im " + "chromedriver.exe");
			command("taskkill /F /im " + "chrome.exe");
			command("taskkill /F /im " + "RuntimeBroker.exe");
			command("taskkill /F /im " + "GoogleCrashHandler.exe");
			command("taskkill /F /im " + "GoogleCrashHandler64.exe");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Thread.sleep(3000);

		// assign the content to input parameter for runRecord
		recordlist.clear();
		recordlist.add(sheetIn);
		recordlist.add(rowidIn);
		recordlist.add(detailRowidIn);
		recordlist.add("webDriverTemp");
		recordlist.add(startPageNumIn);
		recordlist.add(runStatus);
		recordlist.add(writerIn);

		// stop the info pannel thread
		thread1.interrupt();
		return recordlist;
	}

	public static void runSearch() throws InterruptedException {
		try {
			// Remain the search page
			ArrayList<String> tabs;
			tabs = new ArrayList<String>(webDriverIn.getWindowHandles());
			if (tabs.size() > 1) {
				for (int a = tabs.size(); a > 1; a--) {
					webDriverIn.switchTo().window(tabs.get(a - 1));
					Thread.sleep(500);
					webDriverIn.close();
				}
				webDriverIn.switchTo().window(tabs.get(0));
			}
			tabs = null;

			int status = searchName(webDriverIn);
			if (status == 1) {
				// Get the item name
				int getNameStatus = getAName(webDriverIn);
			} else if (status == 2) {
				// Status is 2 means the result number is zero
				int h;
				for (h = 0; h < 40; h++) {
					Result[h] = "0";
				}
				h = 0;
				writrintExcel();
				Thread.sleep(30000);
				for (h = 0; h < 40; h++) {
					Result[h] = "";
				}
				h = 0;
				runStatus = 1;
				return;
			} else {
				// Status is else means the exception
				int h;
				for (h = 0; h < 40; h++) {
					Result[h] = "SEER";
				}
				h = 0;
				writrintExcel();
				Thread.sleep(30000);
				for (h = 0; h < 40; h++) {
					Result[h] = "";
				}
				h = 0;
				runStatus = 2;
				return;
			}
		} catch (Exception e1) {
			int h;
			for (h = 0; h < 40; h++) {
				Result[h] = "SEER";
			}
			h = 0;
			try {
				writrintExcel();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (h = 0; h < 40; h++) {
				Result[h] = "";
			}
			h = 0;
			runStatus = 2;
			return;
		}
		return;
	}

	public static int searchName(WebDriver webDriver) throws IOException {
		try {
			// Waiting for element for 10 seconds

			WebDriverWait wait = new WebDriverWait(webDriver, 30);
			wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//*[@id=\"value(input1)\"]")));

			// Input the author
			String searchQuery = search_list;
			WebElement author_input = webDriver.findElement(By.xpath("//*[@id=\"value(input1)\"]"));
			author_input.clear();
			author_input.sendKeys(searchQuery);

			// scroll to the element
			String js3 = "arguments[0].scrollIntoView();";
			WebElement element = webDriver.findElement(By.xpath("//*[@id=\"value(input1)\"]"));
			((JavascriptExecutor) webDriver).executeScript(js3, element);

			// Year range from to
			Thread.sleep(1000);
			Select yearRange = new Select(webDriver.findElement(By.cssSelector(".j-custom-select-yeardropdown")));
			yearRange.selectByIndex(6);
			WebElement timeSpan = webDriver.findElement(By.cssSelector(".timespan_custom"));
			List<WebElement> tss = timeSpan.findElements(By.cssSelector(".select2-container--yeardropdown"));
			tss.get(0).click();
			Thread.sleep(500);
			WebElement yearFrom = webDriver.findElement(By.cssSelector(".select2-search__field"));
			yearFrom.clear();
			yearFrom.sendKeys(time_from);
			yearFrom.sendKeys(Keys.ENTER);
			tss.get(1).click();
			Thread.sleep(500);
			WebElement yearTo = webDriver.findElement(By.cssSelector(".select2-search__field"));
			yearTo.clear();
			yearTo.sendKeys(time_to);
			yearTo.sendKeys(Keys.ENTER);

			if (isFirstSearch) {
				try {
					Thread.sleep(3000);
					// Input the language
					Select select_language = new Select(webDriver.findElement(By.xpath("//*[@id='value(input2)']")));
					// deselect all option
					select_language.deselectAll();
					select_language.selectByIndex(0);

					// Input the article
					Select select_article = new Select(webDriver.findElement(By.xpath("//*[@id='value(input3)']")));
					// deselect all option
					select_article.deselectAll();
					select_article.selectByIndex(1);
					isFirstSearch = false;
				} catch (Exception e1) {
				}
			}

			try {
				String searchSetNumStr = webDriver.findElements(By.cssSelector(".historySetNum")).get(0).getText();
				searchSetNumStr = searchSetNumStr.substring(searchSetNumStr.indexOf("#") + 1).replace(" ", "");
				int searchSetNum = Integer.parseInt(searchSetNumStr);
				if (searchSetNum > 100) {
					webDriver.findElement(By.cssSelector("#SearchHistoryTableBanner + table"))
							.findElement(By.cssSelector("[title='选择所有检索式']")).click();
					webDriver.findElement(By.cssSelector("#SearchHistoryTableBanner + table"))
							.findElement(By.cssSelector("#deleteTop")).click();
				}
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

			// Click "search" button
			((ChromeDriver) webDriver).findElementByXPath("//*[@id='search-button']").click();

			// Waiting for the result for 10 seconds
			wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".historyResults")));

			// Get the link
			// Close the detail page and return the list
			// page
			ArrayList<String> tabs;
			tabs = new ArrayList<String>(webDriver.getWindowHandles());
			if (tabs.size() > 1) {
				for (int a = tabs.size(); a > 1; a--) {
					webDriver.switchTo().window(tabs.get(a - 1));
					Thread.sleep(1500);
					webDriver.close();
				}
				webDriver.switchTo().window(tabs.get(0));
			}
			tabs.clear();

			JavascriptExecutor executor = (JavascriptExecutor) webDriver;
			String searchlink = "";
			String searchResultNo = "";

			try {
				if (webDriver.findElement(By.cssSelector(".errorText")).getText().contains("zh_CN")) {
					return 4;
				} else if (webDriver.findElement(By.cssSelector(".errorText")).getText().length() > 0) {
					return 3;
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			try {
				searchResultNo = webDriver.findElements(By.cssSelector(".historyResults")).get(0).getText();
				if (!searchResultNo.equals("0")) {
					WebElement searchlink_webelement = webDriver.findElements(By.cssSelector(".historyResults")).get(0)
							.findElement(By.cssSelector("a"));
					searchlink = searchlink_webelement.getAttribute("href");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				searchlink = "";
				e.printStackTrace();
				return 0;
			}
			if (searchlink != null && searchlink.length() != 0) {
				tempLink = searchlink;
				executor.executeScript("window.open('" + searchlink + "')");
			} else {
				return 2;
			}
			Thread.sleep(3000);
			return 1;
		} catch (Exception e2) {
			return 0;
		}
	}

	public static int getAName(WebDriver webDriver) throws IOException {
		try {
			// Shift the second page
			ArrayList<String> tabs;
			tabs = new ArrayList<String>(webDriver.getWindowHandles());
			if (tabs.size() > 1) {
				for (int a = tabs.size(); a > 1; a--) {
					if (a > 2) {
						webDriver.switchTo().window(tabs.get(a - 1));
						Thread.sleep(500);
						webDriver.close();
					}
				}
				webDriver.switchTo().window(tabs.get(1));
			}
			tabs = null;

			// Get the page number
			int pages;
			try {
				webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
				// Waiting for element for 10 seconds
				WebDriverWait wait = new WebDriverWait(webDriver, 40);
				wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#pageCount\\.top")));

				// Get the result number
				String pc_string = webDriver.findElement(By.cssSelector("#pageCount\\.top")).getText();
				// Remove the characters
				Pattern pattern = Pattern.compile("[^0-9]");
				Matcher matcher = pattern.matcher(pc_string);
				pc_string = matcher.replaceAll("");

				if (!pc_string.equals("")) {
					if (Integer.parseInt(pc_string) == 0) {
						pages = 0;
						// result array clear
						int h;
						for (h = 0; h < 40; h++) {
							Result[h] = "0";
						}
						h = 0;
						// run status is normal
						runStatus = 1;
						writrintExcel();
						searchCount++;
						if (searchCount > 3) {
							Thread.sleep(10000);
						}
						for (h = 0; h < 40; h++) {
							Result[h] = "";
						}
						h = 0;
						return 0;
					} else {
						searchCount = 0;
						pages = Integer.parseInt(pc_string);
						if (pages > 100) {
							int h;
							for (h = 0; h < 40; h++) {
								Result[h] = "OT";
							}
							h = 0;
							// run status is normal
							runStatus = 1;
							writrintExcel();
							for (h = 0; h < 40; h++) {
								Result[h] = "";
							}
							searchCount++;
							if (searchCount > 3) {
								Thread.sleep(10000);
							}
							return 1;
						}
					}
				} else {
					pages = 0;
					searchCount++;
					if (searchCount > 3) {
						Thread.sleep(10000);
					}
					// result array clear
					int h;
					for (h = 0; h < 40; h++) {
						Result[h] = "0";
					}
					h = 0;

					// run status is normal
					runStatus = 1;
					writrintExcel();
					for (h = 0; h < 40; h++) {
						Result[h] = "";
					}
					h = 0;
					return 0;
				}
				// If the result is too less, then sleep 15 seconds
				if (Integer.parseInt(pc_string) < 3) {
					Thread.sleep(2000);
				}
			} catch (Exception e1) {
				pages = 0;
				// result array clear
				int h;
				for (h = 0; h < 40; h++) {
					Result[h] = "REREAD";
				}
				Result[33] = tempLink;
				h = 0;

				// run status is abnormal
				runStatus = 2;
				writrintExcel();
				Result[33] = "";
				tempLink = "";
				Thread.sleep(30000);
				for (h = 0; h < 40; h++) {
					Result[h] = "";
				}
				h = 0;
				return 2;
			}

			total = pages;
			dataProgress.setPanel(total, page, rowidIn, sim_row);

			page = page + startPageNumIn;
			dataProgress.setPanel(total, page, rowidIn, sim_row);

			WebDriverWait wait;
			int specPage, specRow;
			specPage = detailRowidIn / 10 + 1;
			specRow = detailRowidIn % 10;
			startPageNumIn = specPage;

			// go to the specific page
			try {
				if (startPageNumIn != 1) {
					WebElement next = webDriver.findElement(By.cssSelector("[title='下一页']"));
					next.click();
					// Waiting for element for 10 seconds
					wait = new WebDriverWait(webDriver, 40);
					wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#pageCount\\.top")));
					String current_url = webDriver.getCurrentUrl();
					String cut_url = current_url.substring(0, current_url.lastIndexOf("=") + 1);
					webDriver.get(cut_url + String.valueOf(startPageNumIn));
				}
				wait = new WebDriverWait(webDriver, 40);
				wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#pageCount\\.top")));
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				int h;
				for (h = 0; h < 40; h++) {
					Result[h] = "RESTARTPAGE";
				}
				Result[33] = tempLink;
				h = 0;
				// run status is abnormal
				runStatus = 2;
				writrintExcel();
				Result[33] = "";
				tempLink = "";
				for (h = 0; h < 40; h++) {
					Result[h] = "";
				}
				h = 0;
				Thread.sleep(30000);
				return 2;
			}

			// Loop in pages
			for (int k = startPageNumIn - 1; k < pages; k++) {
				page = startPageNumIn - 1;
				page++;
				// Close detail page return the list page
				// ArrayList<String> tabs;
				tabs = new ArrayList<String>(webDriver.getWindowHandles());
				if (tabs.size() > 1) {
					for (int a = tabs.size(); a > 1; a--) {
						if (a > 2) {
							webDriver.switchTo().window(tabs.get(a - 1));
							Thread.sleep(500);
							webDriver.close();
						}
					}
					webDriver.switchTo().window(tabs.get(1));
				}
				tabs = null;
				;
				// Waiting for element for 10 seconds
				wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".search-results")));
				WebElement ta = webDriver.findElement(By.cssSelector(".search-results"));
				List<WebElement> tb = ta.findElements(By.cssSelector(".search-results-item"));

				// Get row loop
				pageRowID = 0;
				for (WebElement tbb : tb) {
					pageRowID++;
					// skip to the specific row
					if (pageRowID < specRow) {
						continue;
					}
					try {
						tabs = new ArrayList<String>(webDriver.getWindowHandles());
						if (tabs.size() > 1) {
							for (int a = tabs.size(); a > 1; a--) {
								if (a > 2) {
									webDriver.switchTo().window(tabs.get(a - 1));
									Thread.sleep(500);
									webDriver.close();
								}
							}
							webDriver.switchTo().window(tabs.get(1));
						}
						tabs = null;

						row++;
						dataProgress.setPanel(total, page, rowidIn, sim_row);
						// result array clear
						int h;
						for (h = 0; h < 40; h++) {
							Result[h] = " ";
						}
						h = 0;

						// Get the result row
						List<WebElement> tc = tbb.findElements(By.cssSelector(".search-results-content"));

						// Title
						WebElement titleItem = tbb.findElement(By.cssSelector("a.smallV110"));
						Result[3] = titleItem.getText();
						tempTitle = Result[3];

						/// Get the record link
						String detailrecord = titleItem.getAttribute("href");
						tempLink = detailrecord;

						/*
						 * author List<WebElement> authorItem =
						 * tc.get(1).findElements(By.cssSelector("a[title]"));
						 * for (WebElement tAu : authorItem) { Result[1] =
						 * Result[1] + ";" + tAu.getText(); } Result[1] =
						 * Result[1].substring(1);
						 */

						for (int p = 0; p < tc.size(); p++) {
							List<WebElement> td = tc.get(p).findElements(By.cssSelector("div"));
							for (int j = 0; j < td.size(); j++) {
								if (td.get(j).getText().contains("出版年")) {
									String sourceStr;
									try {
										sourceStr = td.get(j).getText();
									} catch (Exception e) {
										// TODO Auto-generated catch block
										sourceStr = "";
										e.printStackTrace();
									}
									// Journal
									try {
										Result[4] = sourceStr.substring(0, sourceStr.indexOf("卷:"))
												.replaceAll("(\\r\\n|\\r|\\n|\\n\\r)", "");
									} catch (Exception e) {
										// TODO Auto-generated catch block
										Result[4] = "";
										e.printStackTrace();
									}
									// Publish Year
									try {
										String publishDate, publishYear, publishmonth;
										publishDate = sourceStr.substring(sourceStr.indexOf("出版年: ") + 4);
										// match year format
										Pattern pattern = Pattern.compile("(19|20)[0-9]{2}");
										Matcher matcher = pattern.matcher(publishDate);
										publishmonth = matcher.replaceAll("");
										Pattern pattern2 = Pattern.compile("[(\\u4e00-\\u9fa5)]");
										Matcher matcher2 = pattern2.matcher(publishmonth);
										publishmonth = matcher2.replaceAll("");
										publishmonth = publishmonth.replaceAll("-|\\r\\n|\\r|\\n|\\n\\r", " ");

										Matcher matcher3 = pattern.matcher(publishDate);
										if (matcher3.find()) {
											publishYear = matcher3.group();
										} else {
											publishYear = publishDate.substring(
													publishDate.indexOf(publishmonth) + publishmonth.length());
										}
										Result[22] = publishYear;
										Result[21] = publishmonth;
									} catch (Exception e) {
										// TODO Auto-generated catch block
										Result[22] = "";
										Result[21] = "";
										e.printStackTrace();
									}

								}
							}
						}

						// Being cite of web science
						try {
							WebElement beingCiteItem = tbb.findElement(By.cssSelector(".search-results-data-cite"));
							WebElement beingCiteItem_remove = webDriver
									.findElement(By.cssSelector(".search-results-data-cite .en_data_bold"));
							String beingCiteStr = beingCiteItem.getText().substring(0,
									beingCiteItem.getText().indexOf(beingCiteItem_remove.getText()));
							// Remove the characters
							Pattern pattern = Pattern.compile("[^0-9]");
							Matcher matcher = pattern.matcher(beingCiteStr);
							Result[13] = matcher.replaceAll("");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							Result[13] = "";
							e.printStackTrace();
						}

						// Open the detail record page
						JavascriptExecutor executor = (JavascriptExecutor) webDriver;
						try {
							executor.executeScript("window.open('" + detailrecord + "')");

							// Switch to detail page
							tabs = new ArrayList<String>(webDriver.getWindowHandles());
							if (tabs.size() > 1) {
								for (int a = tabs.size(); a > 1; a--) {
									if (a > 3) {
										webDriver.switchTo().window(tabs.get(a - 1));
										try {
											Thread.sleep(500);
										} catch (Exception e2) {
										}
										webDriver.close();
									}
								}
								webDriver.switchTo().window(tabs.get(2));
							}
							tabs = null;

							wait = new WebDriverWait(webDriver, 40);
							/*
							 * temporal change wait.until(ExpectedConditions.
							 * presenceOfAllElementsLocatedBy( By.xpath(
							 * "//*[@id='records_form']/div/div/div/div[1]/div/div[1]/value"
							 * )));
							 */
							wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".title")));

							int detailStatus;
							detailStatus = getDetail(webDriver);
							// if read the row data problem, throw exception
							if (detailStatus == 0) {
								int j;
								for (j = 0; j < 40; j++) {
									Result[j] = "ROWER1";
								}
								Result[33] = Integer.toString(pageRowID);
								j = 0;
								// run status is abnormal
								runStatus = 2;
								writrintExcel();
								for (h = 0; h < 40; h++) {
									Result[h] = "";
								}
								h = 0;
								return 2;
							}
						} catch (Exception e3) {
							int j;
							for (j = 0; j < 40; j++) {
								Result[j] = "ROWER1";
							}
							Result[33] = Integer.toString(pageRowID);
							j = 0;
							// run status is abnormal
							runStatus = 2;
							writrintExcel();
							for (h = 0; h < 40; h++) {
								Result[h] = "";
							}
							h = 0;
							return 2;
						}

						// Write the data into excel
						writrintExcel();
						detailRowidIn++;
						runStatus = 1;
						tempLink = "";
						tempTitle = "";
						tabs = new ArrayList<String>(webDriver.getWindowHandles());
						if (tabs.size() > 1) {
							for (int a = tabs.size(); a > 1; a--) {
								if (a > 2) {
									webDriver.switchTo().window(tabs.get(a - 1));
									Thread.sleep(500);
									webDriver.close();
								}
							}
							webDriver.switchTo().window(tabs.get(1));
						}
						tabs = null;
						// result array clear
						for (h = 0; h < 40; h++) {
							Result[h] = " ";
						}
						h = 0;
					} catch (Exception e) {
						// Write the data into excel
						// result array clear
						int j;
						for (j = 0; j < 40; j++) {
							Result[j] = "ROWER2";
						}
						j = 0;
						// run status is abnormal
						runStatus = 2;
						Result[33] = Integer.toString(pageRowID);
						Result[3] = tempTitle;
						writrintExcel();
						Result[33] = "";
						Result[3] = "";
						tempLink = "";
						tempTitle = "";

						// Close the detail page and return the list
						// page
						tabs = new ArrayList<String>(webDriver.getWindowHandles());
						if (tabs.size() > 1) {
							for (int a = tabs.size(); a > 1; a--) {
								if (a > 2) {
									webDriver.switchTo().window(tabs.get(a - 1));
									Thread.sleep(500);
									webDriver.close();
								}
							}
							webDriver.switchTo().window(tabs.get(1));
						}
						tabs = null;
						return 2;
					}
				}

				// Close the detail page and return the list
				// page
				tabs = new ArrayList<String>(webDriver.getWindowHandles());
				if (tabs.size() > 1) {
					for (int a = tabs.size(); a > 1; a--) {
						if (a > 2) {
							webDriver.switchTo().window(tabs.get(a - 1));
							Thread.sleep(500);
							webDriver.close();
						}
					}
					webDriver.switchTo().window(tabs.get(1));
				}
				tabs = null;

				// get the next page if it is not last page
				int turnpage = 0;
				if (k < pages - 1) {
					try {
						WebElement next = webDriver.findElement(By.cssSelector("[title='下一页']"));
						next.click();
					} catch (Exception e3) {
						// writrintExcel();
						Thread.sleep(10000);
						int h;
						for (h = 0; h < 40; h++) {
							Result[h] = "TurnPageWR";
						}
						writrintExcel();
						// run status is abnormal
						runStatus = 2;

						for (h = 0; h < 40; h++) {
							Result[h] = "";
						}
						h = 0;
						return 2;
					}
				}
			}
			Thread.sleep(3000);
			int h;
			for (h = 0; h < 40; h++) {
				Result[h] = "";
			}
			h = 0;
			return 1;
		} catch (Exception e2) {
			int h;
			for (h = 0; h < 40; h++) {
				Result[h] = "ROWER1";
			}
			h = 0;
			try {
				writrintExcel();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (h = 0; h < 40; h++) {
				Result[h] = "";
			}
			h = 0;
			runStatus = 2;
			// TODO Auto-generated catch block
			e2.printStackTrace();
			return 2;
		}
	}

	public static int getDetail(WebDriver webDriver) throws IOException {
		// Switch to detail page
		ArrayList<String> tabs;
		tabs = new ArrayList<String>(webDriver.getWindowHandles());
		if (tabs.size() > 1) {
			for (int a = tabs.size(); a > 1; a--) {
				if (a > 3) {
					webDriver.switchTo().window(tabs.get(a - 1));
					try {
						Thread.sleep(500);
					} catch (Exception e2) {
					}
					webDriver.close();
				}
			}
			webDriver.switchTo().window(tabs.get(2));
		}
		tabs = null;

		try {
			WebDriverWait wait = new WebDriverWait(webDriver, 40);

			/*
			 * temporal change wait.until(ExpectedConditions.
			 * presenceOfAllElementsLocatedBy( By.xpath(
			 * "//*[@id='records_form']/div/div/div/div[1]/div/div[1]/value"
			 * )));
			 */
			wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".title")));

			if (webDriver.findElement(By.cssSelector("#hidden_section_label")).getText().contains("查看更多数据字段")) {
				// see more
				try {
					webDriver.findElement(By.linkText("查看更多数据字段")).click();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			// Gether FR_label
			List<WebElement> FRLabel = webDriver.findElements(By.cssSelector(".FR_label"));
			ArrayList<String> FRLabelStr = new ArrayList<String>();
			for (WebElement FRLabellist : FRLabel) {
				FRLabelStr.add(FRLabellist.getText());
			}

			try {
				// Get the author‘s short & full names
				String authorCombine = "";
				String authorShortName = "";
				String authorFullName = "";
				WebElement authorItem = webDriver.findElement(By.xpath("//*[text()='作者:']/.."));
				authorCombine = authorItem.getText();

				if (authorCombine.contains("更多内容")) {
					authorItem.findElement(By.cssSelector("#show_more_authors_authors_txt_label")).click();

				}

				authorItem = webDriver.findElement(By.xpath("//*[text()='作者:']/.."));
				authorCombine = authorItem.getText();
				try {
					if (authorCombine.contains("...更少内容")) {
						authorCombine = authorCombine.substring(0, authorCombine.indexOf("...更少内容"));
					}

					// get the full & short names
					Pattern pattern = Pattern.compile("\\(.*?\\)|\\[.*?\\]");
					Matcher matcher = pattern.matcher(authorCombine);

					while (matcher.find()) {
						for (int i = 0; i <= matcher.groupCount(); i++) {
							if (matcher.group(i).subSequence(0, 1).equals("[")) {
								authorFullName = authorFullName + " " + matcher.group(i);
							} else {
								authorFullName = authorFullName + ";" + matcher.group(i);
							}
						}
					}
					authorFullName = authorFullName.substring(1).replaceAll("\\(|\\)", "");
					authorShortName = matcher.replaceAll("").replaceAll("  ; ", ";");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					authorFullName = authorCombine;
					authorShortName = authorCombine;
					e.printStackTrace();
				}
				if (FRLabelStr.contains("团体作者:")) {
					WebElement authorGroupItem = webDriver.findElement(By.xpath("//*[text()='团体作者:']/.."));
					authorFullName = authorFullName + "||" + authorGroupItem.getText();
				}
				Result[1] = authorShortName.replace('\n', ' ').replace("作者:", "");
				Result[2] = authorFullName.replace('\n', ' ').replace("作者:", "");
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
				Result[1] = "";
				Result[2] = "";
			}
			// see more
			try {
				webDriver.findElement(By.linkText("查看更多数据字段")).click();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// Get Volume
			try {
				WebElement volumeStr;
				try {
					volumeStr = webDriver.findElement(By.xpath("//*[text()='卷:']/following-sibling::value"));
				} catch (Exception e) {
					volumeStr = webDriver.findElement(By.xpath("//*[text()='卷:']/.."));
					e.printStackTrace();
				}
				Result[23] = volumeStr.getText().replaceAll("卷:", "");
			} catch (Exception e) {
				Result[23] = " ";
			}

			// Get phase
			try {
				WebElement phaseStr;
				try {
					phaseStr = webDriver.findElement(By.xpath("//*[text()='期:']/following-sibling::value"));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					phaseStr = webDriver.findElement(By.xpath("//*[text()='期:']/.."));
					e.printStackTrace();
				}
				Result[24] = "*" + phaseStr.getText().replaceAll("期:", "");
			} catch (Exception e) {
				Result[24] = " ";
			}

			// Get page
			try {
				WebElement pageStr;
				try {
					pageStr = webDriver.findElement(By.xpath("//*[text()='页:']/following-sibling::value"));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					pageStr = webDriver.findElement(By.xpath("//*[text()='页:']/.."));
					e.printStackTrace();
				}
				String[] strPageArray = pageStr.getText().replaceAll("页:", "").split("-");
				Result[25] = strPageArray[0];
				Result[26] = strPageArray[1];
			} catch (Exception e) {
				Result[25] = "";
				Result[26] = "";
			}

			// Get the all being cite
			try {
				WebElement beCite = webDriver.findElement(By.xpath("//*[text()=' / 所有数据库']"));
				Result[14] = beCite.getText().substring(0, beCite.getText().indexOf(" / 所有数据库"));
			} catch (Exception e) {
				Result[14] = "";
			}

			// Get article category
			try {
				try {
					Result[6] = webDriver.findElement(By.xpath("//*[text()='文献类型:']/following-sibling::span"))
							.getText();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Result[6] = webDriver.findElement(By.xpath("//*[text()='文献类型:']/..")).getText().replaceAll("文献类型:",
							"");
					e.printStackTrace();
				}
			} catch (Exception e) {
				Result[6] = "";
			}

			// Get the DOI
			if (FRLabelStr.contains("DOI:")) {
				try {
					try {
						Result[27] = webDriver.findElement(By.xpath("//*[text()='DOI:']/following-sibling::value"))
								.getText();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						Result[27] = webDriver.findElement(By.xpath("//*[text()='DOI:']/..")).getText()
								.replaceAll("DOI:", "");
						e.printStackTrace();
					}
				} catch (Exception e) {
					Result[27] = "";
				}
			}

			// get author keywords & keywords plus
			String keywordsStr = "";
			if (FRLabelStr.contains("作者关键词:")) {
				try {
					List<WebElement> tk = webDriver
							.findElements(By.xpath("//*[text()='作者关键词:']/../following-sibling::a"));
					for (WebElement tkk : tk) {
						keywordsStr = keywordsStr + ";" + tkk.getText();
					}
					keywordsStr = keywordsStr.substring(1);
				} catch (Exception e) {
					keywordsStr = "";
				}
			}

			String keywordsPlusStr = "";
			if (FRLabelStr.contains("KeyWords Plus:")) {
				try {
					List<WebElement> tl = webDriver
							.findElements(By.xpath("//*[text()='KeyWords Plus:']/following-sibling::a"));
					for (WebElement tll : tl) {
						keywordsPlusStr = keywordsPlusStr + ";" + tll.getText();
					}
					keywordsPlusStr = keywordsPlusStr.substring(1);
				} catch (Exception e) {
					keywordsPlusStr = "";
				}
				Result[7] = keywordsStr + ";" + keywordsPlusStr;
				Result[7] = Result[7].substring(1).replace('\n', ' ');
				;
			}

			// Get the corresponding address
			try {
				List<WebElement> addressItem = webDriver.findElements(
						By.xpath("//span[contains(text(), '通讯作者地址:')]/../following-sibling::table/tbody/tr"));
				for (WebElement tkk3 : addressItem) {
					if (tkk3.getText().substring(0, 1).equals("[")) {
						Result[8] = Result[8] + "||" + tkk3.getText();
					} else {
						Result[9] = tkk3.getText();
					}
				}
				Result[8] = Result[8].substring(2).replace('\n', ' ');
				;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Result[8] = "";
				Result[9] = "";
			}

			// scroll to the element of "email address" title
			try {
				String js4 = "arguments[0].scrollIntoView();";
				WebElement element = webDriver.findElement(By.xpath("//*[text()='电子邮件地址:']"));
				((JavascriptExecutor) webDriver).executeScript(js4, element);
			} catch (Exception e) {
			}

			// Get email address
			try {
				List<WebElement> temail = webDriver
						.findElements(By.xpath("//*[text()='电子邮件地址:']/following-sibling::a"));
				for (WebElement tee : temail) {
					Result[10] = tee.getText();
				}
			} catch (Exception e) {
				Result[10] = "";
			}

			// Get the being cite 180days&since 2013
			WebElement sideBar = webDriver.findElement(By.cssSelector("#sidebar-container"));

			try {
				List<WebElement> tbs = sideBar
						.findElements(By.xpath("//*[text()='在 Web of Science 中 使用次数']/../following-sibling::div/div"));
				Result[15] = tbs.get(0).getText();
				Result[16] = tbs.get(1).getText();

			} catch (Exception e) {
				Result[15] = "";
				Result[16] = "";
			}

			// Get the fund organization & authorized codes
			// Gether title3
			List<WebElement> title3 = webDriver.findElements(By.cssSelector(".title3"));
			ArrayList<String> title3Str = new ArrayList<String>();
			for (WebElement title3list : title3) {
				title3Str.add(title3list.getText());
			}

			if (title3Str.contains("基金资助致谢")) {
				try {
					List<WebElement> tfd = webDriver
							.findElements(By.xpath("//*[text()='基金资助致谢']/following-sibling::table/tbody/tr"));
					tfd.remove(0);
					String fundOrgSingle = "";
					for (WebElement tfdd : tfd) {
						String tfddCodeStr = "";
						if (!tfdd.findElements(By.cssSelector("td")).get(0).getText().equals("")) {
							Result[11] = Result[11] + "||" + tfdd.findElements(By.cssSelector("td")).get(0).getText();
							try {
								List<WebElement> tfddCodes = tfdd.findElements(By.cssSelector("td")).get(1)
										.findElements(By.cssSelector("div"));
								for (WebElement tfddCode : tfddCodes) {
									tfddCodeStr = tfddCodeStr + "&" + tfddCode.getText();
								}
								tfddCodeStr = tfddCodeStr.substring(1);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								tfddCodeStr = "None";
							}
							Result[11] = Result[11] + ";" + tfddCodeStr;
						} else {
							break;
						}
					}
					if (Result[11].length() > 0)
						Result[11] = Result[11].substring(2);
					Result[11] = Result[11].replace('\n', ' ');
					;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			// scroll to the element of fund assistant information
			if (title3Str.contains("基金资助致谢")) {
				try {
					String js5 = "arguments[0].scrollIntoView();";
					WebElement element = webDriver.findElement(By.xpath("//*[contains(text(), '查看基金资助信息')]"));
					((JavascriptExecutor) webDriver).executeScript(js5, element);
				} catch (Exception e) {
				}
			}

			// Get the fund assistant information
			if (title3Str.contains("基金资助致谢")) {
				try {
					webDriver.findElement(By.xpath("//*[contains(text(), '查看基金资助信息')]")).click();
					WebElement fdi = webDriver.findElement(By.xpath("//*[text()='基金资助致谢']/.."));

					Result[12] = fdi.findElement(By.cssSelector("#show_fund_blurb")).getText();
					Result[12] = Result[12].replace('\n', ' ');
				} catch (Exception e) {
					Result[12] = "";
				}
			}

			// scroll to the element of "类别 / 分类"
			if (title3Str.contains("类别 / 分类")) {
				try {
					String js6 = "arguments[0].scrollIntoView();";
					WebElement element = webDriver.findElement(By.xpath("//*[text()='类别 / 分类']"));
					((JavascriptExecutor) webDriver).executeScript(js6, element);
				} catch (Exception e) {
				}
			}

			// Get the research direction
			if (FRLabelStr.contains("研究方向:")) {
				try {
					WebElement tl = webDriver.findElement(By.xpath("//*[text()='研究方向:']/.."));
					Result[29] = tl.getText().substring(5);
				} catch (Exception e) {
					Result[29] = "";
				}
			}

			// Get the Web of Science category
			if (FRLabelStr.contains("Web of Science 类别:")) {
				try {
					WebElement tl = webDriver.findElement(By.xpath("//*[text()='Web of Science 类别:']/.."));
					Result[28] = tl.getText().substring(18);
				} catch (Exception e) {
					Result[28] = "";
				}
			}

			// Get language
			try {
				WebElement tl = webDriver.findElement(By.xpath("//*[text()='语言:']/.."));
				Result[5] = tl.getText().substring(3);
			} catch (Exception e) {
				Result[5] = "";
			}

			// Get ru zang number
			if (FRLabelStr.contains("入藏号:")) {
				try {
					WebElement tl = webDriver.findElement(By.xpath("//*[text()='入藏号:']/.."));
					Result[31] = tl.getText().substring(4);
				} catch (Exception e) {
					Result[31] = "";
				}
			}

			// Get IDS number
			if (FRLabelStr.contains("IDS 号:")) {
				try {
					WebElement tl;
					try {
						tl = webDriver.findElement(By.xpath("//*[text()='IDS 号:']/following-sibling::value"));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						tl = webDriver.findElement(By.xpath("//*[text()='IDS 号:']/.."));
						e.printStackTrace();
					}
					Result[30] = tl.getText().replaceAll("IDS 号", "");
				} catch (Exception e) {
					Result[30] = "";
				}
			}

			// Get PubMed ID
			if (FRLabelStr.contains("PubMed ID:")) {
				try {
					WebElement tl;
					try {
						tl = webDriver.findElement(By.xpath("//*[text()='PubMed ID:']/following-sibling::value"));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						tl = webDriver.findElement(By.xpath("//*[text()='PubMed ID:']/.."));
						e.printStackTrace();
					}
					Result[32] = tl.getText().replaceAll("PubMed ID:", "");
				} catch (Exception e) {
					Result[32] = "";
				}
			}

			// Get ISSN:
			if (FRLabelStr.contains("ISSN:")) {
				try {
					WebElement tl;
					try {
						tl = webDriver.findElement(By.xpath("//*[text()='ISSN:']/following-sibling::value"));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						tl = webDriver.findElement(By.xpath("//*[text()='ISSN:']/.."));
						e.printStackTrace();
					}
					Result[17] = tl.getText().replaceAll("ISSN:", "");
				} catch (Exception e) {
					Result[17] = "";
				}
			}

			// Get eISSN
			if (FRLabelStr.contains("eISSN:")) {
				try {
					WebElement tl;
					try {
						tl = webDriver.findElement(By.xpath("//*[text()='eISSN:']/following-sibling::value"));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						tl = webDriver.findElement(By.xpath("//*[text()='eISSN:']/.."));
						e.printStackTrace();
					}
					Result[18] = tl.getText().replaceAll("eISSN:", "");
				} catch (Exception e) {
					Result[18] = "";
				}
			}
			return 1;
		} catch (Exception e) {
			return 0;
		}

	}

	public static void writrintExcel() throws IOException {
		// write into excel
		writerIn.println(ID + "\t" + "*" + PID + "\t" + name + "\t" + lastname + "\t" + firstname + "\t" + midname
				+ "\t" + phdu + "\t" + phdyr + "\t" + phd_country + "\t" + search_list + "\t" + time_from + "\t"
				+ time_to + "\t" + Result[0] + "\t" + Result[1] + "\t" + Result[2] + "\t" + Result[3] + "\t" + Result[4]
				+ "\t" + Result[5] + "\t" + Result[6] + "\t" + Result[7] + "\t" + Result[8] + "\t" + Result[9] + "\t"
				+ Result[10] + "\t" + Result[11] + "\t" + Result[12] + "\t" + Result[13] + "\t" + Result[14] + "\t"
				+ Result[15] + "\t" + Result[16] + "\t" + Result[17] + "\t" + Result[18] + "\t" + Result[19] + "\t"
				+ Result[20] + "\t" + Result[21] + "\t" + Result[22] + "\t" + Result[23] + "\t" + Result[24] + "\t"
				+ Result[25] + "\t" + Result[26] + "\t" + Result[27] + "\t" + Result[28] + "\t" + Result[29] + "\t"
				+ Result[30] + "\t" + Result[31] + "\t" + Result[32] + "\t" + Result[33]);
		writerIn.flush();
	}

	public static void getAdvancedPage() throws InterruptedException {
		// Acess the WOS page
		// URL = "https://www.lib.umd.edu/dbfinder/id/UMD04150";
		// Initialize chrome drive in Seleuium
		System.getProperties().setProperty("webdriver.chrome.driver", "chromedriver.exe");
		// modify the download path
		DesiredCapabilities caps = setDownloadsPath();

		ChromeOptions options = new ChromeOptions();
		options.addArguments("--lang=zh-cn");
		webDriverIn = new ChromeDriver(options);
		webDriverIn.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		// lanunch the webdriver
		// webDriver.get(URL);
		webDriverIn.get("data:,");

		/*
		 * // Input the query condition Thread.sleep(3000); ArrayList<String>
		 * tabs; tabs = new ArrayList<String>(webDriver.getWindowHandles()); if
		 * (tabs.size() > 1) { for (int a = tabs.size(); a > 1; a--) {
		 * webDriver.switchTo().window(tabs.get(a - 1)); Thread.sleep(500);
		 * webDriver.close(); } webDriver.switchTo().window(tabs.get(0)); ; }
		 * tabs = null; webDriver.findElement(By.linkText(
		 * "Web of Science Core Collection")).click();
		 * 
		 * Thread.sleep(3000); tabs = new
		 * ArrayList<String>(webDriver.getWindowHandles());
		 * webDriver.switchTo().window(tabs.get(0)); webDriver.close(); tabs =
		 * new ArrayList<String>(webDriver.getWindowHandles());
		 * Thread.sleep(3000); webDriver.switchTo().window(tabs.get(0));
		 * 
		 * // Waiting for element for 10 seconds /* WebDriverWait wait = new
		 * WebDriverWait(webDriver, 30);
		 * wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.
		 * cssSelector("ul.searchtype-nav"))); WebElement searchElement =
		 * webDriver.findElement(By.cssSelector("ul.searchtype-nav"));
		 * searchElement.findElements(By.cssSelector(
		 * ".searchtype-sub-nav__list-item")).get(3).click();
		 * wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.
		 * cssSelector(".AdvSearchBox")));
		 */

	}

	public static void getaBlankPage() throws InterruptedException {
		System.getProperties().setProperty("webdriver.chrome.driver", "chromedriver.exe");
		// modify the download path
		DesiredCapabilities caps = setDownloadsPath();

		ChromeOptions options = new ChromeOptions();
		options.addArguments("--lang=zh-cn");

		webDriverIn = new ChromeDriver(options);
		// WebDriver webDriver = new ChromeDriver(caps);
		webDriverIn.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		// lanunch the webdriver
		// webDriver.get(URL);
		webDriverIn.get("data:,");
		return;
	}

	public static WebDriver gotoSpecific(WebDriver webDriver) throws InterruptedException, IOException {
		int specPage, specRow;
		specPage = detailRowidIn / 10;
		specRow = detailRowidIn % 10;

		WebDriverWait wait;
		try {
			if (specPage != 1) {
				WebElement next = webDriver.findElement(By.cssSelector("[title='下一页']"));
				next.click();
				// Waiting for element for 10 seconds
				wait = new WebDriverWait(webDriver, 40);
				wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#pageCount\\.top")));
				String current_url = webDriver.getCurrentUrl();
				String cut_url = current_url.substring(0, current_url.lastIndexOf("=") + 1);
				webDriver.get(cut_url + String.valueOf(specPage));
			}
			wait = new WebDriverWait(webDriver, 40);
			wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#pageCount\\.top")));
		} catch (Exception e1) {
			// TODO Auto-generated catch block

			int h;
			for (h = 0; h < 40; h++) {
				Result[h] = "RESTARTPAGE";
			}
			Result[33] = tempLink;
			h = 0;
			// run status is abnormal
			runStatus = 2;
			writrintExcel();
			Result[33] = "";
			tempLink = "";
			Thread.sleep(30000);
			runStatus = 2;
			return webDriver;
		}

		return webDriver;
	}

	public static void readExcel(Sheet sheet, int rowid) {
		Cell cell1, cell2, cell3, cell4, cell5, cell6, cell7, cell8, cell9, cell10, cell11, cell12;
		try {
			cell1 = sheet.getCell(0, rowid);
			cell2 = sheet.getCell(1, rowid);
			cell3 = sheet.getCell(2, rowid);
			cell4 = sheet.getCell(3, rowid);
			cell5 = sheet.getCell(4, rowid);
			cell6 = sheet.getCell(5, rowid);
			cell7 = sheet.getCell(6, rowid);
			cell8 = sheet.getCell(7, rowid);
			cell9 = sheet.getCell(8, rowid);
			cell10 = sheet.getCell(9, rowid);
			cell11 = sheet.getCell(10, rowid);
			cell12 = sheet.getCell(11, rowid);

			if ("".equals(cell1.getContents()) != true) {
				ID = cell1.getContents().replace('\n', ' ');
				PID = cell2.getContents().replace('\n', ' ');
				name = cell3.getContents().replace('\n', ' ');
				lastname = cell4.getContents().replace('\n', ' ');
				firstname = cell5.getContents().replace('\n', ' ');
				midname = cell6.getContents().replace('\n', ' ');
				phdu = cell7.getContents().replace('\n', ' ');
				phdyr = cell8.getContents().replace('\n', ' ');
				phd_country = cell9.getContents().replace('\n', ' ');
				search_list = cell10.getContents().replace('\n', ' ');
				time_from = cell11.getContents().replace('\n', ' ');
				time_to = cell12.getContents().replace('\n', ' ');
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 执行dos命令
	 * 
	 * @param command
	 */
	public static void command(String command) {
		try {
			Runtime.getRuntime().exec(command);
			// process.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
