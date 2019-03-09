package edu.usc.ir.hadoop;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class InvertedIndexReducer extends Reducer<Text, Text, Text, Text> {

	@Override
	protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		Map<String, Integer> freq = new ConcurrentHashMap<>();
		for (Text value : values) {
			int count = freq.containsKey(value.toString()) ? freq.get(value.toString()) : 0;
			freq.put(value.toString(), count + 1);
		}
		StringBuilder sb = new StringBuilder();
		for (Entry<String, Integer> entry : freq.entrySet()) {
			sb.append(entry.getKey());
			sb.append(":");
			sb.append(entry.getValue());
			sb.append(" ");
		}
		context.write(key, new Text(sb.toString()));
	}

}
