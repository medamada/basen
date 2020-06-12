package basen.entity;

import java.util.Map;
import java.util.Set;

public interface IEntity {

    // Returns a unique identifier
    Long getId();

    // Returns the sub-entities of this entity
    Set getSubEntities();

    // Returns a set of key-value data belonging to this entity
    Map getData();

}
