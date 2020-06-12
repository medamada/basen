package basen.util;

import basen.entity.DefaultEntity;
import basen.repository.EntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class DBSetup implements CommandLineRunner {

    @Autowired
    private EntityRepository entityRepository;

    @Override
    public void run(String... arg0) throws Exception {

        Map<String, String> parentData = new HashMap<>();
        parentData.put("parentKey", "parentValue");
        Map<String, String> childData = new HashMap<>();
        childData.put("childKey", "childValue");
        Set<DefaultEntity> subEntities = new HashSet();

        DefaultEntity parentEntity = new DefaultEntity();
        DefaultEntity childEntity = new DefaultEntity();

        parentEntity.setData(parentData);
        childEntity.setData(childData);

        subEntities.add(childEntity);
        parentEntity.setSubEntities(subEntities);
        childEntity.setParentId(Long.valueOf(1));

        this.entityRepository.save(parentEntity);
        this.entityRepository.save(childEntity);
    }

}
