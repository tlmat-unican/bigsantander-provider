/**
 * Copyright (c) 2016-2017 in alphabetical order:
 * Bosch Software Innovations GmbH, Robert Bosch GmbH, Siemens AG
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Denis Kramer     (Bosch Software Innovations GmbH)
 *    Stefan Schmid    (Robert Bosch GmbH)
 *    Andreas Ziller   (Siemens AG)
 */
package org.eclipse.bigiot.lib;

import java.io.IOException;

import org.eclipse.bigiot.lib.embeddedspark.EmbeddedSpark;
import org.eclipse.bigiot.lib.embeddedspark.ServerOptionsSpark;
import org.eclipse.bigiot.lib.exceptions.IncompleteOfferingDescriptionException;
import org.eclipse.bigiot.lib.exceptions.InvalidOfferingException;
import org.eclipse.bigiot.lib.exceptions.NotRegisteredException;
import org.eclipse.bigiot.lib.misc.Constants;
import org.eclipse.bigiot.lib.offering.RegisteredOffering;
import org.eclipse.bigiot.lib.offering.RegistrableOfferingDescription;
import org.eclipse.bigiot.lib.offering.RegistrableOfferingDescriptionChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProviderSpark extends Provider {

  private static final Logger logger =
      LoggerFactory.getLogger(ProviderSpark.class);

  public static class Builder {

    private static String DEFAULT_LOCAL_DOMAIN = "localhost";
    private static int DEFAULT_LOCAL_PORT = 9004;
    private static String DEFAULT_BASE_ROUTE = "access";

    private String providerId;
    private String marketplaceUri;
    private String localDomain = DEFAULT_LOCAL_DOMAIN;
    private int localPort = DEFAULT_LOCAL_PORT;
    private String publishDomain;
    private int publishPort = 0;
    private String baseRoute = DEFAULT_BASE_ROUTE;
    private String keyStoreFile;
    private String keyStorePassword;

    public Builder(String providerId, String marketplaceUri) {
      this.providerId = providerId;
      this.marketplaceUri = marketplaceUri;
    }

    public Builder setLocalDomain(String localDomain) {
      this.localDomain = localDomain;
      return this;
    }

    public Builder setLocalPort(int localPort) {
      this.localPort = localPort;
      return this;
    }

    public Builder setLocalNetworkInfo(String localDomain, int localPort) {
      return this.setLocalDomain(localDomain).setLocalPort(localPort);
    }

    public Builder setPublicDomain(String publishDomain) {
      this.publishDomain = publishDomain;
      return this;
    }

    public Builder setPublicPort(int publishPort) {
      this.publishPort = publishPort;
      return this;
    }

    public Builder setPublicNetworkInfo(String publishDomain, int publishPort) {
      return this.setPublicDomain(publishDomain).setPublicPort(publishPort);
    }

    public Builder setBaseRoute(String baseRoute) {
      this.baseRoute = baseRoute;
      return this;
    }

    public Builder setKeyStoreFile(String keyStoreFile) {
      System.out.println(keyStoreFile);
      this.keyStoreFile = keyStoreFile;
      return this;
    }

    public Builder setKeyStorePassword(String keyStorePassword) {
      System.out.println(keyStorePassword);
      this.keyStorePassword = keyStorePassword;
      return this;
    }

    public ProviderSpark build() {
      // Check publishing parameters
      if (publishDomain.isEmpty()) {
        publishDomain = localDomain;
      }
      if (publishPort == 0) {
        publishPort = localPort;
      }



      return new ProviderSpark(providerId, marketplaceUri, localDomain,
                               localPort, publishDomain, publishPort,
                               baseRoute, keyStoreFile, keyStorePassword);
    }
  }

  protected ProviderSpark(String providerId, String marketplaceUri,
      String localDomain, int localPort) {
    super(providerId, marketplaceUri);
    server =
        new EmbeddedSpark(localDomain, localPort, Constants.DEFAULT_BASE_ROUTE,
                          ServerOptionsSpark.defaultOptions);
    server.start();

    setBaseUrl(new StringBuilder()
        .append("https://")
        .append(localDomain)
        .append(":")
        .append(localPort)
        .append("/")
        .append(Constants.DEFAULT_BASE_ROUTE)
        .toString());
  }

  protected ProviderSpark(String providerId, String marketplaceUri,
      String localDomain, int localPort, String publishDomain, int publishPort,
      String baseRoute) {
    super(providerId, marketplaceUri);
    server = new EmbeddedSpark(localDomain, localPort, baseRoute,
                               ServerOptionsSpark.defaultOptions);
    server.start();

    StringBuilder sb = new StringBuilder()
        .append("https://")
        .append(publishDomain)
        .append(":")
        .append(publishPort);
    if (!baseRoute.isEmpty()) {
      sb.append("/").append(baseRoute);
    }
    setBaseUrl(sb.toString());
  }

  protected ProviderSpark(String providerId, String marketplaceUri,
      String localDomain, int localPort, String publishDomain, int publishPort,
      String baseRoute, String keyStoreFile, String keyStorePassword) {
    super(providerId, marketplaceUri);
    server = new EmbeddedSpark(localDomain, localPort, baseRoute,
                               ServerOptionsSpark.defaultOptions);
    server.start(keyStoreFile, keyStorePassword);

    StringBuilder sb = new StringBuilder()
        .append("https://")
        .append(publishDomain)
        .append(":")
        .append(publishPort);
    if (!baseRoute.isEmpty()) {
      sb.append("/").append(baseRoute);
    }
    setBaseUrl(sb.toString());



  }

  /**
   * Instantiates the Provider instance
   *
   */
  public static ProviderSpark create(String providerId, String marketplaceUri,
                                     String localDomain, int localPort) {
    return new ProviderSpark(providerId, marketplaceUri, localDomain,
                             localPort);
  }

  /**
   * Authenticates instance at the Marketplace.
   *
   * @param clientSecret
   *          API Key for authentication at the marketplace
   * @throws IOException
   */
  @Override
  public ProviderSpark authenticate(String clientSecret) throws IOException {
    return (ProviderSpark) super.authenticate(clientSecret);
  }

  /**
   * Creates a basic offering description for registration at the marketplace.
   *
   * @return
   */
  @Override
  public RegistrableOfferingDescriptionChain
      createOfferingDescription(String localId) {
    RegistrableOfferingDescriptionChain registrableOfferingDescriptionChain =
        new RegistrableOfferingDescriptionChain(localId, this, this.clientId,
                                                this.marketplaceClient,
                                                registeredOfferingMap);

    return registrableOfferingDescriptionChain
        .withRoute(localId)
        .deployOn(server);
  }

  @Override
  public RegisteredOffering
      register(RegistrableOfferingDescription offeringDescription) throws IncompleteOfferingDescriptionException,
                                                                   NotRegisteredException {

    if (offeringDescription.getProvider() == null) {
      offeringDescription.setProvider(this);
      offeringDescription.setProviderId(getClientId().toString());
      offeringDescription.setMarketplaceClient(getMarketplaceClient());
      offeringDescription.setOfferingMap(this.registeredOfferingMap);
      offeringDescription.setServerAndDefaultEndpoint(server);
    }
    RegisteredOffering offering = offeringDescription.register();
    registeredOfferingMap.put(offering.getOfferingId(), offering);
    return offering;
  }

  /**
   * Retrieves the offering description from the marketplace referenced by the
   * offering ID.
   *
   * @param offeringId
   * @return
   * @throws IOException
   * @throws InvalidOfferingException
   */
  @Override
  public RegistrableOfferingDescriptionChain
      createOfferingDescriptionFromOfferingId(String offeringId) throws InvalidOfferingException,
                                                                 IOException {
    RegistrableOfferingDescriptionChain registrableOfferingDescriptionChain =
        createOfferingDescription("");
    return registrableOfferingDescriptionChain
        .useOfferingDescription(offeringId);
  }

  @Override
  public void terminate() {
    super.terminate();
    server.stop();
  }

  /**
   * Enables proxy configuration only if it is required. This is useful if
   * connections are sometimes from inside a
   * proxy-gated network
   *
   * @param proxyHost
   * @param proxyPort
   * @return
   */
  @Override
  public ProviderSpark withAutoProxy(String proxyHost, int proxyPort) {
    return (ProviderSpark) super.withAutoProxy(proxyHost, proxyPort);
  }

}
