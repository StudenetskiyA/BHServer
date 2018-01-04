package ru.studenetskiy.model;

import java.sql.*;
import java.sql.DriverManager;

public class SQLHelper {

	String url;
	String login;
	String password;
	Connection con;
	String databaseName;

	SQLHelper(String databaseName, String url, String login, String password) {
		this.url=url;
		this.databaseName=databaseName;
		this.login=login;
		this.password=password;
	}

	void connect() {
		try {
			con = DriverManager.getConnection(url, login, password);
			Statement stmt2 = con.createStatement();
			if (stmt2 != null) {
				stmt2.executeUpdate("USE " + databaseName);
				stmt2.executeUpdate("SET character_set_results='utf8'");
				stmt2.executeUpdate("SET character_set_client='utf8'");

				stmt2.executeUpdate("SET character_set_connection='utf8'");
				stmt2.executeUpdate("SET SESSION collation_connection = 'utf8_general_ci'");
			}
		} catch (SQLException sqlEx) {
			sqlEx.printStackTrace();
		}

	}

	void disconnect() {
		try {
			con.close();
		} catch (SQLException se) {
		}
	}
}
