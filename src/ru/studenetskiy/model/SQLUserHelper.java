package ru.studenetskiy.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLUserHelper extends SQLHelper {

	SQLUserHelper(String databaseName, String url, String login, String password) {
		super(databaseName, url, login, password);
	}

	PowerSide getUserPowerside(String _userName) {
		if (isUserExist(_userName)) {
			String query = "SELECT powerside FROM users WHERE name='";
			query += _userName + "'" + ";";

			try {
				Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				if (rs != null) {
					while (rs.next()) {
						return PowerSide.Human.toPowerside(rs.getInt(1));
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return PowerSide.Human;
	}

	Boolean isUserExist(String _userName) {
		String query = "SELECT count(*) FROM users WHERE name='";
		query += _userName + "'" + ";";

		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			if (rs != null) {
				while (rs.next()) {
					return rs.getInt(1) == 1;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	Boolean isUserPasswordCorrect(String _userName, String _password) {
		if (isUserExist(_userName)) {
			String query = "SELECT password FROM users WHERE name='";
			query += _userName + "'" + ";";
			System.out.println("Query : " + query);
			try {
				Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				if (rs != null) {
					while (rs.next()) {
						return rs.getString(1).equals(_password);
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	void writeUserCoordinates(String userName, int latitude, int longitude) {
		if (isUserExist(userName)) {
			String query = "UPDATE users SET latitude=";
			query += latitude + ",longitude=" + longitude + " WHERE name=" + "'" + userName + "';";
			try {
				// executing SELECT query
				con.createStatement().executeUpdate(query);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	void writeUserLastConneted(String userName) {
		if (isUserExist(userName)) {
			String query = "UPDATE users SET lastconnected=NOW() WHERE name=" + "'" + userName + "';";
			try {
				// executing SELECT query
				con.createStatement().executeUpdate(query);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
