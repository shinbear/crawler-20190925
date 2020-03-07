package com.waverly.crawler;

import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.By.ByXPath;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
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

public class Main55_W {
	public static int i = 0;
	public static int j = 0;
	public static int pages = 0;
	public static JTextField filename = new JTextField("Jobs");	
	public static JRadioButton jRadio1 = new JRadioButton("Run all records",true);
	public static JRadioButton jRadio2 = new JRadioButton("Run the specific records from- to");
	public static ButtonGroup jRadioGroup = new ButtonGroup();
	public static JTextField recordFrom = new JTextField("");
	public static JTextField recordTo = new JTextField("");
	
	public static String URL = "";
	public static String q;
	public static String dcs;
	public static String jtype;
	public static String radius;
	public static String l;
	public static String dc = "";
	public static JFrame frame = new JFrame();
	public static PrintWriter writer;
	private static ReadProgress dataProgress;
	private static int total = 0;
	private static int page = 0;
	private static int row = 0;
	private static int sim_row = 0;
	public static String auadQuery = "";
	//The content read from Excel
	public static String  ID= "";
	public static String  Stkcd= "";
	public static String  year= "";
	public static String  Name_cn= "";
	public static String  PersonID= "";
	public static String  university_cn= "";
	public static String  firstname_en= "";
	public static String  lastname_en= "";
	public static String  name_en= "";
	public static String  university_en="";

	public static String author = "";
	public static String authorOrg = "";
	public static int rowid = 1;
	public static int rowid_Total = 0;
	public static int exceptionCode = 0;
	public static int clickCount = 0;
	public static int searchCount = 0;

	// List item String
	public static boolean isFirstPage = true;
	public static boolean isPatentPage = false;
	public static boolean isFirstSearch = true;
	public static String Result[] = new String[40];
	
	public static String tempLink= "";

	 

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
			System.out.println("用户的当前工作目录:"+System.getProperty("user.dir"));
			input();
			if (filename.getText().equalsIgnoreCase("")) {
				JOptionPane.showMessageDialog(null, "Please enter the file path.");
				filename.requestFocusInWindow();
				filename.setText("jobs");
				input();
			}

			// Read the excel sheet
			Sheet sheet;
			Workbook book;
			// book = Workbook.getWorkbook(new File(filename_sheet.getText()));
			book = Workbook.getWorkbook(new File("postdoc_list.xls"));
			sheet = book.getSheet(0);
			rowid_Total = sheet.getRows();

			URL = "https://www.lib.umd.edu/dbfinder/id/UMD04150";
			// Initialize chrome drive in Seleuium
			System.getProperties().setProperty("webdriver.chrome.driver", "chromedriver.exe");
			//modify the download path	
			DesiredCapabilities caps = setDownloadsPath();

			ChromeOptions options = new ChromeOptions();
			options.addArguments("--lang=zh-cn");
			WebDriver webDriver = new ChromeDriver(options);
			// WebDriver webDriver = new ChromeDriver(caps);
			webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			// lanunch the webdriver 
			// webDriver.get(URL);		
			// WebElement focus_element = webDriver.findElement(By.linkText("Web of Science Core Collection"));
			// focus_element.click();
			// webDriver.get("");
			Thread.sleep(3000);
			
			// Show the dialog to wait
			int res = JOptionPane.showConfirmDialog(null, "Waiting for you access the advanced search page", " ",
					JOptionPane.YES_NO_OPTION);
			if (res == JOptionPane.YES_OPTION) {
				System.out.println("Go to download");
			} else {
				writer.close();
				webDriver.quit();
				System.exit(0);
				return;
			}

			// Input the query condition
			ArrayList<String> tabs;
			tabs = new ArrayList<String>(webDriver.getWindowHandles());
			webDriver.switchTo().window(tabs.get(0));
			tabs = null;
			
			// Waiting for element for 10 seconds
			WebDriverWait wait = new WebDriverWait(webDriver, 10);
			wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//*[@id=\'value(input1)\']")));

