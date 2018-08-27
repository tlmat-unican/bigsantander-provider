package es.unican.tlmat.smartsantander.big_iot.provider.offerings;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import es.unican.tlmat.smartsantander.big_iot.provider.fiware.Query;

public class ParkingSpaceAvailabilityOffering extends GenericOffering {

  private static String DESCRIPTION = "SantanderParkingSpaceAvailabilityOffering";
  private static String NAME = "Santander Parking Space Availability Offering";
  private static String CATEGORY = "urn:big-iot:ParkingSpaceCategory";

  private static String FIWARE_TYPE = "ParkingSpot";

  private static List<InputOutputData> INPUT_DATA = Arrays.asList(InputOutputData.PARKING_SPOT_ID,
      InputOutputData.LONGITUDE, InputOutputData.LATITUDE, InputOutputData.RADIUS);

  private static List<InputOutputData> OUTPUT_DATA = Arrays.asList(InputOutputData.PARKING_SPOT_ID,
      InputOutputData.PARKING_SPOT_STATUS, InputOutputData.LONGITUDE, InputOutputData.LATITUDE,
      InputOutputData.TIMESTAMP);

  private static ParkingSpaceAvailabilityOffering offering = null;

  protected ParkingSpaceAvailabilityOffering() {
    super(NAME, DESCRIPTION, CATEGORY, INPUT_DATA, OUTPUT_DATA);
  }

  public static final ParkingSpaceAvailabilityOffering getInstance() {
    if (offering == null) {
      offering = new ParkingSpaceAvailabilityOffering();
    }

    return offering;
  }

  @Override
  protected Query createFiwareQuery(Map<String, Object> inputData) {
    Query query = super.createFiwareQuery(inputData);

    Query.Entity entity = new Query.Entity();
    entity.type = FIWARE_TYPE;
    entity.idPattern = (inputData.containsKey(InputOutputData.PARKING_SPOT_ID.getName()))
        ? String.format(":%s$", inputData.get(InputOutputData.PARKING_SPOT_ID.getName()))
        : ".*";

    query.addEntity(entity);

    return query;
  }
}
