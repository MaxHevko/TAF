package TestProject.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;


public class EnviromentUtils {

    final static String envKeyPrefix = "envKey_";
    protected static Document xmlDocument = null;
    private static final Logger logger = LogManager.getLogger();


    public static String getEnvironmentDependentValue(String stepValue) throws Exception {
        if (!stepValue.contains(envKeyPrefix)) {

            return stepValue;
        }
        String xmlKey = stepValue.replace(envKeyPrefix, "");

        return getValueByXMLKeyFromTestResources(xmlKey);
    }


    public static String getValueByXMLKeyFromTestResources(String xmlKey) throws Exception {
        initXMLDocument(System.getProperty("test.resources"));
        NodeList nList = xmlDocument.getElementsByTagName(xmlKey);

        isSingleNodeReturned(nList, xmlKey);

        String value = nList.item(0).getTextContent();
        return value;
    }



    private static void isSingleNodeReturned(NodeList nList, String xmlKey) throws Exception {
        if (nList.getLength() == 0) {
            throw new Exception("envKey with name: " + xmlKey + " not found in the test resource file "
                    + System.getProperty("test.resources"));
        } else if (nList.getLength() > 1) {
            throw new Exception("Several envKey with name: " + xmlKey + " are found in the test resource file " + System.getProperty("test.resources")
                    + " Xml key name must be unique.");
        }
    }


    protected static void initXMLDocument(String pathToFile) throws SAXException, IOException, ParserConfigurationException {
        InputStream is;
        DocumentBuilderFactory dbFactory;
        DocumentBuilder dBuilder;
        if (xmlDocument == null) {
            dbFactory = DocumentBuilderFactory.newInstance();
            dBuilder = dbFactory.newDocumentBuilder();
            try {
                ClassLoader classloader = Thread.currentThread().getContextClassLoader();
                is = classloader.getResourceAsStream(pathToFile);
                xmlDocument = dBuilder.parse(is);
                xmlDocument.getDocumentElement().normalize();
            }catch (IllegalArgumentException ia){
                logger.warn("Test resource was not succesfully initialized by clasloader. Will try to open as file using absolute path.");
                is = new BufferedInputStream(new FileInputStream(new File(pathToFile)));
                xmlDocument = dBuilder.parse(is);
                xmlDocument.getDocumentElement().normalize();
            }
        }
    }

}

