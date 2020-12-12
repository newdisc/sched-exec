package nd.sched.job.vertx;

import io.vertx.core.Handler;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public interface HandlerBase extends Handler<RoutingContext> {
	public HandlerBase setRouter(Router router);
}
