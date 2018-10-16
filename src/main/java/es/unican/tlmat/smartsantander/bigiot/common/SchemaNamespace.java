package es.unican.tlmat.smartsantander.bigiot.common;

public enum SchemaNamespace {

  BASE("http://schema.org/"),
  MOBILITY("http://schema.big-iot.org/mobility/"),
  ENVIRONMENT("http://schema.big-iot.org/environment/"),
  PROPOSED("http://schema.big-iot.org/proposed/");

  private final String value;

  SchemaNamespace(final String name){
    this.value = name;
 }

  @Override
  public String toString() {
    return value;
  }

  public String entity(String name) {
    return toString().concat(name);
  }
}
