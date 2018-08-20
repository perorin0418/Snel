package org.net.perorin.snel.main.properties;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class SnelProperties extends Properties {

	public static final String snel_index_cmd_dir_folder_find_regex = "snel.index.cmd.dir.folder.find.regex";
	public static final String snel_index_cmd_dir_folder_delete_regex = "snel.index.cmd.dir.folder.delete.regex";
	public static final String snel_index_cmd_dir_file_find_regex = "snel.index.cmd.dir.file.find.regex";
	public static final String snel_index_sql_flush_buffer_size = "snel.index.sql.flush.buffer.size";
	public static final String snel_index_file_targets_folder = "snel.index.file.targets.folder";
	public static final String snel_search_record_counts = "snel.search.record.counts";
	public static final String snel_search_file_sort_by = "snel.search.file.sort.by";
	public static final String snel_search_folder_sort_by = "snel.search.folder.sort.by";
	public static final String snel_search_favo_sort_by = "snel.search.favo.sort.by";
	public static final String snel_search_delay = "snel.search.delay";
	public static final String snel_execute_file_cmd = "snel.execute.file.cmd";
	public static final String snel_execute_folder_cmd = "snel.execute.folder.cmd";
	public static final String snel_execute_cmd_cmd = "snel.execute.cmd.cmd";

	private static SnelProperties properties = null;

	private SnelProperties() {
		try {
			this.load(new FileReader("./contents/properties/snel.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static SnelProperties getInstance() {
		if (properties == null) {
			properties = new SnelProperties();
		}
		return properties;
	}

	public int getPropertyAsInt(String key) {
		try {
			return Integer.parseInt(properties.getProperty(key));
		} catch (Exception e) {
			return 0;
		}
	}

	public long getPropertyAsLong(String key) {
		try {
			return Long.parseLong(properties.getProperty(key));
		} catch (Exception e) {
			return 0;
		}
	}

}
