package es.unican.tlmat.smartsantander.bigiot.provider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.bigiot.lib.ProviderSpark;
import org.eclipse.bigiot.lib.embeddedspark.EmbeddedSpark;
import org.eclipse.bigiot.lib.exceptions.IncompleteOfferingDescriptionException;
import org.eclipse.bigiot.lib.exceptions.NotRegisteredException;
import org.eclipse.bigiot.lib.offering.Endpoints;
import org.eclipse.bigiot.lib.offering.OfferingId;
import org.eclipse.bigiot.lib.offering.RegisteredOffering;
import org.eclipse.bigiot.lib.offering.RegistrableOfferingDescription;

import es.unican.tlmat.smartsantander.bigiot.provider.offerings.GenericOffering;

public class Provider {

  List<OfferingId> registeredOfferings = new ArrayList<>();

  Configuration config;
  private ProviderSpark providerSpark;

  public Provider(Configuration config) {
    // BridgeIotProperties.load("smartsantander.properties");
    this.config = config;
  }

  // Start the provider instance
  public void start() throws IOException {
    startServer();
  }

  // Start the Spark Provider instance
  private void startServer() throws IOException {
    // Initialize a provider with Provider ID and Marketplace URI, the local
    // IP/DNS, etc., and
    // authenticate it on the Marketplace
    providerSpark =
        new ProviderSpark.Builder(config.getProviderId(), config.getMarketplaceUri())
            .setLocalDomain(config.getLocalDnsName())
            .setLocalPort(config.getLocalPort())
            .setPublicDomain(config.getPublicDnsName())
            .setPublicPort(config.getPublicPort())
            .setKeyStoreFile(config.getKeyStoreFile())
            .setKeyStorePassword(config.getKeyStorePassword())
            .build()
            .authenticate(config.getProviderSecret());

    // Either enable it here
    // Using the static caller/constructor in EmbeddedSpark class it didn't work
    // as expected
    ((EmbeddedSpark)providerSpark.getEmbeddedServer()).enableCorsAll();
  }

  public void
      registerOffering(GenericOffering offering) throws IncompleteOfferingDescriptionException,
                                                 NotRegisteredException {
    final RegistrableOfferingDescription offeringDescription =
        offering.getOfferingDescription();
    final Endpoints endpoint = Endpoints
        .create(offeringDescription)
        .withAccessRequestHandler(offering);

    // Register OfferingDescription on Marketplace - this will create a local
    // endpoint based on the
    // embedded Spark Web server
    RegisteredOffering registeredOffering =
        providerSpark.register(offeringDescription, endpoint);
    registeredOfferings.add(registeredOffering.getOfferingId());
  }

  // Unregister the Offering from the Marketplace
  public void unregisterOffering(GenericOffering offering) {
    final OfferingId offeringId =
        offering.getOfferingDescription().getOfferingId();
    providerSpark.deregister(offeringId);
    registeredOfferings.remove(offeringId);
  }

  // Unregister the Registered Offerings from the Marketplace
  public void unregisterOfferings() {
    Iterator<OfferingId> it = registeredOfferings.iterator();
    while (it.hasNext()) {
      OfferingId offeringId = it.next(); // must be called before you can call
                                         // i.remove()
      providerSpark.deregister(offeringId);
      it.remove();
    }

  }

  // Terminate the Spark Provider instance
  private void stopServer() {
    providerSpark.terminate();
  }

  // Terminate the provider instance
  public void stop() {
    unregisterOfferings();
    stopServer();
  }
}
