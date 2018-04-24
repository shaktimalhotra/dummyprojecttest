package mapreduce;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class J2Map extends Mapper<LongWritable, Text, Text, Text> {
	@Override
	protected void map(LongWritable keyvlaue, Text videoOccurenceInfo, Context context)
			throws IOException, InterruptedException {
		// mapper 2 output is feed from disk / hdfs
		// map input is ( rownumber, (videoid\t[US~MUSIC,GB~MUSIC])
		String[] countyCategKey = videoOccurenceInfo.toString().split("\t")[1].replaceAll("\\[", "")
				.replaceAll("\\]", "").split(",");
		/// now we map all info to each country and category
		for (int i = 0; i < countyCategKey.length; i++) {
			context.write(new Text(countyCategKey[i].trim()), videoOccurenceInfo);
		}
	}
}
