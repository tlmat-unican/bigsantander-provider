package es.unican.tlmat.smartsantander.bigiot.provider.offerings;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.unican.tlmat.smartsantander.bigiot.provider.fiware.OrionHttpClient;
import es.unican.tlmat.smartsantander.bigiot.provider.fiware.Query;

public class BusesLocationOffering extends GenericOffering {

  private static final Logger log =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static String UNKNOWN = "Unknown";

  private static String DESCRIPTION = "SantanderBusesLocationOffering";
  private static String NAME = "Santander Bus Location Offering";
  private static String CATEGORY = "urn:big-iot:LocationTrackingCategory";

  private static String ID_PATTERN = "urn:ngsi-ld:Vehicle:santander:tus:bus:.*";
  private static String FIWARE_TYPE = "Vehicle";

  private static List<InputOutputData> INPUT_DATA = Arrays
      .asList(InputOutputData.LONGITUDE,
              InputOutputData.LATITUDE,
              InputOutputData.RADIUS,
              InputOutputData.BUS_VEHICLE_ID);

  private static List<InputOutputData> OUTPUT_DATA = Arrays
      .asList(InputOutputData.BUS_VEHICLE_ID,
              InputOutputData.LONGITUDE,
              InputOutputData.LATITUDE,
              InputOutputData.TIMESTAMP,
              InputOutputData.BUS_VEHICLE_LINE,
              InputOutputData.BUS_VEHICLE_SPEED);

  private static List<InputOutputData> MANDATORY_OUTPUT_DATA = Arrays
      .asList(InputOutputData.BUS_VEHICLE_LINE,
              InputOutputData.BUS_VEHICLE_SPEED);

  protected BusesLocationOffering(OrionHttpClient orion) {
    super(orion, NAME, DESCRIPTION, CATEGORY, INPUT_DATA, OUTPUT_DATA,
          MANDATORY_OUTPUT_DATA);
  }

  public static final BusesLocationOffering
      create(OrionHttpClient orion) throws IOException {
    return new BusesLocationOffering(orion);
    }

  @Override
  protected Query createFiwareQuery(Map<String, Object> inputData) {
    Query query = super.createFiwareQuery(inputData);

    Query.Entity entity = new Query.Entity();
    entity.type = FIWARE_TYPE;
    entity.idPattern =
        (inputData.containsKey(InputOutputData.BUS_VEHICLE_ID.getName()))
            ? String
                .format(":%s$",
                        inputData.get(InputOutputData.BUS_STOP_ID.getName()))
            : ID_PATTERN;

    query.addEntity(entity);

    return query;
  }
}
