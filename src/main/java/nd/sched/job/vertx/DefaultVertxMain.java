package nd.sched.job.vertx;

import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import nd.sched.job.factory.IJobFactory;
import nd.sched.job.factory.IJobRegistryPopulator;
import nd.sched.job.factory.JobFactory;
import nd.sched.job.factory.JobRegistryPopulator;
import nd.sched.job.service.ExecutorService;

public class DefaultVertxMain {
	private static final Logger logger = LoggerFactory.getLogger(DefaultVertxMain.class);
	public static void main(String[] args) throws Exception{
		// Create the pieces needed and link together - can use spring instead
		logger.info("Creating default Vertx");
		final IJobFactory jf = new JobFactory();
		final ExecutorService je = new ExecutorService();
		je.setJobFactory(jf);
		final IJobRegistryPopulator jrp = new JobRegistryPopulator().setFactory(jf);
		final Properties props = new Properties();
		jrp.setConfiguration(props);
		jrp.registerJobs();
		
		final JobExecutorVertx jev = new JobExecutorVertx();
		jev.setExecutorService(je);
		jev.setJobFactory(jf);
		jev.setPopulator(jrp);

		final BlockingQueue<AsyncResult<String>> q = new ArrayBlockingQueue<>(1);
		Vertx.vertx().deployVerticle(jev, q::offer);
		final AsyncResult<String> result = q.take();
		if (result.failed()) {
		    throw new RuntimeException(result.cause());
		}
	}
}
