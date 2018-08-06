package org.net.perorin.snel.main.field;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.LogManager;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileSystemView;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyAdapter;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.net.perorin.snel.main.exec.Executer;
import org.net.perorin.snel.main.index.IndexCreator;
import org.net.perorin.snel.main.index.IndexSelector;
import org.net.perorin.snel.main.index.datum.Datum;
import org.net.perorin.snel.main.properties.SnelProperties;

public class SnelField extends JDialog {

	private static final String FILE_SEARCH_TITLE = "ファイル検索";
	private static final String FOLDER_SEARCH_TITLE = "フォルダー検索";
	private static final String APP_SEARCH_TITLE = "アプリケーション検索";
	private static final Color FILE_SEARCH_COLOR = new Color(0, 255, 127);
	private static final Color FOLDER_SEARCH_COLOR = new Color(255, 215, 0);
	private static final Color APP_SEARCH_COLOR = new Color(153, 255, 255);
	private static final Icon file_icon = new ImageIcon("./contents/icon/file_icon.png");

	private JLabel lblSearchType;
	private JPanel pnlFieldCover;
	private JTextField field;
	private List<ResultRecord> rrList = new ArrayList<>();
	private SnelProperties propertis = SnelProperties.getInstance();
	private IndexSelector is = new IndexSelector();
	private int mode = 0;
	private int select = 0;
	private int page = 0;

	/** 全角を文字を入力中のときはtrueになる */
	private boolean typing = false;

	private boolean isIndexCreating = false;
	private boolean visible = true;
	private int record_count = 10;
	private String currenText = "";
	private Timer searchTimer = new Timer();
	private JPanel pnlFieldAndLoading;
	private JLabel lblLoading;
	private JLabel lblNotFound;
	private JLabel lblPage;
	private JLabel lblProc;

