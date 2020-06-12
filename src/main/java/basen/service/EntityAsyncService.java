package basen.service;

import basen.entity.DefaultEntity;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import java.util.List;

@ProxyGen
public interface EntityAsyncService {

    /**
     * The service address on the Vert.x event bus.
     */
    String ADDRESS = EntityAsyncService.class.getName();

    /**
     * Adds a new entity in the database
     *
     * @param defaultEntity the entity to add
     * @param resultHandler the result to be returned
     */
    void addEntity(DefaultEntity defaultEntity, Handler<AsyncResult<DefaultEntity>> resultHandler);

    /**
     * Gets all entities from the database
     *
     * @param resultHandler the result to be returned
     */
    void getAllEntities(Handler<AsyncResult<List<DefaultEntity>>> resultHandler);

    /**
     * Gets an entity from the database by its id
     *
     * @param id            the id of the entity to be returned
     * @param resultHandler the result to be returned
     */
    void getEntity(Long id, Handler<AsyncResult<DefaultEntity>> resultHandler);

}
