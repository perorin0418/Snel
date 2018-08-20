package org.net.perorin.snel.main.field;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ResultRecord extends JPanel {

	private boolean selected = false;
	private JPanel pnlImgName;
	public JLabel lblImg;
	public JLabel lblName;
	public JLabel lblPath;
	public JLabel lblMessage;
	private JPanel pnlExtension;
	private JButton btnPlay;
	private JButton btnFolder;
	private JButton btnOpen;
	private JButton btnClip;
	private JButton btnFavo;
	private KeyAdapter btn_KeyAdapter = new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent e) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				requestFocuesPrev((JButton) e.getSource());
				break;

			case KeyEvent.VK_UP:
				setExtensionVisible(false);
				break;

			case KeyEvent.VK_RIGHT:
				requestFocuesNext((JButton) e.getSource());
				break;

			case KeyEvent.VK_DOWN:
				setExtensionVisible(false);
				break;

			case KeyEvent.VK_ENTER:
				((JButton) e.getSource()).doClick();
				break;

			default:
				break;
			}
		}
	};
	private MouseAdapter btn_MouseAdapter = new MouseAdapter() {
		@Override
		public void mouseEntered(MouseEvent e) {
			if(e.getSource().equals(btnPlay)){
				lblMessage.setText("　ファイルを実行します。　");
			}else if(e.getSource().equals(btnFolder)){
				lblMessage.setText("　フォルダーを開きます。　");
			}else if(e.getSource().equals(btnOpen)){
				lblMessage.setText("　プログラムを指定して実行します。　");
			}else if(e.getSource().equals(btnClip)){
				lblMessage.setText("　クリップボードにコピーします。　");
			}else if(e.getSource().equals(btnFavo)){
				lblMessage.setText("　お気に入りに追加or削除します。　");
			}
			ResultRecord.this.mouseEntered(e);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			lblMessage.setText("");
			ResultRecord.this.mouseExited(e);
		}
	};

	public ResultRecord() {
		this.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.setMaximumSize(new Dimension(780, 50));
		this.setLayout(new BorderLayout(0, 0));

		pnlImgName = new JPanel();
		pnlImgName.setBackground(Color.GRAY);
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
		pnlImgName.add(lblPath, BorderLayout.SOUTH);

		JPanel pnlMessage = new JPanel();
		pnlMessage.setLayout(new BorderLayout(0, 0));
		pnlMessage.setBackground(Color.DARK_GRAY);
		add(pnlMessage, BorderLayout.EAST);

		lblMessage = new JLabel();
		lblMessage.setBackground(new Color(0, 0, 0, 0));
		lblMessage.setForeground(new Color(250, 250, 250, 255));
		lblMessage.setOpaque(true);
		lblMessage.setFont(new Font("メイリオ", Font.BOLD, 20));
		pnlMessage.add(lblMessage, BorderLayout.WEST);

		pnlExtension = new JPanel();
		pnlMessage.add(pnlExtension);
		pnlExtension.setBackground(Color.DARK_GRAY);
		pnlExtension.setVisible(false);
		pnlExtension.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		pnlExtension.setLayout(new GridLayout(0, 5, 5, 0));

		btnPlay = new JButton();
		btnPlay.setIcon(new ImageIcon("./contents/icon/play_off.png"));
		btnPlay.setRolloverIcon(new ImageIcon("./contents/icon/play_on.png"));
		btnPlay.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		btnPlay.setContentAreaFilled(false);
		btnPlay.addKeyListener(btn_KeyAdapter);
		btnPlay.addMouseListener(btn_MouseAdapter);
		btnPlay.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				actionPlay();
			}
		});
		btnPlay.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				btnPlay.setIcon(new ImageIcon("./contents/icon/play_off.png"));
				lblMessage.setText("");
				repaint();
			}

			@Override
			public void focusGained(FocusEvent e) {
				btnPlay.setIcon(new ImageIcon("./contents/icon/play_on.png"));
				lblMessage.setText("　ファイルを実行します。　");
				repaint();
			}
		});
		pnlExtension.add(btnPlay);

		btnFolder = new JButton();
		btnFolder.setIcon(new ImageIcon("./contents/icon/folder_off.png"));
		btnFolder.setRolloverIcon(new ImageIcon("./contents/icon/folder_on.png"));
		btnFolder.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		btnFolder.setContentAreaFilled(false);
		btnFolder.addKeyListener(btn_KeyAdapter);
		btnFolder.addMouseListener(btn_MouseAdapter);
		btnFolder.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				actionFolder();
			}
		});
		btnFolder.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				btnFolder.setIcon(new ImageIcon("./contents/icon/folder_off.png"));
				lblMessage.setText("");
				repaint();
			}

			@Override
			public void focusGained(FocusEvent e) {
				btnFolder.setIcon(new ImageIcon("./contents/icon/folder_on.png"));
				lblMessage.setText("　フォルダーを開きます。　");
				repaint();
			}
		});
		pnlExtension.add(btnFolder);

		btnOpen = new JButton();
		btnOpen.setIcon(new ImageIcon("./contents/icon/open_off.png"));
		btnOpen.setRolloverIcon(new ImageIcon("./contents/icon/open_on.png"));
		btnOpen.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		btnOpen.setContentAreaFilled(false);
		btnOpen.addKeyListener(btn_KeyAdapter);
		btnOpen.addMouseListener(btn_MouseAdapter);
		btnOpen.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				actionOpen();
			}
		});
		btnOpen.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				btnOpen.setIcon(new ImageIcon("./contents/icon/open_off.png"));
				lblMessage.setText("");
				repaint();
			}

			@Override
			public void focusGained(FocusEvent e) {
				btnOpen.setIcon(new ImageIcon("./contents/icon/open_on.png"));
				lblMessage.setText("　プログラムを指定して実行します。　");
				repaint();
			}
		});
		pnlExtension.add(btnOpen);

		btnClip = new JButton();
		btnClip.setIcon(new ImageIcon("./contents/icon/clip_off.png"));
		btnClip.setRolloverIcon(new ImageIcon("./contents/icon/clip_on.png"));
		btnClip.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		btnClip.setContentAreaFilled(false);
		btnClip.addKeyListener(btn_KeyAdapter);
		btnClip.addMouseListener(btn_MouseAdapter);
		btnClip.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				actionClip();
			}
		});
		btnClip.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				btnClip.setIcon(new ImageIcon("./contents/icon/clip_off.png"));
				lblMessage.setText("");
				repaint();
			}

			@Override
			public void focusGained(FocusEvent e) {
				btnClip.setIcon(new ImageIcon("./contents/icon/clip_on.png"));
				lblMessage.setText("　クリップボードにコピーします。　");
				repaint();
			}
		});
		pnlExtension.add(btnClip);

		btnFavo = new JButton();
		btnFavo.setIcon(new ImageIcon("./contents/icon/favo_off.png"));
		btnFavo.setRolloverIcon(new ImageIcon("./contents/icon/favo_on.png"));
		btnFavo.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		btnFavo.setContentAreaFilled(false);
		btnFavo.addKeyListener(btn_KeyAdapter);
		btnFavo.addMouseListener(btn_MouseAdapter);
		btnFavo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				actionFavo();
			}
		});
		btnFavo.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				btnFavo.setIcon(new ImageIcon("./contents/icon/favo_off.png"));
				lblMessage.setText("");
				repaint();
			}

			@Override
			public void focusGained(FocusEvent e) {
				btnFavo.setIcon(new ImageIcon("./contents/icon/favo_on.png"));
				lblMessage.setText("　お気に入りに追加or削除します。　");
				repaint();
			}
		});
		pnlExtension.add(btnFavo);
	}

	public void setSelected(boolean b) {
		selected = b;
		if (selected) {
			pnlImgName.setBackground(new Color(0, 153, 255, 255));
		} else {
			pnlImgName.setBackground(Color.GRAY);
		}
	}

	public boolean isSelected() {
		return selected;
	}

	public void setExtensionVisible(boolean b) {
		pnlExtension.setVisible(b);
		if (b) {
			btnPlay.requestFocusInWindow();
		}
	}

	public void actionPlay() {
		// NOP
	}

	public void actionFolder() {
		// NOP
	}

	public void actionOpen() {
		// NOP
	}

	public void actionClip() {
		// NOP
	}

	public void actionFavo() {
		// NOP
	}

	public void mouseEntered(MouseEvent e) {
		// NOP
	}

	public void mouseExited(MouseEvent e) {
		// NOP
	}

	private void requestFocuesNext(JButton btn) {
		getNextBtn(btn).requestFocusInWindow();
	}

	private void requestFocuesPrev(JButton btn) {
		getPrevBtn(btn).requestFocusInWindow();
	}

	private JButton getNextBtn(JButton btn) {
		if (btn.equals(btnPlay)) {
			return btnFolder;
		} else if (btn.equals(btnFolder)) {
			return btnOpen;
		} else if (btn.equals(btnOpen)) {
			return btnClip;
		} else if (btn.equals(btnClip)) {
			return btnFavo;
		} else if (btn.equals(btnFavo)) {
			return btnPlay;
		} else {
			return btnPlay;
		}
	}

	private JButton getPrevBtn(JButton btn) {
		if (btn.equals(btnPlay)) {
			return btnFavo;
		} else if (btn.equals(btnFavo)) {
			return btnClip;
		} else if (btn.equals(btnClip)) {
			return btnOpen;
		} else if (btn.equals(btnOpen)) {
			return btnFolder;
		} else if (btn.equals(btnFolder)) {
			return btnPlay;
		} else {
			return btnPlay;
		}
	}
}
