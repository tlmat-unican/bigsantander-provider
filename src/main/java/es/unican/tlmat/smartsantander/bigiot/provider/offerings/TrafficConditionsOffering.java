package es.unican.tlmat.smartsantander.bigiot.provider.offerings;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import es.unican.tlmat.smartsantander.bigiot.provider.fiware.OrionHttpClient;
import es.unican.tlmat.smartsantander.bigiot.provider.fiware.Query;

public class TrafficConditionsOffering extends GenericOffering {

  private static String DESCRIPTION = "SantanderTrafficConditionsOffering";
  private static String NAME = "Santander Traffic Conditions Offering";
  private static String CATEGORY = "urn:big-iot:TrafficDataCategory";

  private static String FIWARE_TYPE = "TrafficFlowObserved";

  private static List<InputOutputData> INPUT_DATA = Arrays
      .asList(InputOutputData.LONGITUDE,
              InputOutputData.LATITUDE,
              InputOutputData.RADIUS);

  private static List<InputOutputData> OUTPUT_DATA = Arrays
      .asList(InputOutputData.TRAFFIC_FLOW_ROAD_SPOT_ID,
              InputOutputData.TRAFFIC_FLOW_OCCUPANCY,
              InputOutputData.TRAFFIC_FLOW_INTENSITY,
              InputOutputData.TRAFFIC_FLOW_LOAD,
              InputOutputData.LONGITUDE,
              InputOutputData.LATITUDE,
              InputOutputData.TIMESTAMP);

  private static List<InputOutputData> MANDATORY_OUTPUT_DATA = Arrays
      .asList(InputOutputData.TRAFFIC_FLOW_OCCUPANCY,
              InputOutputData.TRAFFIC_FLOW_INTENSITY,
              InputOutputData.TRAFFIC_FLOW_LOAD);

  protected TrafficConditionsOffering(OrionHttpClient orion) {
    super(orion, NAME, DESCRIPTION, CATEGORY, INPUT_DATA, OUTPUT_DATA,
          MANDATORY_OUTPUT_DATA);
  }

  public static final TrafficConditionsOffering create(OrionHttpClient orion) {
    return new TrafficConditionsOffering(orion);
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
