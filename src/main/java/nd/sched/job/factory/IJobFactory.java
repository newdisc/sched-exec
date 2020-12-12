package nd.sched.job.factory;

import java.util.List;

import nd.sched.job.BaseJobExecutor;

public interface IJobFactory {
    BaseJobExecutor getJobExecutor(final String name);
    boolean registerJobExecutor(final String name, BaseJobExecutor executor);
    void printJobsRegistered();
    public List<BaseJobExecutor> list();
    public BaseJobExecutor get(final String name);
    public List<String> getLogs(final String pattern);
}