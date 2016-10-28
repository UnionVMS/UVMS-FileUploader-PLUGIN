/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.fisheries.uvms.plugins.uploader.mapper;

import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.*;
import eu.europa.ec.fisheries.uvms.plugins.uploader.constants.UploaderConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * @author akovi
 */
public class ServiceMapper {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceMapper.class);

    private ServiceMapper(){
        super();
    }

    public static ServiceType getServiceType(String serviceClassName, String uploaderDisplayName, String description, PluginType uploaderType, String responseMessageName) {

        if (responseMessageName == null) {
            throw new IllegalArgumentException("Response message must be provided!");
        }

        if (serviceClassName == null) {
            throw new IllegalArgumentException("ServiceClassName message must be provided!");
        }

        ServiceType serviceType = new ServiceType();
        serviceType.setDescription(description);
        serviceType.setName(uploaderDisplayName);
        serviceType.setServiceClassName(serviceClassName);
        serviceType.setServiceResponseMessageName(responseMessageName);
        serviceType.setPluginType(uploaderType);
        return serviceType;
    }

    public static SettingListType getSettingsListTypeFromMap(ConcurrentMap<String, String> settings) {
        SettingListType settingListType = new SettingListType();
        Iterator<Map.Entry<String, String>> itr = settings.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry<String, String> tmp = itr.next();
            SettingType setting = new SettingType();
            setting.setKey(tmp.getKey());
            setting.setValue(tmp.getValue());
            settingListType.getSetting().add(setting);
        }
        return settingListType;
    }

    public static CapabilityListType getCapabilitiesListTypeFromMap(ConcurrentMap<String, String> capabilities) {
        CapabilityListType capabilityListType = new CapabilityListType();
        Iterator<Map.Entry<String, String>> itr = capabilities.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry<String, String> tmp = itr.next();
            CapabilityType setting = new CapabilityType();
            try {
                setting.setType(CapabilityTypeType.valueOf(tmp.getKey()));
            } catch (Exception e) {
                LOG.error("Error when parsing to Enum type from String KEY: {}", tmp.getKey(),e);
            }
            setting.setValue(tmp.getValue());
            capabilityListType.getCapability().add(setting);
        }
        return capabilityListType;
    }

    public static void mapToMapFromProperties(ConcurrentMap<String, String> map, Properties props, String registerClassName) {
        for (Map.Entry entrySet : props.entrySet()) {
            Object key = entrySet.getKey();
            if (key.getClass().isAssignableFrom(String.class)) {
                String keyString = (String) key;
                if (registerClassName != null) {
                    keyString = registerClassName.concat(UploaderConstants.DOT + keyString);
                }
                String valueString = (String) entrySet.getValue();
                map.put(keyString, valueString);
            }
        }

    }

}
