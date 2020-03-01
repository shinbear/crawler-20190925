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
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class Main70_linkedin {
	public static int i = 0;
	public static int j = 0;
	public static int pages = 0;
	public static JTextField filename = new JTextField("Jobs");
	public static JRadioButton jRadio1 = new JRadioButton("Run all records", true);
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
	// The content read from Excel
	public static String ID = "";
	public static String analystid = "";
	public static String analyst = "";
	public static String linkedinaddress = "";

	public static String about = "";
	public static String currentTitle = "";
	public static String currentAddress = "";
	public static String organizationName = "";
	public static String title = "";
	public static String degreeTitle = "";
	public static String degreeSecondaryTitle = "";
	public static String periodStart = "";
	public static String periodEnd = "";
	public static String location = "";

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

	/*
	 * store the page data Easy Apply, Assoc. Position ID, Dice ID Position ID, Job
	 * Title, Employer, Job Description Location, Posted Keyword1, Keyword2,
	 * Keyword3, Keyword4, comlink posiCount, companyOverview, companyWebsite,
	 * quickFacts, easyApply2
	 */
	public static String[] result_sub = new String[15];
	public static String easyflag = "";

	public static void main(String[] args) throws IOException {
		try {
			System.out.println("用户的当前工作目录:" + System.getProperty("user.dir"));
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

			// URL = "https://www.lib.umd.edu/dbfinder/id/UMD04150";
			// Initialize chrome drive in Seleuium
			System.getProperties().setProperty("webdriver.chrome.driver", "chromedriver.exe");
			// modify the download path
			DesiredCapabilities caps = setDownloadsPath();

			ChromeOptions options = new ChromeOptions();
			options.addArguments("--lang=en");
			WebDriver webDriver = new ChromeDriver(options);
			// WebDriver webDriver = new ChromeDriver(caps);
			webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
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
			String toptitle = "ID\tAnalystid\tAnalyst\tLinkedinaddress\tData_type\tAbout\t"
					+ "Current title\tCurrent address\tOrganization name\tTitle\tDegree title"
					+ "\tDegree secondary title\tPeriod Start" + "\tPeriod End\tLocation";
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

					// Remain the search page
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

					int status = searchName(webDriver);
					int getNameStatus;
					if (status == 1) {
						// Get the item name
						getNameStatus = getAName(webDriver);
						if (getNameStatus == 0) {
							Thread.sleep(60000);
						}
					} else {
						webDriver.navigate().refresh();
						status = searchName(webDriver);
						if (status == 1) {
							getNameStatus = getAName(webDriver);
							if (getNameStatus == 0) {
								Thread.sleep(60000);
							}
						} else {
							throw new Exception("throw error");
						}
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
					for (i = 0; i < 40; i++) {
						Result[i] = "Error";
					}
					writrintExcel();
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
					// continue;
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
			webDriver.get(linkedinaddress);
			// webDriver.get("https://www.linkedin.com/in/adrienne-yih-857a149/");
			// Waiting for element for 10 seconds
			WebDriverWait wait = new WebDriverWait(webDriver, 10);
			wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#mynetwork-tab-icon")));
			return 1;
		} catch (Exception e2) {
			writrintExcel();
			System.out.print(e2);
			return 0;
		}
	}

	public static int getAName(WebDriver webDriver) throws IOException {
		try {
			// result array clear
			for (i = 0; i < 40; i++) {
				Result[i] = "NA";
			}
			// Get the about
			try {
				Result[0] = "experience";
				try {
					Thread.sleep(1000);
					WebElement seeMore = webDriver.findElement(By.cssSelector(".lt-line-clamp__more"));
					Thread.sleep(1000);
					seeMore.click();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					WebElement aboutWebelement = webDriver.findElement(By.cssSelector(".pv-about__summary-text"));
					about = aboutWebelement.getText().replace('\n', ' ');
					Result[1] = about;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					about = "NA";
					Result[1] = about;
					e.printStackTrace();
				}

				// Get the Headline web elements
				WebElement headlineWebelement = webDriver.findElements(By.cssSelector(".flex-1")).get(1);
				try {
					currentTitle = headlineWebelement.findElement(By.cssSelector(".mt1")).getText().replace('\n', ' ');
					Result[2] = currentTitle;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					currentTitle = "NA";
					Result[2] = about;
					e.printStackTrace();
				}
				try {
					currentAddress = headlineWebelement.findElements(By.cssSelector(".pv-top-card--list")).get(1)
							.findElement(By.cssSelector(".t-16")).getText().replace('\n', ' ');
					Result[3] = currentAddress;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					currentAddress = "NA";
					Result[3] = about;
					e.printStackTrace();
				}
				try {
					organizationName = webDriver.findElement(By.cssSelector(".pv-top-card--experience-list-item")).getText().replace('\n', ' ');
					Result[4] = organizationName;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					organizationName = "NA";
					Result[4] = about;
					e.printStackTrace();
				}
				writrintExcel();
				Thread.sleep(500);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				Result[0] = "experience";
				Result[1] = "NA";
				Result[2] = "NA";
				Result[3] = "NA";
				Result[4] = "NA";
				writrintExcel();
				Thread.sleep(500);
				for (i = 0; i < 40; i++) {
					Result[i] = "NA";
				}
				e1.printStackTrace();
			}

			// Get the experience loop
			for (i = 0; i < 40; i++) {
				Result[i] = "NA";
			}

			try {
				List<WebElement> showMore = webDriver.findElements(By.cssSelector(".pv-profile-section__see-more-inline"));
				for (WebElement showMoreElement : showMore) {
					if (showMoreElement.getText().contains("more experiences")
							|| showMoreElement.getText().contains("more experience")) {
						showMoreElement.findElement(By.cssSelector("li-icon")).click();
					}
				}
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			Thread.sleep(1000);

			try {
				WebElement experienceSection = webDriver.findElement(By.cssSelector("#experience-section"));
				List<WebElement> experienceList = experienceSection
						.findElements(By.cssSelector(".pv-entity__position-group-pager"));
				for (WebElement tex : experienceList) {
					Result[0] = "experience";

					WebElement experienceItem;
					try {
						experienceItem = tex.findElement(By.cssSelector(".pv-entity__summary-info"));
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						experienceItem = null;
						e1.printStackTrace();
					}

					if (experienceItem == null) {
						try {
							// Multi positions in company
							organizationName = tex.findElement(By.cssSelector(".pv-entity__company-summary-info	"))
									.getText().replace('\n', ' ');
							organizationName = organizationName.substring(organizationName.indexOf("Company Name") + 13,
									organizationName.indexOf("Total Duration")).replace('\n', ' ');

							List<WebElement> multiCompanyList = tex
									.findElements(By.cssSelector(".pv-entity__role-details"));

							for (WebElement multiPos : multiCompanyList) {
								try {
									title = multiPos.findElement(By.cssSelector("h3 > span.visually-hidden + span"))
											.getText().replace('\n', ' ');
								} catch (Exception e1) {
									// TODO Auto-generated catch block
									title = "NA";
									e1.printStackTrace();
								}
								try {
									String periodStr = multiPos.findElement(By.cssSelector(
											"h4.pv-entity__date-range.t-14.t-black--light.t-normal > span.visually-hidden + span"))
											.getText();
									periodStart = periodStr.substring(0, periodStr.indexOf("–")).replace('\n', ' ');
									if (periodStart.contains("Dates Employed")) {
										periodStart = periodStart.substring(periodStart.indexOf("Dates Employed") + 14)
												.replace('\n', ' ');
									}
									periodEnd = periodStr.substring(periodStr.indexOf("–") + 2).replace('\n', ' ');
								} catch (Exception e1) {
									// TODO Auto-generated catch block
									// TODO Auto-generated catch block
									periodStart = "NA";
									periodEnd = "NA";
									e1.printStackTrace();
								}
								try {
									location = multiPos.findElement(By.cssSelector(".pv-entity__location")).getText();
									location = location.substring(9).replace('\n', ' ');
								} catch (Exception e1) {
									// TODO Auto-generated catch block
									location = "NA";
									e1.printStackTrace();
								}
								Result[4] = organizationName;
								Result[5] = title;
								Result[8] = periodStart;
								Result[9] = periodEnd;
								Result[10] = location;
								writrintExcel();
								Thread.sleep(500);
								Result[4] = "NA";
								Result[5] = "NA";
								Result[8] = "NA";
								Result[9] = "NA";
								Result[10] = "NA";
							}
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							Result[4] = "NA";
							Result[5] = "NA";
							Result[8] = "NA";
							Result[9] = "NA";
							Result[10] = "NA";
							writrintExcel();
							Thread.sleep(500);
							e1.printStackTrace();
						}
					} else {
						try {
							organizationName = experienceItem.findElement(By.cssSelector(".pv-entity__secondary-title"))
									.getText().replace('\n', ' ');
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							title = experienceItem.findElement(By.cssSelector(".t-16")).getText().replace('\n', ' ');
						} catch (Exception e) {
							// TODO Auto-generated catch block
							title = "NA";
							e.printStackTrace();
						}
						try {
							String periodStr = experienceItem.findElement(By.cssSelector(".pv-entity__date-range"))
									.getText();
							periodStart = periodStr.substring(0, periodStr.indexOf("–")).replace('\n', ' ');
							if (periodStart.contains("Dates Employed")) {
								periodStart = periodStart.substring(periodStart.indexOf("Dates Employed") + 14);
							}
							periodEnd = periodStr.substring(periodStr.indexOf("–") + 2).replace('\n', ' ');
						} catch (Exception e) {
							// TODO Auto-generated catch block
							periodStart = "NA";
							periodEnd = "NA";
							e.printStackTrace();
						}
						try {
							location = experienceItem.findElement(By.cssSelector(".pv-entity__location")).getText()
									.substring(9).replace('\n', ' ');
						} catch (Exception e) {
							// TODO Auto-generated catch block
							location = "NA";
							e.printStackTrace();
						}
						Result[4] = organizationName;
						Result[5] = title;
						Result[8] = periodStart;
						Result[9] = periodEnd;
						Result[10] = location;
						writrintExcel();
						Thread.sleep(500);
						for (i = 0; i < 40; i++) {
							Result[i] = "NA";
						}
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Result[4] = "NA";
				Result[5] = "NA";
				Result[8] = "NA";
				Result[9] = "NA";
				Result[10] = "NA";
				writrintExcel();
				Thread.sleep(500);
				for (i = 0; i < 40; i++) {
					Result[i] = "NA";
				}
				e.printStackTrace();
			}

			// Get the education loop
			for (i = 0; i < 40; i++) {
				Result[i] = "NA";
			}
			try {
				WebElement educationSection = webDriver.findElement(By.cssSelector("#education-section"));
				List<WebElement> educationList = educationSection
						.findElements(By.cssSelector(".pv-profile-section__list-item"));

				for (WebElement ted : educationList) {
					Result[0] = "education";
					Result[5] = "student";
					WebElement educationItem = ted.findElement(By.cssSelector(".pv-entity__summary-info"));
					try {
						organizationName = educationItem.findElement(By.cssSelector(".pv-entity__school-name"))
								.getText().replace('\n', ' ');
						Result[4] = organizationName;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						organizationName = "NA";
						e.printStackTrace();
					}
					List<WebElement> degreeList = educationItem.findElements(By.cssSelector(".pv-entity__comma-item"));
					int w = 0;
					for (WebElement tdegree : degreeList) {
						if (w < 8) {
							try {
								Result[6 + w] = tdegree.getText().replace('\n', ' ');
								w++;
							} catch (Exception e) {
								// TODO Auto-generated catch block
								Result[6 + w] = "NA";
								w++;
								e.printStackTrace();
							}
						}
					}

					List<WebElement> periodList = educationItem.findElements(By.tagName("time"));
					int q = 0;
					for (WebElement tperiod : periodList) {
						if (q < 10) {
							try {
								Result[8 + q] = tperiod.getText().replace('\n', ' ');
								q++;
							} catch (Exception e) {
								// TODO Auto-generated catch block
								Result[8 + q] = "NA";
								q++;
								e.printStackTrace();
							}
						}
					}
					writrintExcel();
					Thread.sleep(500);
					Result[0] = "NA";
					Result[4] = "NA";
					Result[5] = "NA";
					Result[6] = "NA";
					Result[7] = "NA";
					Result[8] = "NA";
					Result[9] = "NA";
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Result[0] = "education";
				Result[4] = "NA";
				Result[5] = "student";
				Result[6] = "NA";
				Result[7] = "NA";
				Result[8] = "NA";
				Result[9] = "NA";
				writrintExcel();
				Thread.sleep(500);
				for (i = 0; i < 40; i++) {
					Result[i] = "NA";
				}
				e.printStackTrace();
			}
			
			//Get the Volunteer Experience
			for (i = 0; i < 5; i++) {
				Result[i] = "NA";
			}
			
			try {
				List<WebElement> voluExperienceList = webDriver.findElements(By.cssSelector(".pv-volunteering-entity"));
				for (WebElement voluExperienceItem : voluExperienceList) {
					Result[0] = "volunteer Experience";
					try {
						organizationName = voluExperienceItem.findElement(By.cssSelector(".pv-entity__secondary-title")).getText();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						organizationName = "NA";
						e.printStackTrace();
					}
					try {
						title = voluExperienceItem.findElement(By.cssSelector("h3.t-16")).getText();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						title = "volunteer";
						e.printStackTrace();
					}
					try {
						String periodStr = voluExperienceItem.findElement(By.cssSelector(".pv-entity__date-range"))
								.getText();
						periodStart = periodStr.substring(0, periodStr.indexOf("–")).replace('\n', ' ');
						if (periodStart.contains("Dates volunteered")) {
							periodStart = periodStart.substring(periodStart.indexOf("Dates volunteered") + 17);
						}
						periodEnd = periodStr.substring(periodStr.indexOf("–") + 2).replace('\n', ' ');
					} catch (Exception e) {
						// TODO Auto-generated catch block
						periodStart = "NA";
						periodEnd = "NA";
						e.printStackTrace();
					}					
					Result[4] = organizationName;
					Result[5] = title;
					Result[8] = periodStart;
					Result[9] = periodEnd;
					writrintExcel();
					Thread.sleep(500);
					Result[0] = "NA";
					Result[4] = "NA";
					Result[5] = "NA";
					Result[6] = "NA";
					Result[7] = "NA";
					Result[8] = "NA";
					Result[9] = "NA";						
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Result[0] = "volunteer Experience";
				Result[4] = "NA";
				Result[5] = "student";
				Result[6] = "NA";
				Result[7] = "NA";
				Result[8] = "NA";
				Result[9] = "NA";
				writrintExcel();
				Thread.sleep(500);
				for (i = 0; i < 40; i++) {
					Result[i] = "NA";
				}
				e.printStackTrace();
			}	
			Thread.sleep(60000);
			return 1;
		} catch (Exception e2) {
			exceptionCode = 2;
			for (i = 0; i < 40; i++) {
				Result[i] = "Error";
			}
			writrintExcel();
			return 0;
		}
	}

	public static void writrintExcel() throws IOException {
		// write into excel
		writer.println(ID + "\t" + analystid + "\t" + analyst + "\t" + linkedinaddress + "\t" + Result[0] + "\t"
				+ Result[1] + "\t" + Result[2] + "\t" + Result[3] + "\t" + Result[4] + "\t" + Result[5] + "\t"
				+ Result[6] + "\t" + Result[7] + "\t" + Result[8] + "\t" + Result[9] + "\t" + Result[10]);
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
		Cell cell1, cell2, cell3, cell4;
		try {
			cell1 = sheet.getCell(0, rowid);
			cell2 = sheet.getCell(1, rowid);
			cell3 = sheet.getCell(2, rowid);
			cell4 = sheet.getCell(3, rowid);

			if ("".equals(cell1.getContents()) != true) {
				ID = cell1.getContents().replace('\n', ' ');
				analystid = cell2.getContents().replace('\n', ' ');
				analyst = cell3.getContents().replace('\n', ' ');
				linkedinaddress = cell4.getContents().replace('\n', ' ');
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