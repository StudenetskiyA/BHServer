package ru.studenetskiy.model;

import java.util.ArrayList;

enum PowerSide {
	Human, Light, Dark;

	PowerSide toPowerside(int n) {
		if (n == 0)
			return Human;
		else if (n == 1)
			return Light;
		else
			return Dark;
	}
}

public class Commons {
	private static String database = "twilight";
	private static String url = "jdbc:mysql://localhost:3306/users?useUnicode=yes&characterEncoding=utf8";
	private static String rootLogin = "root";
	private static String password = "4lifewithBerserk";
	//private static String address = "ws://localhost:8080/BHServer/serverendpointdemo";// "ws://test1.uralgufk.ru:8080/BHServer/serverendpointdemo";
	//private String cycleReadFromServer = CycleServerRead();

	static SQLUserHelper sql = new SQLUserHelper(database, url, rootLogin, password);
	//static SQLZoneHelper sqlZone = new SQLZoneHelper(database, url, rootLogin, password);

	static ArrayList<String> getTextBetween(String txt) {
		String fromText = txt;
		ArrayList<String> rtrn = new ArrayList<String>();
		String beforeText = "(";
		fromText = fromText.substring(fromText.indexOf(beforeText) + 1, fromText.indexOf(")"));
		String[] par = fromText.split(",");
		for (int i = 0; i < par.length; i++) {
			System.out.println("Par : " + par[i]);
			rtrn.add(par[i]);
		}
		return rtrn;
	}
}
