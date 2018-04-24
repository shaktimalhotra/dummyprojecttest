package assignment1.correlation;

import org.apache.spark.sql.api.java.UDF2;

public class CalculatePercentIncrease implements UDF2<Integer, Integer, Double> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Double call(Integer fdv, Integer sdv) throws Exception {
		double percentIncrease = (((sdv - fdv) / fdv.doubleValue())) * 100;// we will be rounding up percentage two
																			// decimal places
		double x = Math.round(percentIncrease * 100);
		double percentIncreaseScaled = x / 100.0;
		return percentIncreaseScaled;
	}
}
