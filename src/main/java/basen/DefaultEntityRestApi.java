package basen;

import basen.entity.DefaultEntity;
import basen.service.EntityAsyncService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.serviceproxy.ServiceProxyBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * A standard verticle, consuming the {@link basen.service.EntityAsyncService} over the event bus to expose a REST API.
 */
@Component
public class DefaultEntityRestApi extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultEntityRestApi.class);

    private EntityAsyncService entityAsyncService;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        entityAsyncService = new ServiceProxyBuilder(vertx).setAddress(EntityAsyncService.ADDRESS).build(EntityAsyncService.class);

        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.post("/entity").handler(this::add);
        router.get("/entities").handler(this::getAll);
        router.get("/entity").handler(this::get);

        StaticHandler staticHandler = StaticHandler.create();
        router.route().handler(staticHandler);
        vertx.createHttpServer().requestHandler(router).listen(8080, listen -> {
            if (listen.succeeded()) {
                LOG.info("EntityRestApi started");
                startFuture.complete();
            } else {
                startFuture.fail(listen.cause());
            }
        });
    }

    private void add(RoutingContext routingContext) {
        DefaultEntity entity = new DefaultEntity(routingContext.getBodyAsJson());
        entityAsyncService.addEntity(entity, ar -> {
            if (ar.succeeded()) {
                routingContext.response().setStatusCode(HTTP_CREATED).end();
            } else {
                routingContext.response().setStatusCode(HTTP_NOT_FOUND).end();
            }
        });
    }

    private void getAll(RoutingContext routingContext) {
        entityAsyncService.getAllEntities(ar -> {
            if (ar.succeeded()) {
                List<DefaultEntity> result = ar.result();
                JsonArray jsonArray = new JsonArray(result);
                routingContext.response().setStatusCode(HTTP_OK).end(jsonArray.encodePrettily());
            } else {
                routingContext.fail(ar.cause());
            }
        });
    }

    private void get(RoutingContext routingContext) {
        List<String> params = routingContext.queryParam("id");
        Long id = Long.parseLong(params.get(0));
        entityAsyncService.getEntity(id, ar -> {
            if (ar.succeeded()) {
                DefaultEntity result = ar.result();
                JsonObject json = result.toJson();
                routingContext.response().end(json.encodePrettily());
            } else {
                routingContext.response().setStatusCode(HTTP_NOT_FOUND).end();
            }
        });
    }
}
