package mapreduce;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class J2AbstractReducerCombiner  extends Reducer<Text, Text, Text, Text>  {

	@Override
	protected void reduce(Text keyvalue, Iterable<Text> infoItr, Reducer<Text, Text, Text, Text>.Context context)
			throws IOException, InterruptedException {
		
		// we have for each country category complete information
		// US:MUSIC,   []
		
		String countryCatKey  = keyvalue.toString().replaceAll("~", ":");
		String keyCountry=countryCatKey.split(":")[0];
		Map<String, CountyCounter> map = new HashMap<>(); 
		//Reducer2OutputUtil calulate = new Reducer2OutputUtil(countryCatKey.toString().trim());
		for (Text info : infoItr) {

			StringTokenizer st = new StringTokenizer(
					info.toString().split("\t")[1].replaceAll("\\[", "").replaceAll("\\]", ""), ",");

			while (st.hasMoreTokens()) {
				String countryCode = st.nextToken().trim().split("~")[0];
				try {
					CountyCounter writable = map.get(countryCode);
					writable.increment();
				} catch (NullPointerException exception) {
					CountyCounter counter = new CountyCounter(countryCode);
					counter.increment();
					map.put(countryCode, counter);
				}
			}

		}
		
		// now we have all info for the counts 
		CountyCounter counter = map.get(keyCountry); 
		long totalCount=counter.getCounter();
		String out = countryCatKey+"; Total="+totalCount+";"; 
		StringBuffer sb = new StringBuffer(out);
		for (CountyCounter cc : map.values()) {
			double percent = cc.getCounter() * 100 / totalCount;
			//fixing decimla place 
			percent = Math.round(percent*100)/100.0;
			sb.append(percent).append(" % in ").append(cc.getCountry()).append(";  ");
		} 
		context.write(keyvalue, new Text(sb.toString()));
	}
}
