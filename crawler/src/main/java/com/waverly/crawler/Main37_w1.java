package com.waverly.crawler;

import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class Main37_w1 {
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
	public static String[] compseg = { "Recruiter", "DirectHire", "None" };
	public static String[] r = { "30", "5", "10", "20", "40", "50", "75", "100" };
	public static String[] emptype = { "Full Time", "Part Time", "Contracts", "Third Party", "None" };
	public static String[] compsim = { "Y", "N" };
	public static JComboBox combo1 = new JComboBox(emptype);
	public static JComboBox combo2 = new JComboBox(compseg);
	public static JComboBox combo3 = new JComboBox(compsim);
	public static JComboBox comborad = new JComboBox(r);
	public static JFrame frame = new JFrame();
	public static PrintWriter writer;
	private static ReadProgress dataProgress;
	private static int total=0;
	private static int page=0;
	private static int row=0;
	private static int sim_row = 0;
	public static String cliuid_2 = "";
	public static String unedname = "";
	public static String fname = "";
	public static String lname = "";
	public static String labname = "";
	public static String year = "";
	public static int rawID = 1;
	public static int rawID_Total = 0;
	public static int exceptionCode = 0;
	/*
	 * store the page data Easy Apply, Assoc. Position ID, Dice ID Position ID,
	 * Job Title, Employer, Job Description Location, Posted Keyword1, Keyword2,
	 * Keyword3, Keyword4, comlink posiCount, companyOverview, companyWebsite,
	 * quickFacts, easyApply2
	 */
	public static String[] result = new String[15];
	public static String[] result_sub = new String[15];
	public static String easyflag = "";

	public static void main(String[] args) throws IOException {
		try {
			input();
			q = "q-" + searchstring.getText();
			if (location.getText() == null || location.getText().equals("")) {
				l = "";
			} else {
				l = "-l-" + location.getText();
			}
			if (combo1.getSelectedItem().toString() == null || combo1.getSelectedItem().toString().equals("")
					|| combo1.getSelectedItem().toString() == "None") {
				jtype = "";
			} else {
				jtype = "-jtype-" + combo1.getSelectedItem().toString();
			}
			if (combo2.getSelectedItem().toString() == null || combo2.getSelectedItem().toString().equals("")
					|| combo2.getSelectedItem().toString() == "None") {
				dcs = "";
			} else {
				dcs = "-dcs-" + combo2.getSelectedItem().toString();
			}
			if (comborad.getSelectedItem().toString() == null || comborad.getSelectedItem().toString().equals("")
					|| comborad.getSelectedItem().toString() == "None") {
				radius = "";
			} else {
				radius = "-radius-" + comborad.getSelectedItem().toString();
			}

			String q1 = q.replace(" ", "_");
			String l1 = l.replace(" ", "_");
			String jtype1 = jtype.replace(" ", "_");
			String dcs1 = dcs.replace(" ", "_");
			String radius1 = radius.replace(" ", "_");

			if (l.isEmpty())
				l1 = "";
			if (dcs.equalsIgnoreCase("None"))
				dcs1 = "";
			if (jtype.equalsIgnoreCase("None"))
				jtype1 = "";
			if (filename.getText().equalsIgnoreCase("")) {
				JOptionPane.showMessageDialog(null, "Please enter the file path.");
				filename.requestFocusInWindow();
				filename.setText("E:/jobs");
				input();
			}

			//Read the excel sheet
			Sheet sheet;
			Workbook book;
			// book = Workbook.getWorkbook(new File(filename_sheet.getText()));
			book = Workbook.getWorkbook(new File("postdoc_list.xls"));
			sheet = book.getSheet(0);
			rawID_Total = sheet.getRows(); 

			URL = "https://www.lib.umd.edu/dbfinder/id/UMD07254";
			// Initialize chrome drive in Seleuium
			System.getProperties().setProperty("webdriver.chrome.driver", "chromedriver.exe");

			ChromeOptions options = new ChromeOptions();
			options.addArguments("--lang=en");
			WebDriver webDriver = new ChromeDriver(options);
			webDriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
			webDriver.get(URL);		
			WebElement focus_element = webDriver.findElement(By.cssSelector(".col-lg-10"));
			focus_element.click();
			Thread.sleep(3000);

			// Get the handle of current page
			String handle = webDriver.getWindowHandle();
			for (String handles : webDriver.getWindowHandles()) {
				// Get all handles of pages
				if (!handles.equals(handle)) {
					// if it is not the current page then close it
					webDriver.close();
					// switch to the second page
					webDriver.switchTo().window(handles);
				}
			}

			// Show the dialog to wait
			int res = JOptionPane.showConfirmDialog(null, "Waiting for you sign in proquest", " ",
					JOptionPane.YES_NO_OPTION);
			if (res == JOptionPane.YES_OPTION) {
				System.out.println("Go to download dissertations");
			} else {
				writer.close();
				webDriver.quit();
				System.exit(0);
				return;
			}

			// Display data extract progress
			dataProgress = new ReadProgress();
			dataProgress.setVisible(true);
			Thread thread1 = new Thread(dataProgress);
			thread1.start();
			
			String URL_search = "https://search.proquest.com/pqdtglobal/advanced?accountid=14696";
			try {
				writer = new PrintWriter(filename.getText() + "_0" + ".xls", "UTF-8");
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null,
						"File already open with same path & file name. Please close it & re-run the application");
				writer.close();
			}
			
			// write the excel the top item
			String toptitle = "cliuid_2\tunedname\tfname\tmname\tlname\tyear\tAbstract\tSubject\tClassification\tIdentifier&keyword\tTitle\tAuthor\tNumber of pages\tPublication year\tDegree date\tSchool code\tSource\tPlace of publication\tCountry of publication\tISBN\tAdvisor\tCommittee member\tUniversity/institution\tDepartment\tUniversity location\tDegree\tSource type\tLanguage\tDocument type\tDissertation thesis number\tProQuest document ID\tDocument URL\tCopyright\tDatabase";
			writer.println(toptitle);

			// Read the unedname from exccel sheet
			for (int i = 1; i < rawID_Total; i++) {
				try {
					sim_row = i;
					dataProgress.setPanel(total, page, row, sim_row);
					rawID = i;
					readExcel(sheet, rawID);
					exceptionCode=0;
					// Split the result file
					if (i % 200 == 0) {
						writer.close();
						int t = i / 200;
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
					
					// Input the query condition
					searchName(webDriver, unedname);
					if (!webDriver.getPageSource().contains("found 0 results")) {
						// Get the result of dissertation
						getAName(webDriver);
					} else {
						// If no result, write into a blank record
						writer.println(cliuid_2 + "\t" + unedname + "\t" + fname + "\t" + lname + "\t" + labname + "\t"
								+ year);
						Thread.sleep(20000);
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
				    webDriver.get(URL_search);
					//webDriver.get("www.google.com");
					
				} catch (Exception e3) {
					// e3.printStackTrace();
					// If in exception
					writer.println(cliuid_2 + "\t" + unedname + "\t" + fname + "\t" + lname + "\t" + labname + "\t"
							+ year + "\tError");
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

		} catch (Exception e2) {
			writer.close();
			JOptionPane.showMessageDialog(null, e2.getMessage());
		}
		System.exit(0);
	}

	public static void searchName(WebDriver webDriver, String unedname) throws IOException {
		try {
			// Waiting for element for 10 seconds
			WebDriverWait wait = new WebDriverWait(webDriver, 10);
			wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//*[@id='Language_ENG']")));
			
			//select the period 
			Select periodBox = new Select (webDriver.findElement(By.xpath("//*[@id='select_yearMultiDateRange']")) );
			periodBox.selectByIndex(6);
			
			// Input the year
			WebElement year_input = webDriver.findElement(By.xpath("//*[@id='textfield']"));
			year_input.clear();
			year_input.sendKeys(year);	
			
			// Select the language
			if (!webDriver.findElement(By.xpath("//*[@id='Language_ENG']")).isSelected()) {
				webDriver.findElement(By.xpath("//*[@id='Language_ENG']")).click();
			}

			// Select the manuscript type
			if (!webDriver.findElement(By.xpath("//*[@id='ManuscriptType_doctoral_diss']")).isSelected()) {
				webDriver.findElement(By.xpath("//*[@id='ManuscriptType_doctoral_diss']")).click();
			}

			// Input the unedname into the author box
			WebElement author_input = webDriver.findElement(By.id("author"));
			author_input.clear();
			author_input.sendKeys(unedname);
			
			//Click the search button
			WebElement searchButton = webDriver.findElement(By.id("searchToResultPage"));
			searchButton.click();		
		} catch (Exception e2) {
			exceptionCode=1;
		}
	}
	
	public static void getAName(WebDriver webDriver) throws IOException {
		try {
			//Waiting for element for 10 seconds
			WebDriverWait wait=new WebDriverWait(webDriver,10);        
			wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#pqResultsCount")));
			
			// Get the page number
			int pages;
			String pc_string = webDriver.findElement(By.cssSelector("#pqResultsCount")).getText();

			// Remove the characters
			Pattern pattern = Pattern.compile("[^0-9]");
			Matcher matcher = pattern.matcher(pc_string);
			pc_string = matcher.replaceAll("");
			if (!pc_string.equals("")) {
				pages = (Integer.parseInt(pc_string) - 1) / 20 + 1;
			} else {
				pages = 1;
			}
			
			//If the result is too less, then sleep 15 seconds
			if(Integer.parseInt(pc_string)<3)
			{
				Thread.sleep(10000);				
			}
			
			total = pages;
			dataProgress.setPanel(total, page, row, sim_row);

			String dissertation_content[] = new String[26];
			List<WebElement> dissertations_links = null;
			List<WebElement> result_list;
			String[] title_String = { "Abstract", "Subject", "Classification", "Identifier / keyword", "Title", "Author",
					"Number of pages", "Publication year", "Degree date", "School code", "Source",
					"Place of publication", "Country of publication", "ISBN", "Advisor", "Committee member",
					"University/institution", "Department", "University location", "Degree", "Source type", "Language",
					"Document type", "Dissertation/thesis number", "ProQuest document ID", "Document URL" };
			String currentTitle;
			// Create a tab to swtich tab in chrome broswer
			ArrayList<String> tabs;
			// Parent tab ID
			String parentId = "";
			String write_excel = "";
			//count the item No
			int itemNo=0;
			for (int p = 1; p <= pages; p++) {
				page = p;
				dataProgress.setPanel(total, page, row, sim_row);
				// Get parent tab ID
				parentId = webDriver.getWindowHandle();
				webDriver.switchTo().window(parentId);

				dissertations_links = null;
				// Get the each row data
				dissertations_links = webDriver.findElements(By.cssSelector("#addFlashPageParameterformat_abstract"));
				// add some row with details without abstract into list
				dissertations_links
						.addAll(webDriver.findElements(By.cssSelector("#addFlashPageParameterformat_citation")));

				// cycle for each row
				for (WebElement dissertation_href : dissertations_links) {
					try {
						// count the item number
						itemNo++;

						// each No10 item will pasuse for 1 minute
						if (itemNo % 10 == 0) {
							Thread.sleep(50000);
							if (itemNo % 30 == 0) {
								Thread.sleep(90000);
							}

						}

						// dissertation_href.click();
						try {
							JavascriptExecutor executor = (JavascriptExecutor) webDriver;
							executor.executeScript("window.open('" + dissertation_href.getAttribute("href") + "')");
						} catch (Exception e7) {
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
							Thread.sleep(1000);
							row++;
							dataProgress.setPanel(total, page, row, sim_row);
							continue;
						}

						// Switch to detail page
						tabs = new ArrayList<String>(webDriver.getWindowHandles());
						// switches to new tab
						webDriver.switchTo().window(tabs.get(1));
						tabs = null;

						// Waiting for element for 10 seconds
						WebDriverWait wait_detail = new WebDriverWait(webDriver, 10);
						wait_detail.until(ExpectedConditions
								.presenceOfAllElementsLocatedBy(By.cssSelector(".display_record_indexing_fieldname")));
						Thread.sleep(2500);

						/* Get current url and title */
						System.out.println("URL=" + webDriver.getCurrentUrl());
						System.out.println("title=" + webDriver.getTitle());

						// Initial the dissertation_content value
						for (i = 0; i < 26; i++) {
							dissertation_content[i] = "N/A";
						}

						// Get abstract
						try {
							dissertation_content[0] = webDriver.findElement(By.cssSelector(".truncatedAbstract"))
									.getText();
							dissertation_content[0] = dissertation_content[0].replaceAll("\r|\n|\t", " ");
						} catch (Exception e3) {
							dissertation_content[0] = "N/A";
						}

						result_list = webDriver.findElements(By.cssSelector("div.display_record_indexing_row"));
						int k = 1;
						int r = 1;
						for (WebElement result_item : result_list) {
							try {
								currentTitle = result_item
										.findElement(By.cssSelector(".display_record_indexing_fieldname")).getText();
								if (title_String[k].equals(currentTitle)) {
									dissertation_content[k] = result_item
											.findElement(By.cssSelector(".display_record_indexing_data")).getText();
									dissertation_content[k] = dissertation_content[k].replaceAll("\r|\n|\t", " ");
									k++;
								} else {
									dissertation_content[k] = "N/A";
									k++;
									for (r = k; r <= 24; r++) {
										if (title_String[r].equals(currentTitle)) {
											dissertation_content[k] = result_item
													.findElement(By.cssSelector(".display_record_indexing_data"))
													.getText();
											dissertation_content[k] = dissertation_content[k].replaceAll("\r|\n|\t",
													" ");
											k++;
											break;
										} else {
											dissertation_content[k] = "N/A";
											k++;
										}
									}
								}
							} catch (Exception e4) {
								dissertation_content[k] = "N/A";
								k++;
							}
							if (k == 26)
								break;
						}
					} catch (Exception e6) {
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
						Thread.sleep(60000);
						row++;
						dataProgress.setPanel(total, page, row, sim_row);
						continue;
					}
					// write into excel
					writer.println(cliuid_2 + "\t" + unedname + "\t" + fname + "\t" + lname + "\t" + labname + "\t"
							+ year + "\t" + "-" + "\t" + dissertation_content[1] + "\t" + dissertation_content[2] + "\t"
							+ dissertation_content[3] + "\t" + dissertation_content[4] + "\t" + dissertation_content[5]
							+ "\t" + dissertation_content[6] + "\t" + dissertation_content[7] + "\t"
							+ dissertation_content[8] + "\t" + dissertation_content[9] + "\t" + dissertation_content[10]
							+ "\t" + dissertation_content[11] + "\t" + dissertation_content[12] + "\t"
							+ dissertation_content[13] + "\t" + dissertation_content[14] + "\t"
							+ dissertation_content[15] + "\t" + dissertation_content[16] + "\t"
							+ dissertation_content[17] + "\t" + dissertation_content[18] + "\t"
							+ dissertation_content[19] + "\t" + dissertation_content[20] + "\t"
							+ dissertation_content[21] + "\t" + dissertation_content[22] + "\t"
							+ dissertation_content[23] + "\t" + dissertation_content[24] + "\t"
							+ dissertation_content[25]);
					writer.flush();
					// writer.println(write_excel);
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
					Thread.sleep(3000);
					row++;
					dataProgress.setPanel(total, page, row, sim_row);

				}
				try {
					webDriver.findElement(By.linkText("Next page")).click();
					Thread.sleep(5000);
					/*
					 * JavascriptExecutor executor = (JavascriptExecutor)
					 * webDriver; executor.executeScript("window.open('" +
					 * webDriver.findElement(By.linkText("Next page"
					 * )).getAttribute("href") + "')"); Thread.sleep(3000); //
					 * switches to new tab tabs = new
					 * ArrayList<String>(webDriver.getWindowHandles());
					 * webDriver.switchTo().window(tabs.get(1)); parentId =
					 * webDriver.getWindowHandle(); Thread.sleep(500);
					 * webDriver.switchTo().window(tabs.get(0));
					 * webDriver.close(); webDriver.switchTo().window(parentId);
					 */
				} catch (Exception e6) {
					System.out.println("It is the last page");
				}
				row = 1;
			}

		} catch (Exception e2) {
			exceptionCode = 2;
		}
	}

	public static void input() throws IOException {
		JPanel panel = new JPanel(new GridLayout(0, 1));
		panel.add(new JLabel("File path to store results (without extention):"));
		panel.add(filename);

		int result = JOptionPane.showConfirmDialog(null, panel, "Dice.com - Search Criteria", 2, -1);
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

/*		File file = new File("E:/sistertask/2019-02-24/log.txt");
        String pageXml = txt2String(file);
        Document doc = Jsoup.parse(pageXml);// 获取html文档
        */
		Document doc = Jsoup.parse(responseBody);// 获取html文档
		webDriver.close();        
		return doc;
	}
	
	public static Document getPageDocByHtmlunit(String URL) {
		System.out.print("read page:"+page+" row:"+row+" sim_row:"+row);
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
		//webClient.setJavaScriptTimeout(5 * 1000);
		//webClient.getOptions().setTimeout(5000);

		HtmlPage page = null;
		try {
			page = webClient.getPage(URL);
		} catch (Exception e) {
			e.printStackTrace();
			Document doc = Jsoup.parse(" ");
			System.out.print("read FAIL on the page:"+page+" row:"+row+" sim_row:"+row);
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
		Cell cell1, cell2, cell3, cell4, cell5, cell6;
		try {
			cell1 = sheet.getCell(0, rawID);
			cell2 = sheet.getCell(1, rawID);
			cell3 = sheet.getCell(2, rawID);
			cell4 = sheet.getCell(3, rawID);
			cell5 = sheet.getCell(4, rawID);
			cell6 = sheet.getCell(5, rawID);

			if ("".equals(cell1.getContents()) != true) {
				cliuid_2 = cell1.getContents();
				unedname = cell2.getContents();
				fname = cell3.getContents();
				lname = cell4.getContents();
				labname = cell5.getContents();
				year = cell6.getContents();

				System.out.println(rawID + " " + cell1.getContents() + " " + cell2.getContents());
			}
		} catch (Exception e) {
		}
	}
	
	

}