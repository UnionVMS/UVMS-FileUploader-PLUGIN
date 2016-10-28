package eu.europa.ec.fisheries.uvms.plugins.uploader.exception;

/**
 * Created by kovian on 14/10/2016.
 */
public class UploaderConfigurationException extends Exception {
    public UploaderConfigurationException(String s) {
        super(s);
    }
    public UploaderConfigurationException(String s, Throwable cause) {
        super(s, cause);
    }
}
