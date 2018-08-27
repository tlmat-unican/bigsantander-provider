package es.unican.tlmat.smartsantander.big_iot.provider.offerings;

import org.eclipse.bigiot.lib.model.BigIotTypes.ValueType;

public enum InputOutputData {
  // Generic
  LONGITUDE("longitude", "schema:longitude", ValueType.NUMBER, "/location/coordinates/0"),
  LATITUDE("latitude", "schema:latitude", ValueType.NUMBER, "/location/coordinates/1"),
  RADIUS("geoRadius", "schema:geoRadius", ValueType.NUMBER, InputOutputData.NULL_PATH),
  TIMESTAMP("timestamp", "schema:geoRadius", ValueType.DATETIME, "/dateModified"),
  // Parking Space Availability
  PARKING_SPOT_ID("parkingSpotId", "mobility:parkingSpaceOrGroupIdentifier", ValueType.TEXT, "/id"),
  PARKING_SPOT_STATUS("status", "mobility:parkingSpaceStatus", ValueType.TEXT, "/status"),
  // Bike Sharing Station
  BIKE_SHARING_STATION_ID(
      "stationId",
      "mobility:bikeSharingStationIdentifier",
      ValueType.TEXT,
      "/id"),
  BIKE_SHARING_STATION_AVAILABLE_BIKES(
      "availableBikesCount",
      "mobility:numberOfAvailableBikes",
      ValueType.NUMBER,
      "/availableBikeNumber"),
  BIKE_SHARING_STATION_AVAILABLE_SLOTS(
      "availableBikeSlotsCount",
      "mobility:numberOfAvailableParkingSlots",
      ValueType.NUMBER,
      "/freeSlotNumber");

  private final String name;
  private final String rdfAnnotation;
  private final ValueType valueType;
  private final String fiwareJsonPath;

  InputOutputData(final String name, final String rdfAnnotation, final ValueType valueType,
      final String jsonPath) {
    this.name = name;
    this.rdfAnnotation = rdfAnnotation;
    this.valueType = valueType;
    this.fiwareJsonPath = jsonPath;
  }

  @Override
  public String toString() {
    return name;
  }

  public String getName() {
    return name;
  }

  public String getRdfAnnotation() {
    return rdfAnnotation;
  }

  public ValueType getValueType() {
    return valueType;
  }

  public String getFiwareJsonPath() {
    return fiwareJsonPath;
  }

  private static final String NULL_PATH = "/null";
}
