package nd.sched.job.vertx;

import io.vertx.core.Handler;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public abstract class HandlerBase implements Handler<RoutingContext> {
	public abstract HandlerBase setRouter(Router router);
}
