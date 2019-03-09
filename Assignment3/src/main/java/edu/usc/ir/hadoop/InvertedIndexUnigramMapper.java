package edu.usc.ir.hadoop;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class InvertedIndexUnigramMapper extends Mapper<LongWritable, Text, Text, Text> {
	private Text word = new Text();

	@Override
	protected void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		String line = value.toString();
		String[] keyVal = line.split("\t", 2);

		String k = keyVal[0];
		String val = keyVal[1];

		val = val.replaceAll("[^a-zA-Z]+", " ").toLowerCase();
		StringTokenizer tokenizer = new StringTokenizer(val);
		while (tokenizer.hasMoreTokens()) {
			word.set(tokenizer.nextToken());
			context.write(word, new Text(k));
		}
	}

}
