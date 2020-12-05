package nd.sched.job.vertx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;
import nd.sched.job.factory.IJobFactory;
import nd.sched.job.factory.IJobRegistryPopulator;
import nd.sched.job.service.ILogJobExecutorService;

public class JobExecutorVertx extends AbstractVerticle {
	private static final Logger logger = LoggerFactory.getLogger(JobExecutorVertx.class);
	private static final String CONTENT_TYPE = "content-type";
	private static final String JSON_UTF8 = "application/json; charset=utf-8";
	private ILogJobExecutorService executorService;
	private IJobFactory jobFactory;
	private IJobRegistryPopulator populator;
	private Router router;
	
	@Override
	public void start(Promise<Void> prm) {
		router = Router.router(getVertx());
		registerRoutes();
		createHttpServer(prm);
	}
	
	public void createHttpServer(Promise<Void> prm) {
		vertx.createHttpServer().requestHandler(router).listen(
				// Retrieve the port from the configuration,
				// default to 8080.
				config().getInteger("http.port", 8080),
				result -> {
					if (result.succeeded()) {
						prm.complete();
					} else {
						prm.fail(result.cause());
					}
				}
			);
	}

	public void registerRoutes() {
		router.route("/api/job/list").handler(this::list);
		router.route("/api/job/details").handler(this::details);
		router.route("/api/job/execute").handler(this::execute);
		router.route("/api/job/load").handler(this::load);
		router.route("/api/job/shutdown").handler(rc -> vertx.close());
		router.route("/api/job/logs").handler(this::logs);
		router.route("/api/log/*").handler(StaticHandler.create("./logs"));
	}
	
	public void execute(final RoutingContext rc) {
		final String job = rc.request().getParam("jobName");
		final String arguments = rc.request().getParam("args");
		logger.info("Executing: {}, {}", job, arguments);
		rc.response()
	      .putHeader(CONTENT_TYPE, JSON_UTF8)
	      .end(Json.encodePrettily(executorService.logAndExecute("Web" + job, job, arguments)));
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

	public void setExecutorService(ILogJobExecutorService executorService) {
		this.executorService = executorService;
	}
	public void setJobFactory(IJobFactory jobFactory) {
		this.jobFactory = jobFactory;
	}
	public void setPopulator(IJobRegistryPopulator jrp) {
		populator = jrp;
	}
}
