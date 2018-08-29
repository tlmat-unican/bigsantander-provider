package es.unican.tlmat.smartsantander.big_iot.provider.fiware;

public class BikeHireDockingStationQuery extends Query {

  private BikeHireDockingStationQuery(final Entity e) {
    super(e);
  }

  public static BikeHireDockingStationQuery create() {
    return create(".*");
  }

  public static BikeHireDockingStationQuery create(int id) {
    return create(String.format(":%d$", id));
  }

  public static BikeHireDockingStationQuery create(String idPattern) {
    Query.Entity entity = new Query.Entity();
    entity.idPattern = idPattern;
    entity.type = "BikeHireDockingStation";

    BikeHireDockingStationQuery query = new BikeHireDockingStationQuery(entity);

    return query;
  }
}