			// Show the progress
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
			String toptitle = "ID\tStkcd\tyear\tName_cn\tPersonID\tuniversity_cn\t"
					+ "firstname_en\tlastname_en\tname_en\tuniversity_en\tPT\tAU\tAF"
					+ "\tTI\tSO\tLA\tDT\tID\tC1\tRP\tEM\tFU\tFX\tTC\tZ9\tU1\tU2\tSN\t"
					+ "EI\tJ9\tJI\tPD\tPY\tVL\tIS\tBP\tEP\tDI\tWC\tSC\tGA\tUT\tPM";
			writer.println(toptitle);
			
			int startRow, endRow;
			if (jRadio1.isSelected()) {
				startRow = 1;
				endRow = rowid_Total;
			} else {
				startRow = Integer.parseInt(recordFrom.getText());
				if (!recordFrom.getText().equals("")) {
					startRow = Integer.parseInt(recordFrom.getText());
				} else {
					startRow = 1;
				}

				if (!recordTo.getText().equals("")) {
					endRow = Integer.parseInt(recordTo.getText()) + 1;
				} else {
					endRow = rowid_Total;
				}
			}

			// Read the name from exccel sheet
			for (int i = startRow; i <= endRow; i++) {
				try {
					sim_row = i;
					dataProgress.setPanel(total, page, row, sim_row);
					rowid = i;
					readExcel(sheet, rowid);
					// Split the result file
					if (i % 500 == 0) {
						writer.close();
						int t = i / 500;
						try {
							writer = new PrintWriter(filename.getText() + "_" + t + ".xls", "GB2312");
						} catch (Exception e1) {
							JOptionPane.showMessageDialog(null,
									"File already open with same path & file name. Please close it & re-run the application");
							writer.close();
						}
						// write the excel the top item
						writer.println(toptitle);
					}

					//Remain the search page
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
					
					try {
						int status = searchName(webDriver);
						if (status == 1) {
							// Get the item name
							int getNameStatus = getAName(webDriver);
						} else if (status == 2) {
							// Status is 2 means the result number is zero
							int h;
							for (h = 0; h < 40; h++) {
								Result[h] = "0";
							}
							h = 0;
							writrintExcel();
							Thread.sleep(30000);
							continue;
						} else if (status == 3) {
							// Status is 2 means the result number is zero
							int h;
							for (h = 0; h < 40; h++) {
								Result[h] = "ER";
							}
							h = 0;
							writrintExcel();
							Thread.sleep(30000);
							continue;
						} else {
							try {
								webDriver.navigate().refresh();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							status = searchName(webDriver);
							if (status == 1) {
								int getNameStatus = getAName(webDriver);
							} else if (status == 2) {
								// Status is 2 means the result number is zero
								int h;
								for (h = 0; h < 40; h++) {
									Result[h] = "0";
								}
								h = 0;
								writrintExcel();
								Thread.sleep(30000);
								continue;
							}

							else if (status == 3) {
								// Status is 2 means the result number is zero
								int h;
								for (h = 0; h < 40; h++) {
									Result[h] = "ER";
								}
								Result[18] = tempLink;
								h = 0;
								writrintExcel();
								Result[18] = "";
								tempLink= "";
								Thread.sleep(30000);
								continue;
							} else {
								throw new Exception("throw error");

							}
						}
					} catch (Exception e1) {
						// result array clear
						int h;
						for (h = 0; h < 40; h++) {
							Result[h] = " ";
						}
						Result[18] = tempLink;
						h = 0;
						writrintExcel();
						Result[18] = "";
						tempLink= "";
						Thread.sleep(300000);
					}
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
					// webDriver.get(URL);
				} catch (Exception e3) {
					// e3.printStackTrace();
					// If in exception
					// result array clear
					int h;
					for (h = 0; h < 40; h++) {
						Result[h] = " ";
					}
					Result[18] = tempLink;
					h = 0;
					writrintExcel();
					Result[18] = "";
					tempLink= "";
					Thread.sleep(30000);
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

	public static int searchName(WebDriver webDriver) throws IOException {
		try {
			// Waiting for element for 10 seconds
			WebDriverWait wait = new WebDriverWait(webDriver, 10);
			wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//*[@id=\'value(input1)\']")));

			// Input the author
			String searchQuery = "AU=" + name_en + " AND AD=" + university_en;
			WebElement author_input = webDriver.findElement(By.xpath("//*[@id=\'value(input1)\']"));
			author_input.clear();
			author_input.sendKeys(searchQuery);
			
			//scroll to the element
			String js3 = "arguments[0].scrollIntoView();";
			WebElement element = webDriver.findElement(By.xpath("//*[@id=\'value(input1)\']"));
			((JavascriptExecutor) webDriver).executeScript(js3, element);

			Thread.sleep(1000);
			// Year range from to
			if (isFirstSearch) {
				Thread.sleep(1000);
				Select yearRange = new Select(webDriver.findElement(By.cssSelector(".j-custom-select-yeardropdown")));				
				yearRange.selectByIndex(6);
				WebElement timeSpan = webDriver.findElement(By.cssSelector(".timespan_custom"));
				List<WebElement> tss = timeSpan.findElements(By.cssSelector(".select2-container--yeardropdown"));
				tss.get(0).click();
				Thread.sleep(500);
				WebElement yearFrom = webDriver.findElement(By.cssSelector(".select2-search__field"));
				yearFrom.clear();
				yearFrom.sendKeys("2006");
				yearFrom.sendKeys(Keys.ENTER);
				tss.get(1).click();
				Thread.sleep(500);
				WebElement yearTo = webDriver.findElement(By.cssSelector(".select2-search__field"));
				yearTo.clear();
				yearTo.sendKeys("2019");	
				yearTo.sendKeys(Keys.ENTER);
				Thread.sleep(200);
			}
								
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
			String searchlink="";
			String searchResultNo = ""; 
			
			try {
				if (webDriver.findElement(By.cssSelector(".errorText")).getText().contains("检索错误")) {
					return 3;}
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
			// result array clear
			int h;
			for (h = 0; h < 40; h++) {
				Result[h] = " ";
			}
			Result[18] = tempLink;
			h = 0;
			writrintExcel();
			Result[18] = "";
			tempLink= "";
			System.out.print(e2);
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
						// result array clear
						int h;
						for (h = 0; h < 40; h++) {									
							Result[h] = "0";								
						}	
						h = 0;
						writrintExcel();
						searchCount++;
						if (searchCount > 3) {
							Thread.sleep(10000);
						}
						return 0;
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
					// result array clear
					int h;
					for (h = 0; h < 40; h++) {									
						Result[h] = "0";								
					}	
					h = 0;
					writrintExcel();
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
					Result[h] = " ";
				}
				Result[18] = tempLink;
				h = 0;
				writrintExcel();
				Result[18] = "";
				tempLink= "";
				Thread.sleep(30000);
				return 0;
			}

			total = pages;
			dataProgress.setPanel(total, page, row, sim_row);

			//Loop in pages
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
				// Waiting for element for 10 seconds
				WebDriverWait wait = new WebDriverWait(webDriver, 10);
				wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".search-results")));	
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
					int h;
					for (h = 0; h < 40; h++) {									
						Result[h] = " ";								
					}	
					h = 0;

					try {
						// Get the result row
						List<WebElement> tc = tbb.findElements(By.cssSelector(".search-results-content"));

						// Title
						WebElement titleItem = tbb.findElement(By.cssSelector("a.smallV110"));
						Result[3] = titleItem.getText();

						/* author
						List<WebElement> authorItem = tc.get(1).findElements(By.cssSelector("a[title]"));
						for (WebElement tAu : authorItem) {
							Result[1] = Result[1] + ";" + tAu.getText();
						}
						Result[1] = Result[1].substring(1);
						*/
						
						for (i = 0; i < tc.size(); i++) {
							List<WebElement> td = tc.get(i).findElements(By.cssSelector("div"));
							for (j = 0; j < td.size(); j++) {
								if (td.get(j).getText().contains("出版年")) {
									String sourceStr = td.get(j).getText();
									// Journal
									Result[4] = sourceStr.substring(0, sourceStr.indexOf("卷:"))
											.replaceAll("(\\r\\n|\\r|\\n|\\n\\r)", "");
									// Publish Year
									String publishDate, publishYear, publishmonth;
									publishDate = sourceStr.substring(sourceStr.indexOf("出版年: ") + 4);
									// match year format
									Pattern pattern = Pattern.compile("(19|20)[0-9]{2}");
									Matcher matcher = pattern.matcher(publishDate);
									publishmonth = matcher.replaceAll("");
									publishYear = publishDate
											.substring(publishDate.indexOf(publishmonth) + publishmonth.length());
									Result[22] = publishYear;
									Result[21] = publishmonth;

								}
							}
						}

						// Being cite of web science
						WebElement beingCiteItem = tbb.findElement(By.cssSelector(".search-results-data-cite"));
						WebElement beingCiteItem_remove = webDriver
								.findElement(By.cssSelector(".search-results-data-cite .en_data_bold"));
						String beingCiteStr = beingCiteItem.getText().substring(0,
								beingCiteItem.getText().indexOf(beingCiteItem_remove.getText()));
						// Remove the characters
						Pattern pattern = Pattern.compile("[^0-9]");
						Matcher matcher = pattern.matcher(beingCiteStr);
						Result[13] = matcher.replaceAll("");					
						
						// Open the detail record page
						String detailrecord = titleItem.getAttribute("href");												
						JavascriptExecutor executor = (JavascriptExecutor) webDriver;
						Thread.sleep(3500);
						try {		
							tempLink = detailrecord;
							executor.executeScript("window.open('" + detailrecord + "')");
							Thread.sleep(1000);
							int detailStatus;
							detailStatus = getDetail(webDriver);
							if (detailStatus == 2)
							{
								webDriver.switchTo().window(tabs.get(2));
								webDriver.navigate().refresh();
								getDetail(webDriver);
							}
						} catch (Exception e3) {
							// result array clear
							for (h = 0; h < 40; h++) {
								Result[h] = " ";
							}
							Result[18] = tempLink;
							h = 0;
							writrintExcel();
							Result[18] = "";
							tempLink= "";
							continue;
						}

						// Write the data into excel
						writrintExcel();

						// result array clear
						for (h = 0; h < 40; h++) {									
							Result[h] = " ";								
						}	
						h = 0;
					} catch (Exception e) {
						// Write the data into excel
						// result array clear
						for (h = 0; h < 40; h++) {									
							Result[h] = " ";								
						}	
						h = 0;
						writrintExcel();

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

				// get the next page
				int turnpage=0;
				try {
					WebElement next = webDriver.findElement(By.cssSelector("[title='下一页']"));
					next.click();
				} catch (Exception e3) {
					// writrintExcel();
					Thread.sleep(10000);
					int h;
					for (h = 0; h < 40; h++) {									
						Result[h] = " ";								
					}	
					h = 0;
					return 0;
				}
			}
			Thread.sleep(3000);
			return 1;
		} catch (Exception e2) {
			exceptionCode = 2;
			return 0;
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
			Thread.sleep(4000);
		} catch (InterruptedException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}

