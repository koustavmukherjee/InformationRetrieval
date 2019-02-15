package edu.usc.ir.crawler.stats.writer;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.http.MethodNotSupportedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import edu.usc.ir.crawler.stats.model.Urls;

public class UrlsWriterThread implements Runnable {

	private BlockingQueue<Urls> blockingQueue = new LinkedBlockingQueue<>();
	private static final Logger logger = LoggerFactory.getLogger(UrlsWriterThread.class);
	private static final List<Urls> urls = new CopyOnWriteArrayList<>();
	private static final Integer WRITE_BATCH_SIZE = 1000;
	
	public void addToQueue(Urls url) {
		blockingQueue.add(url);
	}

	private void addToUrls(Urls url) throws MethodNotSupportedException, CsvDataTypeMismatchException,
			CsvRequiredFieldEmptyException, IOException {
		urls.add(url);
		if (urls.size() >= WRITE_BATCH_SIZE) {
			this.writeUrls();
		}
	}

	private void writeUrls() throws MethodNotSupportedException, CsvDataTypeMismatchException,
			CsvRequiredFieldEmptyException, IOException {
		logger.debug("Writing Urls to CSV Started");
		synchronized (urls) {
			if (urls.size() > 0) {
				CsvCrawlStatsWriter.writeUrlsToCSV(urls);
				urls.clear();
			}
		}
		logger.debug("Writing Urls to CSV Done");
	}

	@Override
	public void run() {
		while (true) {
			try {
				logger.debug("Urls about to take");
				Urls url = blockingQueue.take();
				if (url.isTerminalPacket()) {
					writeUrls();
					logger.debug("Breaking out of URLs loop");
					break;
				}
				this.addToUrls(url);
			} catch (InterruptedException | MethodNotSupportedException | CsvDataTypeMismatchException
					| CsvRequiredFieldEmptyException | IOException e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}
	}

}