package nd.sched.job.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import nd.sched.job.IJobExecutor.JobReturn;

public interface ILogJobExecutorService extends IExecutorService {
	public CompletableFuture<JobReturn> initiateExecute(final String triggerName, final String jobName, 
			final String arguments);
	public JobReturn logAndExecute(final String triggerName, final String jobName, final String arguments);
	public void setJavaExecutor(ExecutorService javaExecutor);
}
