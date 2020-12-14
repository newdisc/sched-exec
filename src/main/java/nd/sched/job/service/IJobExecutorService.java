package nd.sched.job.service;

import java.util.function.UnaryOperator;

import nd.data.util.IConfigurable;
import nd.sched.job.BaseJobExecutor;
import nd.sched.job.JobReturn;

public interface IJobExecutorService extends IConfigurable {
	public void initiateExecute(final String triggerName, final String jobName, 
			final String arguments, final UnaryOperator<JobReturn> callBack);
	public JobReturn execute(final String triggerName, final String jobName, 
			final String arguments);
	public boolean stop(final String triggerName, final String jobName);
	public BaseJobExecutor getDetails(final String triggerName, final String jobName);
}
