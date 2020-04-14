package nd.sched.job.factory;

import nd.sched.job.IJobExecutor;

public interface IJobFactory {
    IJobExecutor getJobExecutor(final String name);
    boolean registerJobExecutor(final String name, IJobExecutor executor);
    void printJobsRegistered();
}