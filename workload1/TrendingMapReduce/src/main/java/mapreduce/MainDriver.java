package mapreduce;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import java.io.IOException; 
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.jobcontrol.ControlledJob;
import org.apache.hadoop.mapreduce.lib.jobcontrol.JobControl;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * 
 * @author Shakti Malhotra
 *
 */

public class MainDriver {

	private static String input = "/user/shaktimalhotra/ALLVideos.csv";
	private static String output = "user/shaktimalhotra/output";
	private static final String output1Path = "/user/shaktimalhotra/data2";

	private static Job createJob1() throws IOException {
		Configuration config = new Configuration();
		Job job = Job.getInstance(config, "J1");

		job.setJarByClass(MainDriver.class);
		job.setMapperClass(J1Map.class);
		job.setMapOutputValueClass(Text.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		job.setReducerClass(J1Reducer.class);
		return job;

	}

	private static Job createJob2() throws IOException {
		Configuration config2 = new Configuration();
		Job job = Job.getInstance(config2, "J2");
		job.setJarByClass(MainDriver.class);
		job.setMapperClass(J2Map.class);
		job.setReducerClass(J2AbstractReducerCombiner.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		return job;
	}

	public static void main(String[] args) throws Exception {

		if (args.length > 1) {// set input & output path
			input = args[0];
			output = args[1];
		}

		Job j1 = createJob1();
		// set paths
		FileInputFormat.addInputPath(j1, new Path(input));
		FileOutputFormat.setOutputPath(j1, new Path(output1Path));

		Job j2 = createJob2();
		// set paths
		FileInputFormat.addInputPath(j2, new Path(output1Path));
		FileOutputFormat.setOutputPath(j2, new Path(output));

		// setup chaining
		ControlledJob cj1 = new ControlledJob(j1.getConfiguration());
		cj1.setJob(j1);

		ControlledJob cj2 = new ControlledJob(j2.getConfiguration());
		cj2.setJob(j2);

		JobControl cyclecontroller = new JobControl("JOB_CONTROL");
		cyclecontroller.addJob(cj1);
		cyclecontroller.addJob(cj2);
		cj2.addDependingJob(cj1);

		Thread cyclethread = new Thread(new CycleRunnerclass(cyclecontroller));

		// execute jobs
		cyclethread.start();

		if (!cyclecontroller.allFinished()) {
			Thread.sleep(5000);
		}

		cyclecontroller.stop();

	}
}
