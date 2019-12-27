package com.waverly.crawler;

import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;



/**
 * Hello world!
 *
 */

public class App 
{
	public static PrintWriter writer;
    public static void main( String[] args )
    {
		try {
			writer = new PrintWriter("E:/Jobs" + "_0" + ".xls", "GB2312");
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(null,
					"File already open with same path & file name. Please close it & re-run the application");
			writer.close();
		}

		writer.println("张大兵" + "\t" + "33876666");
		writer.close();
    	
		String a = "Stolyarova, E (Stolyarova, Elena)[ 1,4 ] ; Stolyarov, D (Stolyarov, Daniil)[ 2 ] ; "
				+ "Liu, L (Liu, Li)[ 1,4 ] ; Rim, KT (Rim, Kwang T.)[ 1,4 ] ; Zhang, Y (Zhang, Yuanbo)[ 3 ] ; "
				+ "Han, M (Han, Melinda); Hybersten, M (Hybersten, Mark)[ 2 ] ; Kim, P (Kim, Philip)[ 5,1 ] ; "
				+ "Flynn, G (Flynn, George)[ 1,4 ]";
		String b = "[ 1 ] Columbia Univ, Ctr Electron Transport Mol Nanostruct, New York, NY 10027 USA\r\n" + 
				"  增强组织信息的名称\r\n" + 
				"    Columbia University";
		String c = "";
		Pattern pattern = Pattern.compile("\\(.*?\\)|\\[.*?\\]");
		Matcher matcher = pattern.matcher(a);
		while (matcher.find()) {
			for (int i = 0; i <= matcher.groupCount(); i++) {
				int h =1;
				c = c + ";" + matcher.group(i);
			}
		}

		// a = matcher.replaceAll("");
		
		
		System.out.println(c);
    }
}