		try {
			WebDriverWait wait = new WebDriverWait(webDriver, 10);
			wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
					By.xpath("//*[@id='records_form']/div/div/div/div[1]/div/div[1]/value")));
			
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
			List<WebElement> FRLabel =  webDriver.findElements(By.cssSelector(".FR_label"));
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
				if (FRLabelStr.contains("团体作者:")) {
					WebElement authorGroupItem = webDriver.findElement(By.xpath("//*[text()='团体作者:']/.."));
					authorFullName = authorFullName + "||" + authorGroupItem.getText();
				}				
				Result[1] = authorShortName.replace('\n', ' ').replace("作者:", "");
				Result[2] = authorFullName.replace('\n', ' ');
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
				WebElement volumeStr = webDriver.findElement(By.xpath("//*[text()='卷:']/following-sibling::value"));
				Result[23] = volumeStr.getText();
			} catch (Exception e) {
				Result[23] = " ";
			}
			
			// Get phase
			try {
				WebElement phaseStr = webDriver.findElement(By.xpath("//*[text()='期:']/following-sibling::value"));			
				Result[24] = "*" + phaseStr.getText();
			} catch (Exception e) {
				Result[24] = " ";
			}
			 
			// Get page
			try {
				WebElement pageStr = webDriver.findElement(By.xpath("//*[text()='页:']/following-sibling::value"));
				String[] strPageArray = pageStr.getText().split("-");
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
				Result[6]= webDriver.findElement(By.xpath("//*[text()='文献类型:']/following-sibling::span")).getText();
			} catch (Exception e) {
				Result[6] = "";
			}		
			
			// Get the DOI
			if (FRLabelStr.contains("DOI:")) {
				try {
					Result[27] = webDriver.findElement(By.xpath("//*[text()='DOI:']/following-sibling::value"))
							.getText();
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
				Result[8] = Result[8].substring(2).replace('\n',' ');;
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
				List<WebElement> tbs = sideBar.findElements(
						By.xpath("//*[text()='在 Web of Science 中 使用次数']/../following-sibling::div/div"));
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
					WebElement tl = webDriver.findElement(By.xpath("//*[text()='IDS 号:']/following-sibling::value"));
					Result[30] = tl.getText();
				} catch (Exception e) {
					Result[30] = "";
				}
			}
			
			// Get PubMed ID
			if (FRLabelStr.contains("PubMed ID:")) {
				try {
					WebElement tl = webDriver
							.findElement(By.xpath("//*[text()='PubMed ID:']/following-sibling::value"));
					Result[32] = tl.getText();
				} catch (Exception e) {
					Result[32] = "";
				}
			}
			
			// Get ISSN:
			if (FRLabelStr.contains("ISSN:")) {
				try {
					WebElement tl = webDriver.findElement(By.xpath("//*[text()='ISSN:']/following-sibling::value"));
					Result[17] = tl.getText();
				} catch (Exception e) {
					Result[17] = "";
				}
			}
			
			// Get eISSN
			if (FRLabelStr.contains("eISSN:")) {
				try {
					WebElement tl = webDriver.findElement(By.xpath("//*[text()='eISSN:']/following-sibling::value"));
					Result[18] = tl.getText();
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
		writer.println(ID + "\t" + Stkcd + "\t" + year + "\t" + Name_cn + "\t" + PersonID + "\t" + university_cn + "\t"
				+ firstname_en + "\t" + lastname_en + "\t" + name_en + "\t" + university_en + "\t" + Result[0] + "\t"
				+ Result[1] + "\t" + Result[2] + "\t" + Result[3] + "\t" + Result[4] + "\t" + Result[5] + "\t"
				+ Result[6] + "\t" + Result[7] + "\t" + Result[8] + "\t" + Result[9] + "\t" + Result[10] + "\t"
				+ Result[11] + "\t" + Result[12] + "\t" + Result[13] + "\t" + Result[14] + "\t" + Result[15] + "\t"
				+ Result[16] + "\t" + Result[17] + "\t" + Result[18] + "\t" + Result[19] + "\t" + Result[20] + "\t"
				+ Result[21] + "\t" + Result[22] + "\t" + Result[23] + "\t" + Result[24] + "\t" + Result[25] + "\t"
				+ Result[26] + "\t" + Result[27] + "\t" + Result[28] + "\t" + Result[29] + "\t" + Result[30] + "\t"
				+ Result[31] + "\t" + Result[32] + "\t" + Result[33]);
		writer.flush();
	}

	public static void input() throws IOException {
		JPanel panel = new JPanel(new GridLayout(0, 1));
		panel.add(new JLabel("File path to store results (without extention):"));
		panel.add(filename);
		
		panel.add(new JLabel("Author name:"));
		panel.add(jRadio1);
		panel.add(jRadio2);
		jRadioGroup.add(jRadio1);
		jRadioGroup.add(jRadio2);
		panel.add(new JLabel("From:"));
		panel.add(recordFrom);
		panel.add(new JLabel("To:"));
		panel.add(recordTo);
		
		int result = JOptionPane.showConfirmDialog(null, panel, "web of science - Search Criteria", 2, -1);
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

	public static void readExcel(Sheet sheet, int rowid) {
		Cell cell1, cell2, cell3, cell4, cell5, cell6, cell7, cell8, cell9, cell10;
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

			if ("".equals(cell1.getContents()) != true) {
				ID = cell1.getContents().replace('\n', ' ');
				Stkcd = cell2.getContents().replace('\n', ' ');
				year = cell3.getContents().replace('\n', ' ');
				Name_cn = cell4.getContents().replace('\n', ' ');
				PersonID = cell5.getContents().replace('\n', ' ');			
				university_cn = cell6.getContents().replace('\n', ' ');
				firstname_en = cell7.getContents().replace('\n', ' ');
				lastname_en = cell8.getContents().replace('\n', ' ');
				name_en = cell9.getContents().replace('\n', ' ');
				university_en = cell10.getContents().replace('\n', ' ');
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