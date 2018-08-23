package es.unican.tlmat.smartsantander.big_iot.provider.offerings;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.bigiot.lib.model.BigIotTypes.LicenseType;
import org.eclipse.bigiot.lib.model.BigIotTypes.PricingModel;
import org.eclipse.bigiot.lib.model.BigIotTypes.ValueType;
import org.eclipse.bigiot.lib.model.Price.Euros;
import org.eclipse.bigiot.lib.offering.OfferingDescription;
import org.eclipse.bigiot.lib.offering.RegistrableOfferingDescription;

import com.fasterxml.jackson.databind.node.ObjectNode;

import es.unican.tlmat.smartsantander.big_iot.provider.fiware.ParkingSpotQuery;
import es.unican.tlmat.smartsantander.big_iot.provider.fiware.Query;

public class ParkingSpaceAvailabilityOffering extends GenericOffering {

  private static String DESCRIPTION = "SantanderParkingSpaceAvailabilityOffering";
  private static String NAME = "Santander Parking Space Availability Offering";
  private static String CATEGORY = "urn:big-iot:ParkingSpaceCategory";

  private enum InputData {
    ID("parkingSpotId"), LONGITUDE("longitude"), LATITUDE("latitude"), RADIUS("geoRadius");

    private final String value;

    InputData(final String s) {
      value = s;
    }

    @Override
    public String toString() {
      return value;
    }
  }

  private enum OutputData {
    ID("parkingSpotId"), TIMESTAMP("timestamp"), STATUS("status"), LONGITUDE("longitude"),
    LATITUDE("latitude");

    private final String value;

    OutputData(final String s) {
      value = s;
    }

    @Override
    public String toString() {
      return value;
    }
  }

  private static final Map<String, String> BIGIOT_TO_FIWARE_JSON_PATH = Stream
      .of(new SimpleEntry<>(OutputData.ID.toString(), "/id"),
          new SimpleEntry<>(OutputData.TIMESTAMP.toString(), "/dateModified"),
          new SimpleEntry<>(OutputData.STATUS.toString(), "/status"),
          new SimpleEntry<>(OutputData.LONGITUDE.toString(), "/location/coordinates/0"),
          new SimpleEntry<>(OutputData.LATITUDE.toString(), "/location/coordinates/1"))
      .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue));

  private static final Collection<String> FIWARE_FIELDS = GenericOffering
      .getParentFiwareFieldFromJsonPath(BIGIOT_TO_FIWARE_JSON_PATH.values());

  private static ParkingSpaceAvailabilityOffering offering = null;

  public static final ParkingSpaceAvailabilityOffering getInstance() {
    if (offering == null) {
      offering = new ParkingSpaceAvailabilityOffering();
    }

    return offering;
  }

  @Override
  public RegistrableOfferingDescription getOfferingDescription() {
    // TODO: Add InputData and OutputData based on defined enums
    return OfferingDescription.createOfferingDescription(DESCRIPTION).withName(NAME)
        .withCategory(CATEGORY)
        .addInputData(InputData.LATITUDE.toString(), "schema:latitude", ValueType.NUMBER)
        .addInputData(InputData.LONGITUDE.toString(), "schema:longitude", ValueType.NUMBER)
        .addInputData(InputData.RADIUS.toString(), "schema:geoRadius", ValueType.NUMBER)
        .addInputData(InputData.ID.toString(), "mobility:parkingSpaceOrGroupIdentifier",
            ValueType.NUMBER)
        .addOutputData(OutputData.ID.toString(), "mobility:parkingSpaceOrGroupIdentifier",
            ValueType.TEXT)
        .addOutputData(OutputData.TIMESTAMP.toString(), "schema:dateTime", ValueType.DATETIME)
        .addOutputData(OutputData.LATITUDE.toString(), "schema:latitude", ValueType.NUMBER)
        .addOutputData(OutputData.LONGITUDE.toString(), "schema:longitude", ValueType.NUMBER)
        .addOutputData(OutputData.STATUS.toString(), "mobility:parkingSpaceStatus", ValueType.TEXT)
        .withPrice(Euros.amount(0.001)).withPricingModel(PricingModel.PER_ACCESS)
        .withLicenseType(LicenseType.OPEN_DATA_LICENSE);
  }

  @Override
  public Query createFiwareQuery(Map<String, Object> inputData) {
    ParkingSpotQuery query = (inputData.containsKey(InputData.ID.toString()))
        ? ParkingSpotQuery.create((String) inputData.get(InputData.ID.toString()))
        : ParkingSpotQuery.create();

    if (inputData.containsKey(InputData.LONGITUDE.toString())
        && inputData.containsKey(InputData.LATITUDE.toString())
        && inputData.containsKey(InputData.RADIUS.toString())) {
      query.withinAreaFilter(
          Double.parseDouble((String) inputData.get(InputData.LATITUDE.toString())),
          Double.parseDouble((String) inputData.get(InputData.LONGITUDE.toString())),
          Integer.parseUnsignedInt((String) inputData.get(InputData.RADIUS.toString())));
    }

    return query;
  }

  @Override
  public ObjectNode transformFiwareToBigiot(final ObjectNode src) {
    ObjectNode rootNode = mapper.createObjectNode();

    BIGIOT_TO_FIWARE_JSON_PATH.forEach((k, v) -> rootNode.set(k, src.at(v)));

//    double longitude = src.get("location").get("coordinates").get(0).asDouble();
//    double latitude = src.get("location").get("coordinates").get(1).asDouble();
//
//    // With a for loop over keys in a map
//    // obj.put("longitude", obj.get("serviceCentreLon"));
//    // obj.remove("serviceCentreLon");
//    // JsonNode coordinatesNode = node.at("/address/coordinates");
//    // int pincode = node.at("/delivery_codes/0/postal_code/pin").asInt();
//
//    String id = src.get("id").asText();
//    id = id.substring(id.lastIndexOf(':') + 1);
//
//    String status = src.get("status").asText();
//    String dateModified = src.get("dateModified").asText();
//
//    ObjectNode rootNode = mapper.createObjectNode();
//    rootNode.put("longitude", longitude).put("latitude", latitude).put("parkingSpotId", id)
//        .put("status", status).put("timestamp", dateModified);

    return rootNode;
  }

  @Override
  public Collection<String> getFiwareFields() {
    return FIWARE_FIELDS;
  }
}
