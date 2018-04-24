package assignment1.correlation;
 
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.expressions.Window;
import org.apache.spark.sql.expressions.WindowSpec;
import org.apache.spark.sql.types.DataTypes;

public class Driver {
	protected static final String COMMA_DELIMITER = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";// regular expression to select ,
	// as delimiter but not ".. ,..
	// "
	private static String inputPath = "in/ALLvideos.csv", outputPath = "output", master = "local[*]";
	
	private static Logger log = Logger.getLogger(Driver.class);

	public static void main(String[] args) throws Exception {

		Logger.getLogger("org").setLevel(Level.ERROR);// reduce noise from spark
		Logger.getLogger("org").setLevel(Level.ERROR);

		if (args.length == 3) {
			inputPath = args[0];
			outputPath = args[1];
			master = args[2];

		} else {
			log.info("using default parameters");
		}

		log.info("Input Path:" + inputPath + " output:" + outputPath + "    master=" + master);

		// Spark Context Initializination
		SparkConf conf = new SparkConf().setAppName("A1 task2").setMaster(master);

		SparkSession spark = SparkSession.builder().config(conf).enableHiveSupport().getOrCreate();
		spark.udf().register("calculatePercentIncrease", new CalculatePercentIncrease(), DataTypes.DoubleType);

		Dataset<Row> ds = spark.read().format("csv").option("delimiter", ",").option("header", "true")
				.option("inferSchema", "true").load(inputPath).select("video_id", "country", "views", "trending_date");

		Column td = ds.col("trending_date");
		ds = ds.withColumn("trending_date_", functions.to_date(td, "yy.dd.MM").cast(DataTypes.DateType)).drop(td);
		ds = ds.withColumnRenamed("trending_date_", "trending_date");

		// ds.printSchema();
		// ds.show();

		// System.out.println("*******************************************");
		int exec = conf.getInt("spark.executor.num", 2);
		int cores = conf.getInt("spark.executor.cores", 2);

		ds.repartition(exec * cores * 20, ds.col("video_id"));
		ds.createTempView("AllVideos");
		// ds.show();
		// ds.take(1);// to trigger partioning

		Dataset tmp = spark.sql(
				"Select * from (SELECT video_id,trending_date,country,views, rank() OVER (partition by video_id,country order by trending_date ASC) as rank from AllVideos) tmp where rank<=2");
		tmp.cache();
		tmp.createTempView("t1");
		tmp.createTempView("t2");
		//tmp.show();

		// SQl to get first 2 day data
		Dataset first2DaysIncreaseDS = spark.sql(
				"Select t1.video_id,calculatePercentIncrease(t1.views,t2.views) as percent_increase,t1.country from t1 JOIN t2 ON (t1.video_id=t2.video_id AND t1.rank=1 AND t2.rank=2 AND t1.country=t2.country)");
		first2DaysIncreaseDS.cache();
		Dataset result = first2DaysIncreaseDS.filter("percent_increase>=1000");
		
		result.show();
	    log.info("Saving output to output folder");
	    result.select("country","video_id","percent_increase").orderBy("country","percent_increase").coalesce(1).write().format("com.databricks.spark.csv").option("header", "true").save(outputPath);

		spark.stop();

	}

}
