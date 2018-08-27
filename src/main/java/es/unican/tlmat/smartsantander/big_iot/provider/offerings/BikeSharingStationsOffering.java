package es.unican.tlmat.smartsantander.big_iot.provider.offerings;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import es.unican.tlmat.smartsantander.big_iot.provider.fiware.BikeHireDockingStationQuery;
import es.unican.tlmat.smartsantander.big_iot.provider.fiware.Query;

public class BikeSharingStationsOffering extends GenericOffering {

  private static String DESCRIPTION = "SantanderBikeSharingStationsOffering";
  private static String NAME = "Santander Bike Sharing Stations Offering";
  private static String CATEGORY = "urn:big-iot:BikeSharingStationCategory";

  private static List<InputOutputData> INPUT_DATA = Arrays.asList(
      InputOutputData.BIKE_SHARING_STATION_ID, InputOutputData.LONGITUDE, InputOutputData.LATITUDE,
      InputOutputData.RADIUS);

  private static List<InputOutputData> OUTPUT_DATA = Arrays.asList(
      InputOutputData.BIKE_SHARING_STATION_ID, InputOutputData.BIKE_SHARING_STATION_AVAILABLE_BIKES,
      InputOutputData.BIKE_SHARING_STATION_AVAILABLE_SLOTS, InputOutputData.LONGITUDE,
      InputOutputData.LATITUDE, InputOutputData.TIMESTAMP);

  private static BikeSharingStationsOffering offering = null;

  protected BikeSharingStationsOffering() {
    super(NAME, DESCRIPTION, CATEGORY, INPUT_DATA, OUTPUT_DATA);
  }

  public static final BikeSharingStationsOffering getInstance() {
    if (offering == null) {
      offering = new BikeSharingStationsOffering();
    }

    return offering;
  }

  @Override
  public Query createFiwareQuery(Map<String, Object> inputData) {
    BikeHireDockingStationQuery query = (inputData.containsKey(InputOutputData.BIKE_SHARING_STATION_ID.toString()))
        ? BikeHireDockingStationQuery.create((String) inputData.get(InputOutputData.BIKE_SHARING_STATION_ID.toString()))
        : BikeHireDockingStationQuery.create();

    if (inputData.containsKey(InputOutputData.LONGITUDE.toString())
        && inputData.containsKey(InputOutputData.LATITUDE.toString())
        && inputData.containsKey(InputOutputData.RADIUS.toString())) {
      query.withinAreaFilter(
          Double.parseDouble((String) inputData.get(InputOutputData.LATITUDE.toString())),
          Double.parseDouble((String) inputData.get(InputOutputData.LONGITUDE.toString())),
          Integer.parseUnsignedInt((String) inputData.get(InputOutputData.RADIUS.toString())));
    }

    return query;
  }
}