	public SnelField() {

		LogManager.getLogManager().reset();
		//フックされていなかったら
		if (!GlobalScreen.isNativeHookRegistered()) {
			try {
				//フックを登録
				GlobalScreen.registerNativeHook();
			} catch (NativeHookException e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
		//キー・リスナを登録
		GlobalScreen.addNativeKeyListener(new NativeKeyAdapter() {

			@Override
			public void nativeKeyPressed(NativeKeyEvent e) {
				// Shift + Enter でモード切替
				if (((e.getModifiers() & NativeKeyEvent.SHIFT_L_MASK) != 0) &&
						(e.getKeyCode() == NativeKeyEvent.VC_ENTER)) {
					changeModeNext();
					search();
				} else
				// Shift + Ctrl で画面表示切替
				if (((e.getModifiers() & NativeKeyEvent.SHIFT_L_MASK) != 0) &&
						((e.getModifiers() & NativeKeyEvent.CTRL_L_MASK) != 0) &&
						((e.getKeyCode() == NativeKeyEvent.VC_SHIFT) ||
								(e.getKeyCode() == NativeKeyEvent.VC_CONTROL))) {
					setVisibleField(!isVisibleField());
				} else
				// 上下キーの検索結果の選択切替
				if (e.getModifiers() == 0 && e.getKeyCode() == NativeKeyEvent.VC_DOWN) {
					changeSelectNext();
				} else if (e.getModifiers() == 0 && e.getKeyCode() == NativeKeyEvent.VC_UP) {
					changeSelectPrev();
				} else
				// Enterキーで実行
				if (e.getModifiers() == 0 && e.getKeyCode() == NativeKeyEvent.VC_ENTER) {
					execute();
				} else
				// Escキーで画面消す
				if (e.getModifiers() == 0 && e.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
					setVisibleField(false);
				}
			}

			@Override
			public void nativeKeyReleased(NativeKeyEvent e) {
				callSearch();
			}

		});

		setTitle("Snel");
		setIconImage(new ImageIcon("./contents/icon/snel.png").getImage());
		setResizable(false);
		Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(ss.width / 2 - 400, ss.height / 2 - 300, 800, 126);
		getContentPane().setLayout(new BorderLayout());
		setAlwaysOnTop(true);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		JPanel pnlFieldBack = new JPanel();
		pnlFieldBack.setBackground(Color.GRAY);
		pnlFieldBack.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		pnlFieldBack.setLayout(new BorderLayout(0, 0));
		getContentPane().add(pnlFieldBack, BorderLayout.NORTH);

		JPanel pnlTypeAndPage = new JPanel();
		pnlTypeAndPage.setBackground(Color.GRAY);
		pnlFieldBack.add(pnlTypeAndPage, BorderLayout.NORTH);
		pnlTypeAndPage.setLayout(new BorderLayout(0, 0));

		lblSearchType = new JLabel();
		pnlTypeAndPage.add(lblSearchType, BorderLayout.CENTER);
		lblSearchType.setText(FILE_SEARCH_TITLE);
		lblSearchType.setFont(new Font("メイリオ", Font.BOLD, 20));
		lblSearchType.setForeground(FILE_SEARCH_COLOR);

		JPanel pnlPageAndProc = new JPanel();
		pnlPageAndProc.setBackground(Color.GRAY);
		pnlTypeAndPage.add(pnlPageAndProc, BorderLayout.EAST);
		pnlPageAndProc.setLayout(new BorderLayout(0, 0));

		lblPage = new JLabel("0 page");
		pnlPageAndProc.add(lblPage, BorderLayout.WEST);
		lblPage.setBorder(BorderFactory.createEmptyBorder(11, 0, 0, 0));
		lblPage.setFont(new Font("メイリオ", Font.PLAIN, 12));
		lblPage.setForeground(FILE_SEARCH_COLOR);

		lblProc = new JLabel("");
		lblProc.setIcon(new ImageIcon("./contents/icon/processing.png"));
		lblProc.setToolTipText("クリックすると、インデックスを再作成します。");
		lblProc.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (isIndexCreating) {
					return;
				}
				java.awt.Toolkit.getDefaultToolkit().beep();
				int option = JOptionPane.showConfirmDialog(null,
						"インデックスを再作成しますか？\nインデックス作成はバックグラウンドで実行されます。",
						"確認", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
				if (option == 0) {
					Thread t = new Thread() {
						public void run() {
							isIndexCreating = true;
							lblProc.setToolTipText("インデックス作成中");
							lblProc.setIcon(new ImageIcon("./contents/icon/processing.gif"));
							IndexCreator.createIndex();
							java.awt.Toolkit.getDefaultToolkit().beep();
							JOptionPane.showMessageDialog(null,
									"インデックスが完成しました。\nインデックスを置き換えます。\nOKボタンを押した後、しばらくお待ちください",
									"注意", JOptionPane.INFORMATION_MESSAGE);
							field.setEnabled(false);
							IndexCreator.replaceIndex();
							field.setEnabled(true);
							java.awt.Toolkit.getDefaultToolkit().beep();
							JOptionPane.showMessageDialog(null,
									"インデックスを置き換えました。\nインデックス作成は完了です。",
									"報告", JOptionPane.INFORMATION_MESSAGE);
							lblProc.setIcon(new ImageIcon("./contents/icon/processing.png"));
							lblProc.setToolTipText("クリックすると、インデックスを再作成します。");
							isIndexCreating = false;
						};
					};
					t.start();
				}
			}
		});
		pnlPageAndProc.add(lblProc, BorderLayout.EAST);

		pnlFieldCover = new JPanel();
		pnlFieldCover.setLayout(new BorderLayout(0, 0));
		pnlFieldCover.setBackground(Color.GRAY);
		pnlFieldCover.setBorder(BorderFactory.createLineBorder(FILE_SEARCH_COLOR, 3, true));
		pnlFieldBack.add(pnlFieldCover, BorderLayout.SOUTH);

		pnlFieldAndLoading = new JPanel();
		pnlFieldAndLoading.setBackground(Color.DARK_GRAY);
		pnlFieldCover.add(pnlFieldAndLoading, BorderLayout.CENTER);
		pnlFieldAndLoading.setLayout(new BorderLayout(0, 0));

		field = new JTextField();
		field.setFont(new Font("メイリオ", Font.PLAIN, 32));
		field.setPreferredSize(new Dimension(774, 69));
		field.setBackground(Color.DARK_GRAY);
		field.setCaretColor(Color.lightGray);
		field.setForeground(FILE_SEARCH_COLOR);
		field.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		field.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent e) {
				setVisibleField(false);
			}
		});
		field.addInputMethodListener(new InputMethodListener() {

			@Override
			public void inputMethodTextChanged(InputMethodEvent event) {
				typing = (event.getText() != null);
			}

			@Override
			public void caretPositionChanged(InputMethodEvent event) {
				// NOP
			}
		});
		pnlFieldAndLoading.add(field);

		lblLoading = new JLabel(new ImageIcon("./contents/icon/loading.gif"));
		lblLoading.setBackground(Color.DARK_GRAY);
		lblLoading.setPreferredSize(new Dimension(69, 69));
		lblLoading.setVisible(false);
		pnlFieldAndLoading.add(lblLoading, BorderLayout.EAST);

		JPanel pnlResult = new JPanel();
		pnlResult.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
		pnlResult.setBackground(Color.GRAY);
		pnlResult.setLayout(new BoxLayout(pnlResult, BoxLayout.Y_AXIS));
		getContentPane().add(pnlResult, BorderLayout.CENTER);

		JPanel pnlNotFound = new JPanel();
		pnlNotFound.setBackground(Color.GRAY);
		getContentPane().add(pnlNotFound, BorderLayout.SOUTH);
		pnlNotFound.setLayout(new BorderLayout(0, 0));

		lblNotFound = new JLabel("Not Found ...");
		lblNotFound.setFont(new Font("メイリオ", Font.BOLD, 32));
		lblNotFound.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 0));
		lblNotFound.setVisible(false);
		pnlNotFound.add(lblNotFound);

		for (int i = 0; i < propertis.getPropertyAsInt(SnelProperties.snel_search_record_counts); i++) {
			ResultRecord rr = new ResultRecord();
			if (i == 0) {
				rr.setSelected(true);
			}
			pnlResult.add(rr);
			rrList.add(rr);
		}
	}

	private void execute() {
		if (isVisibleField() && !typing) {
			String path = rrList.get(select).lblPath.getText();
			if (mode == 0) {
				Executer.execFile(path);
			} else if (mode == 1) {
				Executer.execFolder(path);
			} else if (mode == 2) {
				Executer.execApp(path);
			}
		}
	}

	private void setVisibleField(boolean b) {
		System.out.println("Change visible:" + b);
		visible = b;
		Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
		if (visible) {
			setLocation(ss.width / 2 - 400, ss.height / 2 - 300);
			try {
				Robot r = new Robot();
				r.mouseMove(ss.width / 2 + 280, ss.height / 2 - 230);
				r.mousePress(InputEvent.BUTTON1_MASK);
				r.delay(10);
				r.mouseRelease(InputEvent.BUTTON1_MASK);
			} catch (AWTException e) {
				e.printStackTrace();
			}
		} else {
			setLocation(20000, 20000);
		}
	}

	private boolean isVisibleField() {
		return visible;
	}

	private void callSearch() {
		System.out.println("Call search:" + field.getText().trim());
		if (currenText.equals(field.getText().trim())) {
			return;
		} else {
			currenText = field.getText().trim();
		}

		lblLoading.setVisible(true);
		setPageNo(0);
		searchTimer.cancel();
		searchTimer = new Timer(true);
		searchTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				select = 0;
				search();
			}
		}, propertis.getPropertyAsLong(SnelProperties.snel_search_delay));
	}

	private void setPageNo(int no) {
		System.out.println("Set page No.:" + no);
		page = no;
		lblPage.setText(page + " page");
	}

	private List<String> splitTarget(String text) {

		StringBuffer sb = new StringBuffer();
		boolean quote = false;
		for (char c : text.toCharArray()) {
			if (c == '"') {
				quote = !quote;
				continue;
			}
			if (!quote && (c == ' ' || c == '　')) {
				sb.append("$SEP$");
			} else {
				sb.append(c);
			}
		}

		return Arrays.asList(sb.toString().split("\\$SEP\\$"));
	}

	private void search() {
		System.out.println("Search:" + currenText);
		if (!"".equals(currenText)) {

			List<String> targets = splitTarget(currenText);
			if (targets.size() <= 0) {
				lblLoading.setVisible(false);
				return;
			}

			List<Datum> result = null;
			if (mode == 0) {
				result = is.selectFile(targets, page);
			} else if (mode == 1) {
				result = is.selectFolder(targets, page);
			} else if (mode == 2) {
				result = is.selectApp(targets, page);
			} else {
				return;
			}

			resetResultRecordList();
			for (int i = 0; i < result.size(); i++) {
				Datum datum = result.get(i);
				Icon icon = FileSystemView.getFileSystemView().getSystemIcon(new File(datum.path));
				if (icon == null) {
					icon = file_icon;
				}
				rrList.get(i).lblImg.setIcon(icon);
				rrList.get(i).lblName.setText(datum.name);
				rrList.get(i).lblPath.setText(datum.path);
			}
			record_count = result.size();
		} else {
			record_count = 0;
		}
		for (ResultRecord rr : rrList) {
			rr.setSelected(false);
		}
		rrList.get(select).setSelected(true);

		if (record_count > 0) {
			SnelField.this.setSize(800, 136 + 50 * record_count);
			lblNotFound.setVisible(false);
		} else {
			if (!"".equals(currenText) && record_count <= 0) {
				lblNotFound.setVisible(true);
				SnelField.this.setSize(800, 186);
			} else {
				lblNotFound.setVisible(false);
				SnelField.this.setSize(800, 126);
			}
		}
		lblLoading.setVisible(false);
		SnelField.this.repaint();
	}

	private void resetResultRecordList() {
		for (ResultRecord rr : rrList) {
			rr.lblImg.setIcon(null);
			rr.lblName.setText("");
			rr.lblPath.setText("");
		}
	}

	private void changeSelectNext() {
		System.out.println("Change select next" + select);

		// 検索結果が最大表示件数未満なのに、次のページに行くことはできない
		if (select + 1 == record_count && select + 1 < propertis.getPropertyAsInt(SnelProperties.snel_search_record_counts)) {
			return;
		}
		select++;
		if (select >= record_count && record_count == propertis.getPropertyAsInt(SnelProperties.snel_search_record_counts)) {
			select = 0;
			lblLoading.setVisible(true);
			setPageNo(page + 1);
			search();
		}
		for (ResultRecord rr : rrList) {
			rr.setSelected(false);
		}
		rrList.get(select).setSelected(true);
	}

	private void changeSelectPrev() {
		System.out.println("Change select prev" + select);

		// 0番目の0ページより上には何もない
		if (select == 0 && page == 0) {
			return;
		}
		select--;
		if (select < 0) {
			select = record_count - 1;
			if (page > 0) {
				lblLoading.setVisible(true);
				setPageNo(page - 1);
				search();
			}
		}
		for (ResultRecord rr : rrList) {
			rr.setSelected(false);
		}
		rrList.get(select).setSelected(true);
	}

	private void changeModeNext() {
		System.out.println("Chang Mode");
		if (lblSearchType.getForeground().equals(FILE_SEARCH_COLOR)) {
			mode = 1;
			lblSearchType.setText(FOLDER_SEARCH_TITLE);
			lblSearchType.setForeground(FOLDER_SEARCH_COLOR);
			lblPage.setForeground(FOLDER_SEARCH_COLOR);
			pnlFieldCover.setBorder(BorderFactory.createLineBorder(FOLDER_SEARCH_COLOR, 3, true));
			field.setForeground(FOLDER_SEARCH_COLOR);

		} else if (lblSearchType.getForeground().equals(FOLDER_SEARCH_COLOR)) {
			mode = 2;
			lblSearchType.setText(APP_SEARCH_TITLE);
			lblSearchType.setForeground(APP_SEARCH_COLOR);
			lblPage.setForeground(APP_SEARCH_COLOR);
			pnlFieldCover.setBorder(BorderFactory.createLineBorder(APP_SEARCH_COLOR, 3, true));
			field.setForeground(APP_SEARCH_COLOR);

		} else if (lblSearchType.getForeground().equals(APP_SEARCH_COLOR)) {
			mode = 0;
			lblSearchType.setText(FILE_SEARCH_TITLE);
			lblSearchType.setForeground(FILE_SEARCH_COLOR);
			lblPage.setForeground(FILE_SEARCH_COLOR);
			pnlFieldCover.setBorder(BorderFactory.createLineBorder(FILE_SEARCH_COLOR, 3, true));
			field.setForeground(FILE_SEARCH_COLOR);
		}
	}

}
