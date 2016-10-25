package eu.europa.ec.fisheries.uvms.plugins.mapper;

import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kovian on 06/09/2016.
 */
public class JAXBMarshaller {
    private static final Logger LOG = LoggerFactory.getLogger(eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller.class);

    private static Map<String, JAXBContext> contexts = new HashMap<>();

    public static final String STORED_CONTEXTS = "Stored contexts: {}";
    public static final String JAXBCONTEXT_CREATION_TIME = "JAXBContext creation time: {}";
    public static final String UNMARSHALLING_TIME = "Unmarshalling time: {}";
    public static final String ERROR_WHEN_UNMARSHALLING_RESPONSE_IN_RESPONSE_MAPPER = "[Error when unmarshalling response in ResponseMapper ]";
    public static final String ERROR_DURING_UNMARSHALLING_IN_UPLOADER_MODULE = "Error during unmarshalling in Uploader module ";


    private JAXBMarshaller(){
        super();
    }

    /**
     * Marshalls a JAXB Object to a XML String representation
     *
     * @param <T>
     * @param data
     * @return
     * @throws eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException
     */
    public static <T> String marshallJaxBObjectToString(T data) throws ExchangeModelMarshallException {
        try {
            JAXBContext jaxbContext = contexts.get(data.getClass().getName());
            if (jaxbContext == null) {
                long before = System.currentTimeMillis();
                jaxbContext = JAXBContext.newInstance(data.getClass());
                contexts.put(data.getClass().getName(), jaxbContext);
                LOG.debug(STORED_CONTEXTS, contexts.size());
                LOG.debug(JAXBCONTEXT_CREATION_TIME, getTimeDifference(before));
            }
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            StringWriter sw = new StringWriter();
            marshaller.marshal(data, sw);
            return sw.toString();
        } catch (JAXBException ex) {
            LOG.error(ERROR_DURING_UNMARSHALLING_IN_UPLOADER_MODULE,ex);
            throw new ExchangeModelMarshallException("[ Error when marshalling Object to String ]");
        }
    }

    /**
     * Unmarshalls A textMessage to the desired Object. The object must be the
     * root object of the unmarchalled message!
     *
     * @param <R>
     * @param textMessage
     * @param clazz       pperException
     * @return
     * @throws eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException
     */
    public static <R> R unmarshallTextMessage(TextMessage textMessage, Class clazz) throws ExchangeModelMarshallException {
        try {
            JAXBContext jc = contexts.get(clazz.getName());
            if (jc == null) {
                long before = System.currentTimeMillis();
                jc = JAXBContext.newInstance(clazz);
                contexts.put(clazz.getName(), jc);
                LOG.debug(STORED_CONTEXTS, contexts.size());
                LOG.debug(JAXBCONTEXT_CREATION_TIME, getTimeDifference(before));
            }
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            StringReader sr = new StringReader(textMessage.getText());
            StreamSource source = new StreamSource(sr);
            long before = System.currentTimeMillis();
            R object = (R) unmarshaller.unmarshal(source);
            LOG.debug(UNMARSHALLING_TIME, getTimeDifference(before));
            return object;
        } catch (JMSException | JAXBException | NullPointerException ex) {
            LOG.error(ERROR_DURING_UNMARSHALLING_IN_UPLOADER_MODULE,ex);
            throw new ExchangeModelMarshallException(ERROR_WHEN_UNMARSHALLING_RESPONSE_IN_RESPONSE_MAPPER);
        }
    }

    public static <R> R unmarshallStringTextMessage(String textMessage, Class clazz) throws ExchangeModelMarshallException {
        try {
            Map<String, JAXBContext> contexts = new HashMap<>();
            JAXBContext jc = contexts.get(clazz.getName());
            if (jc == null) {
                long before = System.currentTimeMillis();
                jc = JAXBContext.newInstance(clazz);
                contexts.put(clazz.getName(), jc);
                LOG.debug(STORED_CONTEXTS, contexts.size());
                LOG.debug(JAXBCONTEXT_CREATION_TIME, getTimeDifference(before));
            }
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            StringReader sr = new StringReader(textMessage);
            StreamSource source = new StreamSource(sr);
            long before = System.currentTimeMillis();
            R object = (R) unmarshaller.unmarshal(source);
            LOG.debug(UNMARSHALLING_TIME, getTimeDifference(before));
            return object;
        } catch (JAXBException | NullPointerException ex) {
            LOG.error(ERROR_DURING_UNMARSHALLING_IN_UPLOADER_MODULE,ex);
            throw new ExchangeModelMarshallException(ERROR_WHEN_UNMARSHALLING_RESPONSE_IN_RESPONSE_MAPPER);
        }
    }

    private static long getTimeDifference(long before) {
        return System.currentTimeMillis() - before;
    }

}