package es.unican.tlmat.smartsantander.big_iot.provider;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Scanner;

import org.eclipse.bigiot.lib.exceptions.IncompleteOfferingDescriptionException;
import org.eclipse.bigiot.lib.exceptions.NotRegisteredException;
import org.eclipse.bigiot.lib.misc.BridgeIotProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.unican.tlmat.smartsantander.big_iot.provider.fiware.OrionHttpClient;
import es.unican.tlmat.smartsantander.big_iot.provider.offerings.AirPollutionSensorsOffering;
import es.unican.tlmat.smartsantander.big_iot.provider.offerings.AtmosphericPressureSensorOffering;
import es.unican.tlmat.smartsantander.big_iot.provider.offerings.BikeSharingStationsOffering;
import es.unican.tlmat.smartsantander.big_iot.provider.offerings.BusArrivalEstimationOffering;
import es.unican.tlmat.smartsantander.big_iot.provider.offerings.NoiseSensorsOffering;
import es.unican.tlmat.smartsantander.big_iot.provider.offerings.ParkingSpaceAvailabilityOffering;
import es.unican.tlmat.smartsantander.big_iot.provider.offerings.RelativeHumiditySensorsOffering;
import es.unican.tlmat.smartsantander.big_iot.provider.offerings.TemperatureSensorsOffering;
import es.unican.tlmat.smartsantander.big_iot.provider.offerings.TrafficConditionsOffering;
import es.unican.tlmat.smartsantander.big_iot.provider.offerings.WindSensorsOffering;

/**
 * Hello world!.
 *
 */
public class App {
  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public static void main(String[] args) throws InterruptedException,
      IncompleteOfferingDescriptionException, IOException, NotRegisteredException {

    // Load example properties file
    BridgeIotProperties prop = BridgeIotProperties.load("smartsantander.properties");

    Configuration config = Configuration.load("smartsantander.properties");

    Provider smsProvider = new Provider(prop);

    OrionHttpClient orion = new OrionHttpClient(config.getOrionUrl());

    smsProvider.start();

    smsProvider.registerOffering(ParkingSpaceAvailabilityOffering.create(orion));
    smsProvider.registerOffering(BikeSharingStationsOffering.create(orion));
    smsProvider.registerOffering(TrafficConditionsOffering.create(orion));
    smsProvider.registerOffering(BusArrivalEstimationOffering.create(orion));
    smsProvider.registerOffering(NoiseSensorsOffering.create(orion));
    smsProvider.registerOffering(TemperatureSensorsOffering.create(orion));
    smsProvider.registerOffering(RelativeHumiditySensorsOffering.create(orion));
    smsProvider.registerOffering(WindSensorsOffering.create(orion));
    smsProvider.registerOffering(AtmosphericPressureSensorOffering.create(orion));
    smsProvider.registerOffering(AirPollutionSensorsOffering.create(orion));

    final Thread mainThread = Thread.currentThread();
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        log.info("Shutdown hook running for graceful shutdown");
        // Deregister the Offering from the Marketplace
        smsProvider.stop();

        try {
          mainThread.join();
        } catch (InterruptedException e) {
          log.error("Shutdown", e);
          Thread.currentThread().interrupt();
        }
      }
    });

    // Run until user presses the ENTER key
    System.out.println(">>>>>>  Terminate SmartSantander Provider by pressing ENTER  <<<<<<");
    Scanner keyboard = new Scanner(System.in);
    keyboard.nextLine();
    keyboard.close();

    smsProvider.stop();
  }

}
