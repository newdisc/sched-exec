package nd.sched.job.service;

import java.util.function.UnaryOperator;

import nd.data.util.IConfigurable;
import nd.sched.job.JobReturn;

public interface IJobExecutorService extends IConfigurable {
	public void initiateExecute(final String triggerName, final String jobName, 
			final String arguments, final UnaryOperator<JobReturn> callBack);
	public JobReturn execute(final String triggerName, final String jobName, 
			final String arguments);
}
