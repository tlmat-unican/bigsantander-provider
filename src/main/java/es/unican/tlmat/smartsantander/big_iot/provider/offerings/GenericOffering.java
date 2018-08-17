package es.unican.tlmat.smartsantander.big_iot.provider.offerings;

import org.eclipse.bigiot.lib.handlers.AccessRequestHandler;
import org.eclipse.bigiot.lib.offering.RegistrableOfferingDescription;

public abstract class GenericOffering implements AccessRequestHandler {

  public abstract RegistrableOfferingDescription getOfferingDescription();
  
}
