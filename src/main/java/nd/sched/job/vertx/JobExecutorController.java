package nd.sched.job.vertx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import nd.sched.job.JobReturn;
import nd.sched.job.factory.IJobFactory;
import nd.sched.job.factory.JobRegistryPopulator;
import nd.sched.job.service.IJobExecutorService;

public class JobExecutorController implements HandlerBase {
	private static final Logger logger = LoggerFactory.getLogger(JobExecutorController.class);
	private static final String CONTENT_TYPE = "content-type";
	private static final String JSON_UTF8 = "application/json; charset=utf-8";
	private IJobExecutorService executorService;
	private IJobFactory jobFactory;
	private JobRegistryPopulator populator;
	private Router router;

	@Override
	public void handle(RoutingContext event) {
		router.handleContext(event);
	}
	
	public void execute(final RoutingContext rc) {
		final String job = rc.request().getParam("jobName");
		final String arguments = rc.request().getParam("args");
		logger.info("Executing: {}, {}", job, arguments);
		final JobReturn jr = executorService.execute("Web" + job, job, arguments);
		rc.response()
	      .putHeader(CONTENT_TYPE, JSON_UTF8)
	      .end(Json.encodePrettily(jr));
	}
	public void details(final RoutingContext rc){
		final String name = rc.request().getParam("jobName");
		logger.info("Details: {}", name);
		rc.response()
	      .putHeader(CONTENT_TYPE, JSON_UTF8)
	      .end(Json.encodePrettily(jobFactory.get(name)));
	}
	public void list(final RoutingContext rc){
		logger.info("Job List: ");
		rc.response()
	      .putHeader(CONTENT_TYPE, JSON_UTF8)
	      .end(Json.encodePrettily(jobFactory.list()));
	}
	public void logs(final RoutingContext rc){
		final String pattern = rc.request().getParam("pattern");
		logger.info("Logs List: {}", pattern);
		rc.response()
	      .putHeader(CONTENT_TYPE, JSON_UTF8)
	      .end(Json.encodePrettily(jobFactory.getLogs(pattern)));
	}
	public void load(final RoutingContext rc){
		logger.info("Job Load: ");
		populator.registerJobs();
		rc.response()
	      .putHeader(CONTENT_TYPE, JSON_UTF8)
	      .end("{\"return\": \"SUCCESS\"}");
	}

	public void setExecutorService(IJobExecutorService executorService) {
		this.executorService = executorService;
	}
	public void setJobFactory(IJobFactory jobFactory) {
		this.jobFactory = jobFactory;
	}
	public void setPopulator(JobRegistryPopulator jrp) {
		populator = jrp;
	}
	public JobExecutorController setRouter(Router router) {
		this.router = router;
		router.get("/list").handler(this::list);
		router.get("/details").handler(this::details);
		router.get("/execute").handler(this::execute);
		router.get("/load").handler(this::load);
		router.get("/logs").handler(this::logs);
		return this;
	}

	public IJobExecutorService getExecutorService() {
		return executorService;
	}

	public Router getRouter() {
		return router;
	}
}
