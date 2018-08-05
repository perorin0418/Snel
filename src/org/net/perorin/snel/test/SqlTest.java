package org.net.perorin.snel.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class SqlTest {
	public static void main(String[] args) {

		Connection connection = null;
		Statement statement = null;

		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:/path/to/hoge.db");
			statement = connection.createStatement();
			String sql = "select * from fruits";
			ResultSet rs = statement.executeQuery(sql);
			while (rs.next()) {
				System.out.println(rs.getString(1));
			}
		} catch (Exception e) {

		}
	}
}
