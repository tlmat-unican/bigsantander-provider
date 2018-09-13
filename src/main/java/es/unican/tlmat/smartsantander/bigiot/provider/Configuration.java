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

  private static final String ORION_URL = "orion";

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

  public String getOrionUrl() {
    return getProperty(ORION_URL);
  }

}
