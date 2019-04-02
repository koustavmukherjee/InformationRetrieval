package edu.usc.ir.solr;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import edu.usc.ir.solr.model.CsvFileUrl;

public class CreateEdgeList {
	private static final String CSV_FILE_PATH = "D:\\usc_courses\\Semester-4\\Assignments\\Assignment4\\data\\URLtoHTML_guardian_news.csv";
	private static final String EDGE_LIST_FILE_PATH = "D:\\usc_courses\\Semester-4\\Assignments\\Assignment4\\data\\edgelist.txt";
	private static final File dir = new File(
			"D:\\usc_courses\\Semester-4\\Assignments\\Assignment4\\data\\guardiannews");
	private static AtomicInteger count = new AtomicInteger(0); 
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) throws IOException {
		Map<String, String> fileUrlMap = new HashMap<>();
		Map<String, String> urlFileMap = new HashMap<>();
		try (Reader reader = Files.newBufferedReader(Paths.get(CSV_FILE_PATH));) {
			ColumnPositionMappingStrategy strategy = new ColumnPositionMappingStrategy();
			strategy.setType(CsvFileUrl.class);
			String[] memberFieldsToBindTo = { "filename", "URL" };
			strategy.setColumnMapping(memberFieldsToBindTo);
			CsvToBean<CsvFileUrl> csvToBean = new CsvToBeanBuilder(reader).withMappingStrategy(strategy)
					.withSkipLines(1).withIgnoreLeadingWhiteSpace(true).build();

			Iterator<CsvFileUrl> fileUrlCsvIterator = csvToBean.iterator();
			while (fileUrlCsvIterator.hasNext()) {
				CsvFileUrl csvFileUrl = fileUrlCsvIterator.next();
				fileUrlMap.put(csvFileUrl.getFilename(), csvFileUrl.getUrl());
				urlFileMap.put(csvFileUrl.getUrl(), csvFileUrl.getFilename());
			}
		}
		Set<String> edges = new HashSet<String>();
		for (File file : dir.listFiles()) {
			Document doc = Jsoup.parse(file, "UTF-8", fileUrlMap.get(file.getName()));
			Elements links = doc.select("a[href]");
			for (Element link : links) {
				String url = link.attr("abs:href").trim();
				if (urlFileMap.containsKey(url)) {
					edges.add(file.getName() + " " + urlFileMap.get(url));
				}
			}
			Integer current = count.getAndIncrement();
			System.out.println(current);
		}
		try (PrintWriter writer = new PrintWriter(new FileWriter(EDGE_LIST_FILE_PATH))) {
			for(String s : edges) {
				writer.println(s);
			}
		}
	}
}
