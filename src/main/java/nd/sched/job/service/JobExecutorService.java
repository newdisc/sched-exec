package nd.sched.job.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nd.sched.job.IJobExecutor;
import nd.sched.job.IJobExecutor.JobReturn;
import nd.sched.job.IJobExecutor.JobStatus;
import nd.sched.job.factory.IJobFactory;

public class JobExecutorService implements IExecutorService {
    private static final Logger logger = LoggerFactory.getLogger(JobExecutorService.class);
    private IJobFactory jobFactory;

    @Override
    public IJobFactory getJobFactory() {
        return jobFactory;
    }
    public JobExecutorService setJobFactory(IJobFactory jobFactory) {
        this.jobFactory = jobFactory;
        return this;
    }

    @Override
    public JobReturn execute(final String jobName, final String arguments) {
        final IJobExecutor job = jobFactory.getJobExecutor(jobName);
        if (null == job) {
        	logger.error("Job NOT found: {}", jobName);
            final JobReturn jr = new JobReturn();
            jr.setJobStatus(JobStatus.FAILURE);
            jr.setReturnValue("Job NOT found!");
            return jr;
        }
        return job.execute(arguments);
    }
}
