package org.net.perorin.snel.main.field;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ResultRecord extends JPanel {

	private boolean selected = false;
	public JLabel lblImg;
	public JLabel lblName;
	public JLabel lblPath;

	public ResultRecord() {
		this.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.setMaximumSize(new Dimension(780, 50));
		this.setLayout(new BorderLayout(0, 0));
		this.setBackground(Color.GRAY);
		this.setOpaque(true);

		JPanel pnlImgName = new JPanel();
		pnlImgName.setBackground(new Color(0, 0, 0, 0));
		pnlImgName.setOpaque(true);
		pnlImgName.setLayout(new BorderLayout(0, 0));
		this.add(pnlImgName, BorderLayout.CENTER);

		lblImg = new JLabel();
		lblImg.setPreferredSize(new Dimension(24, 20));
		lblImg.setBackground(new Color(0, 0, 0, 0));
		lblImg.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
		lblImg.setOpaque(true);
		pnlImgName.add(lblImg, BorderLayout.WEST);

		lblName = new JLabel();
		lblName.setBackground(new Color(0, 0, 0, 0));
		lblName.setForeground(new Color(250, 250, 250, 255));
		lblName.setOpaque(true);
		lblName.setFont(new Font("メイリオ", Font.BOLD, 20));
		pnlImgName.add(lblName, BorderLayout.CENTER);

		lblPath = new JLabel();
		lblPath.setBackground(new Color(0, 0, 0, 0));
		lblPath.setForeground(new Color(250, 250, 250, 255));
		lblPath.setOpaque(true);
		lblPath.setFont(new Font("メイリオ", Font.BOLD, 11));
		lblPath.setBorder(BorderFactory.createEmptyBorder(0, 5, 2, 0));
		this.add(lblPath, BorderLayout.SOUTH);
	}

	public void setSelected(boolean b) {
		selected = b;
		if (selected) {
			this.setBackground(new Color(0, 153, 255, 255));
		} else {
			this.setBackground(Color.GRAY);
		}
	}

	public boolean isSelected() {
		return selected;
	}

}
