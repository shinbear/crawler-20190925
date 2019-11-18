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

public class Main52 {
	public static int i = 0;
	public static int j = 0;
	public static int pages = 0;
	public static JTextField filename = new JTextField("E:/Jobs");	
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
	public static String source_ID = "";
	public static String source_NameCN = "";
	public static String source_LstName = "";
	public static String source_FstName = "";
	public static String source_DissYear = "";
	public static String source_DissInst = "";
	public static String source_DissIntstWOS = "";
	public static String source_CurAffi = "";
	public static String source_CurAffiWOS = "";
	public static String source_ProgYear = "";
	public static String source_ProgAffi = "";
	public static String source_ProgAffiWOS = "";
	public static String[] source_SearchArry = new String[3];
	public static String[] source_SearchYearFromArry = new String[3];
	public static String[] source_SearchYearToArry = new String[3];
	public static String source_Search = "";
	public static String source_SearchYearFrom = "";
	public static String source_SearchYearTo = "";	

	public static String author = "";
	public static String authorOrg = "";
	public static int rawID = 1;
	public static int rawID_Total = 0;
	public static int exceptionCode = 0;
	public static int clickCount = 0;
	public static int searchCount = 0;

	// List item String
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
			if (filename.getText().equalsIgnoreCase("")) {
				JOptionPane.showMessageDialog(null, "Please enter the file path.");
				filename.requestFocusInWindow();
				filename.setText("E:/jobs");
				input();
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
			// lanunch the webdriver 
			// webDriver.get(URL);
			
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
			String toptitle = "ID\tName\tLast Name\tFirst Name\tDissertation Year\tDissertation Institution\t"
					+ "Dissertation Institution(WOS)\tCurrent Affilication\tCurrent Affilication(WOS)\t"
					+ "Program Year\tProgram Affication\tProgram Affication(WOS)\tSearch keywords\t"
					+ "Year From\tYear To\tPT\tAU\tAF\tTI\tSO\tLA\tDT\tID\tC1\tRP\tEM\tFU\tFX\tTC\tZ9\tU1\tU2\tSN\t"
					+ "EI\tJ9\tJI\tPD\tPY\tVL\tIS\tBP\tEP\tDI\tWC\tSC\tGA\tUT\tPM";
			writer.println(toptitle);
			
			int startRow, endRow;
			if (jRadio1.isSelected()) {
				startRow = 1;
				endRow = rawID_Total;
			} else {
				startRow = Integer.parseInt(recordFrom.getText());
				endRow = Integer.parseInt(recordTo.getText())+1;
			}

			// Read the unedname from exccel sheet
			for (int i = startRow; i < endRow; i++) {
				try {
					sim_row = i;
					dataProgress.setPanel(total, page, row, sim_row);
					rawID = i;
					readExcel(sheet, rawID);
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

					//Remain the search page
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
					
					try {
						for (int k = 0; k < 3; k++) {
							// Input the query condition
							source_Search = source_SearchArry[k];
							source_SearchYearFrom = source_SearchYearFromArry[0];
							source_SearchYearTo = source_SearchYearToArry[0];
							searchName(webDriver, k);
							// Get the item name
							getAName(webDriver);
						}
					} catch (Exception e1) {
						Thread.sleep(3000);
						writrintExcel();
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

	public static void searchName(WebDriver webDriver, int searchNo) throws IOException {
		try {
			// Waiting for element for 10 seconds
			WebDriverWait wait = new WebDriverWait(webDriver, 10);
			wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//*[@id=\'value(input1)\']")));

			// Input the author
			WebElement author_input = webDriver.findElement(By.xpath("//*[@id=\'value(input1)\']"));
			author_input.clear();	
			author_input.sendKeys(source_SearchArry[searchNo]);
			
			// Year range from to
			if (isFirstSearch) {
				Select yearRange = new Select(webDriver.findElement(By.cssSelector(".j-custom-select-yeardropdown")));
				yearRange.selectByIndex(6);
			}
		
			WebElement timeSpan = webDriver.findElement(By.cssSelector(".timespan_custom"));
			List<WebElement> tss = timeSpan.findElements(By.cssSelector(".select2-container--yeardropdown"));
			tss.get(0).click();
			WebElement yearFrom = webDriver.findElement(By.cssSelector(".select2-search__field"));
			yearFrom.clear();
			yearFrom.sendKeys(source_SearchYearFromArry[searchNo]);
			yearFrom.sendKeys(Keys.ENTER);
			tss.get(1).click();
			WebElement yearTo = webDriver.findElement(By.cssSelector(".select2-search__field"));
			yearTo.clear();
			yearTo.sendKeys(source_SearchYearToArry[searchNo]);	
			yearTo.sendKeys(Keys.ENTER);
			
			
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
			
			// Waiting for the result for 10 seconds
			wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("/html/body/div[13]/form/table/tbody/tr")));

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
					for (i = 0; i < 48; i++) {
						Result[i] = "";
					}

					try {
						// Get the result row
						List<WebElement> tc = tbb.findElements(By.cssSelector(".search-results-content"));

						// Title
						WebElement titleItem = tbb.findElement(By.cssSelector("a.smallV110"));
						Result[0] = titleItem.getText();

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
									Result[2] = sourceStr.substring(0, sourceStr.indexOf("卷:"))
											.replaceAll("(\\r\\n|\\r|\\n|\\n\\r)", "");
									// Publish Year
									Result[3] = sourceStr.substring(sourceStr.indexOf("出版年: ") + 4);
								}
							}
						}

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

						// Open the detail record page
						String detailrecord = titleItem.getAttribute("href");
						JavascriptExecutor executor = (JavascriptExecutor) webDriver;
						Thread.sleep(3500);
						try {
							executor.executeScript("window.open('" + detailrecord + "')");
							getDetail(webDriver);
						} catch (Exception e3) {
							writrintExcel();
							continue;
						}

						// Write the data into excel
						writrintExcel();

						// result array clear
						for (i = 0; i < 48; i++) {
							Result[i] = "";
						}
					} catch (Exception e) {
						// Write the data into excel
						writrintExcel();

						// result array clear
						for (i = 0; i < 48; i++) {
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
				int pageclick = 0;
				try {
					WebElement next = webDriver.findElement(By.cssSelector("[title='下一页']"));
					next.click();
				} catch (Exception e3) {
					if (pageclick < 3) {
						WebElement next = webDriver.findElement(By.cssSelector("[title='下一页']"));
						next.click();
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
					By.xpath("//*[@id='records_form']/div/div/div/div[1]/div/div[1]/value")));
			
			// Get the authors
			List<WebElement> authorItem = webDriver.findElements(By.xpath("//*[text()='作者:']/.."));
			for (WebElement tAu : authorItem) {
				if (!tAu.getText().substring(0, 1).equals("["))
					Result[1] = Result[1] + tAu.getText();
			}
			Pattern pattern = Pattern.compile("\\[.*?\\]");
			Matcher matcher = pattern.matcher(Result[1]);
			Result[1] = matcher.replaceAll("");
			Result[1] = Result[1].substring(3);
			
			// get author keywords
			try {
				List<WebElement> tk = webDriver.findElements(By.xpath("//*[text()='作者关键词:']/../following-sibling::a"));
				for (WebElement tkk : tk) {
					Result[5] = Result[5] + ";" + tkk.getText();
				}
				Result[5] = Result[5].substring(1);
			} catch (Exception e) {
				Result[5] = " ";
			}

			// Get keywords plus
			try {
				List<WebElement> tl = webDriver
						.findElements(By.xpath("//*[text()='KeyWords Plus:']/following-sibling::a"));
				String ddd = tl.get(0).getText();
				for (WebElement tll : tl) {
					Result[6] = Result[6] + ";" + tll.getText();
				}
				Result[6] = Result[6].substring(1);
			} catch (Exception e) {
				Result[6] = " ";
			}
			
			try {
				// Get the address
				List<WebElement> addressItem = webDriver
						.findElements(By.xpath("//span[contains(text(), '地址:')]/../following-sibling::table/tbody/tr"));
				for (WebElement tkk3 : addressItem) {
					if (tkk3.getText().substring(0,1).equals("["))
						Result[7] = Result[7] + "||" + tkk3.getText().substring(5);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Result[7] = "";
			}
			Result[7] = Result[7].substring(2);
					
			// Impact factors
			try {
				WebElement journalItem = webDriver.findElement(By.xpath("//*[contains(text(), '查看期刊影响力')]"));
				journalItem.click();
				Thread.sleep(1500);
				List<WebElement> impactFrItem = webDriver.findElements(
						By.xpath("//span[contains(text(), 'impact factor')]/../following-sibling::table/tbody/tr/td"));
				Result[8] = impactFrItem.get(0).getText();
				Result[9] = impactFrItem.get(1).getText();

				// Close the impact factor window
				Thread.sleep(1500);
				webDriver.findElement(By.xpath("//*[contains(text(), '关闭窗口')]")).click();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Result[8] = "";
				Result[9] = "";
			}					
		} catch (Exception e) {
			return;
		}
	}

	public static void writrintExcel() throws IOException {
		// write into excel
		writer.println(source_ID + "\t" + source_NameCN + "\t" + source_LstName + "\t" + source_FstName + "\t"
				+ source_DissYear + "\t" + source_DissInst + "\t" + source_DissIntstWOS + "\t" + source_CurAffi + "\t"
				+ source_CurAffiWOS + "\t" + source_ProgYear + "\t" + source_ProgAffi + "\t" + source_ProgAffiWOS + "\t"
				+ source_Search + "\t" + source_SearchYearFrom + "\t" + source_SearchYearTo + "\t" + Result[0] + "\t"
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
		Cell cell1, cell2, cell3, cell4, cell5, cell6, cell7, cell8, cell9, cell10, cell11, cell12, cell13, cell14,
				cell15, cell16, cell17, cell18, cell19, cell20, cell21;
		try {
			cell1 = sheet.getCell(0, rawID);
			cell2 = sheet.getCell(1, rawID);
			cell3 = sheet.getCell(2, rawID);
			cell4 = sheet.getCell(3, rawID);
			cell5 = sheet.getCell(4, rawID);
			cell6 = sheet.getCell(5, rawID);
			cell7 = sheet.getCell(6, rawID);
			cell8 = sheet.getCell(7, rawID);
			cell9 = sheet.getCell(8, rawID);
			cell10 = sheet.getCell(9, rawID);
			cell11 = sheet.getCell(10, rawID);
			cell12 = sheet.getCell(11, rawID);
			cell13 = sheet.getCell(12, rawID);
			cell14 = sheet.getCell(13, rawID);
			cell15 = sheet.getCell(14, rawID);
			cell16 = sheet.getCell(15, rawID);
			cell17 = sheet.getCell(16, rawID);
			cell18 = sheet.getCell(17, rawID);
			cell19 = sheet.getCell(18, rawID);
			cell20 = sheet.getCell(19, rawID);
			cell21 = sheet.getCell(20, rawID);

			if ("".equals(cell1.getContents()) != true) {
				source_ID = cell1.getContents();
				source_NameCN = cell2.getContents();
				source_LstName = cell3.getContents();
				source_FstName = cell4.getContents();
				source_DissYear = cell5.getContents();
				source_DissInst = cell6.getContents();
				source_DissIntstWOS = cell7.getContents();
				source_CurAffi = cell8.getContents();
				source_CurAffiWOS = cell9.getContents();
				source_ProgYear = cell10.getContents();
				source_ProgAffi = cell11.getContents();
				source_ProgAffiWOS = cell12.getContents();
				source_SearchArry[0]  = cell13.getContents();
				source_SearchYearFromArry[0]   = cell14.getContents();
				source_SearchYearToArry[0] = cell15.getContents();
				source_SearchArry[1] = cell16.getContents();
				source_SearchYearFromArry[1] = cell17.getContents();
				source_SearchYearToArry[1] = cell18.getContents();
				source_SearchArry[2] = cell19.getContents();
				source_SearchYearFromArry[2] = cell20.getContents();
				source_SearchYearToArry[2] = cell21.getContents();
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