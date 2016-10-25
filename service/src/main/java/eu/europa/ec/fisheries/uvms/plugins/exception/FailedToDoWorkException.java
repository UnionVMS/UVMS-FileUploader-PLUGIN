package eu.europa.ec.fisheries.uvms.plugins.exception;

import java.io.IOException;

/**
 * Created by kovian on 19/10/2016.
 */
public class FailedToDoWorkException extends Exception {
    public FailedToDoWorkException(IOException e) {
        super(e);
    }
}
