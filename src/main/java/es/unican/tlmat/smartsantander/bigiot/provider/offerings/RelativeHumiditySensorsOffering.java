package es.unican.tlmat.smartsantander.bigiot.provider.offerings;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import es.unican.tlmat.smartsantander.bigiot.provider.fiware.OrionHttpClient;
import es.unican.tlmat.smartsantander.bigiot.provider.fiware.Query;

public class RelativeHumiditySensorsOffering extends GenericOffering {

  private static String DESCRIPTION =
      "SantanderRelativeHumiditySensorsOffering";
  private static String NAME = "Santander Relative Humidity Sensors Offering";
  private static String CATEGORY = "urn:big-iot:WeatherIndicatorCategory";

  private static Stream<String> FIWARE_TYPE =
      Stream.of("WeatherObserved", "AirQualityObserved");

  private static List<InputOutputData> INPUT_DATA = Arrays
      .asList(InputOutputData.LONGITUDE,
              InputOutputData.LATITUDE,
              InputOutputData.RADIUS);

  private static List<InputOutputData> OUTPUT_DATA = Arrays
      .asList(InputOutputData.WEATHER_HUMIDITY,
              InputOutputData.LONGITUDE,
              InputOutputData.LATITUDE,
              InputOutputData.TIMESTAMP);

  private static List<InputOutputData> MANDATORY_OUTPUT_DATA =
      Arrays.asList(InputOutputData.WEATHER_HUMIDITY);

  protected RelativeHumiditySensorsOffering(OrionHttpClient orion) {
    super(orion, NAME, DESCRIPTION, CATEGORY, INPUT_DATA, OUTPUT_DATA,
          MANDATORY_OUTPUT_DATA);
  }

  public static final RelativeHumiditySensorsOffering
      create(OrionHttpClient orion) {
    return new RelativeHumiditySensorsOffering(orion);
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
