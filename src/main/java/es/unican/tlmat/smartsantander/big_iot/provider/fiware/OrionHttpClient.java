package es.unican.tlmat.smartsantander.big_iot.provider.fiware;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class OrionHttpClient {
  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static String USER_AGENT = "BIG IoT/1.0 BigSantander Provider/1.0";

  private static final ObjectMapper mapper = new ObjectMapper();

  private final String orionHost;

  public OrionHttpClient(String url) {
    this.orionHost = url;
  }

  private HttpURLConnection makeQueryRequest(Query query) throws Exception {
    String queryParams = "options=keyValues,count&limit=1000";
    String url = orionHost.concat("?").concat(queryParams);
    URL obj = new URL(url);
    HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
    conn.setRequestMethod("POST");
    conn.setReadTimeout(10000);
    conn.setConnectTimeout(15000);
    conn.setDoInput(true);
    conn.setDoOutput(true);

    log.debug("adding headers");

    // Add request headers
    conn.setRequestProperty("User-Agent", USER_AGENT);
    conn.setRequestProperty("Content-Type", "application/json");
    conn.setRequestProperty("Accept", "application/json");

    // Write to output stream
    OutputStream out = conn.getOutputStream();
    mapper.writer().writeValue(out, query);
    // Meter en finally
    out.flush();

    log.debug("Connection sent");

    return conn;
  }

  private ArrayNode processQueryResponse(HttpURLConnection conn) throws IOException {
    int responseCode = conn.getResponseCode();
    log.debug("Response code" + responseCode);
    if (responseCode != HttpURLConnection.HTTP_OK) {
      // In case we want to capture the error message from the server
      // Orion returns a JSON document:
      // {
      // "error": "BadRequest",
      // "description": "Invalid value for URI param /options/"
      // }
      // InputStream stream = connection.getErrorStream();
      throw new IOException("HTTP error " + responseCode);
    }

    // Just in case the below code doesn't work
    // try (BufferedReader reader = new BufferedReader(
    // new InputStreamReader(myURLConnection.getInputStream()))) {
    // reader.lines().forEach(System.out::println);
    // }
    log.debug("Start parsing output");
    try {
      InputStream in = conn.getInputStream();
      final JsonNode fiwareNodes = mapper.reader().readTree(in);
      if (fiwareNodes.isArray()) {
        return (ArrayNode)fiwareNodes;
      } else {
        throw new IOException("Not expected response from Orion server.");
      }
    } catch (IOException e) {
      throw new IOException(e);
    }
  }

  public ArrayNode sendQuery(Query query) throws Exception {
    HttpURLConnection conn = makeQueryRequest(query);
    return processQueryResponse(conn);
  }

}
