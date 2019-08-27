/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.apimgt.impl.wsdl;

import org.apache.commons.io.IOUtils;
import org.apache.xerces.impl.Constants;
import org.apache.xerces.util.SecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.wso2.carbon.apimgt.api.ErrorHandler;
import org.wso2.carbon.apimgt.impl.wsdl.exceptions.APIMgtWSDLException;
import org.wso2.carbon.apimgt.impl.wsdl.model.WSDLInfo;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Interface to extend different the wsdl operation extractor implementations.
 */
abstract class AbstractWSDLProcessor implements WSDLProcessor {

    /**
     * Returns an "XXE safe" built DOM XML object by reading the content from the provided URL.
     *
     * @param url URL to fetch the content
     * @return an "XXE safe" built DOM XML object by reading the content from the provided URL
     * @throws APIMgtWSDLException When error occurred while reading from URL
     */
    Document getSecuredParsedDocumentFromURL(URL url) throws APIMgtWSDLException {
        InputStream inputStream = null;
        try {
            DocumentBuilderFactory factory = getSecuredDocumentBuilder();
            DocumentBuilder builder = factory.newDocumentBuilder();
            inputStream = url.openStream();
            return builder.parse(inputStream);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new APIMgtWSDLException("Error while reading WSDL document", e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }


    /**
     * Returns an "XXE safe" built DOM XML object by reading the content from the provided file path.
     *
     * @param path path to fetch the content
     * @return an "XXE safe" built DOM XML object by reading the content from the provided file path
     * @throws APIMgtWSDLException When error occurred while reading from file path
     */
    Document getSecuredParsedDocumentFromPath(String path) throws APIMgtWSDLException {
        InputStream inputStream = null;
        try {
            DocumentBuilderFactory factory = getSecuredDocumentBuilder();
            DocumentBuilder builder = factory.newDocumentBuilder();
            inputStream = new FileInputStream(new File(path));
            return builder.parse(inputStream);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new APIMgtWSDLException("Error while reading WSDL document", e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    /**
     * Returns an "XXE safe" built DOM XML object by reading the content from the byte array.
     *
     * @param content xml content
     * @return an "XXE safe" built DOM XML object by reading the content from the byte array
     * @throws APIMgtWSDLException When error occurred while reading from the byte array
     */
    Document getSecuredParsedDocumentFromContent(byte[] content) throws APIMgtWSDLException {
        InputStream inputStream = null;
        try {
            DocumentBuilderFactory factory = getSecuredDocumentBuilder();
            DocumentBuilder builder = factory.newDocumentBuilder();
            inputStream = new ByteArrayInputStream(content);
            return builder.parse(inputStream);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new APIMgtWSDLException("Error while reading WSDL document", e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    /**
     * Returns a secured document builder to avoid XXE attacks
     *
     * @return secured document builder to avoid XXE attacks
     */
    private DocumentBuilderFactory getSecuredDocumentBuilder() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setXIncludeAware(false);
        dbf.setExpandEntityReferences(false);
        try {
            dbf.setFeature(Constants.SAX_FEATURE_PREFIX + Constants.EXTERNAL_GENERAL_ENTITIES_FEATURE, false);
            dbf.setFeature(Constants.SAX_FEATURE_PREFIX + Constants.EXTERNAL_PARAMETER_ENTITIES_FEATURE, false);
            dbf.setFeature(Constants.XERCES_FEATURE_PREFIX + Constants.LOAD_EXTERNAL_DTD_FEATURE, false);
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        } catch (ParserConfigurationException e) {
            // Skip throwing the error as this exception doesn't break actual DocumentBuilderFactory creation
            log.error("Failed to load XML Processor Feature " + Constants.EXTERNAL_GENERAL_ENTITIES_FEATURE + " or "
                    + Constants.EXTERNAL_PARAMETER_ENTITIES_FEATURE + " or " + Constants.LOAD_EXTERNAL_DTD_FEATURE, e);
        }
        SecurityManager securityManager = new SecurityManager();
        securityManager.setEntityExpansionLimit(ENTITY_EXPANSION_LIMIT);
        dbf.setAttribute(Constants.XERCES_PROPERTY_PREFIX + Constants.SECURITY_MANAGER_PROPERTY, securityManager);
        return dbf;
    }
}
