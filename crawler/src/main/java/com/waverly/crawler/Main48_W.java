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
import org.openqa.selenium.support.ui.WebDriverWait;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class Main48_W {
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

			// assemble the library code
			String libraryCJFQ = "";
			String libraryCJRF = "";
			String libraryCJFN = "";
			String libraryCDFD = "";
			String libraryCPFD = "";
			String libraryIPFD = "";
			String librarySCOD = "";
			String libraryCCJD = "";
			String libraryStr = "";

			if (CJFQ.isSelected())
				libraryCJFQ = ",CJFQ";

			if (CJRF.isSelected())
				libraryCJRF = ",CJRF";

			if (CJFN.isSelected())
				libraryCJFN = ",CJFN";

			if (CDFD.isSelected())
				libraryCDFD = ",CDFD";

			if (CPFD.isSelected())
				libraryCPFD = ",CPFD";

			if (IPFD.isSelected())
				libraryIPFD = ",IPFD";

			if (SCOD.isSelected())
				librarySCOD = ",SCOD";

			if (CCJD.isSelected())
				libraryCCJD = ",CCJD";

			libraryStr = libraryCJFQ + libraryCJRF + libraryCJFN + libraryCDFD + libraryCPFD + libraryIPFD + librarySCOD
					+ libraryCCJD;
			if (libraryStr.substring(0, 1).equals(",")) {
				libraryStr = libraryStr.substring(1);
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
			URL = "https://kns.cnki.net/kns/brief/result.aspx?dbprefix=SCDB&crossDbcodes=" + libraryStr;

			// Initialize chrome drive in Seleuium
			System.getProperties().setProperty("webdriver.chrome.driver", "chromedriver.exe");

			ChromeOptions options = new ChromeOptions();
			// options.addArguments("--lang=zh-cn");
			WebDriver webDriver = new ChromeDriver(options);
			webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

			webDriver.get(URL);
			// Waiting for element for 10 seconds
			WebDriverWait wait = new WebDriverWait(webDriver, 10);
			wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//*[@id='btnSearch']")));

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

			String toptitle = "ID\tStkcd\tyear\tName_cn\tPersonID\tuniversity_cn\tfirstname_en\tlastname_en\tname_en\tuniversity_en"
					+ "\t标题\t作者code\t发表时间\t数据库\t被引\t下载"
					+ "\t阅读\t关键词\t基金\t分类号\tISSN\tDOI\t专利申请号\t专利申请日\t专利公开号\t专利公开日"
					+ "\t专利申请人\t专利地址\t专利发明人\t专利代理机构\t专利代理人\t专利主分类号\t专利分类号\t国省代码";
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

			// Read the unedname from exccel sheet
			for (int i = startRow; i <= endRow; i++) {
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

					int status = searchName(webDriver, author, authorOrg);
					try {
						if (status == 1) {
							// Get the item name
							getAName(webDriver);
						} else {
							webDriver.navigate().refresh();
							status = searchName(webDriver, author, authorOrg);
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

	public static int searchName(WebDriver webDriver, String author, String authorOrg) throws IOException {
		try {
			// Waiting for element for 10 seconds
			WebDriverWait wait = new WebDriverWait(webDriver, 10);
			wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//*[@id='au_1_value1']")));

			// Input the author
			WebElement author_input = webDriver.findElement(By.xpath("//*[@id='au_1_value1']"));
			author_input.clear();
			if (auhorParamState) {
				author_input.sendKeys(Name_cn);
				// author_input.sendKeys("高文");
			}

			// Input the author organization
			WebElement authorOrg_input = webDriver.findElement(By.xpath("//*[@id='au_1_value2']"));
			authorOrg_input.clear();
			if (organizationParamState) {
				authorOrg_input.sendKeys(university_cn);
				// authorOrg_input.sendKeys("中国科学院");
			}

			// input the project duration from - to
			WebElement yearParamfrom = webDriver.findElement(By.xpath("//*[@id='publishdate_from']"));
			yearParamfrom.clear();
			WebElement yearParamto = webDriver.findElement(By.xpath("//*[@id='publishdate_to']"));
			yearParamto.clear();
			yearParamfrom.sendKeys("2010-01-01");
			yearParamto.sendKeys("2019-12-30");

			// Click "search" button
			((ChromeDriver) webDriver).findElementByXPath("//*[@id='ddSubmit']/b").click();
			((ChromeDriver) webDriver).findElementByXPath("//*[@id='btnSearch']").click();
			return 1;
		} catch (Exception e2) {
			writrintExcel();
			System.out.print(e2);
			return 0;
		}
	}

	public static void getAName(WebDriver webDriver) throws IOException {
		try {
			// Get the page number
			int pages;

			// Get iframe
			WebElement iframe = webDriver.findElement(By.id("iframeResult"));

			// Create a map to store the author info in case of
			// identical author name in one search
			Hashtable<String, String> AuthorMaptable = new Hashtable<String, String>();

			String now_handle = webDriver.getWindowHandle();
			Set<String> all_handles = webDriver.getWindowHandles();
			// identify if the windows is correct
			for (String handle : all_handles) {
				if (handle != now_handle) {
					webDriver.switchTo().window(handle);
					((ChromeDriver) webDriver).switchTo().frame(iframe);
				}
			}
			try {
				webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
				// Waiting for element for 10 seconds
				WebDriverWait wait = new WebDriverWait(webDriver, 10);
				wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
						By.xpath("//*[@id='J_ORDER']/tbody/tr[2]/td/table/tbody/tr/td[2]/div/div")));
				// Get the result number
				String pc_string = webDriver
						.findElement(By.xpath("//*[@id='J_ORDER']/tbody/tr[2]/td/table/tbody/tr/td[2]/div/div"))
						.getText().substring(2);
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

			now_handle = webDriver.getWindowHandle();
			all_handles = webDriver.getWindowHandles();
			// 判断窗口是否一致
			for (String handle : all_handles) {
				if (handle != now_handle) {
					webDriver.switchTo().window(handle);
					((ChromeDriver) webDriver).switchTo().frame(iframe);
				}
			}

			// Initialize the flag of page
			isFirstPage = true;
			for (int k = 0; k < pages; k++) {
				page++;
				// Get Window
				now_handle = webDriver.getWindowHandle();
				all_handles = webDriver.getWindowHandles();
				// Identify if the window is correct
				for (String handle : all_handles) {
					if (handle != now_handle) {
						webDriver.switchTo().window(handle);
						((ChromeDriver) webDriver).switchTo().frame(iframe);
					}
				}

				// Select the grid area with 10 items
				if (isFirstPage) {
					WebElement btn = ((ChromeDriver) webDriver)
							.findElementByXPath("//*[@id=\"id_grid_display_num\"]/a[2]");
					btn.click();
					isFirstPage = false;
				}

				Thread.sleep(4000);
				// Get the element tr in this iframe
				List<WebElement> tb = webDriver.findElements(By.xpath("//*[@id=\"ctl00\"]/table/tbody/tr[2]"));


				
				//Get row loop
				for (WebElement t : tb) {
					// Close the detail page and return the list
					// page
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

					// Get iframe
					// iframe = webDriver.findElement(By.id("iframeResult"));				
					now_handle = webDriver.getWindowHandle();
					all_handles = webDriver.getWindowHandles();
					// identify if the windows is
					// correct
					for (String handle2 : all_handles) {
						if (handle2 != now_handle) {
							webDriver.switchTo().window(handle2);
							((ChromeDriver) webDriver).switchTo().frame(iframe);
						}
					}
					
					List<WebElement> tbod = t.findElements(By.tagName("tbody"));
					for (WebElement tr : tbod) {
						row++;
						List<WebElement> td = tr.findElements(By.tagName("tr"));
						td.remove(0);

						// result array clear
						for (i = 0; i < 40; i++) {
							Result[i] = "";
						}

						for (WebElement tds : td) {
							try {
								// Close the detail page and return the list
								// page
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
								
								// Get iframe
								// iframe = webDriver.findElement(By.id("iframeResult"));				
								now_handle = webDriver.getWindowHandle();
								all_handles = webDriver.getWindowHandles();
								// identify if the windows is
								// correct
								for (String handle2 : all_handles) {
									if (handle2 != now_handle) {
										webDriver.switchTo().window(handle2);
										((ChromeDriver) webDriver).switchTo().frame(iframe);
									}
								}
								
								List<WebElement> tdss = tds.findElements(By.tagName("td"));

								// click the more authors
								try {
									WebElement tAuMore = tdss.get(2).findElement(By.cssSelector("[title='显示全部作者']"));
									tAuMore.click();
								} catch (Exception e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}

								// Title
								Result[0] = tdss.get(1).getText();

								// Get the single author link
								List<WebElement> tAu2 = tdss.get(2).findElements(By.cssSelector(".KnowledgeNetLink"));
	
								// Get all author name text including without author link
								List<String> auStrallSingleArray=new ArrayList<String>();
								List<String> auStrallSingleArray_temp=new ArrayList<String>();								
								try {
									Collections.addAll(auStrallSingleArray_temp, tdss.get(2).getText().split(";"));
									for (String tt : auStrallSingleArray_temp) {
										for (String tt2 : tt.split(",")) {
											auStrallSingleArray.add(tt2);
										}
									}
								} catch (Exception e4) {
									// TODO Auto-generated catch block
									e4.printStackTrace();
								}
								
								String tAu_Code_Str = "unknown";
								String auName_Str = "unknown";
								String orgName_Str = "unknown";
								String auStr = "";
								String auStrSingle = "";
								int identicalFlag = 0;
								for (WebElement tAu_link : tAu2) {
									orgName_Str = "unknown";
									try {
										Thread.sleep(1000);
										identicalFlag = 0;
										try {
											// Get the author name
											auName_Str = tAu_link.getText();
											// tAu_link.click();
										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}

										// put the author info into a hashtable
										// to store
										Enumeration e = AuthorMaptable.keys();

										// Get the author code
										String tAu_link_Str;
										try {
											tAu_link_Str = tAu_link.getAttribute("href");
											tAu_Code_Str = tAu_link_Str.substring(tAu_link_Str.lastIndexOf(("code=")) + 5);
										} catch (Exception e2) {
											// TODO Auto-generated catch block
											e2.printStackTrace();
											tAu_Code_Str = " ";
										}

										while (e.hasMoreElements()) {
											if (e.nextElement().toString().equals(auName_Str)) {
												if (!AuthorMaptable.get(auName_Str).contains("unknown")) {
													auStrSingle = AuthorMaptable.get(auName_Str);
													auStr = auStr + ";" + auStrSingle;
													if (auStr.substring(0, 1).equals(";")) {
														auStr = auStr.substring(1);
													}
													identicalFlag = 1;
													break;
												}
											}
										}

										if (identicalFlag == 0) {
											// Get the author code
											tAu_link_Str = tAu_link.getAttribute("href");
											tAu_Code_Str = tAu_link_Str
													.substring(tAu_link_Str.lastIndexOf(("code=")) + 5);
											try {
												// if it is the author name, then enter info page
												if (tAu_link.getText().equals(Name_cn)) {
													// enter author info page
													tAu_link.click();
													Thread.sleep(4000);
													clickCount++;
													if (clickCount == 30) {
														Thread.sleep(30000);
														clickCount = 0;
													}
												}
												// Switch to author page;
												tabs = new ArrayList<String>(webDriver.getWindowHandles());
												// switches to new tab
												webDriver.switchTo().window(tabs.get(1));
												tabs = null;

												WebDriverWait wait = new WebDriverWait(webDriver, 10);
												wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
														By.cssSelector("h2.name")));

												// Get the author name in the person
												// page
												if (auName_Str.equals("")) {
													auName_Str = webDriver
															.findElement(By.xpath("/html/body/div[7]/div[1]/div/h2"))
															.getText();
												}

												// Get the organization
												orgName_Str = webDriver.findElement(By.cssSelector("p.orgn")).getText();
											} catch (Exception e1) {
												// TODO Auto-generated catch
												// block
												e1.printStackTrace();
											}

											auStr = auStr + ";" + auName_Str + "+" + tAu_Code_Str + "+" + orgName_Str;
											auStrSingle = auName_Str + "+" + tAu_Code_Str + "+" + orgName_Str;
											if (auStr.substring(0, 1).equals(";")) {
												auStr = auStr.substring(1);
											}
										}
									} catch (Exception e2) {
										// Get the author code,name,organization
										auStr = auStr + ";" + auName_Str + "+" + tAu_Code_Str+ "+" + orgName_Str;
										if (auStr.substring(0, 1).equals(";")) {
											auStr = auStr.substring(1);
										}
										auStrSingle = auName_Str + "+" + tAu_Code_Str + "+" + orgName_Str;
									}

									// store the author info into a map
									AuthorMaptable.put(auName_Str, auStrSingle);
									
									// Close author page & return the list
									// page
									tabs = new ArrayList<String>(webDriver.getWindowHandles());
									if (tabs.size() > 1) {
										for (int a = tabs.size(); a > 1; a--) {
											webDriver.switchTo().window(tabs.get(a - 1));
											Thread.sleep(500);
											webDriver.close();
										}
									}
										webDriver.switchTo().window(tabs.get(0));
										tabs.clear();

										// Get iframe
										iframe = webDriver.findElement(By.id("iframeResult"));
										now_handle = webDriver.getWindowHandle();
										all_handles = webDriver.getWindowHandles();
										// identify if the windows is
										// correct
										for (String handle2 : all_handles) {
											if (handle2 != now_handle) {
												webDriver.switchTo().window(handle2);
												((ChromeDriver) webDriver).switchTo().frame(iframe);
											}
										}
									}
								
								// process the author name without link
								for (String auStrallSingle : auStrallSingleArray) {
									if (!auStr.contains(auStrallSingle.trim())) {
										int flagTemp = 0;
										Enumeration e2 = AuthorMaptable.keys();
										while (e2.hasMoreElements()) {
											if (e2.nextElement().toString().equals(auStrallSingle)) {
												auStr = auStr + ";" + AuthorMaptable.get(auStrallSingle);
												if (auStr.substring(0, 1).equals(";")) {
													auStr = auStr.substring(1);
												}
												flagTemp = 1;
												break;
											}
										}
										if (flagTemp == 0) {
											auStr = auStr + ";" + auStrallSingle + "+" +  "unknown" + "+" + "unknown";
											if (auStr.substring(0, 1).equals(";")) {
												auStr = auStr.substring(1);
											}
										}
									}
								}

			
								// author name+code+org
								Result[1] = auStr;

								// Publication year
								Result[5] = tdss.get(4).getText();

								// database
								Result[6] = tdss.get(5).getText();

								// Being refer number
								Result[7] = tdss.get(6).getText();
								if (Result[7] == null || Result[7].length() <= 0) {
									Result[7] = "0";
								}

								// Being download number
								Result[8] = tdss.get(7).getText();
								if (Result[8] == null || Result[8].length() <= 0) {
									Result[8] = "0";
								}

								// Being read number
								Result[9] = tdss.get(8).getText();
								if (Result[9] == null || Result[9].length() <= 0) {
									Result[9] = "0";
								}

								// Close author page & return the list
								// page
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

								now_handle = webDriver.getWindowHandle();
								all_handles = webDriver.getWindowHandles();
								// identify if the windows is
								// correct
								for (String handle2 : all_handles) {
									if (handle2 != now_handle) {
										webDriver.switchTo().window(handle2);
										((ChromeDriver) webDriver).switchTo().frame(iframe);
									}
								}

								// Open the detail page
								try {
									tdss.get(1).findElement(By.cssSelector(".fz14")).click();
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									writrintExcel();
									// result array clear
									for (i = 0; i < 40; i++) {
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

								/*
								if (isPatentPage == true) {
									try {
										getDetailPatent(webDriver);
									} catch (Exception e3) {
										writrintExcel();
									}
								}
								isPatentPage = false;
								*/

								// Write the data into excel
								writrintExcel();

								// result array clear
								for (i = 0; i < 40; i++) {
									Result[i] = "";
								}
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
										webDriver.switchTo().window(tabs.get(a - 1));
										Thread.sleep(500);
										webDriver.close();
									}
									webDriver.switchTo().window(tabs.get(0));
								}
								tabs.clear();

								now_handle = webDriver.getWindowHandle();
								all_handles = webDriver.getWindowHandles();
								// identify if the windows is
								// correct
								for (String handle2 : all_handles) {
									if (handle2 != now_handle) {
										webDriver.switchTo().window(handle2);
										((ChromeDriver) webDriver).switchTo().frame(iframe);
									}
								}
								continue;
							}
						}
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
				now_handle = webDriver.getWindowHandle();
				all_handles = webDriver.getWindowHandles();
				// 判断窗口是否一致
				for (String handle : all_handles) {
					if (handle != now_handle) {
						webDriver.switchTo().window(handle);
						((ChromeDriver) webDriver).switchTo().frame(iframe);
					}
				}
				
				// get the next page
				List<WebElement> tk = webDriver
						.findElements(By.xpath("//*[@id=\"ctl00\"]/table/tbody/tr[3]/td/table/tbody/tr/td/div/a"));
				for (WebElement t : tk) {
					if (t.getText().equals("下一页")) {
						try {
							t.click();
						} catch (Exception e3) {							
						}

					}
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

			try {
				WebDriverWait wait = new WebDriverWait(webDriver, 10);
				wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#catalog_Ptitle")));
				isPatentPage = false;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				try {
					Result[10] = "";
					Result[11] = "";
					Result[12] = "";
					Result[13] = "";
					Result[14] = "";
					getDetailPatent(webDriver);
					isPatentPage = true;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					return 0;
				}
			}

			// keywords
			String keywordStr = "";
			String fundStr = "";
			String categoryStr = "";
			String issnStr = "";
			String DOIStr = "";
			try {
				// keywords
				List<WebElement> tu = webDriver.findElements(By.cssSelector("#catalog_KEYWORD~a"));
				for (WebElement tds : tu) {
					keywordStr = keywordStr + tds.getText();
				}
			} catch (Exception e1) {
			}

			try {
				// Funds
				List<WebElement> tk = webDriver.findElements(By.cssSelector("#catalog_FUND~a"));
				for (WebElement tdk : tk) {
					fundStr = fundStr + tdk.getText();
				}
			} catch (Exception e1) {
			}

			try {
				// category no
				List<WebElement> tk1 = webDriver.findElements(By.xpath("//*[@id='catalog_ZTCLS']/.."));
				for (WebElement tdk1 : tk1) {
					categoryStr = categoryStr + tdk1.getText().substring(4);
				}
			} catch (Exception e1) {
			}

			try {
				// ISSN
				List<WebElement> tk2 = webDriver
						.findElements(By.cssSelector("#mainArea > div.wxmain > div.wxInfo > div.wxsour > div.sourinfo >p"));
				for (WebElement tdk2 : tk2) {
					if (tdk2.getText().contains("ISSN")) {
						issnStr = tdk2.getText().substring(tdk2.getText().indexOf("ISSN") + 5);
					}
				}
			} catch (Exception e1) {
			}

			try {
				// DOI
				List<WebElement> tk3 = webDriver.findElements(By.xpath("//*[@id='catalog_ZCDOI']/.."));
				for (WebElement tdk3 : tk3) {
					DOIStr = DOIStr + tdk3.getText().substring(4);
				}
				if (DOIStr.equals(""))
				{
					DOIStr= webDriver.findElement(By.xpath("//*[text()='DOI:']/following-sibling::a")).getText();
				}
			} catch (Exception e1) {
			}

			Result[10] = keywordStr;
			Result[11] = fundStr;
			Result[12] = categoryStr;
			Result[13] = issnStr;
			Result[14] = DOIStr;
			return 1;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}

	public static void getDetailPatent(WebDriver webDriver) throws IOException {
		// Switch to detail page
		ArrayList<String> tabs;
		tabs = new ArrayList<String>(webDriver.getWindowHandles());
		// switches to new tab
		webDriver.switchTo().window(tabs.get(1));
		tabs = null;

		try {
			WebDriverWait wait = new WebDriverWait(webDriver, 10);
			wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#userName")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Result[20] = "";
			Result[21] = "";
			Result[22] = "";
			Result[23] = "";
			Result[24] = "";
			Result[25] = "";
			Result[25] = "";
			Result[27] = "";
			Result[28] = "";
			Result[29] = "";
			Result[30] = "";
			Result[31] = "";
			return;
		}

		// patent body
		String applicationNo = "";
		String applicationDate = "";
		String publicationNo = "";
		String publicationDate = "";
		String applicant = "";
		String address = "";
		String inventor = "";
		String patentAgentOrg = "";
		String Agent = "";
		String patentMainCategoryNo = "";
		String patentCategoryNo = "";
		String nationProvince = "";
		try {
			List<WebElement> tu = webDriver.findElements(By.cssSelector("#box > tbody > tr > td"));
			for (WebElement tds : tu) {
				if (tds.getText().contains("申请号")) {
					applicationNo = tu.get(tu.indexOf(tds) + 1).getText();
				}

				if (tds.getText().contains("申请日")) {
					applicationDate = tu.get(tu.indexOf(tds) + 1).getText();
				}

				if (tds.getText().contains("公开号")) {
					publicationNo = tu.get(tu.indexOf(tds) + 1).getText();
				}

				if (tds.getText().contains("公开日")) {
					publicationDate = tu.get(tu.indexOf(tds) + 1).getText();
				}

				if (tds.getText().contains("申请人")) {
					applicant = tu.get(tu.indexOf(tds) + 1).getText();
				}

				if (tds.getText().contains("地址")) {
					address = tu.get(tu.indexOf(tds) + 1).getText();
				}

				if (tds.getText().contains("发明人")) {
					inventor = tu.get(tu.indexOf(tds) + 1).getText();
				}

				if (tds.getText().contains("专利代理机构")) {
					patentAgentOrg = tu.get(tu.indexOf(tds) + 1).getText();
				}

				if (tds.getText().contains("代理人")) {
					Agent = tu.get(tu.indexOf(tds) + 1).getText();
				}

				if (tds.getText().contains("主分类号")) {
					patentMainCategoryNo = tu.get(tu.indexOf(tds) + 1).getText();
				}

				if (tds.getText().contains("专利分类号")) {
					patentCategoryNo = tu.get(tu.indexOf(tds) + 1).getText();
				}

				if (tds.getText().contains("国省代码")) {
					nationProvince = tu.get(tu.indexOf(tds) + 1).getText();
				}
			}
		} catch (Exception e1) {
		}
		Result[20] = applicationNo;
		Result[21] = applicationDate;
		Result[22] = publicationNo;
		Result[23] = publicationDate;
		Result[24] = applicant;
		Result[25] = address;
		Result[25] = inventor;
		Result[27] = patentAgentOrg;
		Result[28] = Agent;
		Result[29] = patentMainCategoryNo;
		Result[30] = patentCategoryNo;
		Result[31] = nationProvince;
	}

	public static void writrintExcel() throws IOException {
		// write into excel
		writer.println(ID + "\t" + Stkcd + "\t" + year + "\t" + Name_cn + "\t" + PersonID + "\t" + university_cn + "\t"
				+ firstname_en + "\t" + lastname_en + "\t" + name_en + "\t" + university_en + "\t" + Result[0] + "\t"
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
		panel.add(jRadio1);
		panel.add(jRadio2);
		jRadioGroup.add(jRadio1);
		jRadioGroup.add(jRadio2);
		panel.add(new JLabel("From:"));
		panel.add(recordFrom);
		panel.add(new JLabel("To:"));
		panel.add(recordTo);
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

	public static void readExcel(Sheet sheet, int rowID) {
		Cell cell1, cell2, cell3, cell4, cell5, cell6, cell7, cell8, cell9, cell10;
		try {
			cell1 = sheet.getCell(0, rowID);
			cell2 = sheet.getCell(1, rowID);
			cell3 = sheet.getCell(2, rowID);
			cell4 = sheet.getCell(3, rowID);
			cell5 = sheet.getCell(4, rowID);
			cell6 = sheet.getCell(5, rowID);
			cell7 = sheet.getCell(6, rowID);
			cell8 = sheet.getCell(7, rowID);
			cell9 = sheet.getCell(8, rowID);
			cell10 = sheet.getCell(9, rowID);

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

}