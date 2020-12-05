package nd.sched.job;

import java.util.function.Function;

public class TimerJobExecutor implements IJobExecutor, IAsyncJobExecutor {
	private String cronCondition;
	private String timeZone;
	private Function<JobReturn, JobReturn> callBack;
	private String name;
	@Override
	public String getName() {
		return name;
	}
	@Override
	public JobReturn execute(String argumentString) {
		final JobReturn jr = new JobReturn();
		jr.setReturnValue("Asynchronous call");
		jr.setJobStatus(JobStatus.RUNNING);
		return jr;
	}
	@Override
	public void executeAsync(String argumentString, Function<JobReturn, JobReturn> callBack) {
		this.callBack = callBack;
	}
	@Override
	public Function<JobReturn, JobReturn> getCallback() {
		return callBack;
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
	public TimerJobExecutor setName(String name) {
		this.name = name;
		return this;
	}
}
