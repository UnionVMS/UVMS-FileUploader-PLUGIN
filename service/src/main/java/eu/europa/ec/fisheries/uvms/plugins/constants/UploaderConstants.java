package eu.europa.ec.fisheries.uvms.plugins.constants;

/**
 * Created by kovian on 17/10/2016.
 */
public class UploaderConstants {

    private UploaderConstants(){
        super();
    }

    public static final String FILES_UPLOAD_LISTENER_TIMER    = "UploaderFilesTimerService";

    /* Supported Module names constants */
    public static final String FISHING_ACTIVITY_MODULE_NAME = "FA";

    public static final String SUPPORTED_MODULES_KEY          = "uploader.supported.modules";
    public static final String UPLOADER_MAIN_DIR_KEY          = "uploader.main.dir";
    public static final String DOT_UPLOAD_SUPPORTED_FILES_KEY = ".uploader.supported.files";
    public static final String JOB_SCHEDULER_CONFIG_KEY       = "uploader.scheduler.cron.config";

    public static final String UPLOAD    = "upload";
    public static final String PROCESSED = "processed";
    public static final String REFUSED   = "refused";
    public static final String FAILED    = "failed";

    public static final String COMMA         = ",";
    public static final String DOT           = ".";
    public static final String ESCAPED_DOT   = "\\.";
    public static final String ESCAPED_SLASH = "//";
    public static final String COLON         = ":";
    public static final String MINUS         = "-";

    /* Exceptions messages */
    public static final String MAIN_DIR_EXC_MESSAGE                      = "Main directory for uploader module has not been set up. Check config.properties file under the 'resources folder.' ";
    public static final String NOT_CONFIGURED_SUPPORTED_FILES_FOR_MODULE = "The supported files property is missing for module : ";
    public static final String MORE_CONFIGURATION_IS_NEEDED              = ". More configuration in needed (config.properties file).";


    public static final String FLUX_USER_NAME = "flux";
}
