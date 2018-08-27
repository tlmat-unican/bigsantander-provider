package es.unican.tlmat.smartsantander.big_iot.provider.offerings;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import es.unican.tlmat.smartsantander.big_iot.provider.fiware.Query;

public class RelativeHumiditySensorsOffering extends GenericOffering {

  private static String DESCRIPTION = "SantanderRelativeHumiditySensorsOffering";
  private static String NAME = "Santander Relative Humidity Sensors Offering";
  private static String CATEGORY = "urn:big-iot:WeatherIndicatorCategory";

  private static Stream<String> FIWARE_TYPE = Stream.of("WeatherObserved", "AirQualityObserved");

  private static List<InputOutputData> INPUT_DATA = Arrays.asList(InputOutputData.LONGITUDE,
      InputOutputData.LATITUDE, InputOutputData.RADIUS);

  private static List<InputOutputData> OUTPUT_DATA = Arrays.asList(InputOutputData.WEATHER_HUMIDITY,
      InputOutputData.LONGITUDE, InputOutputData.LATITUDE, InputOutputData.TIMESTAMP);

  private static RelativeHumiditySensorsOffering offering = null;

  protected RelativeHumiditySensorsOffering() {
    super(NAME, DESCRIPTION, CATEGORY, INPUT_DATA, OUTPUT_DATA);
  }

  public static final RelativeHumiditySensorsOffering getInstance() {
    if (offering == null) {
      offering = new RelativeHumiditySensorsOffering();
    }

    return offering;
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