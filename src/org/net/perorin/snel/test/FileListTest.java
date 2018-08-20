package org.net.perorin.snel.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Stack;

public class FileListTest {

	public static void main(String[] args) {

		long count = 0;
		long start = System.currentTimeMillis();
		Stack<File> stack = new Stack<>();
		stack.add(new File("C:\\"));
		while (!stack.isEmpty()) {
			File item = stack.pop();
			if (item.isFile()) {
				try {
					if (!Files.isHidden(item.toPath())) {
						count++;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (item.isDirectory()) {
				try {
					for (File child : item.listFiles()) {
						stack.push(child);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println((System.currentTimeMillis() - start) + "ms");
		System.out.println("files : " + count);

	}

}
