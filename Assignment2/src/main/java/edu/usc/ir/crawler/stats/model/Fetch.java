package edu.usc.ir.crawler.stats.model;

public class Fetch extends Stats {

	public static final String WRTIE_LOCATION = "./output/fetch_guardian.csv";
	public static final String[] MEMEBER_FIELD_BINDING = { "url", "httpStatus" };
	public static final String[] HEADERS = { "URL", "HTTP_STATUS" };

	int httpStatus;
	boolean terminalPacket = false;

	public int getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(int httpStatus) {
		this.httpStatus = httpStatus;
	}

	public Fetch(String url, int httpStatus) {
		super(url);
		this.httpStatus = httpStatus;
		this.terminalPacket = false;
	}

	public Fetch(String url, boolean terminalPacket) {
		super(url);
		this.terminalPacket = terminalPacket;
	}

	public boolean isTerminalPacket() {
		return terminalPacket;
	}

}
