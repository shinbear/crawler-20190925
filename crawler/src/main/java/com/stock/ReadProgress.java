package com.stock;

import java.awt.Dimension;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ReadProgress extends JFrame implements Runnable {
	private JFrame frame;
	private JPanel timePanel;
	private JLabel timeLabel;
	private JLabel displayArea;
	private int ONE_SECOND = 1000;
	private String time = "";

	public ReadProgress() {
		timePanel = new JPanel();
		timeLabel = new JLabel("Status:");
		displayArea = new JLabel();
		timePanel.add(timeLabel);
		timePanel.add(displayArea);
		this.add(timePanel);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(new Dimension(500, 200));
		this.setLocationRelativeTo(null);
	}

	public void setPanel() {
		SimpleDateFormat df = new SimpleDateFormat("YYYY-dd-HH:mm");
		String nowtime = df.format(new Date());
		this.time = nowtime;
		repaint();
		// timeLabel = new JLabel("Change: ");
	}

	public void run() {
		while (true) {
			displayArea.setText(" Current time:" + time + " is OK");
			try {
				Thread.sleep(ONE_SECOND);
			} catch (Exception e) {
				displayArea.setText("Error!!!");
			}
		}
	}

}