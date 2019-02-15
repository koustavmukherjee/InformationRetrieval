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

import edu.usc.ir.crawler.stats.model.Fetch;

public class FetchWriterThread implements Runnable {

	private BlockingQueue<Fetch> blockingQueue = new LinkedBlockingQueue<>();
	private static final List<Fetch> fetches = new CopyOnWriteArrayList<>();
	private static final Logger logger = LoggerFactory.getLogger(FetchWriterThread.class);
	private static final Integer WRITE_BATCH_SIZE = 1000;

	public void addToQueue(Fetch fetches) {
		blockingQueue.add(fetches);
	}

	private void addToFetches(Fetch fetch) throws MethodNotSupportedException, CsvDataTypeMismatchException,
			CsvRequiredFieldEmptyException, IOException {
		fetches.add(fetch);
		if (fetches.size() >= WRITE_BATCH_SIZE) {
			this.writeFetches();
		}
	}

	private void writeFetches() throws MethodNotSupportedException, CsvDataTypeMismatchException,
			CsvRequiredFieldEmptyException, IOException {
		logger.debug("Writing Fetches to CSV Started");
		synchronized (fetches) {
			if (fetches.size() > 0) {
				CsvCrawlStatsWriter.writeFetchToCSV(fetches);
				fetches.clear();
			}
		}
		logger.debug("Writing Fetches to CSV Done");
	}

	@Override
	public void run() {
		while (true) {
			try {
				logger.debug("Fetch about to take");
				Fetch fetch = blockingQueue.take();
				if (fetch.isTerminalPacket()) {
					writeFetches();
					logger.debug("Breaking out of Fetches loop");
					break;
				}
				this.addToFetches(fetch);
			} catch (InterruptedException | MethodNotSupportedException | CsvDataTypeMismatchException
					| CsvRequiredFieldEmptyException | IOException e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}
	}

}