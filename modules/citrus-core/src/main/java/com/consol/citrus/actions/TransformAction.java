/*
 * Copyright 2006-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.consol.citrus.actions;

import java.io.IOException;

import javax.xml.transform.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.xml.transform.StringResult;
import org.springframework.xml.transform.StringSource;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.util.FileUtils;


/**
 * Action transforms a XML document(specified inline or from external file resource)
 * with a XSLT document(specified inline or from external file resource)
 * and puts the result in the specified variable.
 *
 * @author Philipp Komninos
 * @since 2010
 */
public class TransformAction extends AbstractTestAction {
	
	/** Inline XML document */
	private String xmlData;
	
	/** External XML document resource path */
	private String xmlResourcePath;
	
	/** Inline XSLT document */
	private String xsltData;
	
	/** External XSLT document resource path */
	private String xsltResourcePath;
	
	/** Target variable for the result */
	private String targetVariable = "transform-result";
	
	/**
     * Logger
     */
    private static Logger log = LoggerFactory.getLogger(TransformAction.class);

	@Override
	public void doExecute(TestContext context) {
		try {
			log.info("Starting XSLT transformation");
			
			//parse XML document and define XML source for transformation
			Source xmlSource = null;
			if (xmlResourcePath != null) {
				xmlSource = new StringSource(context.replaceDynamicContentInString(FileUtils.readToString(FileUtils.getFileResource(xmlResourcePath, context))));
			} else if (xmlData != null) {
				xmlSource = new StringSource(context.replaceDynamicContentInString(xmlData));
			} else {
				throw new CitrusRuntimeException("Neither inline XML nor " +
                		"external file resource is defined for bean. " +
        				"Cannot transform XML document.");
			}
			
			//parse XSLT document and define  XSLT source for transformation
			Source xsltSource = null;
			if (xsltResourcePath != null) {
				xsltSource = new StringSource(context.replaceDynamicContentInString(FileUtils.readToString(FileUtils.getFileResource(xsltResourcePath, context))));
			} else if (xsltData != null) {
				xsltSource = new StringSource(context.replaceDynamicContentInString(xsltData));
			} else {
				throw new CitrusRuntimeException("Neither inline XSLT nor " +
                		"external file resource is defined for bean. " +
        				"Cannot transform XSLT document.");
			}
			
			//create transformer
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer(xsltSource);
			
			//transform
			StringResult result = new StringResult();
			transformer.transform(xmlSource, result);
			
			//save result to specified variable
			context.setVariable(targetVariable, result.toString());
			
			log.info("Transformation finished successfully");
		} catch (IOException e) {
			throw new CitrusRuntimeException(e);
		} catch (TransformerConfigurationException e) {
			throw new CitrusRuntimeException(e);
		} catch (TransformerException e) {
			throw new CitrusRuntimeException(e);
		}
	}

	/**
	 * Set the XML document
	 * @param xmlData the xmlData to set
	 */
	public void setXmlData(String xmlData) {
		this.xmlData = xmlData;
	}

	/**
	 * Set the XML document as resource
	 * @param xmlResource the xmlResource to set
	 */
	public void setXmlResourcePath(String xmlResource) {
		this.xmlResourcePath = xmlResource;
	}

	/**
	 * Set the XSLT document
	 * @param xsltData the xsltData to set
	 */
	public void setXsltData(String xsltData) {
		this.xsltData = xsltData;
	}

	/**
	 * Set the XSLT document as resource
	 * @param xsltResource the xsltResource to set
	 */
	public void setXsltResourcePath(String xsltResource) {
		this.xsltResourcePath = xsltResource;
	}

	/**
	 * Set the target variable for the result
	 * @param targetVariable the targetVariable to set
	 */
	public void setTargetVariable(String targetVariable) {
		this.targetVariable = targetVariable;
	}

    /**
     * Gets the xmlData.
     * @return the xmlData
     */
    public String getXmlData() {
        return xmlData;
    }

    /**
     * Gets the xmlResource.
     * @return the xmlResource
     */
    public String getXmlResourcePath() {
        return xmlResourcePath;
    }

    /**
     * Gets the xsltData.
     * @return the xsltData
     */
    public String getXsltData() {
        return xsltData;
    }

    /**
     * Gets the xsltResource.
     * @return the xsltResource
     */
    public String getXsltResourcePath() {
        return xsltResourcePath;
    }

    /**
     * Gets the targetVariable.
     * @return the targetVariable
     */
    public String getTargetVariable() {
        return targetVariable;
    }

}
