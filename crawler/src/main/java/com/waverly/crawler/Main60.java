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
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
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
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class Main60 {
	public static int i = 0;
	public static int j = 0;
	public static int pages = 0;
	public static JTextField searchstring = new JTextField();
	public static JTextField location = new JTextField();
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
	public static String[] auhorParam = { "Select", "No select" };
	public static String[] organizationParam = { "Select", "No select" };
	public static String[] yearParam = { "Select", "No select" };

	public static JComboBox combo1 = new JComboBox(auhorParam);
	public static JComboBox combo2 = new JComboBox(organizationParam);
	public static JComboBox combo3 = new JComboBox(yearParam);
	private static JCheckBox QKLW = new JCheckBox("期刊论文", true);
	private static JCheckBox XWLW = new JCheckBox("学位论文", true);
	private static JCheckBox HYLW = new JCheckBox("会议论文", true);
	private static JCheckBox ZL = new JCheckBox("专利", true);
	private static JCheckBox ZWBZ = new JCheckBox("中外标准", false);
	private static JCheckBox KJCG = new JCheckBox("科技成果", false);
	private static JCheckBox FLFG = new JCheckBox("法律法规", false);
	private static JCheckBox KJBG = new JCheckBox("科技报告", true);
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
	
	public static String FileID = "";
	public static String ID = "";
	public static String authorName = "";
	public static String phdUniversity = "";
	public static String phdYear = "";
	public static String dissertationAdvisor = "";
	public static String advisor1 = "";
	public static String advisor2= "";


	
	public static int rowID = 1;
	public static int rowID_Total = 0;
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
			System.out.println("用户的当前工作目录:"+System.getProperty("user.dir"));
			input();
			q = "q-" + searchstring.getText();
			String q1 = q.replace(" ", "_");

			if (filename.getText().equalsIgnoreCase("")) {
				JOptionPane.showMessageDialog(null, "Please enter the file path.");
				filename.requestFocusInWindow();
				filename.setText("jobs");
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
			book = Workbook.getWorkbook(new File("postdoc_list.xls"));
			sheet = book.getSheet(0);
			rowID_Total = sheet.getRows();

			// URL =
			// "http://kns.cnki.net/kns/brief/result.aspx?dbprefix=SCDB&crossDbcodes=CCJD,CPFD,IPFD,CDFD,CMFD,SCOD,CJRF,CJFQ,CJFN";
			// URL = "http://kns.cnki.net/kns/brief/result.aspx?dbprefix=SCDB&crossDbcodes=" + libraryStr;
			URL = "http://www.wanfangdata.com.cn/searchResult/getAdvancedSearch.do?searchType=all";

			// Initialize chrome drive in Seleuium
			System.getProperties().setProperty("webdriver.chrome.driver", "chromedriver.exe");

			ChromeOptions options = new ChromeOptions();
			// options.addArguments("--lang=zh-cn");
			WebDriver webDriver = new ChromeDriver(options);
			webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

			webDriver.get(URL);
			// Waiting for element for 10 seconds
			WebDriverWait wait = new WebDriverWait(webDriver, 10);
			wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.linkText("高级检索")));

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
			String toptitle = "FileID\tID\t姓名\tPhD univ\tPhD year\tDissertation advisor\tadvisor 1\tadvisor 2\t题目 Title\t关键词（Key Words）\t作者（Author）"
					+ "\t学位授予单位（University）\t授予学位（Degree）\t学科专业（major）\t导师（advisor）\t学位年度（Year）\t语种（language）"
					+ "\t分类号";
			writer.println(toptitle);
			
			int startRow, endRow;
			if (jRadio1.isSelected()) {
				startRow = 1;
				endRow = rowID_Total;
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
					endRow = rowID_Total;
				}
			}

			// Waiting for element for 10 seconds
			wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".screen_title")));
			
			//Click the libary check options
			//Clear all the library 
			WebElement removeLibrary = webDriver.findElement(By.cssSelector(".condtion_cz"));
			webDriver.manage().window().maximize();
			removeLibrary.findElement(By.linkText("清除")).click();					
			WebElement libraryCondition = webDriver.findElement(By.cssSelector(".screen_condition"));
			//期刊论文
			if (QKLW.isSelected()) {
				libraryCondition.findElement(By.xpath("//*[text()='期刊论文']")).click();						
			}

			//学位论文
			if (XWLW.isSelected()){
				libraryCondition.findElement(By.xpath("//*[text()='学位论文']")).click();
			}

			//会议论文
			if (HYLW.isSelected()) {
				libraryCondition.findElement(By.xpath("//*[text()='会议论文']")).click();					
			}
			
			//专利
			if (ZL.isSelected()) {
				libraryCondition.findElement(By.xpath("//*[text()='专利']")).click();
			}
			
			//中外标准
			if (ZWBZ.isSelected()) {
				libraryCondition.findElement(By.xpath("//*[text()='中外标准']")).click();
			}
			
			//科技成果
			if (ZWBZ.isSelected()) {
				libraryCondition.findElement(By.xpath("//*[text()='科技成果']")).click();
			}
			
			//法律法规
			if (FLFG.isSelected()) {
				libraryCondition.findElement(By.xpath("//*[text()='法律法规']")).click();
			}
			
			//科技报告
			if (KJBG.isSelected()) {
				libraryCondition.findElement(By.xpath("//*[text()='科技报告']")).click();
			}
			/*
			//检索信息选项
			List<WebElement> searchCondition = webDriver.findElements(By.cssSelector(".condition"));				
			Select gaoji = new Select(webDriver.findElement(By.cssSelector("#gaoji")));
			gaoji.selectByIndex(1);
			
			Select gaoji1 = new Select(webDriver.findElement(By.cssSelector("#gaoji1")));
			gaoji.selectByIndex(7);
			
			Select gaoji2 = new Select(webDriver.findElement(By.cssSelector("#gaoji2")));
			gaoji.selectByIndex(6);
			*/
			
			// Read the unedname from exccel sheet
			for (int i = startRow; i <= endRow; i++)  {
				try {
					sim_row = i;
					dataProgress.setPanel(total, page, row, sim_row);
					rowID = i;
					readExcel(sheet, rowID);
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
					
					//Click the libary check options
					//Clear all the library 
					webDriver.manage().window().maximize();
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
					WebElement removeLibrary2 = webDriver.findElement(By.cssSelector(".condtion_cz"));
					removeLibrary2.findElement(By.linkText("清除")).click();		
					WebElement libraryCondition2 = webDriver.findElement(By.cssSelector(".screen_condition"));
					//期刊论文
					if (QKLW.isSelected()) {
						libraryCondition2.findElement(By.xpath("//*[text()='期刊论文']")).click();						
					}

					//学位论文
					if (XWLW.isSelected()){
						libraryCondition2.findElement(By.xpath("//*[text()='学位论文']")).click();
					}

					//会议论文
					if (HYLW.isSelected()) {
						libraryCondition2.findElement(By.xpath("//*[text()='会议论文']")).click();					
					}
					
					//专利
					if (ZL.isSelected()) {
						libraryCondition2.findElement(By.xpath("//*[text()='专利']")).click();
					}
					
					//中外标准
					if (ZWBZ.isSelected()) {
						libraryCondition2.findElement(By.xpath("//*[text()='中外标准']")).click();
					}
					
					//科技成果
					if (ZWBZ.isSelected()) {
						libraryCondition2.findElement(By.xpath("//*[text()='科技成果']")).click();
					}
					
					//法律法规
					if (FLFG.isSelected()) {
						libraryCondition2.findElement(By.xpath("//*[text()='法律法规']")).click();
					}
					
					//科技报告
					if (KJBG.isSelected()) {
						libraryCondition2.findElement(By.xpath("//*[text()='科技报告']")).click();
					}
				
					int status = searchName(webDriver, authorName, phdUniversity);
					try {
						if (status == 1) {
							// Get the item name
							getAName(webDriver);
						} else {
							webDriver.navigate().refresh();
							status = searchName(webDriver, authorName, phdUniversity);
							if (status == 1) {
								getAName(webDriver);
							} else {
								throw new Exception("throw error");
							}
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
					webDriver.get(URL);
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

	public static int searchName(WebDriver webDriver, String author, String authorOrg) throws IOException {
		try {
			// Waiting for element for 10 seconds
			WebDriverWait wait = new WebDriverWait(webDriver, 10);
			wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".screen_title")));

			// Get the search condition item
			List<WebElement> searchCondition = webDriver.findElements(By.cssSelector(".condition"));	
			
			// Input the author
			WebElement author_input = searchCondition.get(2).findElement(By.cssSelector("#ddd"));
			author_input.clear();
			if (auhorParamState) {
				author_input.sendKeys(authorName);
			}

			// Input the author organization
			WebElement authorOrg_input = searchCondition.get(3).findElement(By.cssSelector("#ddd"));
			authorOrg_input.clear();
			if (organizationParamState) {
				authorOrg_input.sendKeys(phdUniversity);
			}
			
			// input the project duration from - to
			Select yearParamfrom = new Select(webDriver.findElement(By.cssSelector("#advanced_search_publshdate_start")));			
			Select yearParamto = new Select(webDriver.findElement(By.cssSelector("#advanced_search_publshdate_end")));
			if (yearParamState)
			{			
				/*
				publishdate_from = String.valueOf((Integer.parseInt(publishdate_from)-1)) + "-01-01";
				publishdate_to = String.valueOf(Integer.parseInt(phdYear)+1);
				*/
				publishdate_from = phdYear+"年";
				publishdate_to = String.valueOf(Integer.parseInt(phdYear)+1)+"年";
				
				yearParamfrom.selectByVisibleText(publishdate_from);
				yearParamto.selectByVisibleText(publishdate_to);
			}
		
			// Click "search" button
			webDriver.findElement(By.cssSelector("#set_advanced_search_btn")).click();
			return 1;
		} catch (Exception e2) {
			System.out.print(e2);
			return 0;
		}
	}

	public static void getAName(WebDriver webDriver) throws IOException {
		try {
			// Get the page number
			int pages;

			// Create a map to store the author info in case of
			// identical author name in one search
			Hashtable<String, String> AuthorMaptable = new Hashtable<String, String>();

			try {
				webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
				// Waiting for element for 10 seconds
				WebDriverWait wait = new WebDriverWait(webDriver, 10);
				wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
						By.cssSelector(".result_stati")));
				// Get the result number
				String pc_string = webDriver
						.findElement(By.cssSelector(".result_stati > strong"))
						.getText();
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
						pages = (Integer.parseInt(pc_string) - 1) / 20 + 1;
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
				// Get Window
				Thread.sleep(4000);
				
				WebElement resultContent = webDriver.findElement(By.cssSelector(".BatchOper + div"));			
				List<WebElement> tb = resultContent.findElements(By.cssSelector(".ResultCont"));
				
				//Get row loop
				for (WebElement t : tb) {
					// Close the detail page and return the list
										// page
					ArrayList<String> tabs;
					try {
						row++;
						total = pages;
						dataProgress.setPanel(total, page, row, sim_row);
						
						tabs = new ArrayList<String>(webDriver.getWindowHandles());
						if (tabs.size() > 1) {
							for (int a = tabs.size(); a > 1; a--) {
								webDriver.switchTo().window(tabs.get(a - 1));
								Thread.sleep(500);
								webDriver.close();
							}
							webDriver.switchTo().window(tabs.get(0));
						}
						tabs.clear();
						
						// result array clear
						for (i = 0; i < 12; i++) {
							Result[i] = "";
						}
						
						// Get the title
						String title = t.findElement(By.cssSelector(".title > a")).getText();
						Result[0] = title;
						
						// Open the detail record page
						String detailrecord = t.findElement(By.cssSelector(".title > a")).getAttribute("href");												
						JavascriptExecutor executor = (JavascriptExecutor) webDriver;
						
						Thread.sleep(2000);						
						// Open the detail page
						try {
							executor.executeScript("window.open('" + detailrecord + "')");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							writrintExcel();
							// result array clear
							for (i = 0; i < 12; i++) {
								Result[i] = "";
							}
							continue;
						}

						int status = getDetail(webDriver);
						try {
							if (status == 0) {
								// Get the item name
								status = getDetail(webDriver);
								if (status == 0) {
									throw new Exception("throw error");
								}
							}
						} catch (Exception e1) {
							Thread.sleep(3000);
							writrintExcel();
							continue;
						}
						
						writrintExcel();
						// result array clear
						for (i = 0; i < 12; i++) {
							Result[i] = "";
						}
					} catch (Exception e3) {
						// TODO Auto-generated catch block
						e3.printStackTrace();
						// Write the data into excel
						writrintExcel();

						// result array clear
						for (i = 0; i < 12; i++) {
							Result[i] = "";
						}

						// Close detail page, return the list page
						tabs = new ArrayList<String>(webDriver.getWindowHandles());
						if (tabs.size() > 1) {
							for (int a = tabs.size(); a > 1; a--) {
								webDriver.switchTo().window(tabs.get(a - 1));
								Thread.sleep(500);
								webDriver.close();
							}
							webDriver.switchTo().window(tabs.get(0));
						}
						tabs.clear();
						continue;
					}
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
				tabs.clear();
				
				// get the next page
				try {
					WebElement tk = webDriver.findElement(By.cssSelector(".laypage_next"));
					if (tk.getText().equals("下一页")) {
						try {
							tk.click();
						} catch (Exception e3) {
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					break;
				}
			}
			Thread.sleep(3000);
		} catch (Exception e2) {
			exceptionCode = 2;
		}
	}

	public static int getDetail(WebDriver webDriver) throws IOException {
		try {
			// Switch to detail page
			ArrayList<String> tabs;
			tabs = new ArrayList<String>(webDriver.getWindowHandles());
			// switches to new tab
			webDriver.switchTo().window(tabs.get(1));
			tabs = null;

			WebDriverWait wait = new WebDriverWait(webDriver, 10);
			wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".crumbs")));
				isPatentPage = false;

			// keywords
			String keywordStr = "";
			String author = "";
			String university = "";
			String degree = "";
			String major = "";
			String advisor = "";
			String year = "";
			String language = "";
			String categoryNo = "";
			
			// Gether label
			List<WebElement> catalog_Label =  webDriver.findElements(By.cssSelector(".info_left"));
			ArrayList<String> catalog_LabelStr = new ArrayList<String>();
			for (WebElement catalog_Labellist : catalog_Label) {
				catalog_LabelStr.add(catalog_Labellist.getText());
			}

			// keywords
			try {
				if (catalog_LabelStr.contains("关键词：")) {
					// keywords
					List<WebElement> tu = webDriver
							.findElements(By.xpath("//*[text()='关键词：']/following-sibling::div"));
					for (WebElement tds : tu) {
						keywordStr = keywordStr + ";" + tds.getText();
					}
					keywordStr = keywordStr.substring(1);
				}
			} catch (Exception e1) {
			}
			
			//author 
			try {
				if (catalog_LabelStr.contains("作者：")) {
					// keywords
					List<WebElement> tu = webDriver
							.findElements(By.xpath("//*[text()='作者：']/following-sibling::div"));
					for (WebElement tds : tu) {
						author = author + ";" + tds.getText();
					}
					author = author.substring(1);
				}
			} catch (Exception e1) {
			}
			
			//university 
			try {
				if (catalog_LabelStr.contains("学位授予单位：")) {
					// keywords
					List<WebElement> tu = webDriver
							.findElements(By.xpath("//*[text()='学位授予单位：']/following-sibling::div"));
					for (WebElement tds : tu) {
						university = university + ";" + tds.getText();
					}
					university = university.substring(1);
				}
			} catch (Exception e1) {
			}
			
			//degree 
			try {
				if (catalog_LabelStr.contains("授予学位：")) {
					// keywords
					List<WebElement> tu = webDriver
							.findElements(By.xpath("//*[text()='授予学位：']/following-sibling::div"));
					for (WebElement tds : tu) {
						degree = degree + ";" + tds.getText();
					}
					degree = degree.substring(1);
				}
			} catch (Exception e1) {
			}
			
			//major 
			try {
				if (catalog_LabelStr.contains("学科专业：")) {
					// keywords
					List<WebElement> tu = webDriver
							.findElements(By.xpath("//*[text()='学科专业：']/following-sibling::div"));
					for (WebElement tds : tu) {
						major = major + ";" + tds.getText();
					}
					major = major.substring(1);
				}
			} catch (Exception e1) {
			}
			
			//advisor 
			try {
				if (catalog_LabelStr.contains("导师姓名：")) {
					// keywords
					List<WebElement> tu = webDriver
							.findElements(By.xpath("//*[text()='导师姓名：']/following-sibling::div"));
					for (WebElement tds : tu) {
						advisor = advisor + ";" + tds.getText();
					}
					advisor = advisor.substring(1);
				}
			} catch (Exception e1) {
			}
			
			//year 
			try {
				if (catalog_LabelStr.contains("学位年度：")) {
					// keywords
					List<WebElement> tu = webDriver
							.findElements(By.xpath("//*[text()='学位年度：']/following-sibling::div"));
					for (WebElement tds : tu) {
						year = year + ";" + tds.getText();
					}
					year = year.substring(1);
				}
			} catch (Exception e1) {
			}
			
			//language 
			try {
				if (catalog_LabelStr.contains("语种：")) {
					// keywords
					List<WebElement> tu = webDriver
							.findElements(By.xpath("//*[text()='语种：']/following-sibling::div"));
					for (WebElement tds : tu) {
						language = language + ";" + tds.getText();
					}
					language = language.substring(1);
				}
			} catch (Exception e1) {
			}

			//categoryNo 
			try {
				if (catalog_LabelStr.contains("分类号：")) {
					// keywords
					List<WebElement> tu = webDriver
							.findElements(By.xpath("//*[text()='分类号：']/following-sibling::div"));
					for (WebElement tds : tu) {
						categoryNo = categoryNo + ";" + tds.getText();
					}
					categoryNo = categoryNo.substring(1);
				}
			} catch (Exception e1) {
			}

			Result[1] = keywordStr;
			Result[2] = author;
			Result[3] = university;
			Result[4] = degree;
			Result[5] = major;
			Result[6] = advisor;
			Result[7] = year;
			Result[8] = language;
			Result[9] = categoryNo;
			return 1;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}

	public static void writrintExcel() throws IOException {
		// write into excel
		writer.println(FileID + "\t" + ID + "\t" + authorName + "\t" + phdUniversity + "\t" + phdYear + "\t"
				+ dissertationAdvisor + "\t" + advisor1 + "\t" + advisor2 + "\t" + Result[0] + "\t" + Result[1] + "\t"
				+ Result[2] + "\t" + Result[3] + "\t" + Result[4] + "\t" + Result[5] + "\t" + Result[6]+ "\t" + Result[7] + "\t" + Result[8] + "\t" + Result[9]);
		writer.flush();
	}

	public static void input() throws IOException {
		JPanel panel = new JPanel(new GridLayout(0, 1));
		panel.add(new JLabel("File path to store results (without extention):"));
		panel.add(filename);
		panel.add(new JLabel("Author name:"));
		panel.add(combo1);
		panel.add(jRadio1);
		panel.add(jRadio2);
		jRadioGroup.add(jRadio1);
		jRadioGroup.add(jRadio2);
		panel.add(new JLabel("From:"));
		panel.add(recordFrom);
		panel.add(new JLabel("To:"));
		panel.add(recordTo);
		panel.add(new JLabel("Organization:"));
		panel.add(combo2);
		panel.add(new JLabel("year?:"));
		panel.add(combo3);
		panel.add(new JLabel("Library select:"));
		panel.add(QKLW);
		panel.add(XWLW);
		panel.add(HYLW);
		panel.add(ZL);
		panel.add(ZWBZ);
		panel.add(KJCG);
		panel.add(FLFG);
		panel.add(KJBG);

		int result = JOptionPane.showConfirmDialog(null, panel, "zhiwang - Search Criteria", 2, -1);
		if (result == 0) {
			return;
		}
		JOptionPane.showMessageDialog(frame, "Cancelled");
		System.exit(0);
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

	public static void readExcel(Sheet sheet, int rowID) {
		Cell cell0, cell1, cell2, cell3, cell4, cell5, cell6, cell7;
		try {
			cell0 = sheet.getCell(0, rowID);
			cell1 = sheet.getCell(1, rowID);
			cell2 = sheet.getCell(2, rowID);
			cell3 = sheet.getCell(3, rowID);
			cell4 = sheet.getCell(4, rowID);
			cell5 = sheet.getCell(5, rowID);
			cell6 = sheet.getCell(6, rowID);
			cell7 = sheet.getCell(7, rowID);


			if ("".equals(cell1.getContents()) != true) {
				FileID = cell0.getContents();
				ID = "ID: "+ cell1.getContents();
				authorName = cell2.getContents();
				phdUniversity = cell3.getContents();
				phdYear = cell4.getContents();
				dissertationAdvisor = cell5.getContents();
				advisor1 = cell6.getContents();
				advisor2 = cell7.getContents();
				System.out.println(rowID + " " + cell1.getContents() + " " + cell2.getContents());
			}
		} catch (Exception e) {
		}
	}

}