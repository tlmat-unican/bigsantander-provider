package es.unican.tlmat.smartsantander.big_iot.provider.offerings;

import java.util.Date;
import java.util.Map;
import java.util.Random;

import org.eclipse.bigiot.lib.model.BigIotTypes.LicenseType;
import org.eclipse.bigiot.lib.model.BigIotTypes.PricingModel;
import org.eclipse.bigiot.lib.model.BigIotTypes.ValueType;
import org.eclipse.bigiot.lib.model.Price.Euros;
import org.eclipse.bigiot.lib.offering.OfferingDescription;
import org.eclipse.bigiot.lib.offering.RegistrableOfferingDescription;
import org.eclipse.bigiot.lib.serverwrapper.BigIotHttpResponse;
import org.json.JSONObject;

public class ParkingAvailabilityOffering extends GenericOffering {

  private static ParkingAvailabilityOffering offering =  null;
  
  private static Random rand = new Random();
  
  public static final ParkingAvailabilityOffering getInstance() {
    if (offering == null) {
      offering = new ParkingAvailabilityOffering();
    }
    
    return offering;
  }
  
  public RegistrableOfferingDescription getOfferingDescription() {
    return OfferingDescription.createOfferingDescription("RandomNumberOffering")
        .withName("Random Number Offering").withCategory("urn:proposed:RandomValues")
        // .addInputData("latitude", "http://schema.org/latitude", ValueType.NUMBER)
        .addOutputData("value", "proposed:randomValue", ValueType.NUMBER)
        .addOutputData("timestamp", "schema:datePublished", ValueType.NUMBER)
        // .inRegion(BoundingBox.create(Location.create(42.1, 9.0), Location.create(43.2, 10.0)))
        // .withTimePeriod(new DateTime(2017, 1, 1, 0, 0, 0), new DateTime())
        .withPrice(Euros.amount(0.001)).withPricingModel(PricingModel.PER_ACCESS)
        .withLicenseType(LicenseType.OPEN_DATA_LICENSE);
  }

  @Override
  public BigIotHttpResponse processRequestHandler(OfferingDescription offeringDescription,
      Map<String, Object> inputData, String subscriberId, String consumerInfo) {
    /*
     * double longitude = 41.0; if (inputData.containsKey("longitude")) longitude =
     * Double.parseDouble((String) inputData.get("longitude"));
     * 
     * double latitude = 9.0; if (inputData.containsKey("latitude")) latitude =
     * Double.parseDouble((String) inputData.get("latitude"));
     */

    // Prepare the offering response as a JSONObject/Array - according to the Output Data defined
    // in the Offering Description
    JSONObject number = new JSONObject();
    number.put("value", rand.nextFloat());
    number.put("timestamp", new Date().getTime());

    // Send the response as JSON in the form: { [ { "value" : 0.XXX, "timestamp" : YYYYYYY } ] }
    return BigIotHttpResponse.okay().withBody(number).asJsonType();
  }
}
