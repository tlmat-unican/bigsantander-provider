package es.unican.tlmat.smartsantander.bigiot.provider.offerings;

import org.eclipse.bigiot.lib.model.BigIotTypes.ValueType;

import es.unican.tlmat.smartsantander.bigiot.common.SchemaNamespace;

public enum InputOutputData {
  // Generic
  ID("id", SchemaNamespace.BASE.entity("id"), ValueType.TEXT, "/id"),
  NAME("name", "schema:id", ValueType.TEXT, "/name"),
  LONGITUDE(
      "longitude",
      SchemaNamespace.BASE.entity("longitude"),
      ValueType.NUMBER,
      "/location/coordinates/0"),
  LATITUDE(
      "latitude",
      SchemaNamespace.BASE.entity("latitude"),
      ValueType.NUMBER,
      "/location/coordinates/1"),
  RADIUS(
      "geoRadius",
      SchemaNamespace.BASE.entity("geoRadius"),
      ValueType.NUMBER,
      InputOutputData.NULL_PATH),
  TIMESTAMP(
      "timestamp",
      SchemaNamespace.BASE.entity("dateTime"),
      ValueType.DATETIME,
      "/dateModified"),
  // Parking Space Availability (ParkingSpot)
  PARKING_SPOT_ID(
      "parkingSpotId",
      SchemaNamespace.MOBILITY.entity("parkingSpaceOrGroupIdentifier"),
      ValueType.TEXT,
      "/id"),
  PARKING_SPOT_STATUS(
      "status",
      SchemaNamespace.MOBILITY.entity("parkingSpaceStatus"),
      ValueType.TEXT,
      "/status"),
  // Bike Sharing Station (BikeHireDockingStation)
  BIKE_SHARING_STATION_ID(
      "stationId",
      SchemaNamespace.MOBILITY.entity("bikeSharingStationIdentifier"),
      ValueType.TEXT,
      "/id"),
  BIKE_SHARING_STATION_AVAILABLE_BIKES(
      "availableBikesCount",
      SchemaNamespace.MOBILITY.entity("numberOfAvailableBikes"),
      ValueType.NUMBER,
      "/availableBikeNumber"),
  BIKE_SHARING_STATION_AVAILABLE_SLOTS(
      "availableBikeSlotsCount",
      SchemaNamespace.MOBILITY.entity("numberOfAvailableParkingSlots"),
      ValueType.NUMBER,
      "/freeSlotNumber"),
  // Traffic Conditions (TrafficFlowObserved)
  TRAFFIC_FLOW_ROAD_SPOT_ID(
      "roadSpotId",
      SchemaNamespace.PROPOSED.entity("roadSpotIdentifier"),
      ValueType.TEXT,
      "/id"),
  TRAFFIC_FLOW_OCCUPANCY(
      "occupation",
      SchemaNamespace.MOBILITY.entity("roadOccupationPercentage"),
      ValueType.NUMBER,
      "/occupancy"),
  TRAFFIC_FLOW_INTENSITY(
      "intensity",
      SchemaNamespace.MOBILITY.entity("roadIntensity"),
      ValueType.NUMBER,
      "/intensity"),
  TRAFFIC_FLOW_LOAD(
      "load",
      SchemaNamespace.MOBILITY.entity("roadLoadEstimation"),
      ValueType.NUMBER,
      "/roadLoad"),
  // Weather Observed (WeatherObserved, AirQualityObserved)
  WEATHER_TEMPERATURE(
      "temperature",
      SchemaNamespace.ENVIRONMENT.entity("hasAirTemperature"),
      ValueType.NUMBER,
      "/temperature"),
  WEATHER_HUMIDITY(
      "relativeHumidity",
      SchemaNamespace.ENVIRONMENT.entity("hasHumidity"),
      ValueType.NUMBER,
      "/relativeHumidity"),
  WEATHER_WIND_SPEED(
      "windSpeed",
      SchemaNamespace.ENVIRONMENT.entity("hasWindSpeed"),
      ValueType.NUMBER,
      "/windSpeed"),
  WEATHER_WIND_DIRECTION(
      "windDirection",
      SchemaNamespace.ENVIRONMENT.entity("hasWindDirection"),
      ValueType.NUMBER,
      "/windDirection"),
  WEATHER_ATMOSPHERIC_PRESSURE(
      "atmosphericPressure",
      SchemaNamespace.ENVIRONMENT.entity("hasPressure"),
      ValueType.NUMBER,
      "/atmosphericPressure"),
  // Air Pollution Sensor (AirQualityObserved)
  AIR_POLLUTION_NO2(
      "NO2",
      SchemaNamespace.ENVIRONMENT.entity("hasNO2Concentration"),
      ValueType.NUMBER,
      "/NO2"),
  AIR_POLLUTION_O3(
      "O3",
      SchemaNamespace.ENVIRONMENT.entity("hasO3Concentration"),
      ValueType.NUMBER,
      "/O3"),
  // TODO: Fix as not found in FIWARE data models
  AIR_POLLUTION_DUST(
      "dust",
      SchemaNamespace.ENVIRONMENT.entity("hasPMConcentration"),
      ValueType.NUMBER,
      "/atmosphericPressure"),
  // Noise sensors (NoiseLevelObserved)
  NOISE_LEVEL(
      "noiseLevelInDecibelA",
      SchemaNamespace.ENVIRONMENT.entity("hasNoiseLevel"),
      ValueType.NUMBER,
      "/Lp"),
  // Bus Arrival Estimation
  BUS_STOP_ID(
      "busStopId",
      SchemaNamespace.PROPOSED.entity("hasBusStopId"),
      ValueType.TEXT,
      "/refBusStop"),
  BUS_STOP_NAME(
      "busStopName",
      SchemaNamespace.MOBILITY.entity("hasBusStopName"),
      ValueType.TEXT,
      "/name"),
  BUS_LINE_ID(
      "busLineId",
      SchemaNamespace.MOBILITY.entity("hasBusLineNumber"),
      ValueType.NUMBER,
      "/refBusLine"),
  BUS_LINE_NAME(
      "busLineName",
      SchemaNamespace.PROPOSED.entity("hasBusLineName"),
      ValueType.TEXT,
      "/name"),
  BUS_STOP_TIME_TO_ARRIVAL(
      "busTimeToArrival",
      SchemaNamespace.PROPOSED.entity("busTimeToArrival"),
      ValueType.TEXT, // ISO8601 timing format
      "/remainingTimes/0"),
  // Bus Location
  BUS_VEHICLE_ID(
      "busId",
      SchemaNamespace.MOBILITY.entity("hasBusId"),
      ValueType.TEXT,
      "/id"),
  BUS_VEHICLE_SPEED(
      "busSpeed",
      SchemaNamespace.MOBILITY.entity("measuredSpeed"),
      ValueType.NUMBER,
      "/speed"),
  BUS_VEHICLE_LINE(
      "busLine",
      SchemaNamespace.MOBILITY.entity("hasBusLineNumber"),
      ValueType.NUMBER,
      "/areaServed"),
  NULL("null", "null", ValueType.NUMBER, InputOutputData.NULL_PATH);

  private final String name;
  private final String rdfAnnotation;
  private final ValueType valueType;
  private final String fiwareJsonPath;

  InputOutputData(final String name, final String rdfAnnotation,
      final ValueType valueType, final String jsonPath) {
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
