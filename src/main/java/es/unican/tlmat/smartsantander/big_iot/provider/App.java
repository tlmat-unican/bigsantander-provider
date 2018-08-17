package es.unican.tlmat.smartsantander.big_iot.provider;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import org.eclipse.bigiot.lib.ProviderSpark;
import org.eclipse.bigiot.lib.exceptions.IncompleteOfferingDescriptionException;
import org.eclipse.bigiot.lib.exceptions.NotRegisteredException;
import org.eclipse.bigiot.lib.handlers.AccessRequestHandler;
import org.eclipse.bigiot.lib.misc.BridgeIotProperties;
import org.eclipse.bigiot.lib.model.AccessList;
import org.eclipse.bigiot.lib.model.BigIotTypes.AccessInterfaceType;
import org.eclipse.bigiot.lib.model.BigIotTypes.LicenseType;
import org.eclipse.bigiot.lib.model.BigIotTypes.PricingModel;
import org.eclipse.bigiot.lib.model.BigIotTypes.ValueType;
import org.eclipse.bigiot.lib.model.Price.Euros;
import org.eclipse.bigiot.lib.offering.Endpoints;
import org.eclipse.bigiot.lib.offering.OfferingDescription;
import org.eclipse.bigiot.lib.offering.RegistrableOfferingDescription;
import org.eclipse.bigiot.lib.serverwrapper.BigIotHttpResponse;
import org.json.JSONObject;

/**
 * Hello world!.
 *
 */
public class App {
  private static Random rand = new Random();

  private static AccessRequestHandler accessCallback = new AccessRequestHandler() {
    @Override
    public BigIotHttpResponse processRequestHandler(OfferingDescription offeringDescription,
        Map<String, Object> inputData, String subscriberId, String consumerInfo) {

      System.out.println("********************" + inputData.toString());
      
      
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
  };

  public static void main(String[] args) throws InterruptedException,
      IncompleteOfferingDescriptionException, IOException, NotRegisteredException {

    // Load example properties file
    BridgeIotProperties prop = BridgeIotProperties.load("smartsantander.properties");

    // Initialize a provider with Provider ID and Marketplace URI, the local IP/DNS, etc., and
    // authenticate it on the Marketplace
    ProviderSpark provider = ProviderSpark
        .create(prop.PROVIDER_ID, prop.MARKETPLACE_URI, prop.PROVIDER_DNS_NAME, prop.PROVIDER_PORT)
        .authenticate(prop.PROVIDER_SECRET);
    
    
    // provider.setProxy(prop.PROXY, prop.PROXY_PORT); //Enable this line if you are behind a proxy
    // provider.addProxyBypass(prop.PROXY_BYPASS); //Enable this line and the addresses for internal
    // hosts

    AccessList a = new AccessList();
    
    
    // Construct Offering Description of your Offering incrementally
    RegistrableOfferingDescription offeringDescription = OfferingDescription
        .createOfferingDescription("RandomNumberOffering").withName("Random Number Offering")
        .withCategory("urn:proposed:RandomValues")
        .withAccessStreamSessionTimeout(5)
        .addInputData("latitude", "http://schema.org/latitude", ValueType.NUMBER)
        .addInputDataInBody("latitude_body", "http://schema.org/latitude", ValueType.NUMBER)
        .addOutputData("value", "proposed:randomValue", ValueType.NUMBER)
        .addOutputData("timestamp", "schema:datePublished", ValueType.NUMBER)
        // .inRegion(BoundingBox.create(Location.create(42.1, 9.0), Location.create(43.2, 10.0)))
        // .withTimePeriod(new DateTime(2017, 1, 1, 0, 0, 0), new DateTime())
        .withPrice(Euros.amount(0.001)).withPricingModel(PricingModel.PER_ACCESS)
        .withLicenseType(LicenseType.OPEN_DATA_LICENSE)
        .asHttpPost();

    Endpoints endpoints = Endpoints.create(offeringDescription)
        .withAccessRequestHandler(accessCallback);

    // Register OfferingDescription on Marketplace - this will create a local endpoint based on the
    // embedded Spark Web server
    provider.register(offeringDescription, endpoints);

    // Run until user presses the ENTER key
    System.out.println(">>>>>>  Terminate ExampleProvider by pressing ENTER  <<<<<<");
    
    //Thread.currentThread().join();
    // O usar un mutex y hacer que se active cuando se produzca un ctl-c o similar
    //new Semaphore(0).acquire();
    Scanner keyboard = new Scanner(System.in);
    keyboard.nextLine();
    keyboard.close();

    System.out.println("Deregister Offering");

    // Deregister the Offering from the Marketplace
    provider.deregister(offeringDescription);

    // Terminate the Provider instance
    provider.terminate();

  }

}
