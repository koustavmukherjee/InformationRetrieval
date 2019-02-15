package edu.usc.ir.crawler.stats.writer;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import org.apache.http.MethodNotSupportedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import edu.usc.ir.crawler.stats.model.Fetch;
import edu.usc.ir.crawler.stats.model.Urls;
import edu.usc.ir.crawler.stats.model.Visit;

public class CsvCrawlStatsWriter {

	private static final Logger logger = LoggerFactory.getLogger(CsvCrawlStatsWriter.class);
	
	public static void writeFetchToCSV(List<Fetch> fetches) throws MethodNotSupportedException, IOException,
			CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
		logger.info("Fetch Size : " + fetches.size());
		Writer writer = null;
		String statsStorageLocation = Fetch.WRTIE_LOCATION;
		if (Files.exists(Paths.get(statsStorageLocation)))
			writer = Files.newBufferedWriter(Paths.get(statsStorageLocation), StandardOpenOption.APPEND);
		else
			writer = Files.newBufferedWriter(Paths.get(statsStorageLocation));

		ColumnPositionMappingStrategy<Fetch> mappingStrategy = new ColumnPositionMappingStrategy<>();
		mappingStrategy.setType(Fetch.class);
		String[] memberFieldsToBindTo = Fetch.MEMEBER_FIELD_BINDING;
		mappingStrategy.setColumnMapping(memberFieldsToBindTo);
		StatefulBeanToCsv<Fetch> beanToCsv = new StatefulBeanToCsvBuilder<Fetch>(writer)
				.withQuotechar(CSVWriter.DEFAULT_QUOTE_CHARACTER).withMappingStrategy(mappingStrategy).build();
		beanToCsv.write(fetches);
		writer.close();
	}

	public static void writeVisitToCSV(List<Visit> visits) throws MethodNotSupportedException, IOException,
			CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {

		Writer writer = null;
		String statsStorageLocation = Visit.WRTIE_LOCATION;
		if (Files.exists(Paths.get(statsStorageLocation)))
			writer = Files.newBufferedWriter(Paths.get(statsStorageLocation), StandardOpenOption.APPEND);
		else
			writer = Files.newBufferedWriter(Paths.get(statsStorageLocation));

		ColumnPositionMappingStrategy<Visit> mappingStrategy = new ColumnPositionMappingStrategy<>();
		mappingStrategy.setType(Visit.class);
		String[] memberFieldsToBindTo = Visit.MEMEBER_FIELD_BINDING;
		mappingStrategy.setColumnMapping(memberFieldsToBindTo);
		StatefulBeanToCsv<Visit> beanToCsv = new StatefulBeanToCsvBuilder<Visit>(writer)
				.withQuotechar(CSVWriter.DEFAULT_QUOTE_CHARACTER).withMappingStrategy(mappingStrategy).build();
		beanToCsv.write(visits);
		writer.close();
	}

	public static void writeUrlsToCSV(List<Urls> urls) throws MethodNotSupportedException, IOException,
			CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {

		Writer writer = null;
		String statsStorageLocation = Urls.WRTIE_LOCATION;
		if (Files.exists(Paths.get(statsStorageLocation)))
			writer = Files.newBufferedWriter(Paths.get(statsStorageLocation), StandardOpenOption.APPEND);
		else
			writer = Files.newBufferedWriter(Paths.get(statsStorageLocation));

		ColumnPositionMappingStrategy<Urls> mappingStrategy = new ColumnPositionMappingStrategy<>();
		mappingStrategy.setType(Urls.class);
		String[] memberFieldsToBindTo = Urls.MEMEBER_FIELD_BINDING;
		mappingStrategy.setColumnMapping(memberFieldsToBindTo);
		StatefulBeanToCsv<Urls> beanToCsv = new StatefulBeanToCsvBuilder<Urls>(writer)
				.withQuotechar(CSVWriter.DEFAULT_QUOTE_CHARACTER).withMappingStrategy(mappingStrategy).build();
		beanToCsv.write(urls);
		writer.close();
	}
}