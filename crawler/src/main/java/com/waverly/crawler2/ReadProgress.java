package com.waverly.crawler2;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ReadProgress extends JFrame implements Runnable {
	private JFrame frame;
	private JPanel timePanel;
	private JLabel timeLabel;
	private JLabel displayArea;
	private JButton buttonClose;
	private int ONE_SECOND = 1000;
	private int total = 0;
	private int page = 0;
	private int row = 0;
	private int sim_row = 0;

	public ReadProgress() {
		timePanel = new JPanel();
		timeLabel = new JLabel("Status:");
		displayArea = new JLabel();
		buttonClose = new JButton(("Close Program"));

		timePanel.add(timeLabel);
		timePanel.add(displayArea);
		this.add(timePanel);
		timePanel.add(buttonClose);

		buttonClose.addActionListener(new CloseAllProgram());

		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(new Dimension(500, 200));
		this.setLocationRelativeTo(null);
	}

	class CloseAllProgram implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			System.exit(0);
		}
	}

	public void setPanel(int total, int page, int row, int sim_row) {
		this.total = total;
		this.page = page;
		this.row = row;
		this.sim_row = sim_row;
		repaint();
		// timeLabel = new JLabel("Change: ");
	}

	public void run() {
		while (true) {
			/*
			 * displayArea.setText(" Total page:" + total + ", Current page:" +
			 * page + " Current row:" + row + " Current name row ID:" +
			 * sim_row);
			 */
			displayArea.setText(" Total page:" + total + " Current name row ID:" + row);
			try {
				Thread.sleep(ONE_SECOND);
			} catch (Exception e) {
				displayArea.setText("Error!!!");
				;
			}
		}
	}

}