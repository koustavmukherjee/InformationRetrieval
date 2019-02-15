package edu.usc.ir.crawler.stats.model;

public class Urls extends Stats {

	public static final String WRTIE_LOCATION = "./output/urls_guardian.csv";
	public static final String[] MEMEBER_FIELD_BINDING = { "url", "location" };
	public static final String[] HEADERS = { "URL", "LOCATION" };

	public enum Location {
		OK, N_OK
	}

	Urls.Location location;
	boolean terminalPacket = false;

	public Urls.Location getLocation() {
		return location;
	}

	public void setLocation(Urls.Location location) {
		this.location = location;
	}

	public Urls(String url, Location location) {
		super(url);
		this.location = location;
	}

	public Urls(String url, boolean terminalPacket) {
		super(url);
		this.terminalPacket = terminalPacket;
	}

	public boolean isTerminalPacket() {
		return terminalPacket;
	}
}
