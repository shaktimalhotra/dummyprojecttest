package mapreduce;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class J1Reducer extends Reducer<Text, Text, Text, Text> {

	
	@Override
	protected void reduce(Text vID, Iterable<Text> valueItr, Context context) throws IOException, InterruptedException { 
		List<String> countryCatlist = new ArrayList<String>();
		HashSet<String> countrySet = new HashSet<>();
	
		for (Text value : valueItr) {
			String countryCategoryInfo = value.toString();
			String county; 
				// unique values
				county = countryCategoryInfo.substring(0, 2);// should be like GB~MUSIC
				if (!countrySet.contains(county)) {
					countryCatlist.add(countryCategoryInfo);
					countrySet.add(county);
				};
			}
	  
			context.write(vID, new Text(countryCatlist.toString())); // write as (VID, [GB~MUSIC,USMUSIC])

	}
}
