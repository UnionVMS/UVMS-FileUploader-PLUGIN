/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.uvms.plugins.uploader.properties.bean;

import eu.europa.ec.fisheries.uvms.plugins.uploader.service.bean.FileUploadListenerBean;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

@Stateless
@LocalBean
public class PropertiesLoaderServiceBean {

    private Properties props;
    private CompositeConfiguration configProps;
    private AtomicInteger accessCount = new AtomicInteger(0);

    private static final Logger LOG = LoggerFactory.getLogger(FileUploadListenerBean.class);

    @PostConstruct
    public void startup() {

        LOG.info("Loading properties for ''Upload'' module ...");

        try {
            InputStream propsStream = PropertiesLoaderServiceBean.class.getResourceAsStream("/config.properties");
            props = new Properties();
            props.load(propsStream);

            CompositeConfiguration config = new CompositeConfiguration();
            config.addConfiguration(new SystemConfiguration());
            config.addConfiguration(new PropertiesConfiguration("/config.properties"));
            configProps = config;

        } catch (IOException e) {
            throw new EJBException("PropertiesLoaderServiceBean initialization error", e);
        } catch (ConfigurationException e) {
            LOG.error("Error during loading of properties file for Uploader Plugin", e);
        }
    }

    public String getProperty(final String name) {
        accessCount.incrementAndGet();
        return props.getProperty(name);
    }

    public Properties getAllProperties(){
        return props;
    }

    public CompositeConfiguration getConfigProps() {
        return configProps;
    }

    public int getAccessCount() {
        return accessCount.get();
    }

    @PreDestroy
    private void shutdown() {
        LOG.info("In PropertiesLoaderServiceBean(Singleton)::shutdown()");
    }
}