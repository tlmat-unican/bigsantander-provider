package es.unican.tlmat.smartsantander.bigiot.provider.offerings;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import es.unican.tlmat.smartsantander.bigiot.provider.fiware.OrionHttpClient;
import es.unican.tlmat.smartsantander.bigiot.provider.fiware.Query;

public class BikeSharingStationsOffering extends GenericOffering {

  private static String DESCRIPTION = "SantanderBikeSharingStationsOffering";
  private static String NAME = "Santander Bike Sharing Stations Offering";
  private static String CATEGORY = "urn:big-iot:BikeSharingStationCategory";

  private static String FIWARE_TYPE = "BikeHireDockingStation";

  private static List<InputOutputData> INPUT_DATA = Arrays
      .asList(InputOutputData.BIKE_SHARING_STATION_ID,
              InputOutputData.LONGITUDE,
              InputOutputData.LATITUDE,
              InputOutputData.RADIUS);

  private static List<InputOutputData> OUTPUT_DATA = Arrays
      .asList(InputOutputData.BIKE_SHARING_STATION_ID,
              InputOutputData.BIKE_SHARING_STATION_AVAILABLE_BIKES,
              InputOutputData.BIKE_SHARING_STATION_AVAILABLE_SLOTS,
              InputOutputData.LONGITUDE,
              InputOutputData.LATITUDE,
              InputOutputData.TIMESTAMP);

  private static List<InputOutputData> MANDATORY_OUTPUT_DATA = Arrays
      .asList(InputOutputData.BIKE_SHARING_STATION_AVAILABLE_BIKES,
              InputOutputData.BIKE_SHARING_STATION_AVAILABLE_SLOTS);

  protected BikeSharingStationsOffering(OrionHttpClient orion) {
    super(orion, NAME, DESCRIPTION, CATEGORY, INPUT_DATA, OUTPUT_DATA,
          MANDATORY_OUTPUT_DATA);
  }

  public static final BikeSharingStationsOffering
      create(OrionHttpClient orion) {
    return new BikeSharingStationsOffering(orion);
  }

  @Override
  protected Query createFiwareQuery(Map<String, Object> inputData) {
    Query query = super.createFiwareQuery(inputData);

    Query.Entity entity = new Query.Entity();
    entity.type = FIWARE_TYPE;
    entity.idPattern = (inputData
        .containsKey(InputOutputData.BIKE_SHARING_STATION_ID.getName()))
            ? String
                .format(":%s$",
                        inputData
                            .get(InputOutputData.BIKE_SHARING_STATION_ID
                                .getName()))
            : ".*";

    query.addEntity(entity);

    return query;
  }
}
