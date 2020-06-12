package basen.entity;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import lombok.Builder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;

@Entity
@DataObject(generateConverter = true)
public class DefaultEntity extends AbstractEntity {

    @Id
    @GeneratedValue
    private Long id;

    private Long parentId = null;

    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name = "key")
    private Map<String, String> data = new HashMap<>();

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Set<DefaultEntity> subEntities = new HashSet<>();

    public DefaultEntity() {
    }

    public DefaultEntity(Long parentId, Set<DefaultEntity> subEntities, Map<String, String> data) {
        super();
        this.parentId = parentId;
        this.subEntities = subEntities;
        this.data = data;
    }

    public DefaultEntity(JsonObject jsonObject) {
        DefaultEntityConverter.fromJson(jsonObject, this);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        DefaultEntityConverter.toJson(this, json);
        return json;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Set<DefaultEntity> getSubEntities() {
        return subEntities;
    }

    public void setSubEntities(Set<DefaultEntity> subEntities) {
        this.subEntities = subEntities;
    }

    @Override
    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
}
