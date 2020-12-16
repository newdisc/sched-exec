package nd.sched.job.vertx;

import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.StaticHandler;

public class VertxVentricleHttp extends AbstractVerticle {
	private static final Logger logger = LoggerFactory.getLogger(VertxVentricleHttp.class);
	private Router router;
	private HttpServer httpserver;
	private Map<String, HandlerBase> handlers = new TreeMap<>();
	
	@Override
	public void start(Promise<Void> prm) {
		final VertxOptions options = new VertxOptions();
		options.setBlockedThreadCheckInterval(1000L*60*60);	
		vertx = Vertx.vertx(options);
		router = Router.router(getVertx());
		registerRoutes();
		createHttpServer(prm);
		logger.debug("Vertx Server up");
	}
	
	public void createHttpServer(Promise<Void> prm) {
		httpserver = vertx.createHttpServer().requestHandler(router).listen(
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
		//router.route().handler(LoggerHandler.create());
		final StaticHandler sh = StaticHandler.create("./logs").setDirectoryListing(true);
		router.route("/api/logs/*").handler(sh);
		router.route("/api/shutdown").handler(rc -> 
			vertx.setTimer(1000, tid -> {
				logger.info("Router Close");
				router.clear();
				logger.info("HttpServer Close");
				httpserver.close();
				logger.info("Vertx Close");
				vertx.close();
			})
		);
		handlers.entrySet().forEach(entry -> {
			final HandlerBase handler = entry.getValue();
			final Router handlerRouter = Router.router(vertx);
			handler.setRouter(handlerRouter);
			router.mountSubRouter(entry.getKey(), handlerRouter);
		});
		router.route("/ui/*").handler(rc -> rc.reroute("/index.html"));
		router.route().handler(StaticHandler.create("public"));
	}
	public VertxVentricleHttp addHandler(final String path, final HandlerBase handler) {
		handlers.put(path, handler);
		return this;
	}
	public Router getRouter() {
		return router;
	}
}
