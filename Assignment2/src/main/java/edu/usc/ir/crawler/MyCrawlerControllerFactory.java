package edu.usc.ir.crawler;

import edu.uci.ics.crawler4j.crawler.CrawlController.WebCrawlerFactory;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.usc.ir.crawler.stats.writer.FetchWriterThread;
import edu.usc.ir.crawler.stats.writer.UrlsWriterThread;
import edu.usc.ir.crawler.stats.writer.VisitWriterThread;

public class MyCrawlerControllerFactory implements WebCrawlerFactory<WebCrawler> {

	private FetchWriterThread fwt;
	private VisitWriterThread vwt;
	private UrlsWriterThread uwt;

	@Override
	public WebCrawler newInstance() throws Exception {
		return new MyCrawler(fwt, vwt, uwt);
	}

	public MyCrawlerControllerFactory(FetchWriterThread fwt, VisitWriterThread vwt, UrlsWriterThread uwt) {
		super();
		this.fwt = fwt;
		this.vwt = vwt;
		this.uwt = uwt;
	}

}