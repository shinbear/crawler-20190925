package com.waverly.crawler2;

import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class Lu0303_Waverly {
	public static int i = 0;
	public static int j = 0;
	public static int pages = 0;
	public static JTextField filename = new JTextField("Jobs");
	public static JRadioButton jRadio1 = new JRadioButton("Run all records", true);
	public static JRadioButton jRadio2 = new JRadioButton("Run the specific records from- to");
	public static ButtonGroup jRadioGroup = new ButtonGroup();
	public static JTextField recordFrom = new JTextField("");
	public static JTextField recordTo = new JTextField("");
	public static JTextField startPage = new JTextField("");

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
	public static String current_url = "";

	// Restore the current download info
	public static int rowid = 1;
	public static WebDriver webDriver;
	public static Sheet sheet;
	// Restore the current number of records in a search query
	public static int detailRowid = 1;
	public static int runStatus = 0;
	// Restore the current download info into a arraylist
	public static ArrayList<Object> recordlist = new ArrayList<Object>();
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
			System.out.println("用户的当前工作目录:" + System.getProperty("user.dir"));
			input();
			if (filename.getText().equalsIgnoreCase("")) {
				JOptionPane.showMessageDialog(null, "Please enter the file path.");
				filename.requestFocusInWindow();
				filename.setText("jobs");
				input();
			}

			Workbook book;
			// book = Workbook.getWorkbook(new File(filename_sheet.getText()));
			book = Workbook.getWorkbook(new File("postdoc_list.xls"));
			sheet = book.getSheet(0);
			rowid_Total = sheet.getRows();

			// Show the progress
			/*
			 * dataProgress = new ReadProgress(); dataProgress.setVisible(true);
			 * Thread thread1 = new Thread(dataProgress); thread1.start();
			 */

			try {
				writer = new PrintWriter(filename.getText() + "_0" + ".xls", "GB2312");
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null,
						"File already open with same path & file name. Please close it & re-run the application");
				writer.close();
			}

			// write the excel the top item
			String toptitle = "ID\tPID\tname\tlastname\tfirstname\tmidname\tphdu\tphdyr"
					+ "\tphd_country\tsearch_list\ttime_from\ttime_to\tPT\tAU\tAF"
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
				sim_row = i;
				// dataProgress.setPanel(total, page, row, sim_row);
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

				// Count the start page
				int startPageNum;
				if (!startPage.getText().equals("")) {
					startPageNum = Integer.parseInt(startPage.getText());
				} else {
					startPageNum = 1;
				}

				// assign the content to input parameter for runRecord
				recordlist.clear();
				recordlist.add(sheet);
				recordlist.add(rowid);
				recordlist.add(detailRowid);
				// recordlist.add(webDriver);
				recordlist.add("webDriverTemp");
				recordlist.add(startPageNum);
				// insert the status code into arraylist
				// 0 is the first run, 1 is the normal run, 2 is exception
				recordlist.add(runStatus);
				recordlist.add(writer);

				int runflag = 0;
				do {
					// Run the record
					recordlist = Runwebdriver_Waverly.runRecord(recordlist);
					runflag = (Integer) recordlist.get(5);

					// import the parameter
					sheet = (Sheet) recordlist.get(0);
					rowid = (Integer) recordlist.get(1);
					detailRowid = (Integer) recordlist.get(2);
					// webDriver = (WebDriver) recordlist.get(3);
					startPageNum = (Integer) recordlist.get(4);
					runStatus = (Integer) recordlist.get(5);
					writer = (PrintWriter) recordlist.get(6);

				} while (runflag == 2);
				rowid++;
				detailRowid = 1;
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

	public static void writrintExcel() throws IOException {
		// write into excel
		writer.println(ID + "\t" + PID + "\t" + name + "\t" + lastname + "\t" + firstname + "\t" + midname + "\t" + phdu
				+ "\t" + phdyr + "\t" + phd_country + "\t" + search_list + "\t" + time_from + "\t" + time_to + "\t"
				+ Result[0] + "\t" + Result[1] + "\t" + Result[2] + "\t" + Result[3] + "\t" + Result[4] + "\t"
				+ Result[5] + "\t" + Result[6] + "\t" + Result[7] + "\t" + Result[8] + "\t" + Result[9] + "\t"
				+ Result[10] + "\t" + Result[11] + "\t" + Result[12] + "\t" + Result[13] + "\t" + Result[14] + "\t"
				+ Result[15] + "\t" + Result[16] + "\t" + Result[17] + "\t" + Result[18] + "\t" + Result[19] + "\t"
				+ Result[20] + "\t" + Result[21] + "\t" + Result[22] + "\t" + Result[23] + "\t" + Result[24] + "\t"
				+ Result[25] + "\t" + Result[26] + "\t" + Result[27] + "\t" + Result[28] + "\t" + Result[29] + "\t"
				+ Result[30] + "\t" + Result[31] + "\t" + Result[32] + "\t" + Result[33]);
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
		panel.add(new JLabel("Start Page:"));
		panel.add(startPage);

		int result = JOptionPane.showConfirmDialog(null, panel, "web of science - Search Criteria", 2, -1);
		if (result == 0) {
			return;
		}
		JOptionPane.showMessageDialog(frame, "Cancelled");
		System.exit(0);
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