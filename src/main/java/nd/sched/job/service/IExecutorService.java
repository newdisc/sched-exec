package nd.sched.job.service;

import nd.sched.job.IJobExecutor.JobReturn;
import nd.sched.job.factory.IJobFactory;

public interface IExecutorService {
    public JobReturn execute(final String jobName, final String arguments);
    public IJobFactory getJobFactory();
}
