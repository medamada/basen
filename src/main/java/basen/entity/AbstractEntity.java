package basen.entity;

import java.util.Map;
import java.util.Set;

public abstract class AbstractEntity implements IEntity {

    @Override
    public abstract Long getId();

    @Override
    public abstract Set getSubEntities();

    @Override
    public abstract Map getData();
}
