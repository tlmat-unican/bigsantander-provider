package es.unican.tlmat.smartsantander.big_iot.provider.fiware;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.HttpURLConnection;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import okhttp3.CacheControl;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OrionHttpClient {
  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static String USER_AGENT = "BIG IoT/1.0 BigSantander Provider/1.0";
  private static final MediaType MEDIA_TYPE_JSON = MediaType
      .parse("application/json; charset=utf-8");

  private static final OkHttpClient client = new OkHttpClient.Builder()
      .connectTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS)
      .readTimeout(30, TimeUnit.SECONDS)
//      .cache(null)
      .build();

  private static final HttpUrl configUrl = HttpUrl
      .parse("http://orion-cb.tlmat.synchronicity-iot.eu:1026/v2/op/query");

  private static final HttpUrl defaultOrionUrl = new HttpUrl.Builder().scheme(configUrl.scheme())
      .host(configUrl.host()).port(configUrl.port()).addPathSegment("v2")
      .addQueryParameter("limit", "1000").addQueryParameter("options", "count").build();

  private static final Request defaultRequest = new Request.Builder()
      .addHeader("User-Agent", USER_AGENT).cacheControl(CacheControl.FORCE_NETWORK)
      .url(defaultOrionUrl).build();

  protected static final ObjectMapper mapper = new ObjectMapper();

  private final String orionHost;

  public OrionHttpClient(String url) {
    this.orionHost = url;
  }

  // IOException when there is a connection error or similar and should be translate to HTTP error 500
  public ArrayNode postQuery(Query query) throws IOException {
    HttpUrl url = defaultOrionUrl.newBuilder().addPathSegments("op/query")
        .addQueryParameter("options", "keyValues").build();

    RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, mapper.writeValueAsString(query));
    Request request = defaultRequest.newBuilder().url(url).post(body)
        .addHeader("Accept", "application/json").build();

    Response response = client.newCall(request).execute();
    if (response.code() != HttpURLConnection.HTTP_OK) {
      // Orion returns a JSON document
      throw new IOException(
          "HTTP error " + response.code() + "from Orion server: " + response.body().string());
    }

    final JsonNode fiwareNodes = mapper.reader().readTree(response.body().byteStream());
    if (fiwareNodes.isArray()) {
      return (ArrayNode) fiwareNodes;
    } else {
      throw new IllegalArgumentException("Expecting JSON array from Orion server");
    }
  }

  public String getEntityAttributeValue(String entityId, String attribute) throws IOException {
    HttpUrl url = defaultOrionUrl.newBuilder()
        .addPathSegment("entities")
        .addPathSegment(entityId)
        .addPathSegment("attrs")
        .addPathSegment(attribute)
        .addPathSegment("value")
        .build();

    Request request = defaultRequest.newBuilder()
        .url(url)
        .get()
        .build();

    Response response = client.newCall(request).execute();
    String name = response.body().string();
    // Trim double and single quotes from beginning and end
    return name.replaceAll("^[\"']+|[\"']+$", "");
  }
}
