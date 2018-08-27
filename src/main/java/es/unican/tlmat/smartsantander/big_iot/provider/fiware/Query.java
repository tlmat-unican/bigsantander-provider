package es.unican.tlmat.smartsantander.big_iot.provider.fiware;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class Query {
  @JsonInclude(Include.NON_NULL)
  public static class Entity {
    @JsonProperty("idPattern")
    public String idPattern;
    @JsonProperty("type")
    public String type;

    @Override
    public boolean equals(Object obj) {
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      if (!super.equals(obj))
        return false;
      else {
        Entity other = (Entity) obj;
        if ((this.idPattern == null) ? (other.idPattern != null)
            : !idPattern.equals(other.idPattern)) {
          return false;
        }
        if ((this.type == null) ? (other.type != null)
            : !type.equals(other.type)) {
          return false;
        }
        return true;
      }
    }
  }

  @JsonProperty("entities")
  public Set<Entity> entities;

  @JsonProperty("attrs")
  public Set<String> attrs;

  @JsonInclude(Include.NON_NULL)
  public static class Expression {
    @JsonProperty("q")
    public String q;
    @JsonProperty("mq")
    public String mq;
    @JsonProperty("georel")
    public String georel;
    @JsonProperty("coords")
    public String coords;
    @JsonProperty("geometry")
    public String geometry;
  }

  @JsonProperty("expression")
  Expression expression;

  public Query() {
  }

  public Query(final Entity e) {
    this.addEntity(e);
  }

  public Query(final List<Entity> e) {
    this.addEntities(e);
  }

  private void checkAndCreateEntities() {
    if (entities == null) {
      entities = Collections.synchronizedSet(new HashSet<>());
    }
  }

  public void addEntity(Entity e) {
    checkAndCreateEntities();
    entities.add(e);
  }

  public void addEntities(Collection<Entity> e) {
    checkAndCreateEntities();
    entities.addAll(e);
  }

  private void checkAndCreateAttributes() {
    if (attrs == null) {
      attrs = Collections.synchronizedSet(new HashSet<>());
    }
  }

  public void addAttribute(String attribute) {
    checkAndCreateAttributes();
    attrs.add(attribute);
  }

  public void addAttributes(Collection<String> attributes) {
    checkAndCreateAttributes();
    attrs.addAll(attributes);
  }

  private void checkAndCreateExpression() {
    if (expression == null) {
      expression = new Expression();
    }
  }

  public void withinAreaFilter(double latitude, double longitude, int radius) {
    checkAndCreateExpression();
    expression.georel = String.format("near;maxDistance:%d", radius);
    expression.geometry = "point";
    expression.coords = String.format("%f,%f", latitude, longitude);
  }

  public void addAttributeFilter(String filter) {
    checkAndCreateExpression();
    expression.q = (expression.q == null) ? filter : expression.q.concat(";").concat(filter);

  }

  public void addMetadataFilter(String filter) {
    checkAndCreateExpression();
    expression.q = (expression.q == null) ? filter : expression.q.concat(";").concat(filter);
  }

}
