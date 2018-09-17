package es.unican.tlmat.smartsantander.bigiot.provider.offerings;

import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.eclipse.bigiot.lib.handlers.AccessRequestHandler;
import org.eclipse.bigiot.lib.model.BigIotTypes.LicenseType;
import org.eclipse.bigiot.lib.model.BigIotTypes.PricingModel;
import org.eclipse.bigiot.lib.model.BoundingBox;
import org.eclipse.bigiot.lib.model.Location;
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

import es.unican.tlmat.smartsantander.bigiot.provider.fiware.OrionHttpClient;
import es.unican.tlmat.smartsantander.bigiot.provider.fiware.Query;
import es.unican.tlmat.utils.collectors.CustomCollectors;

public abstract class GenericOffering implements AccessRequestHandler {

  // TODO: hacerlo como builder o direcatement como parametros, pero guardar los
  // enlaces del
  // builder.
  // https://stackoverflow.com/questions/17164375/subclassing-a-java-builder-class
  // https://stackoverflow.com/questions/21086417/builder-pattern-and-inheritance

  private static final Logger log =
      LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  // TODO: JSON numbers as strings
  // JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS
  protected static final ObjectMapper mapper = new ObjectMapper();

  private final List<InputOutputData> inputData;
  private final List<InputOutputData> outputData;
  private final Collection<String> mandatoryOutputData;
  private final Collection<String> fiwareFields;
  private final RegistrableOfferingDescription offeringDescription;

  private final OrionHttpClient orion;

  protected GenericOffering(OrionHttpClient orionHttpClient, String name,
      String description, String category, List<InputOutputData> inputData,
      List<InputOutputData> outputData,
      List<InputOutputData> mandatoryOutputData) {

    this.orion = orionHttpClient;
    this.inputData = inputData;
    this.outputData = outputData;
    this.mandatoryOutputData = mandatoryOutputData
        .stream()
        .map(InputOutputData::getName)
        .collect(Collectors.toSet());

    RegistrableOfferingDescriptionChain offeringChain = OfferingDescription
        .createOfferingDescription(description)
        .withName(name)
        .withCategory(category)
        .inRegion(BoundingBox
            .create(Location.create(43.49711, -3.85454),
                    Location.create(43.41802, -3.75887)));
    // .inRegion(Region.create(City.create("Santander")));

    getInputData()
        .stream()
        .forEach(e -> offeringChain
            .addInputData(e.getName(), e.getRdfAnnotation(), e.getValueType()));
    getOutputData()
        .stream()
        .forEach(e -> offeringChain
            .addOutputData(e.getName(),
                           e.getRdfAnnotation(),
                           e.getValueType()));

    offeringDescription = offeringChain
        .withPrice(Euros.amount(0.001))
        .withPricingModel(PricingModel.PER_ACCESS)
        .withLicenseType(LicenseType.OPEN_DATA_LICENSE);

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

  protected OrionHttpClient getOrionHttpClient() {
    return orion;
  }

  protected static Collection<String>
      getParentFiwareFieldFromJsonPath(Collection<InputOutputData> io) {
    return io
        .stream()
        .map(e -> e.getFiwareJsonPath().split("/")[1])
        .collect(Collectors.toSet());
  }

  protected Query createFiwareQuery(Map<String, Object> inputData) {
    Query query = new Query();

    query.addAttributes(getFiwareFields());

    if (inputData.containsKey(InputOutputData.LONGITUDE.toString())
        && inputData.containsKey(InputOutputData.LATITUDE.toString())
        && inputData.containsKey(InputOutputData.RADIUS.toString())) {
      query
          .withinAreaFilter(Double
              .parseDouble((String) inputData
                  .get(InputOutputData.LATITUDE.toString())),
                            Double
                                .parseDouble((String) inputData
                                    .get(InputOutputData.LONGITUDE.toString())),
                            Integer
                                .parseUnsignedInt((String) inputData
                                    .get(InputOutputData.RADIUS.toString())));
    }

    return query;
  }

  protected ObjectNode convertFiwareToBigiot(final ObjectNode src) {
    ObjectNode rootNode = mapper.createObjectNode();

    getOutputData().forEach(v -> {
      // It includes null value if field doesn't exist
      // rootNode.set(v.getName(), src.at(v.getFiwareJsonPath()));
      JsonNode value = src.at(v.getFiwareJsonPath());
      if (!value.isMissingNode()) {
        rootNode.set(v.getName(), value);
      }
    });

    return rootNode;
  }

  // true if there is any field that is not defaultFields
  // true if a JSON node with data
  private boolean checkNotEmptyBigIotValue(final JsonNode node) {
    Iterable<String> iterable = () -> node.fieldNames();
    // Stream<String> defaultFields = Stream.of("latitude", "longitude",
    // "timestamp", "id");
    // Supplier<Stream<String>> streamSupplier = () -> defaultFields;
    // streamSupplier.get().findAny();

    // noneMatch returns true if none of element of stream matches
    // return StreamSupport.stream(iterable.spliterator(), false)
    // .anyMatch(e -> defaultFields.stream().noneMatch(f -> f.equals(e)));
    return StreamSupport
        .stream(iterable.spliterator(), false)
        .anyMatch(mandatoryOutputData::contains);
  }

  @Override
  public BigIotHttpResponse
      processRequestHandler(OfferingDescription offeringDescription,
                            Map<String, Object> inputData, String subscriberId,
                            String consumerInfo) {

    Query query = createFiwareQuery(inputData);

    try {
      ArrayNode fiwareNodes = orion.postQuery(query);

      // Process Orion response
      // Can be done using .foreach(newArray::add)
      // Another option for creating a stream
      // Stream<JsonNode> nodes = IntStream.range(0,
      // fiwareNodes.size()).mapToObj(fiwareNodes::get);
      ArrayNode bigiotNodes = StreamSupport
          .stream(fiwareNodes.spliterator(), true)
          .map(e -> convertFiwareToBigiot((ObjectNode) e))
          .filter(e -> checkNotEmptyBigIotValue(e))
          .collect(CustomCollectors.toArrayNode());

      String jsonString = mapper.writer().writeValueAsString(bigiotNodes);
      return BigIotHttpResponse.okay().withBody(jsonString).asJsonType();
    } catch (Exception e) {
      log.error("Ops!", e);
      return BigIotHttpResponse
          .error()
          .withBody("{ \"error\": \"500\", \"description\": \"Internal server error while retrieving data\"")
          .asJsonType();
    }
  }
}

// private String getQueryParams(HashMap<String, String> params)
// throws UnsupportedEncodingException {
// StringBuilder result = new StringBuilder();
// boolean first = true;
// for (Map.Entry<String, String> entry : params.entrySet()) {
// if (first) {
// first = false;
// } else {
// result.append("&");
// }
//
// result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
// result.append("=");
// result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
// }
//
// return result.toString();
// }
//
// private String queryParam(String key, String value) throws
// UnsupportedEncodingException {
// return URLEncoder.encode(key,
// "UTF-8").concat("=").concat(URLEncoder.encode(value, "UTF-8"));
// }
