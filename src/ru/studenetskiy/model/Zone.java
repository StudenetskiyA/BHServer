package ru.studenetskiy.model;

public class Zone {

	int latitude;
	int longitude;
	int radius;
	String name;
	String textForHuman;
	String textForLight;
	String textForDark;

	Zone(String name, int latitude, int longitude, int radius, String textForHuman, String textForLight,
			String textForDark) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.radius = radius;
		this.name = name;
		this.textForHuman = textForHuman;
		this.textForLight = textForLight;
		this.textForDark = textForDark;
	}

	Boolean isInZone(int lati, int longi) {
		Double l = Math.sqrt(Math.pow(Math.abs(lati - latitude), 2.0) + Math.pow(Math.abs(longi - longitude), 2.0));
		return l <= radius;
	}

	public String toString() {
		return name + "," + latitude + "," + longitude + "," + radius + "," + textForHuman + "," + textForLight + ","
				+ textForDark;
	}
}
