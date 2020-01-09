package com.waverly.crawler;

import java.io.File;
import java.io.IOException;
import java.util.*;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;


public class ReadirFile {
	public static int rowid_Total = 0;
	public static String [][] Huoban;
	public static String empNo;
	public static String empName;
	public static String empDept1;
	public static String empDept2;
	public static String Str;
	

	public static void main(String[] args) throws IOException {
		System.out.println("用户的当前工作目录:"+System.getProperty("user.dir"));
		try {
			// Read the excel sheet
			Sheet sheet;
			Workbook book;
			// book = Workbook.getWorkbook(new File(filename_sheet.getText()));
			//book = Workbook.getWorkbook(new File("postdoc_list.xls"));
			book = Workbook.getWorkbook(new File("name.xls"));
			sheet = book.getSheet(0);
			rowid_Total = sheet.getRows();
			Huoban= new String[rowid_Total][5];
			for (int i = 0; i <= rowid_Total; i++) {
				try {
					readExcel(sheet, i);
				} catch (Exception e) {
					break;
				}
				Huoban[i][0]=empNo;
				Huoban[i][1]=empName;
				Huoban[i][2]=empDept1;
				Huoban[i][3]=empDept2;
				Huoban[i][4] = "N";		
			}

		} catch (BiffException e) {
			e.printStackTrace();
		} 
		
		File file = new File("C:\\Users\\shinbear\\Desktop\\公司照片"); // 创建File对象
		if (file.isDirectory()) { // 判断File对象对应的目录是否存在
			String[] names = file.list(); // 获得目录下的所有文件的文件名
			for (String name : names) {
				String caselsh;
				if (name.contains(".")) {
					caselsh = name.substring(0, name.lastIndexOf("."));
				} else {
					caselsh = name;
				}			
				for (int i = 0; i < rowid_Total; i++) {
					if (caselsh.equals(Huoban[i][1])) {
						Huoban[i][4] = "Y";
						Str = "||" + Huoban[i][0] + " & " + Huoban[i][1] + " & " + Huoban[i][2] + " & " + Huoban[i][3]+ " & " + Huoban[i][4];
						System.out.println(caselsh + Str);
						break;
					}
				}

				if (Str != null && Str.length() > 0) {
				} else {
					System.out.println(caselsh + "--NoMatch");
				}
				Str = "";
			}
			
			System.out.println("===========================================================");
			System.out.println("===========================================================");
			
			for (int i = 0; i < rowid_Total; i++) {
				Str = Huoban[i][4] + " & " + Huoban[i][0] + " & " + Huoban[i][1] + " & " + Huoban[i][2] + " & "
						+ Huoban[i][3];
				System.out.println(Str);

			}
		}
	}

	public static void readExcel(Sheet sheet, int rowid) {
		Cell cell1, cell2, cell3, cell4;

		cell1 = sheet.getCell(0, rowid);
		cell2 = sheet.getCell(1, rowid);
		cell3 = sheet.getCell(2, rowid);
		cell4 = sheet.getCell(3, rowid);
		if ("".equals(cell1.getContents()) != true) {
			try {
				empNo = cell1.getContents().replace('\n', ' ');
			} catch (Exception e) {
				// TODO Auto-generated catch block
				empNo = "";
				e.printStackTrace();
			}
			try {
				empName = cell2.getContents().replace('\n', ' ');
			} catch (Exception e) {
				// TODO Auto-generated catch block
				empName = "";
				e.printStackTrace();
			}
			try {
				empDept1 = cell3.getContents().replace('\n', ' ');
			} catch (Exception e) {
				// TODO Auto-generated catch block
				empDept1 = "";
				e.printStackTrace();
			}
			try {
				empDept2 = cell4.getContents().replace('\n', ' ');
			} catch (Exception e) {
				// TODO Auto-generated catch block
				empDept2 = "";
				e.printStackTrace();
			}
		} else {
			empNo = "";
			empName = "";
			empDept1 = "";
			empDept2 = "";
		}

	}

}
