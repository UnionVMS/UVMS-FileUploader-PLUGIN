package eu.europa.ec.fisheries.uvms.plugins.service;

import javax.ejb.Local;

/**
 * Created by kovian on 21/10/2016.
 */
@Local
public interface UploaderTimerService {
    void setUpScheduler(String schedulerExpressionStr);
}
