package org.net.perorin.snel.main.exec;

import java.io.IOException;

import org.net.perorin.snel.main.logger.SnelLogger;
import org.net.perorin.snel.main.properties.SnelProperties;

public class Executer {

	private static SnelProperties propertis = SnelProperties.getInstance();

	public static void execFile(String file) {
		try {
			String cmd = String.format(propertis.getProperty(SnelProperties.snel_execute_file_cmd), file);
			Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			SnelLogger.warning(e);
		}
	}

	public static void execFolder(String folder) {
		try {
			String cmd = String.format(propertis.getProperty(SnelProperties.snel_execute_folder_cmd), folder);
			Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			SnelLogger.warning(e);
		}
	}

	public static void execFavo(String file) {
		try {
			String cmd = String.format(propertis.getProperty(SnelProperties.snel_execute_file_cmd), file);
			Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			SnelLogger.warning(e);
		}
	}
}
