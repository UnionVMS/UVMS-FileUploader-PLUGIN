package eu.europa.ec.fisheries.uvms.plugins.exception;

import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;

/**
 * Created by kovian on 17/10/2016.
 */
public class ResponseMappingException extends Throwable {
    public ResponseMappingException(String s) {
        super(s);
    }

    public ResponseMappingException(ExchangeModelMarshallException e) {
        super(e);
    }
}
