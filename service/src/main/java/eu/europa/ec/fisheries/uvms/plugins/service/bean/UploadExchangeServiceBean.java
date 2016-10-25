/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.uvms.plugins.service.bean;

import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.plugins.constants.ModuleQueue;
import eu.europa.ec.fisheries.uvms.plugins.constants.UploaderConstants;
import eu.europa.ec.fisheries.uvms.plugins.exception.ResponseMappingException;
import eu.europa.ec.fisheries.uvms.plugins.producer.PluginMessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.jms.JMSException;

/**
 *
 * @author akovi
 */
@Stateless
@LocalBean
public class UploadExchangeServiceBean {

	private static final Logger LOG = LoggerFactory.getLogger(UploadExchangeServiceBean.class);

	@EJB
	PluginMessageProducer producer;

	public void sendMessageToExchange(String textFromUploadedFile, String moduleName) throws JMSException {
		try {
			String faRequest = getTextMessageStringForModule(textFromUploadedFile, moduleName.toUpperCase());
			String messageId = producer.sendModuleMessage(faRequest, ModuleQueue.EXCHANGE);
			LOG.info("FluxMdrResponse Sent to Exchange module. MessageID :" + messageId);
		} catch (JMSException e) {
			LOG.error("Couldn't send SetFLUXMDRSyncMessageResponse to Exchange module", e);
			throw e;
		} catch (ResponseMappingException e) {
			LOG.error("Couldn't map textFromUploadedFile to SetFLUXMDRSyncMessageResponse", e);
			throw new JMSException(e.getMessage());
		}
	}

	/**
	 * Depending on the module name passed as parameter it Maps to the wright Mapper and return the requestObject as String;
	 *
	 * @param message
	 * @param moduleName
	 * @return
	 * @throws ExchangeModelMarshallException
	 */
	private String getTextMessageStringForModule(String message, String moduleName) throws ResponseMappingException {
		String reqStr;
		switch(moduleName){
			case UploaderConstants.FISHING_ACTIVITY_MODULE_NAME :
				try {
					reqStr = ExchangeModuleRequestMapper.createFluxFAManualResponseRequest(message, UploaderConstants.FLUX_USER_NAME);
				} catch (ExchangeModelMarshallException e) {
					throw new ResponseMappingException(e);
				}
				break;
			default :
				throw new ResponseMappingException("The module name to which the message should be delivered is no supported!");
		}
		return reqStr;
	}
}
