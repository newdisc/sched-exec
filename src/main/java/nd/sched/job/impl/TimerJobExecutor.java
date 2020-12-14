package nd.sched.job.impl;

import java.util.function.UnaryOperator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nd.sched.job.BaseJobExecutor;
import nd.sched.job.JobReturn;
import nd.sched.job.JobReturn.JobStatus;
import nd.sched.job.service.QuartzService;

public class TimerJobExecutor extends BaseJobExecutor {
	private static final Logger logger = LoggerFactory.getLogger(TimerJobExecutor.class);
	public static final String TYPE = "CronJob";
	private String cronCondition;
	private String timeZone;
	private QuartzService quartzService;
	private boolean stopped = false;
	@Override
    public void executeAsync(String argumentString, UnaryOperator<JobReturn> callBack) {
    	super.executeAsync(argumentString, callBack);
    	final String nextJobTime = quartzService.addJob(this);
		writeSafe(nextJobTime.getBytes());
		final JobReturn jr = new JobReturn();
		jr.setReturnValue("Asynchronous call: " + nextJobTime);
		jr.setJobStatus(JobStatus.RUNNING);
		logger.info("Next Run time of the job is: {}", nextJobTime);
        callBack.apply(jr);
	}
	@Override
	public void stop() {
		super.stop();
		stopped = true;
		quartzService.stop(this);
		logger.info("Timer should have stopped");
		quartzService.list();
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
	public TimerJobExecutor setQuartzService(QuartzService quartzService) {
		this.quartzService = quartzService;
		return this;
	}
	public boolean isStopped() {
		return stopped;
	}
}
