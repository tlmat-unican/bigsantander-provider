package es.unican.tlmat.smartsantander.big_iot.provider.offerings;

import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.bigiot.lib.handlers.AccessRequestHandler;
import org.eclipse.bigiot.lib.model.BigIotTypes.LicenseType;
import org.eclipse.bigiot.lib.model.BigIotTypes.PricingModel;
import org.eclipse.bigiot.lib.model.Price.Euros;
import org.eclipse.bigiot.lib.offering.OfferingDescription;
import org.eclipse.bigiot.lib.offering.RegistrableOfferingDescription;
import org.eclipse.bigiot.lib.offering.RegistrableOfferingDescriptionChain;
import org.eclipse.bigiot.lib.serverwrapper.BigIotHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import es.unican.tlmat.smartsantander.big_iot.provider.fiware.OrionHttpClient;
import es.unican.tlmat.smartsantander.big_iot.provider.fiware.Query;

public abstract class GenericOffering implements AccessRequestHandler {

  // TODO: hacerlo como builder o direcatement como parametros, pero guardar los enlaces del
  // builder.
  // https://stackoverflow.com/questions/17164375/subclassing-a-java-builder-class
  // https://stackoverflow.com/questions/21086417/builder-pattern-and-inheritance

  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static String USER_AGENT = "BIG IoT/1.0 BigSantander Provider/1.0";
  private static String ORION_HOST = "BASE_URL_ORION/v2/op/query";

  // TODO: JSON numbers as strings
// JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS
  protected static final ObjectMapper mapper = new ObjectMapper();

  private final List<InputOutputData> inputData;
  private final List<InputOutputData> outputData;
  private final Collection<String> fiwareFields;
  private final RegistrableOfferingDescription offeringDescription;

  protected GenericOffering(String name, String description, String category,
      List<InputOutputData> inputData, List<InputOutputData> outputData) {

    this.inputData = inputData;
    this.outputData = outputData;

    RegistrableOfferingDescriptionChain offeringChain = OfferingDescription
        .createOfferingDescription(description).withName(name).withCategory(category);

    getInputData().stream().forEach(
        e -> offeringChain.addInputData(e.getName(), e.getRdfAnnotation(), e.getValueType()));
    getOutputData().stream().forEach(
        e -> offeringChain.addOutputData(e.getName(), e.getRdfAnnotation(), e.getValueType()));

    offeringDescription = offeringChain.withPrice(Euros.amount(0.001))
        .withPricingModel(PricingModel.PER_ACCESS).withLicenseType(LicenseType.OPEN_DATA_LICENSE);

    fiwareFields = GenericOffering.getParentFiwareFieldFromJsonPath(outputData);

  }

  protected List<InputOutputData> getInputData() {
    return inputData;
  }

  protected List<InputOutputData> getOutputData() {
    return outputData;
  }

  public RegistrableOfferingDescription getOfferingDescription() {
    return offeringDescription;
  }

  public Collection<String> getFiwareFields() {
    return fiwareFields;
  }

  protected static Collection<String> getParentFiwareFieldFromJsonPath(
      Collection<InputOutputData> io) {
    return io.stream().map(e -> e.getFiwareJsonPath().split("/")[1]).collect(Collectors.toSet());
  }

  protected Query createFiwareQuery(Map<String, Object> inputData) {
    Query query = new Query();

    query.addAttributes(getFiwareFields());

    if (inputData.containsKey(InputOutputData.LONGITUDE.toString())
        && inputData.containsKey(InputOutputData.LATITUDE.toString())
        && inputData.containsKey(InputOutputData.RADIUS.toString())) {
      query.withinAreaFilter(
          Double.parseDouble((String) inputData.get(InputOutputData.LATITUDE.toString())),
          Double.parseDouble((String) inputData.get(InputOutputData.LONGITUDE.toString())),
          Integer.parseUnsignedInt((String) inputData.get(InputOutputData.RADIUS.toString())));
    }

    return query;
  }

  protected ObjectNode convertFiwareToBigiot(final ObjectNode src) {
    ObjectNode rootNode = mapper.createObjectNode();

    getOutputData().forEach(v -> rootNode.set(v.getName(), src.at(v.getFiwareJsonPath())));

    return rootNode;
  }

  @Override
  public BigIotHttpResponse processRequestHandler(OfferingDescription offeringDescription,
      Map<String, Object> inputData, String subscriberId, String consumerInfo) {

    OrionHttpClient orion = new OrionHttpClient(ORION_HOST);

    Query query = createFiwareQuery(inputData);

    try {
      ArrayNode fiwareNodes = orion.sendQuery(query);

      // Process Orion response
      ArrayNode bigiotNodes = mapper.createArrayNode();
      for (final JsonNode node : fiwareNodes) {
        bigiotNodes.add(convertFiwareToBigiot((ObjectNode) node));
      }

      String jsonString = mapper.writer().writeValueAsString(bigiotNodes);
      return BigIotHttpResponse.okay().withBody(jsonString).asJsonType();
    } catch (Exception e) {
      log.error("Ops!", e);
      return BigIotHttpResponse.error().withBody(
          "{ \"error\": \"500\", \"description\": \"Internal server error while retrieving data\"")
          .asJsonType();
    }
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
