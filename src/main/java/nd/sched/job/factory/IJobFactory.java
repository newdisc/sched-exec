package nd.sched.job.factory;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import nd.sched.job.IJobExecutor;

public interface IJobFactory {
    IJobExecutor getJobExecutor(final String name);
    boolean registerJobExecutor(final String name, IJobExecutor executor);
    void printJobsRegistered();
    public Collection<IJobExecutor> list();
    public IJobExecutor get(final String name);
    public List<String> getLogs(final String pattern);
    public InputStream getLog(final String logName); 
}