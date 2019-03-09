package edu.usc.ir.hadoop;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class InvertedIndexBigramMapper extends Mapper<LongWritable, Text, Text, Text> {
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
		String firstToken = "";
		if(tokenizer.hasMoreTokens())
			firstToken = tokenizer.nextToken();
		String secondToken = "";
		while (tokenizer.hasMoreTokens()) {
			secondToken = tokenizer.nextToken();
			word.set(firstToken + " " + secondToken);
			context.write(word, new Text(k));
			firstToken = secondToken;
		}
	}

}
