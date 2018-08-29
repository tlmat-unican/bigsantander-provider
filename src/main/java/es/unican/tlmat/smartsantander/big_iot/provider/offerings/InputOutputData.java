package es.unican.tlmat.smartsantander.big_iot.provider.offerings;

import org.eclipse.bigiot.lib.model.BigIotTypes.ValueType;

public enum InputOutputData {
  // Generic
  ID("id", "schema:id", ValueType.TEXT, "/id"),
  LONGITUDE("longitude", "schema:longitude", ValueType.NUMBER, "/location/coordinates/0"),
  LATITUDE("latitude", "schema:latitude", ValueType.NUMBER, "/location/coordinates/1"),
  RADIUS("geoRadius", "schema:geoRadius", ValueType.NUMBER, InputOutputData.NULL_PATH),
  TIMESTAMP("timestamp", "schema:geoRadius", ValueType.DATETIME, "/dateModified"),
  // Parking Space Availability (ParkingSpot)
  PARKING_SPOT_ID("parkingSpotId", "mobility:parkingSpaceOrGroupIdentifier", ValueType.TEXT, "/id"),
  PARKING_SPOT_STATUS("status", "mobility:parkingSpaceStatus", ValueType.TEXT, "/status"),
  // Bike Sharing Station (BikeHireDockingStation)
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
      "/freeSlotNumber"),
  // Traffic Conditions (TrafficFlowObserved)
  TRAFFIC_FLOW_ROAD_SPOT_ID(
      "roadSpotId",
      "proposed:roadSpotIdentifier",
      ValueType.TEXT,
      "/id"),
  TRAFFIC_FLOW_OCCUPANCY(
      "occupation",
      "proposed:roadOccupationPercentage",
      ValueType.NUMBER,
      "/occupancy"),
  TRAFFIC_FLOW_INTENSITY(
      "intensity",
      "proposed:roadIntensity",
      ValueType.NUMBER,
      "/intensity"),
  TRAFFIC_FLOW_LOAD(
      "occupation",
      "proposed:roadLoadEstimation",
      ValueType.NUMBER,
      "/roadLoad"),
  // Weather Observed (WeatherObserved, AirQualityObserved)
  WEATHER_TEMPERATURE(
      "temperature",
      "environment:hasAirTemperature",
      ValueType.NUMBER,
      "/temperature"),
  WEATHER_HUMIDITY(
      "relativeHumidity",
      "environment:hasHumidity",
      ValueType.NUMBER,
      "/relativeHumidity"),
  WEATHER_WIND_SPEED(
      "windSpeed",
      "environment:hasWindSpeed",
      ValueType.NUMBER,
      "/windSpeed"),
  WEATHER_WIND_DIRECTION(
      "windDirection",
      "environment:hasWindDirection",
      ValueType.NUMBER,
      "/windDirection"),
  WEATHER_ATMOSPHERIC_PRESSURE(
      "atmosphericPressure",
      "environment:hasPressure",
      ValueType.NUMBER,
      "/atmosphericPressure"),
  // Air Pollution Sensor (AirQualityObserved)
  AIR_POLLUTION_NO2(
      "NO2",
      "environment:hasNO2Concentration",
      ValueType.NUMBER,
      "/NO2"),
  AIR_POLLUTION_O3(
      "O3",
      "environment:hasO3Concentration",
      ValueType.NUMBER,
      "/O3"),
  // TODO: Fix as not found in FIWARE data models
  AIR_POLLUTION_DUST(
      "dust",
      "environment:hasPMConcentration",
      ValueType.NUMBER,
      "/atmosphericPressure"),
  // Noise sensors (NoiseLevelObserved)
  NOISE_LEVEL(
      "noiseLevelInDecibelA",
      "environment:hasNoiseLevel",
      ValueType.NUMBER,
      "/Lp"),
  // Bus Arrival Estimation
  BUS_STOP_ID(
      "busStopId",
      "proposed:hasBusStopId",
      ValueType.TEXT,
      "/refBusStop"),
  BUS_STOP_NAME(
      "busStopName",
      "mobility:hasBusStopName",
      ValueType.TEXT,
      "/name"),
  BUS_LINE_ID(
      "busLineId",
      "mobility:hasBusLineNumber",
      ValueType.NUMBER,
      "/refBusLine"),
  BUS_LINE_NAME(
      "busLineName",
      "proposed:hasBusLineName",
      ValueType.TEXT,
      "/name"),
  BUS_STOP_TIME_TO_ARRIVAL(
      "busTimeToArrival",
      "proposed:busTimeToArrival",
      ValueType.TEXT, // ISO8601 timing format
      "/remainingTimes/0"),
  NULL("null", "null", ValueType.NUMBER, InputOutputData.NULL_PATH);

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
