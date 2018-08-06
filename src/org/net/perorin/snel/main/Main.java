package org.net.perorin.snel.main;

import javax.swing.JDialog;
import javax.swing.UIManager;

import org.net.perorin.snel.main.field.SnelField;

public class Main {

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			SnelField dialog = new SnelField();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setUndecorated(true);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
