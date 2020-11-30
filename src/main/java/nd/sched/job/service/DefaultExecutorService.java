package nd.sched.job.service;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nd.sched.job.factory.IJobFactory;
import nd.sched.job.factory.IJobRegistryPopulator;
import nd.sched.job.factory.JobFactory;
import nd.sched.job.factory.JobRegistryPopulator;

public class DefaultExecutorService extends JobExecutorService {
	private static final Logger logger = LoggerFactory.getLogger(DefaultExecutorService.class);
	public DefaultExecutorService() {
		// Create the pieces needed and link together - can use spring instead
		logger.debug("Creating Default executorService");
		final IJobFactory jf = new JobFactory();
		setJobFactory(jf);
		final IJobRegistryPopulator jrp = new JobRegistryPopulator().setFactory(jf);
		final Properties props = new Properties();
		jrp.setConfiguration(props);
		jrp.registerJobs();
	}
}
