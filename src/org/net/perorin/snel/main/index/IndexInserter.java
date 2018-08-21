package org.net.perorin.snel.main.index;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.net.perorin.snel.main.index.datum.FavoDatum;
import org.net.perorin.snel.main.index.datum.FileDatum;
import org.net.perorin.snel.main.index.datum.FolderDatum;
import org.net.perorin.snel.main.logger.SnelLogger;

public class IndexInserter {

	private String dbPath = "";

	public IndexInserter(Path dbPath) {
		this.dbPath = dbPath.toString();
	}

	private Connection connect() {

		// SQLiteのurl
		String url = "jdbc:sqlite:" + dbPath;
		try {
			Connection conn = DriverManager.getConnection(url);
			conn.setAutoCommit(false);
			return conn;
		} catch (SQLException e) {
			SnelLogger.warning(e);
		}
		return null;
	}

	public List<FileDatum> insert(FileDatum... data) {
		List<FileDatum> errList = new ArrayList<>();
		try (Connection conn = this.connect()) {
			for (FileDatum datum : data) {
				try {
					String sql = "insert into file_table(path, name, date, size) values(?, ?, ?, ?);";
					PreparedStatement pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, datum.path);
					pstmt.setString(2, datum.name);
					pstmt.setLong(3, datum.date);
					pstmt.setLong(4, datum.size);
					pstmt.executeUpdate();
				} catch (Exception ex) {
					errList.add(datum);
					SnelLogger.info("insert失敗:" + datum);
				}
			}
			conn.commit();
		} catch (SQLException e) {
			SnelLogger.info("insert実行中にエラー発生");
		}
		return errList;
	}

	public List<FolderDatum> insert(FolderDatum... data) {
		List<FolderDatum> errList = new ArrayList<>();
		try (Connection conn = this.connect()) {
			for (FolderDatum datum : data) {
				try {
					String sql = "insert into folder_table(path, name) values(?, ?);";
					PreparedStatement pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, datum.path);
					pstmt.setString(2, datum.name);
					pstmt.executeUpdate();
				} catch (Exception ex) {
					errList.add(datum);
					SnelLogger.info("insert失敗:" + datum);
				}
			}
			conn.commit();
		} catch (SQLException e) {
			SnelLogger.info("insert実行中にエラー発生");
		}
		return errList;
	}

	public List<FavoDatum> insert(FavoDatum... data) {
		List<FavoDatum> errList = new ArrayList<>();
		try (Connection conn = this.connect()) {
			for (FavoDatum datum : data) {
				try {
					String sql = "insert into favo_table(path, name) values(?, ?);";
					PreparedStatement pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, datum.path);
					pstmt.setString(2, datum.name);
					pstmt.executeUpdate();
				} catch (Exception ex) {
					errList.add(datum);
					SnelLogger.info("insert失敗:" + datum);
				}
			}
			conn.commit();
		} catch (SQLException e) {
			SnelLogger.info("insert実行中にエラー発生");
		}
		return errList;
	}

	public boolean insert(FavoDatum datum) {
		try (Connection conn = this.connect()) {
			try {
				String sql = "insert into favo_table(path, name) values(?, ?);";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, datum.path);
				pstmt.setString(2, datum.name);
				pstmt.executeUpdate();
			} catch (Exception ex) {
				SnelLogger.info("insert失敗:" + datum);
				return false;
			}
			conn.commit();
			return true;
		} catch (SQLException e) {
			SnelLogger.info("insert実行中にエラー発生");
			return false;
		}
	}
}
