package es.unican.tlmat.smartsantander.big_iot.provider.offerings;

import java.util.Map;

import org.eclipse.bigiot.lib.model.BigIotTypes.LicenseType;
import org.eclipse.bigiot.lib.model.BigIotTypes.PricingModel;
import org.eclipse.bigiot.lib.model.BigIotTypes.ValueType;
import org.eclipse.bigiot.lib.model.Price.Euros;
import org.eclipse.bigiot.lib.offering.OfferingDescription;
import org.eclipse.bigiot.lib.offering.RegistrableOfferingDescription;

import com.fasterxml.jackson.databind.node.ObjectNode;

import es.unican.tlmat.smartsantander.big_iot.provider.fiware.ParkingSpotQuery;
import es.unican.tlmat.smartsantander.big_iot.provider.fiware.Query;

public class ParkingAvailabilityOffering extends GenericOffering {

  private static ParkingAvailabilityOffering offering = null;

  public static final ParkingAvailabilityOffering getInstance() {
    if (offering == null) {
      offering = new ParkingAvailabilityOffering();
    }

    return offering;
  }

  @Override
  public RegistrableOfferingDescription getOfferingDescription() {
    return OfferingDescription
        .createOfferingDescription("SantanderParkingSpaceAvailabilityOffering")
        .withName("Santander Parking Space Availability Offering")
        .withCategory("urn:big-iot:ParkingSpaceCategory")
        .addInputData("latitude", "schema:latitude", ValueType.NUMBER)
        .addInputData("longitude", "schema:longitude", ValueType.NUMBER)
        .addInputData("geoRadius", "schema:geoRadius", ValueType.NUMBER)
        .addOutputData("parkingSpotID", "mobility:parkingSpaceOrGroupIdentifier", ValueType.TEXT)
        .addOutputData("timestamp", "schema:dateTime", ValueType.DATETIME)
        .addOutputData("latitude", "schema:latitude", ValueType.NUMBER)
        .addOutputData("longitude", "schema:longitude", ValueType.NUMBER)
        .addOutputData("status", "mobility:ParkingSpaceStatus", ValueType.BOOLEAN)
        .withPrice(Euros.amount(0.001)).withPricingModel(PricingModel.PER_ACCESS)
        .withLicenseType(LicenseType.OPEN_DATA_LICENSE);
  }

  @Override
  public Query createFiwareQuery(Map<String, Object> inputData) {
    ParkingSpotQuery query = (inputData.containsKey("parkingSpotId"))
        ? ParkingSpotQuery.create((String) inputData.get("parkingSpotId"))
        : ParkingSpotQuery.create();

    if (inputData.containsKey("longitude") && inputData.containsKey("latitude")
        && inputData.containsKey("geoRadius")) {
      query.withinAreaFilter(Double.parseDouble((String) inputData.get("latitude")),
          Double.parseDouble((String) inputData.get("longitude")),
          Integer.parseUnsignedInt((String) inputData.get("geoRadius")));
    }

    return query;
  }

  @Override
  public ObjectNode transformFiwareToBigiot(ObjectNode src) {
    double longitude = src.get("location").get("coordinates").get(0).asDouble();
    double latitude = src.get("location").get("coordinates").get(1).asDouble();

    String id = src.get("id").asText();
    id = id.substring(id.lastIndexOf(':') + 1);

    String status = src.get("status").asText();
    String dateModified = src.get("dateModified").asText();

    ObjectNode rootNode = mapper.createObjectNode();
    rootNode.put("longitude", longitude).put("latitude", latitude).put("parkingSpotId", id)
        .put("status", status).put("timestamp", dateModified);

    return rootNode;
  }
}
