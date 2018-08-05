package org.net.perorin.snel.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CmdTest {

	public static void main(String[] args) {

		Timer timer = new Timer();
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				long start = System.currentTimeMillis();

				List<String> cmd = new ArrayList<>();
				cmd.add("cmd");
				cmd.add("/c");
				cmd.add("dir");
				cmd.add("/-c");
				cmd.add("/4");
				cmd.add("/a-h-s");
				cmd.add("/n");
				cmd.add("/tw");
				cmd.add("/s");
				cmd.add("/on");
				cmd.add("C:\\");

				ProcessBuilder pb = new ProcessBuilder(cmd);
				pb.redirectOutput(new File("./out.txt"));

				try {
					Process proc = pb.start();
					proc.waitFor();

				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}

				System.out.println("task1 " + (System.currentTimeMillis() - start) + "ms");
			}
		};
		timer.schedule(task, 0);

		Timer timer2 = new Timer();
		TimerTask task2 = new TimerTask() {

			@Override
			public void run() {
				long start = System.currentTimeMillis();

				List<String> cmd = new ArrayList<>();
				cmd.add("cmd");
				cmd.add("/c");
				cmd.add("dir");
				cmd.add("/a-h-s");
				cmd.add("/s");
				cmd.add("/b");
				cmd.add("C:\\");

				ProcessBuilder pb = new ProcessBuilder(cmd);
				pb.redirectOutput(new File("./out2.txt"));

				try {
					Process proc = pb.start();
					proc.waitFor();

				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}

				System.out.println("task2 " + (System.currentTimeMillis() - start) + "ms");
			}
		};
		timer2.schedule(task2, 0);

		while (true) {

		}

	}

}
