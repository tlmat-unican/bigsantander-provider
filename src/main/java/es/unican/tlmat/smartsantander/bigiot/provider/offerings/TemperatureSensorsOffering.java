package es.unican.tlmat.smartsantander.bigiot.provider.offerings;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import es.unican.tlmat.smartsantander.bigiot.provider.fiware.OrionHttpClient;
import es.unican.tlmat.smartsantander.bigiot.provider.fiware.Query;

public class TemperatureSensorsOffering extends GenericOffering {

  private static String DESCRIPTION = "SantanderAirTemperatureSensorsOffering";
  private static String NAME = "Santander Air Temperature Sensors Offering";
  private static String CATEGORY = "urn:big-iot:WeatherIndicatorCategory";

  private static Stream<String> FIWARE_TYPE =
      Stream.of("WeatherObserved", "AirQualityObserved");

  private static List<InputOutputData> INPUT_DATA = Arrays
      .asList(InputOutputData.LONGITUDE,
              InputOutputData.LATITUDE,
              InputOutputData.RADIUS);

  private static List<InputOutputData> OUTPUT_DATA = Arrays
      .asList(InputOutputData.WEATHER_TEMPERATURE,
              InputOutputData.LONGITUDE,
              InputOutputData.LATITUDE,
              InputOutputData.TIMESTAMP);

  private static List<InputOutputData> MANDATORY_OUTPUT_DATA =
      Arrays.asList(InputOutputData.WEATHER_TEMPERATURE);

  protected TemperatureSensorsOffering(OrionHttpClient orion) {
    super(orion, NAME, DESCRIPTION, CATEGORY, INPUT_DATA, OUTPUT_DATA,
          MANDATORY_OUTPUT_DATA);
  }

  public static final TemperatureSensorsOffering create(OrionHttpClient orion) {
    return new TemperatureSensorsOffering(orion);
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
