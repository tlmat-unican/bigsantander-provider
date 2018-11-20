package es.unican.tlmat.smartsantander.bigiot.provider;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Scanner;

import org.eclipse.bigiot.lib.exceptions.IncompleteOfferingDescriptionException;
import org.eclipse.bigiot.lib.exceptions.NotRegisteredException;
import org.eclipse.bigiot.lib.misc.BridgeIotProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.unican.tlmat.smartsantander.bigiot.provider.fiware.OrionHttpClient;
import es.unican.tlmat.smartsantander.bigiot.provider.offerings.AirPollutionSensorsOffering;
import es.unican.tlmat.smartsantander.bigiot.provider.offerings.AtmosphericPressureSensorOffering;
import es.unican.tlmat.smartsantander.bigiot.provider.offerings.BikeSharingStationsOffering;
import es.unican.tlmat.smartsantander.bigiot.provider.offerings.BusArrivalEstimationOffering;
import es.unican.tlmat.smartsantander.bigiot.provider.offerings.BusesLocationOffering;
import es.unican.tlmat.smartsantander.bigiot.provider.offerings.NoiseSensorsOffering;
import es.unican.tlmat.smartsantander.bigiot.provider.offerings.ParkingSpaceAvailabilityOffering;
import es.unican.tlmat.smartsantander.bigiot.provider.offerings.RelativeHumiditySensorsOffering;
import es.unican.tlmat.smartsantander.bigiot.provider.offerings.TemperatureSensorsOffering;
import es.unican.tlmat.smartsantander.bigiot.provider.offerings.TrafficConditionsOffering;
import es.unican.tlmat.smartsantander.bigiot.provider.offerings.WindSensorsOffering;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;

/**
 * Hello world!.
 *
 */
public class App {
  private static final Logger log =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public static void main(String[] args) throws InterruptedException,
                                         IncompleteOfferingDescriptionException,
                                         IOException, NotRegisteredException {

    // TODO: When checked that shutdown hook works, try to use Callable<void>
    Options options = new Options();

    try {
      // Populate the created class from the command line arguments.
      CommandLine.populateCommand(options, args);
    } catch (ParameterException e) {
      // The given command line arguments are invalid, for example there
      // are options specified which do not exist or one of the options
      // is malformed (missing a value, for example).
      System.out.println(e.getMessage());
      CommandLine.usage(options, System.out);
      return;
    }

    // Print the state.
    if (options.isHelpRequested()) {
      CommandLine.usage(options, System.out);
      System.exit(1);
    }

    // Load configuration properties file
    Configuration config = Configuration.load(options.getConfigFile());

    BridgeIotProperties prop =
        BridgeIotProperties.load(options.getConfigFile());

    Provider smsProvider = new Provider(config);

    OrionHttpClient orion = new OrionHttpClient(config.getOrionUrl());

    smsProvider.start();

    smsProvider
        .registerOffering(ParkingSpaceAvailabilityOffering.create(orion));
    smsProvider.registerOffering(BikeSharingStationsOffering.create(orion));
    smsProvider.registerOffering(TrafficConditionsOffering.create(orion));
    smsProvider.registerOffering(BusArrivalEstimationOffering.create(orion));
    smsProvider.registerOffering(BusesLocationOffering.create(orion));
    smsProvider.registerOffering(NoiseSensorsOffering.create(orion));
    smsProvider.registerOffering(TemperatureSensorsOffering.create(orion));
    smsProvider.registerOffering(RelativeHumiditySensorsOffering.create(orion));
    smsProvider.registerOffering(WindSensorsOffering.create(orion));
    smsProvider
        .registerOffering(AtmosphericPressureSensorOffering.create(orion));
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

    // Run until user presses the ENTER key (debug configuration)
    boolean terminateUsingConsole = Boolean
        .parseBoolean(System.getProperty("bigiot.terminateUsingConsole"));
    if (terminateUsingConsole) {
      System.out
          .println(">>>>>>  Terminate SmartSantander Provider by pressing ENTER  <<<<<<");
      Scanner keyboard = new Scanner(System.in);
      keyboard.nextLine();
      keyboard.close();

      smsProvider.stop();
    }
  }

  /**
   * This is the main container which will be populated by picocli with values
   * from the arguments.
   */
  private static class Options {
    @Option(names = { "-h", "--help" }, description = "Prints this help text.")
    private boolean helpRequested = false;

    public boolean isHelpRequested() {
      return helpRequested;
    }

    @Option(names = { "-c", "--config" },
            description = "Prints this help text.")
    private String configFile = "config/smartsantander.properties";

    public String getConfigFile() {
      return configFile;
    }
  }
}
