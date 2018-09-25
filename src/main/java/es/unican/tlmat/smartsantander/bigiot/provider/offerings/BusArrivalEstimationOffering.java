package es.unican.tlmat.smartsantander.bigiot.provider.offerings;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import es.unican.tlmat.smartsantander.bigiot.provider.fiware.OrionHttpClient;
import es.unican.tlmat.smartsantander.bigiot.provider.fiware.Query;

public class BusArrivalEstimationOffering extends GenericOffering {

  private static final Logger log =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static String UNKNOWN = "Unknown";

  private static String DESCRIPTION = "SantanderBusArrivalEstimationOffering";
  private static String NAME = "Santander Bus Arrival Estimation Offering";
  private static String CATEGORY = "urn:big-iot:BusCategory";

  // Doesn't have location, so we have to query per stop
  private static String FIWARE_TYPE = "BusArrivalEstimation";

  private static List<InputOutputData> INPUT_DATA = Arrays
      .asList(InputOutputData.LONGITUDE,
              InputOutputData.LATITUDE,
              InputOutputData.RADIUS,
              InputOutputData.BUS_STOP_ID);

  private static List<InputOutputData> OUTPUT_DATA = Arrays
      .asList(InputOutputData.BUS_STOP_ID,
              InputOutputData.BUS_STOP_NAME,
              InputOutputData.BUS_LINE_ID,
              InputOutputData.BUS_LINE_NAME,
              InputOutputData.BUS_STOP_TIME_TO_ARRIVAL,
              InputOutputData.LONGITUDE,
              InputOutputData.LATITUDE,
              InputOutputData.TIMESTAMP);

  private static List<InputOutputData> MANDATORY_OUTPUT_DATA = Arrays
      .asList(InputOutputData.BUS_STOP_ID,
              InputOutputData.BUS_LINE_ID,
              InputOutputData.BUS_STOP_TIME_TO_ARRIVAL);

  private static class StopInformation {
    private String id;
    private String name;
    private double latitude;
    private double longitude;

    public StopInformation(String id, String name, double latitude,
        double longitude) {
      this.id = id;
      this.name = name;
      this.latitude = latitude;
      this.longitude = longitude;
    }

    public String getId() {
      return id;
    }

    public String getName() {
      return name;
    }

    public double getLatitude() {
      return latitude;
    }

    public double getLongitude() {
      return longitude;
    }
  }

  private Map<String, StopInformation> stops;
  private Map<String, String> lines;

  protected BusArrivalEstimationOffering(OrionHttpClient orion) {
    super(orion, NAME, DESCRIPTION, CATEGORY, INPUT_DATA, OUTPUT_DATA,
          MANDATORY_OUTPUT_DATA);
  }

  public static final BusArrivalEstimationOffering
      create(OrionHttpClient orion) throws IOException {
    BusArrivalEstimationOffering offering =
        new BusArrivalEstimationOffering(orion);
    offering.setStops();

    offering.stops.forEach((k, v) -> System.out.println(k + "   " + v));

    offering.setLines();

    return offering;
  }

  // Does have location
  private void setStops() throws IOException {
    this.stops = sendOrionQueryForLocation(new Query("BusStop"));
  }

  // Does not have location
  private void setLines() throws IOException {
    this.lines = sendOrionQueryForIdNameDuple(new Query("BusLine"));
  }

  private Map<String, String>
      sendOrionQueryForIdNameDuple(Query query) throws IOException {
    query
        .addAttributes(Arrays
            .asList(InputOutputData.ID.getName(),
                    InputOutputData.NAME.getName()));
    ArrayNode jsonStops = getOrionHttpClient().postQuery(query);
    // @formatter:off
    return StreamSupport
        .stream(jsonStops.spliterator(), true)
        .map(e -> new SimpleImmutableEntry<>(
            e.at(InputOutputData.ID.getFiwareJsonPath()).asText(),
            e.at(InputOutputData.NAME.getFiwareJsonPath()).asText()))
        .collect(Collectors
            .toMap(SimpleImmutableEntry::getKey,
                   SimpleImmutableEntry::getValue));
    // @formatter:on
  }

  private Map<String, StopInformation>
      sendOrionQueryForLocation(Query query) throws IOException {
    query
        .addAttributes(GenericOffering
            .getParentFiwareFieldFromJsonPath(Arrays
                .asList(InputOutputData.ID,
                        InputOutputData.NAME,
                        InputOutputData.LATITUDE,
                        InputOutputData.LONGITUDE)));
    ArrayNode jsonStops = getOrionHttpClient().postQuery(query);
    // @formatter:off
    return StreamSupport
        .stream(jsonStops.spliterator(), true)
        .map(e -> new SimpleImmutableEntry<>(
            e.at(InputOutputData.ID.getFiwareJsonPath()).asText(),
            new StopInformation(
                e.at(InputOutputData.ID.getFiwareJsonPath()).asText(),
                e.at(InputOutputData.NAME.getFiwareJsonPath()).asText(),
                e.at(InputOutputData.LATITUDE.getFiwareJsonPath()).asDouble(),
                e.at(InputOutputData.LONGITUDE.getFiwareJsonPath()).asDouble())))
        .collect(Collectors
            .toMap(SimpleImmutableEntry::getKey,
                   SimpleImmutableEntry::getValue));
    // @formatter:on
  }

  @Override
  protected Query createFiwareQuery(Map<String, Object> inputData) {
    Query query = super.createFiwareQuery(inputData);

    Query.Entity entity = new Query.Entity();
    entity.type = FIWARE_TYPE;
    entity.idPattern =
        (inputData.containsKey(InputOutputData.BUS_STOP_ID.getName()))
            ? String
                .format(":%s$",
                        inputData.get(InputOutputData.BUS_STOP_ID.getName()))
            : ".*";

    query.addEntity(entity);

    return query;
  }

  @Override
  protected ObjectNode convertFiwareToBigiot(final ObjectNode src) {
    ObjectNode rootNode = super.convertFiwareToBigiot(src);

    // Retrieve bus stop name
    String stopId =
        rootNode.get(InputOutputData.BUS_STOP_ID.getName()).asText();
    StopInformation stopInfo = stops.get(stopId);
    if (stopInfo != null) {
      rootNode.put(InputOutputData.BUS_STOP_NAME.getName(), stopInfo.getName());
      rootNode.put(InputOutputData.LATITUDE.getName(), stopInfo.getLatitude());
      rootNode
          .put(InputOutputData.LONGITUDE.getName(), stopInfo.getLongitude());
    } else {
      rootNode.put(InputOutputData.BUS_STOP_NAME.getName(), UNKNOWN);
    }

    // Retrieve bus line name
    String lineId =
        rootNode.get(InputOutputData.BUS_LINE_ID.getName()).asText();
    String lineName = Optional.ofNullable(lines.get(lineId)).orElse(UNKNOWN);
    rootNode.put(InputOutputData.BUS_LINE_NAME.getName(), lineName);

    return rootNode;
  }

}
