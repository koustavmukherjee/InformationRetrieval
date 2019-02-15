package edu.usc.ir.crawler;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVWriter;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import edu.usc.ir.crawler.stats.model.Fetch;
import edu.usc.ir.crawler.stats.model.Urls;
import edu.usc.ir.crawler.stats.model.Visit;
import edu.usc.ir.crawler.stats.writer.FetchWriterThread;
import edu.usc.ir.crawler.stats.writer.UrlsWriterThread;
import edu.usc.ir.crawler.stats.writer.VisitWriterThread;

public class Controller {

	private static final Logger logger = LoggerFactory.getLogger(Controller.class);

	public static void main(String[] args) throws Exception {
		String crawlStorageFolder = "data/crawl";
		int numberOfCrawlers = 300;
		CrawlConfig config = new CrawlConfig();
		config.setCrawlStorageFolder(crawlStorageFolder);
		config.setMaxPagesToFetch(1000);
		config.setMaxDepthOfCrawling(16);
		config.setIncludeBinaryContentInCrawling(true);
		config.setMaxDownloadSize(Integer.MAX_VALUE);
		config.setRespectNoIndex(false);
		config.setFollowRedirects(true);
		
		// config.setResumableCrawling(true);
		// config.setPolitenessDelay(300);
		/*
		 * Instantiate the controller for this crawl.
		 */
		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

		Files.createDirectories(Paths.get("output"));
		clean(Fetch.WRTIE_LOCATION, Fetch.HEADERS);
		clean(Visit.WRTIE_LOCATION, Visit.HEADERS);
		clean(Urls.WRTIE_LOCATION, Urls.HEADERS);

		/*
		 * For each crawl, you need to add some seed urls. These are the first URLs that
		 * are fetched and then the crawler starts following links which are found in
		 * these pages
		 */
		controller.addSeed("https://www.theguardian.com/us");
		// controller.addSeed("https://www.bbc.com/");
		// controller.addSeed("https://www.theguardian.com/us-news/ng-interactive/2018/oct/03/undefined/assets/lumber.png");
		/*
		 * Start the crawl. This is a blocking operation, meaning that your code will
		 * reach the line after this only when crawling is finished.
		 */
		// controller.start(MyCrawler.class, numberOfCrawlers);

		FetchWriterThread fwt = new FetchWriterThread();
		VisitWriterThread vwt = new VisitWriterThread();
		UrlsWriterThread uwt = new UrlsWriterThread();

		logger.info("Starting Write Threads!!");

		new Thread(fwt).start();
		new Thread(vwt).start();
		new Thread(uwt).start();

		MyCrawlerControllerFactory factory = new MyCrawlerControllerFactory(fwt, vwt, uwt, config);

		logger.info("Starting Controller!!");

		controller.start(factory, numberOfCrawlers);

		logger.info("Ending Controller!!");

		fwt.addToQueue(new Fetch(null, true));
		vwt.addToQueue(new Visit(null, true));
		uwt.addToQueue(new Urls(null, true));

		logger.info("Done!!");
	}

	public static void clean(String statsStorageLocation, String[] headerRecord) {
		try {
			Writer writer = Files.newBufferedWriter(Paths.get(statsStorageLocation));
			CSVWriter csvWriter = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.DEFAULT_QUOTE_CHARACTER,
					CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
			csvWriter.writeNext(headerRecord);
			csvWriter.close();
			writer.close();
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}
}
