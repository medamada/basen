package basen.service;

import basen.entity.DefaultEntity;
import basen.repository.EntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class EntityService {

    @Autowired
    private EntityRepository entityRepository;

    public DefaultEntity save(DefaultEntity defaultEntity) {
        return entityRepository.save(defaultEntity);
    }

    public List<DefaultEntity> getAll() {
        Iterable<DefaultEntity> all = entityRepository.findAll();
        return StreamSupport.stream(all.spliterator(), false).collect(toList());
    }

    public DefaultEntity findById(Long id) {
        Optional<DefaultEntity> found = entityRepository.findById(id);
        if (found.isPresent()) {
            return found.get();
        } else {
            return null;
        }
    }

    public DefaultEntity findRoot() {
        Iterable<DefaultEntity> iterable = entityRepository.findAll();

        //only one entity should have no parent. that means it is the root entity
        Optional<DefaultEntity> root =
            StreamSupport.stream(iterable.spliterator(), false)
                         .filter(entity -> entity.getParentId() == null)
                         .findFirst();

        if (root.isPresent()) {
            return root.get();
        } else {
            return null;
        }
    }
}
