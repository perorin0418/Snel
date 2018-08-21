package org.net.perorin.snel.main.logger;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SnelLogger {

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private static PrintWriter pw = null;

	private static void init() {
		if (pw == null) {
			try {
				pw = new PrintWriter("./contents/log/snel.log");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	private static void flush() {
		if (pw != null) {
			pw.flush();
		}
	}

	public static void info(String message) {
		init();
		pw.println("[" + sdf.format(Calendar.getInstance().getTime()) + "] INFO: " + message);
		System.out.println("[" + sdf.format(Calendar.getInstance().getTime()) + "] INFO: " + message);
		flush();
	}

	public static void warning(String message) {
		init();
		pw.println("[" + sdf.format(Calendar.getInstance().getTime()) + "] WARN: " + message);
		System.out.println("[" + sdf.format(Calendar.getInstance().getTime()) + "] WARN: " + message);
		flush();
	}

	public static void warning(Exception e) {
		init();
		pw.println("[" + sdf.format(Calendar.getInstance().getTime()) + "] WARN: " + e.getMessage());
		for (StackTraceElement ste : e.getStackTrace()) {
			pw.println("[" + sdf.format(Calendar.getInstance().getTime()) + "] WARN: " + ste.toString());
		}

		System.out.println("[" + sdf.format(Calendar.getInstance().getTime()) + "] WARN: " + e.getMessage());
		for (StackTraceElement ste : e.getStackTrace()) {
			System.out.println("[" + sdf.format(Calendar.getInstance().getTime()) + "] WARN: " + ste.toString());
		}
		flush();
	}

}
