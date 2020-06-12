package basen.repository;

import basen.entity.DefaultEntity;
import org.springframework.data.repository.CrudRepository;

public interface EntityRepository extends CrudRepository<DefaultEntity, Long> {

}
