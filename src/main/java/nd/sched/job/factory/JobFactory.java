package nd.sched.job.factory;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nd.sched.job.IJobExecutor;

public class JobFactory implements IJobFactory {
    private static final Logger logger = LoggerFactory.getLogger(JobFactory.class);
    private ConcurrentHashMap<String, IJobExecutor> registry = new ConcurrentHashMap<>();

    @Override
    public IJobExecutor getJobExecutor(String name) {
        return registry.get(name);
    }
    @Override
    public boolean registerJobExecutor(String name, IJobExecutor executor) {
        IJobExecutor jold = registry.putIfAbsent(name, executor);
        if (null == jold) {
            return true;
        }
        logger.error("Job with name {} already registered", name);
        return false;
    }
    @Override
    public void printJobsRegistered() {
        logger.info("Registered Jobs: {}", registry);
    }
}