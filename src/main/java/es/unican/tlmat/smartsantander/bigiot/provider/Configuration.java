package es.unican.tlmat.smartsantander.bigiot.provider;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configuration {

  private static final Logger log =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static final String MARKETPLACE_URI = "marketplaceUri";
  private static final String PROVIDER_ID = "providerId";
  private static final String PROVIDER_SECRET = "providerSecret";

  private static final String LOCAL_DNS_NAME = "providerLocalDnsName";
  private static final String LOCAL_PORT = "providerLocalPort";
  private static final String PUBLIC_DNS_NAME = "providerPublicDnsName";
  private static final String PUBLIC_PORT = "providerPublicPort";
  private static final String ORION_URL = "orion";

  private static final String KEYSTORE_FILE = "keyStoreFile";
  private static final String KEYSTORE_PASS = "keyStorePassword";

  Properties props;

  private Configuration(final String path) throws IOException {
    props = new Properties();

    try (FileInputStream fs = new FileInputStream(new File(path))) {
      props.load(fs);
    }

    log.info("Configuration read from file '" + path + "'");
  }

  public static Configuration load(final String path) throws IOException {
    return new Configuration(path);
  }

  /**
   * Gets the requested property
   *
   * @param key
   *          The key of the property
   * @param defaultValue
   *          The default value for the requested key
   *
   * @return the requested property
   */
  private String getProperty(String key, String defaultValue) {
    return props.getProperty(key, defaultValue);
  }

  /**
   * Gets the requested property
   *
   * @param key
   *          The key of the property
   *
   * @return the requested property
   */
  private String getProperty(String key) {
    return props.getProperty(key);
  }

  public String getMarketplaceUri() {
    // TODO Auto-generated method stub
    return getProperty(MARKETPLACE_URI);
  }

  public String getProviderId() {
    // TODO Auto-generated method stub
    return getProperty(PROVIDER_ID);
  }

  public String getProviderSecret() {
    // TODO Auto-generated method stub
    return getProperty(PROVIDER_SECRET);
  }

  public String getLocalDnsName() {
    return getProperty(LOCAL_DNS_NAME);
  }

  public int getLocalPort() {
    return Integer.parseInt(getProperty(LOCAL_PORT));
  }

  public String getPublicDnsName() {
    return getProperty(PUBLIC_DNS_NAME);
  }

  public int getPublicPort() {
    return Integer.parseInt(getProperty(PUBLIC_PORT));
  }

  public String getOrionUrl() {
    return getProperty(ORION_URL);
  }

  public String getKeyStoreFile() {
    return getProperty(KEYSTORE_FILE);
  }

  public String getKeyStorePassword() {
    return getProperty(KEYSTORE_PASS);
  }

}
