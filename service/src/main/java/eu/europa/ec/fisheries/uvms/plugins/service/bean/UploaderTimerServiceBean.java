package eu.europa.ec.fisheries.uvms.plugins.service.bean;

import eu.europa.ec.fisheries.uvms.plugins.constants.UploaderConstants;
import eu.europa.ec.fisheries.uvms.plugins.service.FileUploadListener;
import eu.europa.ec.fisheries.uvms.plugins.service.UploaderTimerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.*;
import java.util.Collection;

/**
 * Created by kovian on 21/10/2016.
 */
@Stateless
public class UploaderTimerServiceBean implements UploaderTimerService {

    @EJB
    FileUploadListener uploaderListener;

    @Resource
    private TimerService timerServ;

    private static final TimerConfig TIMER_CONFIG  = new TimerConfig(UploaderConstants.FILES_UPLOAD_LISTENER_TIMER, false);
    private static final Logger LOG   = LoggerFactory.getLogger(FileUploadListenerBean.class);


    @Timeout
    public void checkForWorksToBeDone(){
        if(!uploaderListener.controlDirectoriesForNewFiles()){
            LOG.info("\n\nNO WORKS CONFIGURED! CANCELING ALL TIMERS..");
            cancelPreviousTimer(); // No works to do!
        }
    }


    /**
     * Given the schedulerExpressionStr creates a new timer for this bean.
     *
     * @param schedulerExpressionStr
     */
    @Override
    public void setUpScheduler(String schedulerExpressionStr) throws IllegalArgumentException {
        ScheduleExpression expression;
        try {
            // Parse the Cron-Job expression;
            expression = parseExpression(schedulerExpressionStr);
            // Firstly, we need to cancel the current timer, if already exists one;
            cancelPreviousTimer();
            // Set up the new timer for this EJB;
            timerServ.createCalendarTimer(expression, TIMER_CONFIG);;
        } catch(IllegalArgumentException ex){
            LOG.warn("Error creating new scheduled synchronization timer!", ex);
            throw ex;
        }
        LOG.info("New timer scheduler for listening to Uploader configured directories created successfully with the following expression: ", expression);
        Timer timer = (Timer) timerServ.getAllTimers().toArray()[0];
        LOG.debug("Next time the timeOuth method will be called @ "+timer.getNextTimeout());
    }

    /**
     * Cancels the previous set up of the timer for this bean.
     *
     */
    private void cancelPreviousTimer() {
        Collection<Timer> allTimers = timerServ.getTimers();
        for (Timer currentTimer: allTimers) {
            if (TIMER_CONFIG.getInfo().equals(currentTimer.getInfo())) {
                currentTimer.cancel();
                LOG.info("Current FA_XML scheduler timer cancelled.");
                break;
            }
        }
    }

    /**
     * Creates a ScheduleExpression object with the given schedulerExpressionStr String expression.
     *
     * @param schedulerExpressionStr
     * @return
     */
    private ScheduleExpression parseExpression(String schedulerExpressionStr) {
        ScheduleExpression expression = new ScheduleExpression();
        String[] args = schedulerExpressionStr.split("\\s");
        if (args.length != 6) {
            throw new IllegalArgumentException("Invalid scheduler expression: " + schedulerExpressionStr);
        }
        return expression.second(args[0]).minute(args[1]).hour(args[2]);
    }
}
