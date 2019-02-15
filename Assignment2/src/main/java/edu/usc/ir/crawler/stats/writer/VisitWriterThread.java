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

import edu.usc.ir.crawler.stats.model.Visit;

public class VisitWriterThread implements Runnable {

	private BlockingQueue<Visit> blockingQueue = new LinkedBlockingQueue<>();
	private static final Logger logger = LoggerFactory.getLogger(VisitWriterThread.class);
	private static final List<Visit> visits = new CopyOnWriteArrayList<>();
	private static final Integer WRITE_BATCH_SIZE = 1000;

	public void addToQueue(Visit visit) {
		blockingQueue.add(visit);
	}

	private void addToVisits(Visit visit) throws MethodNotSupportedException, CsvDataTypeMismatchException,
			CsvRequiredFieldEmptyException, IOException {
		visits.add(visit);
		if (visits.size() >= WRITE_BATCH_SIZE) {
			this.writeVisits();
		}
	}

	private void writeVisits() throws MethodNotSupportedException, CsvDataTypeMismatchException,
			CsvRequiredFieldEmptyException, IOException {
		logger.debug("Writing Visits to CSV Started");
		synchronized (visits) {
			if (visits.size() > 0) {
				CsvCrawlStatsWriter.writeVisitToCSV(visits);
				visits.clear();
			}
		}
		logger.debug("Writing Visits to CSV Done");
	}

	@Override
	public void run() {
		while (true) {
			try {
				logger.debug("Visits about to take");
				Visit visit = blockingQueue.take();
				if (visit.isTerminalPacket()) {
					writeVisits();
					logger.debug("Breaking out of Visit loop");
					break;
				}
				this.addToVisits(visit);
			} catch (InterruptedException | MethodNotSupportedException | CsvDataTypeMismatchException
					| CsvRequiredFieldEmptyException | IOException e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}
	}

}