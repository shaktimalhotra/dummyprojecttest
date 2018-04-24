package mapreduce;

import org.apache.hadoop.mapreduce.lib.jobcontrol.JobControl;

public class CycleRunnerclass implements Runnable {
	private JobControl stopper;

	public CycleRunnerclass(JobControl _stopper) {
		this.stopper = _stopper;
	}

	public void run() {
		this.stopper.run();
	}
}