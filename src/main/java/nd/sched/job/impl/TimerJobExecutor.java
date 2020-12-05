package nd.sched.job.impl;

import java.util.function.UnaryOperator;

import nd.sched.job.BaseJobExecutor;
import nd.sched.job.JobReturn;
import nd.sched.job.JobReturn.JobStatus;

public class TimerJobExecutor extends BaseJobExecutor {
	private String cronCondition;
	private String timeZone;
	@Override
    public void executeAsync(String argumentString, UnaryOperator<JobReturn> callBack) {
    	super.executeAsync(argumentString, callBack);
		final JobReturn jr = new JobReturn();
		jr.setReturnValue("Asynchronous call");
		jr.setJobStatus(JobStatus.RUNNING);
        callBack.apply(jr);
	}
	public String getCronCondition() {
		return cronCondition;
	}
	public TimerJobExecutor setCronCondition(String cronCondition) {
		this.cronCondition = cronCondition;
		return this;
	}
	public String getTimeZone() {
		return timeZone;
	}
	public TimerJobExecutor setTimeZone(String timeZone) {
		this.timeZone = timeZone;
		return this;
	}
}
