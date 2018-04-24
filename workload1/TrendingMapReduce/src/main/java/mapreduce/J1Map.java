package mapreduce;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class J1Map extends Mapper<LongWritable, Text, Text, Text> {
	public static final String REG_EXP = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";

	@Override
	protected void map(LongWritable keyvalue, Text line, Mapper<LongWritable, Text, Text, Text>.Context context)
			throws IOException, InterruptedException {
		String[] input = line.toString().split(REG_EXP);
		Text vid = new Text(input[0]);
		Text countryCatInfo = new Text(input[17].toUpperCase()+ "~"+ input[5].trim() );
		context.write(vid, countryCatInfo);
	}
}