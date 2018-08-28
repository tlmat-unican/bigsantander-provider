package es.unican.tlmat.smartsantander.big_iot.provider.offerings;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import es.unican.tlmat.smartsantander.big_iot.provider.fiware.Query;

public class BusArrivalEstimationOffering extends GenericOffering {

  private static String DESCRIPTION = "SantanderAirPollutionSensorsOffering";
  private static String NAME = "Santander Air Pollution Sensors Offering";
  private static String CATEGORY = "urn:big-iot:AirPollutionIndicatorCategory";

  private static Stream<String> FIWARE_TYPE = Stream.of("BusArrivalEstimation");

  private static List<InputOutputData> INPUT_DATA = Arrays.asList(InputOutputData.LONGITUDE,
      InputOutputData.LATITUDE, InputOutputData.RADIUS, InputOutputData.BUS_STOP_ID);

  private static List<InputOutputData> OUTPUT_DATA = Arrays.asList(InputOutputData.BUS_STOP_ID,
      InputOutputData.BUS_STOP_NAME, InputOutputData.BUS_LINE_ID, InputOutputData.BUS_LINE_NAME,
      InputOutputData.BUS_STOP_TIME_TO_ARRIVAL, InputOutputData.LONGITUDE, InputOutputData.LATITUDE,
      InputOutputData.TIMESTAMP);

  private static List<InputOutputData> MANDATORY_OUTPUT_DATA = Arrays.asList(
      InputOutputData.BUS_STOP_ID, InputOutputData.BUS_LINE_ID,
      InputOutputData.BUS_STOP_TIME_TO_ARRIVAL);

  private static BusArrivalEstimationOffering offering = null;

  protected BusArrivalEstimationOffering() {
    super(NAME, DESCRIPTION, CATEGORY, INPUT_DATA, OUTPUT_DATA, MANDATORY_OUTPUT_DATA);
  }

  public static final BusArrivalEstimationOffering getInstance() {
    if (offering == null) {
      offering = new BusArrivalEstimationOffering();
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
