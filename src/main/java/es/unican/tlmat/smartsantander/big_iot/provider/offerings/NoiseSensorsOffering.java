package es.unican.tlmat.smartsantander.big_iot.provider.offerings;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import es.unican.tlmat.smartsantander.big_iot.provider.fiware.Query;

public class NoiseSensorsOffering extends GenericOffering {

  private static String DESCRIPTION = "SantanderNoiseSensorOffering";
  private static String NAME = "Santander Noise Sensor Offering";
  private static String CATEGORY = "urn:big-iot:NoisePollutionIndicatorCategory";

  private static String FIWARE_TYPE = "NoiseLevelObserved";

  private static List<InputOutputData> INPUT_DATA = Arrays.asList(InputOutputData.LONGITUDE,
      InputOutputData.LATITUDE, InputOutputData.RADIUS);

  private static List<InputOutputData> OUTPUT_DATA = Arrays.asList(InputOutputData.NOISE_LEVEL,
      InputOutputData.LONGITUDE, InputOutputData.LATITUDE, InputOutputData.TIMESTAMP);

  private static NoiseSensorsOffering offering = null;

  protected NoiseSensorsOffering() {
    super(NAME, DESCRIPTION, CATEGORY, INPUT_DATA, OUTPUT_DATA);
  }

  public static final NoiseSensorsOffering getInstance() {
    if (offering == null) {
      offering = new NoiseSensorsOffering();
    }

    return offering;
  }

  @Override
  protected Query createFiwareQuery(Map<String, Object> inputData) {
    Query query = super.createFiwareQuery(inputData);

    Query.Entity entity = new Query.Entity();
    entity.type = FIWARE_TYPE;
    entity.idPattern = ".*";
    query.addEntity(entity);

    return query;
  }
}