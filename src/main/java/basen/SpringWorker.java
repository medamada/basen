package basen;

import basen.service.EntityAsyncService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.serviceproxy.ServiceBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * A worker verticle, exposing the {@link EntityAsyncService} over the event bus.
 * <p>
 * Since it is a worker verticle, it is perfectly fine for the registered service to delegate calls to backend Spring beans.
 */
@Component
// Prototype scope is needed as multiple instances of this verticle will be deployed
@Scope(SCOPE_PROTOTYPE)
public class SpringWorker extends AbstractVerticle {

    @Autowired
    private EntityAsyncService entityAsyncService;

    @Override
    public void start() {
        new ServiceBinder(vertx).setAddress(EntityAsyncService.ADDRESS)
                                .register(EntityAsyncService.class, entityAsyncService);
    }
}
