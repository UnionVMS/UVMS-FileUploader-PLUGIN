/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.fisheries.uvms.plugins.mapper;

import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author jojoha
 */
public class ServiceMapper {

    final static Logger LOG = LoggerFactory.getLogger(ServiceMapper.class);

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

    public static SettingListType getSettingsListTypeFromMap(ConcurrentHashMap<String, String> settings) {
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

    public static CapabilityListType getCapabilitiesListTypeFromMap(ConcurrentHashMap<String, String> capabilities) {
        CapabilityListType capabilityListType = new CapabilityListType();
        Iterator<Map.Entry<String, String>> itr = capabilities.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry<String, String> tmp = itr.next();
            CapabilityType setting = new CapabilityType();

            try {
                setting.setType(CapabilityTypeType.valueOf(tmp.getKey()));
            } catch (Exception e) {
                LOG.error("Error when parsing to Enum type from String KEY: {}", tmp.getKey());
            }

            setting.setValue(tmp.getValue());
            capabilityListType.getCapability().add(setting);
        }
        return capabilityListType;
    }

    public static void mapToMapFromProperties(ConcurrentHashMap<String, String> map, Properties props, String registerClassName) {
        for (Object col : props.keySet()) {
            if (col.getClass().isAssignableFrom(String.class)) {
                String keyString = (String) col;
                if (registerClassName != null) {
                    keyString = registerClassName.concat("." + keyString);
                }
                String valueString = (String) props.get(col);
                map.put(keyString, valueString);
            }
        }

    }

}
