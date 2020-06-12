package basen;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.TimeUnit.SECONDS;

@SpringBootApplication
public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    @Autowired
    private SpringVerticleFactory verticleFactory;

    /**
     * The Vert.x worker pool size, configured in the {@code application.properties} file.
     * <p>
     * Make sure this is greater than {@link #springWorkerInstances}.
     */
    @Value("${vertx.worker.pool.size}")
    int workerPoolSize;

    /**
     * The number of {@link SpringWorker} instances to deploy, configured in the {@code application.properties} file.
     */
    @Value("${vertx.springWorker.instances}0")
    int springWorkerInstances;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Deploy verticles when the Spring application is ready.
     */
    @EventListener
    public void deployVerticles(ApplicationReadyEvent event) {
        Vertx vertx = Vertx.vertx(new VertxOptions().setWorkerPoolSize(workerPoolSize));

        // The verticle factory is registered manually because it is created by the Spring container
        vertx.registerVerticleFactory(verticleFactory);

        CountDownLatch deployLatch = new CountDownLatch(2);
        AtomicBoolean failed = new AtomicBoolean(false);

        String restApiVerticleName = verticleFactory.prefix() + ":" + DefaultEntityRestApi.class.getName();
        vertx.deployVerticle(restApiVerticleName, ar -> {
            if (ar.failed()) {
                LOG.error("Failed to deploy verticle", ar.cause());
                failed.compareAndSet(false, true);
            }
            deployLatch.countDown();
        });

        DeploymentOptions workerDeploymentOptions = new DeploymentOptions()
            .setWorker(true)
            // As worker verticles are never executed concurrently by Vert.x by more than one thread,
            // deploy multiple instances to avoid serializing requests.
            .setInstances(springWorkerInstances);
        String workerVerticleName = verticleFactory.prefix() + ":" + SpringWorker.class.getName();
        vertx.deployVerticle(workerVerticleName, workerDeploymentOptions, ar -> {
            if (ar.failed()) {
                LOG.error("Failed to deploy verticle", ar.cause());
                failed.compareAndSet(false, true);
            }
            deployLatch.countDown();
        });

        try {
            if (!deployLatch.await(5, SECONDS)) {
                throw new RuntimeException("Timeout waiting for verticle deployments");
            } else if (failed.get()) {
                throw new RuntimeException("Failure while deploying verticles");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
