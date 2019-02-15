package edu.usc.ir.crawler;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;
import edu.usc.ir.crawler.stats.model.Fetch;
import edu.usc.ir.crawler.stats.model.Urls;
import edu.usc.ir.crawler.stats.model.Urls.Location;
import edu.usc.ir.crawler.stats.model.Visit;
import edu.usc.ir.crawler.stats.writer.FetchWriterThread;
import edu.usc.ir.crawler.stats.writer.UrlsWriterThread;
import edu.usc.ir.crawler.stats.writer.VisitWriterThread;

public class MyCrawler extends WebCrawler {
	private static final Pattern filters = Pattern.compile(
			".*(\\.(css|js|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|ram|m4v|xml" + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");
	private static final Pattern imgPatterns = Pattern.compile(".*(\\.(bmp|gif|jpe?g|png|tiff?))$");
	private static final Pattern docPatterns = Pattern.compile(".*(\\.(pdf" + "|doc|docx|docm" + "|htm|html))$");
	private static final Logger logger = LoggerFactory.getLogger(MyCrawler.class);

	private FetchWriterThread fwt;
	private VisitWriterThread vwt;
	private UrlsWriterThread uwt;

	public MyCrawler(FetchWriterThread fwt, VisitWriterThread vwt, UrlsWriterThread uwt) {
		super();
		this.fwt = fwt;
		this.vwt = vwt;
		this.uwt = uwt;
	}

	@Override
	public boolean shouldVisit(Page page, WebURL url) {
		Location location;
		String href = url.getURL().toLowerCase();
		String contentType = "";
		boolean flag = false;
		if (url.getSubDomain().equalsIgnoreCase("www") &&
			url.getDomain().equalsIgnoreCase("theguardian.com") &&
			url.getPath().startsWith("us")) {
			location = Location.OK;
			flag = true;
		} else {
			location = Location.N_OK;
		}
		this.uwt.addToQueue(new Urls(href, location));
		if (!flag || filters.matcher(href).matches()) {
			return false;
		} else {
			try {
				HttpURLConnection.setFollowRedirects(false);
				URL uri = new URL(href);
				HttpURLConnection connection = (HttpURLConnection)  uri.openConnection();
				connection.setRequestMethod("HEAD");
				connection.setConnectTimeout(5000);
				connection.connect();
				contentType = connection == null ? "" : (connection.getContentType() == null ? "" : connection.getContentType());
				connection.disconnect();
			} catch(IOException e) {
				logger.error(e.getMessage());
			}

			if (!contentType.isEmpty()) {
				return StringUtils.indexOfAny(contentType,
						new String[] { 
								"image/gif", 
								"image/jpeg", 
								"image/png",
								"application/pdf", 
								"text/html" }) > -1;
			} else {
				return (imgPatterns.matcher(href).matches() || docPatterns.matcher(href).matches());
			}
		}
	}

	@Override
	public void visit(Page page) {
		String url = page.getWebURL().getURL();
		String contentType = page.getContentType().split(";")[0];
		int noOfOutlinks = page.getParseData().getOutgoingUrls().size();
		int size = page.getContentData().length;
		this.vwt.addToQueue(new Visit(url, size, noOfOutlinks, contentType));
	}

	@Override
	protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {
		String url = webUrl.getURL();
		this.fwt.addToQueue(new Fetch(url, statusCode));
	}

}
