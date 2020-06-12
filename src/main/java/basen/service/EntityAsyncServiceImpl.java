package basen.service;

import basen.entity.DefaultEntity;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class EntityAsyncServiceImpl implements EntityAsyncService {

    @Autowired
    private EntityService entityService;

    @Override
    public void addEntity(DefaultEntity entity, Handler<AsyncResult<DefaultEntity>> resultHandler) {
        Long parentId = entity.getParentId();

        if (parentId == null) {
            addAsRootEntity(entity, resultHandler);
        } else {
            addAsSubentity(entity, resultHandler, parentId);
        }

    }

    private void addAsSubentity(DefaultEntity entity, Handler<AsyncResult<DefaultEntity>> resultHandler, Long parentId) {
        DefaultEntity parent = entityService.findById(parentId);
        if (parent != null) {
            saveSubentity(entity, resultHandler, parent);
        } else {
            resultHandler.handle(Future.failedFuture("Parent id not found"));
        }
    }

    private void saveSubentity(DefaultEntity entity,
                               Handler<AsyncResult<DefaultEntity>> resultHandler,
                               DefaultEntity parent) {
        parent.getSubEntities().add(entity);
        DefaultEntity saved = entityService.save(entity);
        entityService.save(parent);
        resultHandler.handle(Future.succeededFuture(saved));
    }

    private void addAsRootEntity(DefaultEntity entity, Handler<AsyncResult<DefaultEntity>> resultHandler) {
        DefaultEntity oldRoot = entityService.findRoot();
        if (oldRoot != null) {
            saveRootEntity(entity, resultHandler, oldRoot);
        } else {
            //no root defined. means it is the first entity added
            DefaultEntity saved = entityService.save(entity);
            resultHandler.handle(Future.succeededFuture(saved));

        }
    }

    private void saveRootEntity(DefaultEntity entity,
                                Handler<AsyncResult<DefaultEntity>> resultHandler,
                                DefaultEntity oldRoot) {
        DefaultEntity saved = entityService.save(entity);
        Set<DefaultEntity> subentities = new HashSet<>();
        subentities.add(oldRoot);
        entity.setSubEntities(subentities);
        entityService.save(entity);

        oldRoot.setParentId(saved.getId());
        entityService.save(oldRoot);
        resultHandler.handle(Future.succeededFuture(saved));
    }

    @Override
    public void getAllEntities(Handler<AsyncResult<List<DefaultEntity>>> resultHandler) {
        List<DefaultEntity> all = entityService.getAll();
        resultHandler.handle(Future.succeededFuture(all));
    }

    @Override
    public void getEntity(Long id, Handler<AsyncResult<DefaultEntity>> resultHandler) {
        DefaultEntity found = entityService.findById(id);
        if (found != null) {
            resultHandler.handle(Future.succeededFuture(found));
        } else {
            resultHandler.handle(Future.failedFuture("Entity id not Found"));
        }
    }
}
