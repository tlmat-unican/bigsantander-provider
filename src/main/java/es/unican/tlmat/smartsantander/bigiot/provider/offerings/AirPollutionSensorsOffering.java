package es.unican.tlmat.smartsantander.bigiot.provider.offerings;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import es.unican.tlmat.smartsantander.bigiot.provider.fiware.OrionHttpClient;
import es.unican.tlmat.smartsantander.bigiot.provider.fiware.Query;

public class AirPollutionSensorsOffering extends GenericOffering {

  private static String DESCRIPTION = "SantanderAirPollutionSensorsOffering";
  private static String NAME = "Santander Air Pollution Sensors Offering";
  private static String CATEGORY = "urn:big-iot:AirPollutionIndicatorCategory";

  private static List<String> FIWARE_TYPE = Arrays.asList("AirQualityObserved");

  private static List<InputOutputData> INPUT_DATA = Arrays
      .asList(InputOutputData.LONGITUDE,
              InputOutputData.LATITUDE,
              InputOutputData.RADIUS);

  private static List<InputOutputData> OUTPUT_DATA = Arrays
      .asList(InputOutputData.AIR_POLLUTION_NO2,
              InputOutputData.AIR_POLLUTION_O3,
              InputOutputData.AIR_POLLUTION_DUST,
              InputOutputData.LONGITUDE,
              InputOutputData.LATITUDE,
              InputOutputData.TIMESTAMP);

  private static List<InputOutputData> MANDATORY_OUTPUT_DATA = Arrays
      .asList(InputOutputData.AIR_POLLUTION_NO2,
              InputOutputData.AIR_POLLUTION_O3,
              InputOutputData.AIR_POLLUTION_DUST);

  protected AirPollutionSensorsOffering(OrionHttpClient orion) {
    super(orion, NAME, DESCRIPTION, CATEGORY, INPUT_DATA, OUTPUT_DATA,
          MANDATORY_OUTPUT_DATA);
  }

  public static final AirPollutionSensorsOffering
      create(OrionHttpClient orion) {
    return new AirPollutionSensorsOffering(orion);
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
