package es.unican.tlmat.smartsantander.big_iot.provider.offerings;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.eclipse.bigiot.lib.handlers.AccessRequestHandler;
import org.eclipse.bigiot.lib.offering.OfferingDescription;
import org.eclipse.bigiot.lib.offering.RegistrableOfferingDescription;
import org.eclipse.bigiot.lib.serverwrapper.BigIotHttpResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import es.unican.tlmat.smartsantander.big_iot.provider.fiware.Query;

public abstract class GenericOffering implements AccessRequestHandler {

  private static String USER_AGENT = "BIG IoT/1.0 BigSantander Provider/1.0";
  private static String ORION_HOST = "BASE_URL_ORION/v2/op/query";

  // TODO: JSON numbers as strings
// JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS
  ObjectMapper mapper = new ObjectMapper();

  public abstract RegistrableOfferingDescription getOfferingDescription();

  public abstract Query createFiwareQuery(Map<String, Object> inputData);

  public abstract ObjectNode transformFiwareToBigiot(ObjectNode src);

  @Override
  public BigIotHttpResponse processRequestHandler(OfferingDescription offeringDescription,
      Map<String, Object> inputData, String subscriberId, String consumerInfo) {

    Query query = createFiwareQuery(inputData);

    try {
      ArrayNode jsonArray = sendQuery(query);
      String jsonString = mapper.writer().writeValueAsString(jsonArray);
      return BigIotHttpResponse.okay().withBody(jsonString).asJsonType();
    } catch (Exception e) {
      return BigIotHttpResponse.error().withBody(
          "{ \"error\": \"500\", \"description\": \"Internal server error while retrieving data\"")
          .asJsonType();
    }
  }

  private HttpsURLConnection makeHttpRequest(Query query) throws Exception {
    String queryParams = "options=keyValues,count&limit=1000";
    String url = ORION_HOST.concat("?").concat(queryParams);
    URL obj = new URL(url);
    HttpsURLConnection conn = (HttpsURLConnection) obj.openConnection();
    conn.setRequestMethod("POST");
    conn.setReadTimeout(10000);
    conn.setConnectTimeout(15000);
    conn.setDoInput(true);
    conn.setDoOutput(true);

    // Add request headers
    conn.setRequestProperty("User-Agent", USER_AGENT);
    conn.setRequestProperty("Content-Type", "application/json");
    conn.setRequestProperty("Accept", "application/json");

    // Write to output stream
    OutputStream out = conn.getOutputStream();
    mapper.writer().writeValue(out, query);
    // Meter en finally
    out.flush();

    return conn;
  }

  private ArrayNode processHttpResponse(HttpsURLConnection conn) throws IOException {
    int responseCode = conn.getResponseCode();
    if (responseCode != HttpURLConnection.HTTP_OK) {
      // In case we want to capture the error message from the server
      // Orion returns a JSON document:
      // {
      // "error": "BadRequest",
      // "description": "Invalid value for URI param /options/"
      // }
      // InputStream stream = connection.getErrorStream();
      throw new IOException("Error " + responseCode);
    }

    // Just in case the below code doesn't work
    // try (BufferedReader reader = new BufferedReader(
    // new InputStreamReader(myURLConnection.getInputStream()))) {
    // reader.lines().forEach(System.out::println);
    // }
    try {
      InputStream in = conn.getInputStream();
      final JsonNode fiwareNodes = mapper.reader().readTree(in);
      ArrayNode bigiotNodes = mapper.createArrayNode();
      if (fiwareNodes.isArray()) {
        for (final JsonNode node : fiwareNodes) {
          bigiotNodes.add(transformFiwareToBigiot((ObjectNode) node));
        }
        return bigiotNodes;
      } else {
        throw new IOException("mesaje");
      }
    } catch (IOException e) {
      throw new IOException(e);
    }
  }

  public ArrayNode sendQuery(Query query) throws Exception {
    HttpsURLConnection conn = makeHttpRequest(query);
    return processHttpResponse(conn);
  }
}

//private String getQueryParams(HashMap<String, String> params)
//throws UnsupportedEncodingException {
//StringBuilder result = new StringBuilder();
//boolean first = true;
//for (Map.Entry<String, String> entry : params.entrySet()) {
//if (first) {
//first = false;
//} else {
//result.append("&");
//}
//
//result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
//result.append("=");
//result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
//}
//
//return result.toString();
//}
//
//private String queryParam(String key, String value) throws UnsupportedEncodingException {
//return URLEncoder.encode(key, "UTF-8").concat("=").concat(URLEncoder.encode(value, "UTF-8"));
//}
