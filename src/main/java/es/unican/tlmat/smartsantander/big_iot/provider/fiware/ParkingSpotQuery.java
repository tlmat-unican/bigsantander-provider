package es.unican.tlmat.smartsantander.big_iot.provider.fiware;

import java.util.Arrays;

public class ParkingSpotQuery extends Query {

  private ParkingSpotQuery(final Entity e) {
    super(e);
  }

  public static ParkingSpotQuery create() {
    return create(".*");
  }

  public static ParkingSpotQuery create(int id) {
    return create(String.format(":%d$", id));
  }
  
  public static ParkingSpotQuery create(String idPattern) {
    Query.Entity entity = new Query.Entity();
    entity.idPattern = idPattern;
    entity.type = "ParkingSpot";

    ParkingSpotQuery query = new ParkingSpotQuery(entity);
    query.addAttributes(Arrays.asList("id", "location", "status", "dateModified"));

    return query;
  }
}
