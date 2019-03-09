package edu.usc.ir.hadoop;

import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class InvertedIndex {

	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		if (args.length < 2 || args.length > 3) {
			System.err.println("Usage: Word Count <input_path> <output_path> [U | B]");
			System.exit(1);
		}
		Job job = new Job();
		job.setJarByClass(InvertedIndex.class);
		job.setJobName("Word Count!!");
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		if(args.length == 2 || (args.length == 3 && args[2].equalsIgnoreCase("U")))
			job.setMapperClass(InvertedIndexUnigramMapper.class);
		else if(args.length == 3 && args[2].equalsIgnoreCase("B"))
			job.setMapperClass(InvertedIndexBigramMapper.class);
		else {
			System.err.println("Usage: Word Count <input_path> <output_path> [U | B]");
			System.exit(1);
		}
		job.setReducerClass(InvertedIndexReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		job.waitForCompletion(true);
	}
}