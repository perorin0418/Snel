package org.net.perorin.snel.main.index;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.net.perorin.snel.main.index.datum.Datum;
import org.net.perorin.snel.main.properties.SnelProperties;

public class IndexSelector {

	private static SnelProperties propertis = SnelProperties.getInstance();

	public Connection connect() {

		// SQLiteのurl
		String url = "jdbc:sqlite:./contents/sqlite3/snel.db";
		try {
			Connection conn = DriverManager.getConnection(url);
			return conn;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Datum> selectFile(List<String> targets, int page) {
		List<Datum> ret = new ArrayList<>();
		try (Connection conn = this.connect()) {
			String sql = createFileSql(targets, page);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				Datum datum = new Datum();
				datum.path = rs.getString(1);
				datum.name = rs.getString(2);
				ret.add(datum);
			}
		} catch (SQLException e) {
			System.out.println("select実行中にエラー発生");
		}
		System.out.println("select result: " + ret.size() + " records");
		return ret;
	}

	private String createFileSql(List<String> targets, int page) {

		String sql = "select path, name from file_table where";

		List<String> pathTargets = new ArrayList<>();
		List<String> nameTargets = new ArrayList<>();
		targets.forEach(target -> {
			if (target.startsWith(":")) {
				pathTargets.add("\"" + target.replaceFirst(":", "") + "%\"");
			} else {
				nameTargets.add("\"" + target + "%\"");
			}
		});

		String path = pathTargets.size() > 0 ? " ( path like " + String.join(" and path like", pathTargets) + " )" : "";
		String name = nameTargets.size() > 0 ? " ( name like " + String.join(" and name like", nameTargets) + " )" : "";

		if ("".equals(name)) {
			sql += path;
		} else if ("".equals(path)) {
			sql += name;
		} else {
			sql += path + " and" + name;
		}

		switch (propertis.getProperty(SnelProperties.snel_search_file_sort_by)) {
		case "path":
			sql += " order by path";
			break;

		case "name":
			sql += " order by name";
			break;

		case "date":
			sql += " order by date";
			break;

		case "size":
			sql += " order by size";
			break;
		}

		sql += " limit " + propertis.getProperty(SnelProperties.snel_search_record_counts);
		sql += " offset " + (page * propertis.getPropertyAsInt(SnelProperties.snel_search_record_counts));
		sql += ";";

		return sql;
	}

	public List<Datum> selectFolder(List<String> targets, int page) {
		List<Datum> ret = new ArrayList<>();
		try (Connection conn = this.connect()) {
			String sql = createFolderSql(targets, page);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				Datum datum = new Datum();
				datum.path = rs.getString(1);
				datum.name = rs.getString(2);
				ret.add(datum);
			}
		} catch (SQLException e) {
			System.out.println("select実行中にエラー発生");
		}
		System.out.println("select result: " + ret.size() + " records");
		return ret;
	}

	private String createFolderSql(List<String> targets, int page) {

		String sql = "select path, name from folder_table where";

		List<String> pathTargets = new ArrayList<>();
		List<String> nameTargets = new ArrayList<>();
		targets.forEach(target -> {
			if (target.startsWith(":")) {
				pathTargets.add("\"" + target.replaceFirst(":", "") + "%\"");
			} else {
				nameTargets.add("\"" + target + "%\"");
			}
		});

		String path = pathTargets.size() > 0 ? " ( path like " + String.join(" and path like", pathTargets) + " )" : "";
		String name = nameTargets.size() > 0 ? " ( name like " + String.join(" and name like", nameTargets) + " )" : "";

		if ("".equals(name)) {
			sql += path;
		} else if ("".equals(path)) {
			sql += name;
		} else {
			sql += path + " and" + name;
		}

		switch (propertis.getProperty(SnelProperties.snel_search_folder_sort_by)) {
		case "path":
			sql += " order by path";
			break;

		case "name":
			sql += " order by name";
			break;
		}

		sql += " limit " + propertis.getProperty(SnelProperties.snel_search_record_counts);
		sql += " offset " + (page * propertis.getPropertyAsInt(SnelProperties.snel_search_record_counts));
		sql += ";";

		return sql;
	}

	public List<Datum> selectApp(List<String> targets, int page) {
		List<Datum> ret = new ArrayList<>();
		try (Connection conn = this.connect()) {
			String sql = createAppSql(targets, page);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				Datum datum = new Datum();
				datum.path = rs.getString(1);
				datum.name = rs.getString(2);
				ret.add(datum);
			}
		} catch (SQLException e) {
			System.out.println("select実行中にエラー発生");
		}
		System.out.println("select result: " + ret.size() + " records");
		return ret;
	}

	private String createAppSql(List<String> targets, int page) {

		String sql = "select path, name from app_table where";

		List<String> pathTargets = new ArrayList<>();
		List<String> nameTargets = new ArrayList<>();
		targets.forEach(target -> {
			if (target.startsWith(":")) {
				pathTargets.add("\"" + target.replaceFirst(":", "") + "%\"");
			} else {
				nameTargets.add("\"" + target + "%\"");
			}
		});

		String path = pathTargets.size() > 0 ? " ( path like " + String.join(" and path like", pathTargets) + " )" : "";
		String name = nameTargets.size() > 0 ? " ( name like " + String.join(" and name like", nameTargets) + " )" : "";

		if ("".equals(name)) {
			sql += path;
		} else if ("".equals(path)) {
			sql += name;
		} else {
			sql += path + " and" + name;
		}

		switch (propertis.getProperty(SnelProperties.snel_search_app_sort_by)) {
		case "path":
			sql += " order by path";
			break;

		case "name":
			sql += " order by name";
			break;
		}

		sql += " limit " + propertis.getProperty(SnelProperties.snel_search_record_counts);
		sql += " offset " + (page * propertis.getPropertyAsInt(SnelProperties.snel_search_record_counts));
		sql += ";";

		return sql;
	}

}
