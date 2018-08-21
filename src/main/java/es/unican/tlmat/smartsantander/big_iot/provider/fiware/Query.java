package es.unican.tlmat.smartsantander.big_iot.provider.fiware;

import java.util.ArrayList;
import java.util.List;

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
  }

  @JsonProperty("entities")
  public List<Entity> entities;

  @JsonProperty("attrs")
  public List<String> attrs;

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

  private Query() {
  }

  public Query(final Entity e) {
    this.addEntity(e);
  }

  public Query(final List<Entity> e) {
    this.addEntities(e);
  }

  
  private void checkAndCreateEntities() {
    if (entities == null) {
      entities = new ArrayList<>();
    }
  }
  
  public void addEntity(Entity e) {
    checkAndCreateEntities();
    entities.add(e);
  }
  
  public void addEntities(List<Entity> e) {
    checkAndCreateEntities();
    entities.addAll(e);
  }

  private void checkAndCreateAttributes() {
    if (attrs == null) {
      attrs = new ArrayList<>();
    }
  }
  
  public void addAttribute(String attribute) {
    checkAndCreateAttributes();
    attrs.add(attribute);
  }
  
  public void addAttributes(List<String> attributes) {
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
