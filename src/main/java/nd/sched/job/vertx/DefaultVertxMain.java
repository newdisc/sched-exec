package nd.sched.job.vertx;

import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import nd.sched.job.factory.JobFactory;
import nd.sched.job.factory.JobRegistryPopulator;
import nd.sched.job.service.JobExecutorService;
import nd.sched.util.UtilException;

public class DefaultVertxMain {
	static {
		System.setProperty("logback.configurationFile", "./logback.xml");
		System.setProperty("vertx.options.maxEventLoopExecuteTime", "10000000000");
	}
	private static final Logger logger = LoggerFactory.getLogger(DefaultVertxMain.class);
	public static void main(String[] args) throws Exception{
		// Create the pieces needed and link together - can use spring instead
		logger.info("Creating default Vertx");
		final JobExecutorController jec = initJobController();
		final VertxVentricleHttp jev = new VertxVentricleHttp();
		jev.addHandler("/api/job/", jec);

		runVertx(jev);
	}
	public static void runVertx(final VertxVentricleHttp jev) throws InterruptedException {
		final BlockingQueue<AsyncResult<String>> q = new ArrayBlockingQueue<>(1);
		Vertx.vertx().deployVerticle(jev, q::offer);
		final AsyncResult<String> result = q.take();
		if (result.failed()) {
		    throw new UtilException(result.cause());
		}
	}
	public static JobExecutorController initJobController() {
		final Properties props = new Properties();
		final JobFactory jf = new JobFactory();
		jf.setConfig(props);
		final JobExecutorService je = new JobExecutorService();
		je.setConfig(props);
		je.setJobFactory(jf);
		final JobRegistryPopulator jrp = new JobRegistryPopulator().setFactory(jf);
		jrp.setConfiguration(props);
		jrp.registerJobs();
		final JobExecutorController jec = new JobExecutorController();
		jec.setExecutorService(je);
		jec.setJobFactory(jf);
		jec.setPopulator(jrp);
		return jec;
	}
}
