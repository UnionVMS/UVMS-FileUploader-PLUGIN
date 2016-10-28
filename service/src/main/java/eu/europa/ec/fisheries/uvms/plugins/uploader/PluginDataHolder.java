/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.fisheries.uvms.plugins.uploader;

import eu.europa.ec.fisheries.schema.exchange.movement.v1.SetReportMovementType;

import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * @author jojoha
 */
public abstract class PluginDataHolder {

    public static final String PLUGIN_PROPERTIES_KEY  = "uploader.properties";
    public static final String PROPERTIES_KEY         = "settings.properties";
    public static final String CAPABILITIES_PROPS_KEY = "capabilities.properties";

    private Properties uploaderApplicaitonProperties;
    private Properties uploaderProperties;
    private Properties uploaderCapabilities;

    private final ConcurrentHashMap<String, String> settings = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> capabilities = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, SetReportMovementType> cachedMovement = new ConcurrentHashMap<>();

    public ConcurrentMap<String, String> getSettings() {
        return settings;
    }
    public ConcurrentMap<String, String> getCapabilities() {
        return capabilities;
    }
    public ConcurrentMap<String, SetReportMovementType> getCachedMovement() {
        return cachedMovement;
    }
    public Properties getPluginApplicaitonProperties() {
        return uploaderApplicaitonProperties;
    }
    public void setPluginApplicaitonProperties(Properties uploaderApplicaitonProperties) {
        this.uploaderApplicaitonProperties = uploaderApplicaitonProperties;
    }
    public Properties getPluginProperties() {
        return uploaderProperties;
    }
    public void setPluginProperties(Properties uploaderProperties) {
        this.uploaderProperties = uploaderProperties;
    }
    public Properties getPluginCapabilities() {
        return uploaderCapabilities;
    }
    public void setPluginCapabilities(Properties uploaderCapabilities) {
        this.uploaderCapabilities = uploaderCapabilities;
    }

}
