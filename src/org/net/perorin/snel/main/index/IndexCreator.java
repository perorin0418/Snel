package org.net.perorin.snel.main.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.net.perorin.snel.main.index.datum.FavoDatum;
import org.net.perorin.snel.main.index.datum.FileDatum;
import org.net.perorin.snel.main.index.datum.FolderDatum;
import org.net.perorin.snel.main.properties.SnelProperties;

public class IndexCreator {

	private static SnelProperties propertis = SnelProperties.getInstance();
	private static Path db_template = new File("./contents/sqlite3/template/snel.db").toPath();
	private static Path db_file = new File("./contents/sqlite3/snel.db").toPath();
	private static Path db_buf = new File("./contents/tmp/snel-" + System.currentTimeMillis() + ".db").toPath();
	private static List<String> hiddenFolderList = null;

	private static List<String> getPaths() {
		List<String> ret = new ArrayList<>();
		String propertisList = propertis.getProperty(SnelProperties.snel_index_file_targets_folder, "not found");
		if ("not found".equals(propertisList)) {
			File[] roots = File.listRoots();
			for (File f : roots) {
				ret.add(f.getPath());
			}
		} else {
			ret = Arrays.asList(propertisList.split(";"));
		}
		return ret;
	}

	public static void createIndex() {
		List<String> paths = getPaths();

		// 隠しフォルダーを除外するためにリストを作成しておく
		getHiddenFolder(paths);

		// テンプレートDBをtmpフォルダーにコピー
		try {
			Files.copy(db_template, db_buf, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		// スレッドリスト
		List<Thread> threads = new ArrayList<>();

		// ファイル捜索をマルチスレッドで行う
		for (String path : paths) {
			Thread thread = new Thread("CreateIndex:" + path) {

				@Override
				public void run() {
					System.out.println("create start [" + path + "]");
					File file = createFileAndFolderList(path);
					IndexInserter ii = new IndexInserter(db_buf);
					readFileAndFolderList(file, new Consumer<List<FileDatum>>() {

						@Override
						public void accept(List<FileDatum> list) {
							System.out.println("insert [" + path + "]'s file:" + list.size());
							ii.insert(list.toArray(new FileDatum[list.size()]));
						}
					}, new Consumer<List<FolderDatum>>() {

						@Override
						public void accept(List<FolderDatum> list) {
							System.out.println("insert [" + path + "]'s folder:" + list.size());
							ii.insert(list.toArray(new FolderDatum[list.size()]));
						}
					});
					file.delete();
					System.out.println("create end [" + path + "]");
				}
			};
			thread.start();
			threads.add(thread);
		}

		// すべてのスレッドが完了するまで待機
		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void replaceIndex() {

		// お気に入りを退避
		IndexSelector is = new IndexSelector();
		System.out.println("");
		List<FavoDatum> list = is.selectFavo("select * from favo_table;");

		// 書き込んだdb_bufを本番dbに上書き
		try {
			Files.copy(db_buf, db_file, StandardCopyOption.REPLACE_EXISTING);
			Files.delete(db_buf);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		// お気に入りを入れなおす
		IndexInserter ii = new IndexInserter(db_file);
		ii.insert(list.toArray(new FavoDatum[list.size()]));
	}

	/**
	 * ファイルを読み込む。キー『snel_index_sql_flush_buffer_size』の回数だけデータがたまるとDBに書き込む
	 * @param file
	 * @param flushFile
	 */
	private static void readFileAndFolderList(File file, Consumer<List<FileDatum>> flushFile, Consumer<List<FolderDatum>> flushFolder) {
		List<FileDatum> buf_FileList = new ArrayList<FileDatum>();
		List<FolderDatum> buf_FolderList = new ArrayList<FolderDatum>();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "Shift-JIS"))) {
			String curretDir = "";
			String line = null;
			while ((line = reader.readLine()) != null) {

				if (line.matches(propertis.getProperty(SnelProperties.snel_index_cmd_dir_folder_find_regex))) {

					// フォルダー行
					String dir = line.replaceAll(propertis.getProperty(SnelProperties.snel_index_cmd_dir_folder_delete_regex), "").trim();
					curretDir = dir;
					FolderDatum datum = new FolderDatum();
					datum.path = dir;
					datum.name = new File(dir).getName();
					if (!isHiddenFolder(datum.path)) {
						buf_FolderList.add(datum);
					}
				} else if (line.matches(propertis.getProperty(SnelProperties.snel_index_cmd_dir_file_find_regex))) {

					// ファイル行
					long date = Long.parseLong(line.substring(0, 18).replaceAll("[^0-9]", ""));
					long size = Long.parseLong(line.substring(19, line.length()).trim().split(" ", 2)[0]);
					String name = line.substring(19, line.length()).trim().split(" ", 2)[1].trim();
					FileDatum datum = new FileDatum();
					datum.path = curretDir + File.separator + name;
					datum.name = name;
					datum.date = date;
					datum.size = size;
					if (!isHiddenFolder(datum.path)) {
						buf_FileList.add(datum);
					}
				}

				// フォルダー書き込み
				if (buf_FolderList.size() >= propertis.getPropertyAsInt(SnelProperties.snel_index_sql_flush_buffer_size)) {
					flushFolder.accept(buf_FolderList);
					buf_FolderList.clear();
				}

				// ファイル書き込み
				if (buf_FileList.size() >= propertis.getPropertyAsInt(SnelProperties.snel_index_sql_flush_buffer_size)) {
					flushFile.accept(buf_FileList);
					buf_FileList.clear();
				}
			}

			// フォルダー書き込み
			if (buf_FolderList.size() > 0) {
				flushFolder.accept(buf_FolderList);
				buf_FolderList.clear();
			}

			// ファイル書き込み
			if (buf_FileList.size() > 0) {
				flushFile.accept(buf_FileList);
				buf_FileList.clear();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static File createFileAndFolderList(String path) {

		// 出力ファイル
		File ret = new File("./contents/tmp/" + path.hashCode() + ".tmp");

		// コマンド格納
		List<String> cmd = new ArrayList<>();

		// コマンドプロンプト
		cmd.add("cmd");

		// 引数からコマンド実行
		cmd.add("/c");

		// ファイル一覧
		cmd.add("dir");

		// ファイルサイズに区切り文字を入れない
		cmd.add("/-c");

		// 年を4桁数字で表示
		cmd.add("/4");

		// 隠しファイル、システムファイルは対象外
		cmd.add("/a-h-s-d");

		// ファイルを右端に表示する一覧形式
		cmd.add("/n");

		// 最終更新時間を表示
		cmd.add("/tw");

		// サブディレクトをすべて表示
		cmd.add("/s");

		// ファイルのアルファベット順で表示
		cmd.add("/on");

		// 検索対象ディレクトリ
		cmd.add(path);

		ProcessBuilder pb = new ProcessBuilder(cmd);

		// 結果はファイルに出力
		pb.redirectOutput(ret);

		try {
			// コマンド実行
			Process proc = pb.start();

			// コマンド完了まで待機
			proc.waitFor();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}

	private static boolean isHiddenFolder(String file) {
		for (String hidden : hiddenFolderList) {
			if (file.startsWith(hidden)) {
				return true;
			}
		}
		return false;
	}

	private static List<String> getHiddenFolder(List<String> paths) {
		if (hiddenFolderList != null) {
			return hiddenFolderList;
		}

		hiddenFolderList = new ArrayList<>();
		try {
			for (String path : paths) {
				System.out.println("create hidden list start [" + path + "]");
				File file = new File("./contents/tmp/hidden-" + path.hashCode() + ".tmp");

				if (!file.exists()) {
					List<String> cmd = new ArrayList<>();
					cmd.add("cmd");
					cmd.add("/c");
					cmd.add("dir");
					cmd.add("/s");
					cmd.add("/b");
					cmd.add("/adh");
					cmd.add(path);

					ProcessBuilder pb = new ProcessBuilder(cmd);
					pb.redirectOutput(file);
					Process p = pb.start();
					p.waitFor();
				}
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "Shift-JIS"))) {
					String line;
					while ((line = reader.readLine()) != null) {
						hiddenFolderList.add(line);
					}
				}
				System.out.println("create hidden list end   [" + path + "]");
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return hiddenFolderList;
	}

}
