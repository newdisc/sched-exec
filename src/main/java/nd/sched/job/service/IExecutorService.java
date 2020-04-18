package nd.sched.job.service;

import nd.sched.job.IJobExecutor.JobReturn;

public interface IExecutorService {
    public JobReturn execute(final String jobName, final String arguments);
}
