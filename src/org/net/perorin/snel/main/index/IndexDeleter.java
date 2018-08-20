package org.net.perorin.snel.main.index;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.net.perorin.snel.main.index.datum.FavoDatum;

public class IndexDeleter {

	private String dbPath = "";

	public IndexDeleter() {
		this.dbPath = new File("./contents/sqlite3/snel.db").toPath().toString();
	}

	private Connection connect() {

		// SQLiteのurl
		String url = "jdbc:sqlite:" + dbPath;
		try {
			Connection conn = DriverManager.getConnection(url);
			conn.setAutoCommit(false);
			return conn;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean delete(FavoDatum datum) {
		try (Connection conn = this.connect()) {
			try {
				String sql = "delete from favo_table where path = ?;";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, datum.path);
				pstmt.executeUpdate();
			} catch (Exception ex) {
				System.out.println("delete失敗:" + datum);
				return false;
			}
			conn.commit();
			return true;
		} catch (SQLException e) {
			System.out.println("delete実行中にエラー発生");
			return false;
		}
	}
}
