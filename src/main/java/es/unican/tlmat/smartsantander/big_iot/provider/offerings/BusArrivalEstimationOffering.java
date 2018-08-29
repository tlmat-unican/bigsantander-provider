package es.unican.tlmat.smartsantander.big_iot.provider.offerings;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import es.unican.tlmat.smartsantander.big_iot.provider.fiware.OrionHttpClient;
import es.unican.tlmat.smartsantander.big_iot.provider.fiware.Query;

public class BusArrivalEstimationOffering extends GenericOffering {

  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static String DESCRIPTION = "SantanderBusArrivalEstimationOffering";
  private static String NAME = "Santander Bus Arrival Estimation Offering";
  private static String CATEGORY = "urn:big-iot:BusCategory";

  private static String FIWARE_TYPE = "BusArrivalEstimation";

  private static List<InputOutputData> INPUT_DATA = Arrays.asList(InputOutputData.LONGITUDE,
      InputOutputData.LATITUDE, InputOutputData.RADIUS, InputOutputData.BUS_STOP_ID);

  private static List<InputOutputData> OUTPUT_DATA = Arrays.asList(InputOutputData.BUS_STOP_ID,
      InputOutputData.BUS_STOP_NAME, InputOutputData.BUS_LINE_ID, InputOutputData.BUS_LINE_NAME,
      InputOutputData.BUS_STOP_TIME_TO_ARRIVAL, InputOutputData.LONGITUDE, InputOutputData.LATITUDE,
      InputOutputData.TIMESTAMP);

  private static List<InputOutputData> MANDATORY_OUTPUT_DATA = Arrays.asList(
      InputOutputData.BUS_STOP_ID, InputOutputData.BUS_LINE_ID,
      InputOutputData.BUS_STOP_TIME_TO_ARRIVAL);

  private Map<String, String> stops;
  private Map<String, String> lines;

  protected BusArrivalEstimationOffering(OrionHttpClient orion) {
    super(orion, NAME, DESCRIPTION, CATEGORY, INPUT_DATA, OUTPUT_DATA, MANDATORY_OUTPUT_DATA);
  }

  public static final BusArrivalEstimationOffering create(OrionHttpClient orion) throws IOException {
    BusArrivalEstimationOffering offering = new BusArrivalEstimationOffering(orion);
    offering.setStops();
    offering.setLines();

    return offering;
  }

  private void setStops() throws IOException {
    this.stops = sendOrionQueryForIdNameDuple(new Query("BusStop"));
  }

  private void setLines() throws IOException {
    this.lines = sendOrionQueryForIdNameDuple(new Query("BusLine"));
  }

  private Map<String, String> sendOrionQueryForIdNameDuple(Query query) throws IOException {
    query.addAttributes(Arrays.asList("id", "name"));
    ArrayNode jsonStops = getOrionHttpClient().postQuery(query);
    return StreamSupport.stream(jsonStops.spliterator(), true)
        .map(e -> new SimpleImmutableEntry<>(e.get("id").asText(), e.get("name").asText()))
        .collect(Collectors.toMap(SimpleImmutableEntry::getKey, SimpleImmutableEntry::getValue));
  }

  @Override
  protected Query createFiwareQuery(Map<String, Object> inputData) {
    Query query = super.createFiwareQuery(inputData);

    Query.Entity entity = new Query.Entity();
    entity.type = FIWARE_TYPE;
    entity.idPattern = (inputData.containsKey(InputOutputData.BUS_STOP_ID.getName()))
        ? String.format(":%s$", inputData.get(InputOutputData.BUS_STOP_ID.getName()))
        : ".*";

    query.addEntity(entity);

    return query;
  }

  @Override
  protected ObjectNode convertFiwareToBigiot(final ObjectNode src) {
    ObjectNode rootNode = super.convertFiwareToBigiot(src);

    // Retrieve bus stop name
    String stopId = rootNode.get(InputOutputData.BUS_STOP_ID.getName()).asText();
    rootNode.put(InputOutputData.BUS_STOP_NAME.getName(), stops.get(stopId));

    // Retrieve bus line name
    String lineId = rootNode.get(InputOutputData.BUS_LINE_ID.getName()).asText();
    rootNode.put(InputOutputData.BUS_LINE_NAME.getName(), lines.get(lineId));

    return rootNode;
  }

}
