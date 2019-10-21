package com.waverly.crawler;

import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.By.ByXPath;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.Select;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;


import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class Main51 {
	public static int i = 0;
	public static int j = 0;
	public static int pages = 0;
	public static JTextField searchstring = new JTextField();
	public static JTextField location = new JTextField();
	public static JTextField filename = new JTextField("E:/Jobs");
	public static String URL = "";
	public static String q;
	public static String dcs;
	public static String jtype;
	public static String radius;
	public static String l;
	public static String dc = "";
	public static String[] auhorParam = { "Select", "No select" };
	public static String[] organizationParam = { "Select", "No select" };
	public static String[] yearParam = { "Select", "No select" };

	public static JComboBox combo1 = new JComboBox(auhorParam);
	public static JComboBox combo2 = new JComboBox(organizationParam);
	public static JComboBox combo3 = new JComboBox(yearParam);
	private static JCheckBox CJFQ = new JCheckBox("期刊", true);
	private static JCheckBox CJRF = new JCheckBox("教育期刊", true);
	private static JCheckBox CJFN = new JCheckBox("特色期刊", true);
	private static JCheckBox CDFD = new JCheckBox("博士", true);
	private static JCheckBox CPFD = new JCheckBox("国内会议", false);
	private static JCheckBox IPFD = new JCheckBox("国际会议", false);
	private static JCheckBox SCOD = new JCheckBox("专利", false);
	private static JCheckBox CCJD = new JCheckBox("学术辑刊", true);
	private static Boolean auhorParamState = true;
	private static Boolean organizationParamState = true;
	private static Boolean yearParamState = true;
	private static String publishdate_from = "";
	private static String publishdate_to = "";

	public static JFrame frame = new JFrame();
	public static PrintWriter writer;
	private static ReadProgress dataProgress;
	private static int total = 0;
	private static int page = 0;
	private static int row = 0;
	private static int sim_row = 0;
	public static String projectNo = "";
	public static String appCode = "";
	public static String projectName = "";
	public static String ProjectLeaderName = "";
	public static String projectOrg = "";
	public static String approvedAmount = "";
	public static String projectStartEnd = "";
	public static String projectYear = "";
	public static String author = "";
	public static String authorOrg = "";
	public static int rawID = 1;
	public static int rawID_Total = 0;
	public static int exceptionCode = 0;
	public static int clickCount = 0;
	public static int searchCount = 0;

	// List item String
	public static String list_1 = "";
	public static String list_2 = "";
	public static String list_3 = "";
	public static String list_4 = "";
	public static String list_5 = "";
	public static String list_6 = "";
	public static String list_7 = "";
	public static String list_8 = "";
	public static boolean isFirstPage = true;
	public static boolean isPatentPage = false;
	public static boolean isFirstSearch = true;
	public static String Result[] = new String[40];

	/*
	 * store the page data Easy Apply, Assoc. Position ID, Dice ID Position ID,
	 * Job Title, Employer, Job Description Location, Posted Keyword1, Keyword2,
	 * Keyword3, Keyword4, comlink posiCount, companyOverview, companyWebsite,
	 * quickFacts, easyApply2
	 */
	public static String[] result_sub = new String[15];
	public static String easyflag = "";

	public static void main(String[] args) throws IOException {
		try {
			input();
			q = "q-" + searchstring.getText();
			String q1 = q.replace(" ", "_");

			if (filename.getText().equalsIgnoreCase("")) {
				JOptionPane.showMessageDialog(null, "Please enter the file path.");
				filename.requestFocusInWindow();
				filename.setText("E:/jobs");
				input();
			}

			if (combo1.getSelectedItem().toString().equals("Select")) {
				auhorParamState = true;
			} else {
				auhorParamState = false;
			}

			if (combo2.getSelectedItem().toString().equals("Select")) {
				organizationParamState = true;
			} else {
				organizationParamState = false;
			}

			if (combo3.getSelectedItem().toString().equals("Select")) {
				yearParamState = true;
			} else {
				yearParamState = false;
			}

			// Read the excel sheet
			Sheet sheet;
			Workbook book;
			// book = Workbook.getWorkbook(new File(filename_sheet.getText()));
			book = Workbook.getWorkbook(new File("e://postdoc_list.xls"));
			sheet = book.getSheet(0);
			rawID_Total = sheet.getRows();

			URL = "http://apps.webofknowledge.com/WOS_AdvancedSearch_input.do?SID=8DEJ9qRQSdW6p6ieIOa&product=WOS&search_mode=AdvancedSearch";
			// Initialize chrome drive in Seleuium
			System.getProperties().setProperty("webdriver.chrome.driver", "e:\\chromedriver.exe");
			//modify the download path	
			DesiredCapabilities caps = setDownloadsPath();

			ChromeOptions options = new ChromeOptions();
			options.addArguments("--lang=zh-cn");
			WebDriver webDriver = new ChromeDriver(options);
			// WebDriver webDriver = new ChromeDriver(caps);
			webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

			webDriver.get(URL);
			// Waiting for element for 10 seconds
			WebDriverWait wait = new WebDriverWait(webDriver, 10);
			wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//*[@id=\'value(input1)\']")));

			// Display data extract progress
			dataProgress = new ReadProgress();
			dataProgress.setVisible(true);
			Thread thread1 = new Thread(dataProgress);
			thread1.start();

			try {
				writer = new PrintWriter(filename.getText() + "_0" + ".xls", "GB2312");
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null,
						"File already open with same path & file name. Please close it & re-run the application");
				writer.close();
			}

			// write the excel the top item
			String toptitle = "AU\tAD\t题目\t作者\t期刊名称\t出版年\t被引频次\t期刊影响力-现在\t期刊影响力-5年\t作者关键词\\t关键词plus";
			
			writer.println(toptitle);

			// Read the unedname from exccel sheet
			for (int i = 1; i < rawID_Total; i++) {
				try {
					sim_row = i;
					dataProgress.setPanel(total, page, row, sim_row);
					rawID = i;
					readExcel(sheet, rawID);
					exceptionCode = 0;
					// Split the result file
					if (i % 500 == 0) {
						writer.close();
						int t = i / 500;
						try {
							writer = new PrintWriter(filename.getText() + "_" + t + ".xls", "UTF-8");
						} catch (Exception e1) {
							JOptionPane.showMessageDialog(null,
									"File already open with same path & file name. Please close it & re-run the application");
							writer.close();
						}
						// write the excel the top item
						writer.println(toptitle);
					}

					try {				
						// Input the query condition
						searchName(webDriver, author);

						// Get the item name
						getAName(webDriver);
					} catch (Exception e1) {
						Thread.sleep(3000);
						writrintExcel();
					}

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
					webDriver.get(URL);
				} catch (Exception e3) {
					// e3.printStackTrace();
					// If in exception
					writrintExcel();
					Thread.sleep(30000);
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
					continue;
				}
			}
			writer.close();
			JOptionPane.showMessageDialog(frame, "Downloading over. Data ready in " + filename.getText() + ".xls");
			webDriver.close();
		} catch (Exception e2) {
			writer.close();
			JOptionPane.showMessageDialog(null, e2.getMessage());
		}
		System.exit(0);
	}

	public static void searchName(WebDriver webDriver, String author) throws IOException {
		try {
			// Waiting for element for 10 seconds
			WebDriverWait wait = new WebDriverWait(webDriver, 10);
			wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//*[@id=\'value(input1)\']")));

			// Input the author
			WebElement author_input = webDriver.findElement(By.xpath("//*[@id=\'value(input1)\']"));
			author_input.clear();
			
			author = "AU= Zhang JX and AD= Beijing Normal Univ";		
			author_input.sendKeys(author);
			
			if (isFirstSearch) {
				try {
					Thread.sleep(3000);
					// Input the language
					Select select_language = new Select(webDriver.findElement(By.xpath("//*[@id='value(input2)']")));
					// deselect all option
					select_language.deselectAll();
					select_language.selectByIndex(1);

					// Input the article
					Select select_article = new Select(webDriver.findElement(By.xpath("//*[@id='value(input3)']")));
					// deselect all option
					select_article.deselectAll();
					select_article.selectByIndex(1);
					isFirstSearch = false;
				} catch (Exception e1) {
				}
			}

			// Click "search" button
			((ChromeDriver) webDriver).findElementByXPath("//*[@id='search-button']").click();
			
			//Get the link
			List<WebElement> tb = webDriver.findElements(By.xpath("/html/body/div[13]/form/table/tbody/tr"));
			tb.remove(0);
			tb.remove(0);
			for (WebElement t : tb) {
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
				String searchlink = t.findElements(By.cssSelector("a[href]")).get(0).getAttribute("href");
				executor.executeScript("window.open('" + searchlink + "')");	
				Thread.sleep(3000);
				break;
			}						
		} catch (Exception e2) {
			System.out.print(e2);
		}
	}

	public static void getAName(WebDriver webDriver) throws IOException {
		try {
			// Shift the second page
			/*
			 * ArrayList<String> tabs; tabs = new
			 * ArrayList<String>(webDriver.getWindowHandles()); if (tabs.size() > 1) {
			 * webDriver.switchTo().window(tabs.get(1)); } tabs = null;
			 */

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
				WebDriverWait wait = new WebDriverWait(webDriver, 10);
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
						writrintExcel();
						searchCount++;
						if (searchCount > 3) {
							Thread.sleep(10000);
						}
						return;
					} else {
						searchCount = 0;
						pages = Integer.parseInt(pc_string);
						if (pages >= 300) {
							pages = 300;
						}
					}
				} else {
					pages = 0;
					searchCount++;
					if (searchCount > 3) {
						Thread.sleep(10000);
					}
					writrintExcel();
					return;
				}
				// If the result is too less, then sleep 15 seconds
				if (Integer.parseInt(pc_string) < 3) {
					Thread.sleep(2000);
				}
			} catch (Exception e1) {
				pages = 0;
				writrintExcel();
				Thread.sleep(3000);
				return;
			}

			total = pages;
			dataProgress.setPanel(total, page, row, sim_row);

			// Initialize the flag of page
			isFirstPage = true;
			for (int k = 0; k < pages; k++) {
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
				Thread.sleep(4000);
				WebElement ta = webDriver.findElement(By.cssSelector(".search-results"));
				List<WebElement> tb = ta.findElements(By.cssSelector(".search-results-item"));

				// Get row loop
				for (WebElement tbb : tb) {
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
					// result array clear
					for (i = 0; i < 40; i++) {
						Result[i] = "";
					}

					try {
						// Get the result row
						List<WebElement> tc = tbb.findElements(By.cssSelector(".search-results-content > div"));

						// Title
						WebElement titleItem = tbb.findElement(By.cssSelector("a.smallV110"));
						Result[0] = titleItem.getText();

						// author
						List<WebElement> authorItem = tc.get(1).findElements(By.cssSelector("a[title]"));
						for (WebElement tAu : authorItem) {
							Result[1] = Result[1] + ";" + tAu.getText();
						}
						Result[1] = Result[1].substring(1);

						// Journal
						WebElement journalItem = tc.get(2).findElement(By.cssSelector("span > value"));
						Result[2] = journalItem.getText();

						// Publish Year
						List<WebElement> sourceTitle = tc.get(2).findElements(By.cssSelector("span.data_bold > value"));
						Result[3] = sourceTitle.get(2).getText();

						// Being cite
						WebElement beingCiteItem = tbb.findElement(By.cssSelector(".search-results-data-cite"));
						WebElement beingCiteItem_remove = webDriver
								.findElement(By.cssSelector(".search-results-data-cite .en_data_bold"));
						String beingCiteStr = beingCiteItem.getText().substring(0,
								beingCiteItem.getText().indexOf(beingCiteItem_remove.getText()));
						// Remove the characters
						Pattern pattern = Pattern.compile("[^0-9]");
						Matcher matcher = pattern.matcher(beingCiteStr);
						Result[4] = matcher.replaceAll("");

						// Impact factors
						journalItem.click();
						Thread.sleep(1500);
						WebElement impactFrItem = webDriver.findElement(By.xpath("//*[@id='page']/div[11]"));
						WebElement impactFrItem_in = impactFrItem
								.findElement(By.cssSelector(".Impact_Factor_table > tbody > tr"));
						List<WebElement> impactFrItem_Str = impactFrItem_in.findElements(By.cssSelector("td"));
						Result[5] = impactFrItem_Str.get(0).getText();
						Result[6] = impactFrItem_Str.get(1).getText();
						// Close the impact factor window
						Thread.sleep(1500);
						// impactFrItem.findElement(By.xpath(".journal_overlay_close")).click();
						impactFrItem.findElement(By.cssSelector("p.closeWindow > button")).click();

						// Open the detail record page
						String detailrecord = titleItem.getAttribute("href");
						JavascriptExecutor executor = (JavascriptExecutor) webDriver;
						Thread.sleep(3500);
						executor.executeScript("window.open('" + detailrecord + "')");

						try {
							getDetail(webDriver);
						} catch (Exception e3) {
							writrintExcel();
							continue;
						}

						// Write the data into excel
						writrintExcel();

						// result array clear
						for (i = 0; i < 40; i++) {
							Result[i] = "";
						}
						int aaa = 0;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						// Write the data into excel
						writrintExcel();

						// result array clear
						for (i = 0; i < 40; i++) {
							Result[i] = "";
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
						continue;
					}
				}

				// get the next page

				// get the next page
				int pageclick = 0;
				try {
					webDriver.findElement(By.xpath("//*[@id='summary_navigation']/nav/table/tbody/tr/td[3]/a")).click();
				} catch (Exception e3) {
					if (pageclick < 3) {
						webDriver.findElement(By.xpath("//*[@id='summary_navigation']/nav/table/tbody/tr/td[3]/a"))
								.click();
						Thread.sleep(10000);
						pageclick++;
					} else {
						writrintExcel();
						return;
					}
				}
			}
			Thread.sleep(3000);
		} catch (Exception e2) {
			exceptionCode = 2;
		}
	}

	public static void getDetail(WebDriver webDriver) throws IOException {
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
			WebDriverWait wait = new WebDriverWait(webDriver, 10);
			wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
					By.xpath("//*[@id='records_form']/div/div/div/div[1]/div/div[5]/p[1]/a[1]")));
			// get author keywords
			List<WebElement> tk = webDriver
					.findElements(By.xpath("//*[@id='records_form']/div/div/div/div[1]/div/div[5]/p[1]/a"));
			for (WebElement tkk : tk) {
				Result[7] = Result[7] + ";" + tkk.getText();
			}
			Result[7] = Result[7].substring(1);

			// Get keywords plus
			List<WebElement> tk2 = webDriver
					.findElements(By.xpath("//*[@id='records_form']/div/div/div/div[1]/div/div[5]/p[2]/a"));
			for (WebElement tkk2 : tk2) {
				Result[8] = Result[8] + ";" + tkk2.getText();
			}
			Result[8] = Result[8].substring(1);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}

	public static void writrintExcel() throws IOException {
		// write into excel
		writer.println(projectNo + "\t" + appCode + "\t" + projectName + "\t" + ProjectLeaderName + "\t" + projectOrg
				+ "\t" + approvedAmount + "\t" + projectStartEnd + "\t" + projectYear + "\t" + Result[0] + "\t"
				+ Result[1] + "\t" + Result[5] + "\t" + Result[6] + "\t" + Result[7] + "\t" + Result[8] + "\t"
				+ Result[9] + "\t" + Result[10] + "\t" + Result[11] + "\t" + Result[12] + "\t" + Result[13] + "\t"
				+ Result[14] + "\t" + Result[20] + "\t" + Result[21] + "\t" + Result[22] + "\t" + Result[23] + "\t"
				+ Result[24] + "\t" + Result[25] + "\t" + Result[26] + "\t" + Result[27] + "\t" + Result[28] + "\t"
				+ Result[29] + "\t" + Result[30] + "\t" + Result[31]);
		writer.flush();
	}

	public static void input() throws IOException {
		JPanel panel = new JPanel(new GridLayout(0, 1));
		panel.add(new JLabel("File path to store results (without extention):"));
		panel.add(filename);
		panel.add(new JLabel("Author name:"));
		panel.add(combo1);
		panel.add(new JLabel("Organization:"));
		panel.add(combo2);
		panel.add(new JLabel("year?:"));
		panel.add(combo3);
		panel.add(new JLabel("Library select:"));
		panel.add(CJFQ);
		panel.add(CJRF);
		panel.add(CJFN);
		panel.add(CDFD);
		panel.add(CPFD);
		panel.add(IPFD);
		panel.add(SCOD);
		panel.add(CCJD);

		int result = JOptionPane.showConfirmDialog(null, panel, "zhiwang - Search Criteria", 2, -1);
		if (result == 0) {
			return;
		}
		JOptionPane.showMessageDialog(frame, "Cancelled");
		System.exit(0);
	}

	public static Document getPageDoc(String URL) {
		// ChromeOptions options = new ChromeOptions();

		WebDriver webDriver = new ChromeDriver();
		webDriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		String responseBody = "";
		try {
			webDriver.get(URL);
		} catch (Exception e) {
			webDriver.close();
		}
		if (webDriver.getPageSource() != null) {
			responseBody = webDriver.getPageSource();
		}

		/*
		 * File file = new File("E:/sistertask/2019-02-24/log.txt"); String
		 * pageXml = txt2String(file); Document doc = Jsoup.parse(pageXml);//
		 * 获取html文档
		 */
		Document doc = Jsoup.parse(responseBody);// 获取html文档
		webDriver.close();
		return doc;
	}

	public static Document getPageDocByHtmlunit(String URL) {
		System.out.print("read page:" + page + " row:" + row + " sim_row:" + row);
		final WebClient webClient = new WebClient(BrowserVersion.CHROME);// 新建一个模拟谷歌Chrome浏览器的浏览器客户端对象
		webClient.getOptions().setThrowExceptionOnScriptError(false);// 当JS执行出错的时候是否抛出异常,
																		// //
																		// 这里选择不需要
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);// 当HTTP的状态非200时是否抛出异常,
																			// //
																			// 这里选择不需要
		webClient.getOptions().setActiveXNative(false);
		webClient.getOptions().setCssEnabled(false);// 是否启用CSS, 因为不需要展现页面,
		webClient.getOptions().setJavaScriptEnabled(true); // 很重要，启用JS
		webClient.setAjaxController(new NicelyResynchronizingAjaxController());// 很重要，设置支持AJAX
		// webClient.waitForBackgroundJavaScript(10 * 1000);
		webClient.getOptions().setTimeout(5 * 1000);
		// webClient.setJavaScriptTimeout(5 * 1000);
		// webClient.getOptions().setTimeout(5000);

		HtmlPage page = null;
		try {
			page = webClient.getPage(URL);
		} catch (Exception e) {
			e.printStackTrace();
			Document doc = Jsoup.parse(" ");
			System.out.print("read FAIL on the page:" + page + " row:" + row + " sim_row:" + row);
			return doc;
		} finally {
			webClient.close();
		}

		// webClient.waitForBackgroundJavaScript(10000);
		// 异步JS执行需要耗时,所以这里线程要阻塞30秒,等待异步JS执行结束
		String pageXml = page.asXml();// 直接将加载完成的页面转换成xml格式的字符串

		// File file = new File("e:\\log.txt");
		// String pageXml = txt2String(file);

		Document doc = Jsoup.parse(pageXml);// 获取html文档
		return doc;
	}

	public static void contentToTxt(String filePath, String content) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filePath), true));
			writer.write("\n" + content);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String txt2String(File file) {
		StringBuilder result = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));// 构造一个BufferedReader类来读取文件
			String s = null;
			while ((s = br.readLine()) != null) {// 使用readLine方法，一次读一行
				result.append(System.lineSeparator() + s);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result.toString();
	}

	public static void readExcel(Sheet sheet, int rawID) {
		Cell cell1, cell2, cell3, cell4, cell5, cell6, cell7, cell8;
		try {
			cell1 = sheet.getCell(0, rawID);
			cell2 = sheet.getCell(1, rawID);
			cell3 = sheet.getCell(2, rawID);
			cell4 = sheet.getCell(3, rawID);
			cell5 = sheet.getCell(4, rawID);
			cell6 = sheet.getCell(5, rawID);
			cell7 = sheet.getCell(6, rawID);
			cell8 = sheet.getCell(7, rawID);

			if ("".equals(cell1.getContents()) != true) {
				projectNo = cell1.getContents();
				appCode = cell2.getContents();
				projectName = cell3.getContents();
				ProjectLeaderName = cell4.getContents();
				projectOrg = cell5.getContents();
				approvedAmount = cell6.getContents();
				projectStartEnd = cell7.getContents();
				projectYear = cell8.getContents();
				System.out.println(rawID + " " + cell1.getContents() + " " + cell2.getContents());
			}
		} catch (Exception e) {
		}
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