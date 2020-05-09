package nd.sched.job.factory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
    public Map<String, IJobExecutor> getRegistry(){
        return registry;
    }
    public List<String> getJobLogs(final String job) {
        final String jobpat = "JobRun-.*" + job + ".*.log";
        try {
            return Files.walk(Paths.get("./logs")).filter(Files::isRegularFile)
                .map(f -> f.getFileName().toString())
                .filter(f -> f.matches(jobpat)).collect(Collectors.toList());
        } catch (IOException e) {
            final String msg = "Issue listing Job Log Files";
            logger.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }
}