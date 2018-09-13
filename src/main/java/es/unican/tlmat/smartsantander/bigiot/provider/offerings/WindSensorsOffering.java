package es.unican.tlmat.smartsantander.bigiot.provider.offerings;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import es.unican.tlmat.smartsantander.bigiot.provider.fiware.OrionHttpClient;
import es.unican.tlmat.smartsantander.bigiot.provider.fiware.Query;

public class WindSensorsOffering extends GenericOffering {

  private static String DESCRIPTION = "SantanderWindSensorsOffering";
  private static String NAME = "Santander Wind Sensors Offering";
  private static String CATEGORY = "urn:big-iot:WeatherIndicatorCategory";

  private static Stream<String> FIWARE_TYPE = Stream.of("WeatherObserved");

  private static List<InputOutputData> INPUT_DATA = Arrays
      .asList(InputOutputData.LONGITUDE,
              InputOutputData.LATITUDE,
              InputOutputData.RADIUS);

  private static List<InputOutputData> OUTPUT_DATA = Arrays
      .asList(InputOutputData.WEATHER_WIND_SPEED,
              InputOutputData.WEATHER_WIND_DIRECTION,
              InputOutputData.LONGITUDE,
              InputOutputData.LATITUDE,
              InputOutputData.TIMESTAMP);

  private static List<InputOutputData> MANDATORY_OUTPUT_DATA = Arrays
      .asList(InputOutputData.WEATHER_WIND_SPEED,
              InputOutputData.WEATHER_WIND_DIRECTION);

  protected WindSensorsOffering(OrionHttpClient orion) {
    super(orion, NAME, DESCRIPTION, CATEGORY, INPUT_DATA, OUTPUT_DATA,
          MANDATORY_OUTPUT_DATA);
  }

  public static final WindSensorsOffering create(OrionHttpClient orion) {
    return new WindSensorsOffering(orion);
  }

  @Override
  protected Query createFiwareQuery(Map<String, Object> inputData) {
    Query query = super.createFiwareQuery(inputData);

    FIWARE_TYPE.forEach(e -> {
      Query.Entity entity = new Query.Entity();
      entity.type = e;
      entity.idPattern = ".*";
      query.addEntity(entity);
    });

    return query;
  }
}
