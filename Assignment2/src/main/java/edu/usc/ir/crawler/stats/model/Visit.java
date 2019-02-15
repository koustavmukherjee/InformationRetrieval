package edu.usc.ir.crawler.stats.model;

public class Visit extends Stats {

	public static final String WRTIE_LOCATION = "./output/visit_guardian.csv";
	public static final String[] MEMEBER_FIELD_BINDING = { "url", "size", "noOfOutlinks", "contentType" };
	public static final String[] HEADERS = { "URL", "SIZE", "NO_OF_OUTLINKS", "CONTENT_TYPE" };

	int size;
	int noOfOutlinks;
	String contentType;
	boolean terminalPacket = false;

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getNoOfOutlinks() {
		return noOfOutlinks;
	}

	public void setNoOfOutlinks(int noOfOutlinks) {
		this.noOfOutlinks = noOfOutlinks;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public Visit(String url, int size, int noOfOutlinks, String contentType) {
		super(url);
		this.size = size;
		this.noOfOutlinks = noOfOutlinks;
		this.contentType = contentType;
	}
	
	public Visit(String url, boolean terminalPacket) {
		super(url);
		this.terminalPacket = terminalPacket;
	}

	public boolean isTerminalPacket() {
		return terminalPacket;
	}
}
