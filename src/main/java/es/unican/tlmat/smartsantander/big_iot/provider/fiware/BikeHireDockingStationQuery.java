package es.unican.tlmat.smartsantander.big_iot.provider.fiware;

import java.util.Arrays;

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
    // TODO: Get attributes from offering outputdata mapping
    query.addAttributes(Arrays.asList("id", "location", "dateModified", "availableBikeNumber", "freeSlotNumber"));

    return query;
  }
}
