package nd.sched.job.factory;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nd.data.util.IConfigurable;
import nd.sched.job.BaseJobExecutor;
import nd.sched.job.service.JobExecutorService;

public class JobFactory implements IJobFactory, IConfigurable, Closeable {
    private static final Logger logger = LoggerFactory.getLogger(JobFactory.class);
    private ConcurrentHashMap<String, BaseJobExecutor> registry = new ConcurrentHashMap<>();
    private Properties properties;

    @Override
    public BaseJobExecutor getJobExecutor(String name) {
        return registry.get(name);
    }
    @Override
    public boolean registerJobExecutor(String name, BaseJobExecutor executor) {
    	BaseJobExecutor jold = registry.putIfAbsent(name, executor);
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
    public Map<String, BaseJobExecutor> getRegistry(){
        return registry;
    }
	@Override
    public List<String> getLogs(final String job) {
		final String logDir = properties.getProperty(JobExecutorService.CFG_OUT_DIR, 
				JobExecutorService.DEFAULT_LOGDIR);
        final String jobpat = ".*" + job + ".*.[log\\|out]";
        try (final Stream<Path> logfiles = Files.walk(Paths.get(logDir));){
            return logfiles.filter(Files::isRegularFile)
                .map(f -> {
                		final Path fn = f.getFileName();
                		logger.info("Considering {}", f.toAbsolutePath());
                		return fn.toString();
                	})
                .filter(f -> f.matches(jobpat)).collect(Collectors.toList());
        } catch (IOException e) {
            final String msg = "Issue listing Job Log Files";
            logger.error(msg, e);
            return new ArrayList<String>();
        }
    }
	@Override
	public List<BaseJobExecutor> list() {
		return new ArrayList<BaseJobExecutor>(registry.values());
	}
	@Override
	public BaseJobExecutor get(final String name) {
		return registry.get(name);
	}
	@Override
	public IConfigurable setConfig(Properties props) {
		properties = props;
		return this;
	}
	@Override
	public void close() throws IOException {
		for (BaseJobExecutor baseJobExecutor : registry.values()) {
			baseJobExecutor.close();
		}
	}
}