package es.unican.tlmat.smartsantander.big_iot.provider.offerings;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.bigiot.lib.model.BigIotTypes.LicenseType;
import org.eclipse.bigiot.lib.model.BigIotTypes.PricingModel;
import org.eclipse.bigiot.lib.model.Price.Euros;
import org.eclipse.bigiot.lib.offering.OfferingDescription;
import org.eclipse.bigiot.lib.offering.RegistrableOfferingDescription;
import org.eclipse.bigiot.lib.offering.RegistrableOfferingDescriptionChain;

import com.fasterxml.jackson.databind.node.ObjectNode;

import es.unican.tlmat.smartsantander.big_iot.provider.fiware.ParkingSpotQuery;
import es.unican.tlmat.smartsantander.big_iot.provider.fiware.Query;

public class ParkingSpaceAvailabilityOffering extends GenericOffering {

  private static String DESCRIPTION = "SantanderParkingSpaceAvailabilityOffering";
  private static String NAME = "Santander Parking Space Availability Offering";
  private static String CATEGORY = "urn:big-iot:ParkingSpaceCategory";

  private static List<InputOutputData> inputData = Arrays.asList(InputOutputData.PARKING_SPOT_ID,
      InputOutputData.LONGITUDE, InputOutputData.LATITUDE, InputOutputData.RADIUS);

  private static List<InputOutputData> outputData = Arrays.asList(InputOutputData.PARKING_SPOT_ID,
      InputOutputData.LONGITUDE, InputOutputData.LATITUDE, InputOutputData.PARKING_SPOT_STATUS);

//  private static final Map<String, String> BIGIOT_TO_FIWARE_JSON_PATH = Stream
//      .of(new SimpleEntry<>(OutputData.ID.toString(), "/id"),
//          new SimpleEntry<>(OutputData.TIMESTAMP.toString(), "/dateModified"),
//          new SimpleEntry<>(OutputData.STATUS.toString(), "/status"),
//          new SimpleEntry<>(OutputData.LONGITUDE.toString(), "/location/coordinates/0"),
//          new SimpleEntry<>(OutputData.LATITUDE.toString(), "/location/coordinates/1"))
//      .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue));

  private static Collection<String> FIWARE_FIELDS = GenericOffering.getParentFiwareFieldFromJsonPat(outputData);

  private static ParkingSpaceAvailabilityOffering offering = null;

  public static final ParkingSpaceAvailabilityOffering getInstance() {
    if (offering == null) {
      offering = new ParkingSpaceAvailabilityOffering();
    }

    return offering;
  }

  private List<InputOutputData> getInputData() {
    return inputData;
  }

  private List<InputOutputData> getOutputData() {
    return outputData;
  }

  @Override
  public RegistrableOfferingDescription getOfferingDescription() {
    // TODO: Add InputData and OutputData based on defined enums
    RegistrableOfferingDescriptionChain offering = OfferingDescription
        .createOfferingDescription(DESCRIPTION).withName(NAME).withCategory(CATEGORY);

    getInputData().stream()
        .forEach(e -> offering.addInputData(e.getName(), e.getRdfAnnotation(), e.getValueType()));
    getOutputData().stream()
        .forEach(e -> offering.addOutputData(e.getName(), e.getRdfAnnotation(), e.getValueType()));

    return offering.withPrice(Euros.amount(0.001)).withPricingModel(PricingModel.PER_ACCESS)
        .withLicenseType(LicenseType.OPEN_DATA_LICENSE);
  }

  @Override
  public Query createFiwareQuery(Map<String, Object> inputData) {
    ParkingSpotQuery query = (inputData.containsKey(InputOutputData.PARKING_SPOT_ID.getName()))
        ? ParkingSpotQuery.create((String) inputData.get(InputOutputData.PARKING_SPOT_ID.getName()))
        : ParkingSpotQuery.create();

    if (inputData.containsKey(InputOutputData.LONGITUDE.getName())
        && inputData.containsKey(InputOutputData.LATITUDE.getName())
        && inputData.containsKey(InputOutputData.RADIUS.getName())) {
      query.withinAreaFilter(
          Double.parseDouble((String) inputData.get(InputOutputData.LATITUDE.getName())),
          Double.parseDouble((String) inputData.get(InputOutputData.LONGITUDE.getName())),
          Integer.parseUnsignedInt((String) inputData.get(InputOutputData.RADIUS.getName())));
    }

    return query;
  }

  @Override
  public ObjectNode transformFiwareToBigiot(final ObjectNode src) {
    ObjectNode rootNode = mapper.createObjectNode();

    getOutputData().forEach(v -> rootNode.set(v.getName(), src.at(v.getFiwareJsonPath())));

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
