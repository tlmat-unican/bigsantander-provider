package es.unican.tlmat.smartsantander.big_iot.provider;

import java.io.IOException;
import java.util.Scanner;

import org.eclipse.bigiot.lib.exceptions.IncompleteOfferingDescriptionException;
import org.eclipse.bigiot.lib.exceptions.NotRegisteredException;
import org.eclipse.bigiot.lib.misc.BridgeIotProperties;

import es.unican.tlmat.smartsantander.big_iot.provider.offerings.ParkingAvailabilityOffering;

/**
 * Hello world!.
 *
 */
public class App {

  public static void main(String[] args) throws InterruptedException,
      IncompleteOfferingDescriptionException, IOException, NotRegisteredException {

    // Load example properties file
    BridgeIotProperties prop = BridgeIotProperties.load("smartsantander.properties");

    Provider smsProvider = new Provider(prop);

    smsProvider.registerOffering(ParkingAvailabilityOffering.getInstance());

    // Run until user presses the ENTER key
    System.out.println(">>>>>>  Terminate SmartSantander Provider by pressing ENTER  <<<<<<");
    Scanner keyboard = new Scanner(System.in);
    keyboard.nextLine();

    smsProvider.stop();

//    final Thread mainThread = Thread.currentThread();
//    Runtime.getRuntime().addShutdownHook(new Thread() {
//      @Override
//      public void run() {
//        System.out.println("Shutdown hook running for graceful shutdown");
//        // Deregister the Offering from the Marketplace
//        provider.deregister(offeringDescription);
//
//        // Terminate the Provider instance
//        provider.terminate();
//        
//        try {
//          mainThread.join();
//        } catch (InterruptedException e) {
//          // TODO Auto-generated catch block
//          e.printStackTrace();
//        }
//      }
//    });

  }

}
