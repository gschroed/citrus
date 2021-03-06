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

package com.consol.citrus.ws.validation;

import javax.xml.transform.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.xml.transform.StringResult;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.exceptions.ValidationException;
import com.consol.citrus.validation.context.ValidationContext;

/**
 * Abstract implementation of {@link SoapFaultValidator} converting soap fault detail objects to simple String content for
 * further validation.
 * 
 * @author Christoph Deppisch
 */
public abstract class AbstractFaultDetailValidator extends AbstractSoapFaultValidator {

    /**
     * Logger
     */
    private static Logger log = LoggerFactory.getLogger(AbstractFaultDetailValidator.class);
    
    /**
     * @see com.consol.citrus.ws.validation.AbstractSoapFaultValidator#validateFaultDetail(org.springframework.ws.soap.SoapFaultDetail, org.springframework.ws.soap.SoapFaultDetail, com.consol.citrus.context.TestContext)
     */
    @Override
    protected void validateFaultDetail(SoapFaultDetail receivedDetail, SoapFaultDetail controlDetail, 
            TestContext context, ValidationContext validationContext) {
        if (controlDetail == null) { return; }
        
        if (log.isDebugEnabled()) {
            log.debug("Validating SOAP fault detail content ...");
        }
        
        if (receivedDetail != null) {
            String receivedDetailString = extractFaultDetail(receivedDetail);
            String controlDetailString = extractFaultDetail(controlDetail);
            
            if (log.isDebugEnabled()) {
                log.debug("Received fault detail:\n" + StringUtils.trimWhitespace(receivedDetailString));
                log.debug("Control fault detail:\n" + StringUtils.trimWhitespace(controlDetailString));
            }
            
            validateFaultDetailString(receivedDetailString, controlDetailString, context, validationContext);
        } else {
            throw new ValidationException("Missing SOAP fault detail in received message");
        }
    }

    /**
     * Extracts fault detail string from soap fault detail instance. Transforms detail source
     * into string and takes care on detail element that gets stripped off.
     * 
     * @param detail
     * @return
     */
    private String extractFaultDetail(SoapFaultDetail detail) {
        StringResult detailResult = new StringResult();
        
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            
            transformer.transform(detail.getSource(), detailResult);
        } catch (TransformerException e) {
            throw new CitrusRuntimeException(e);
        }
        
        String detailString = detailResult.toString();
        
        if (detailString.startsWith("<detail>")) {
            detailString = detailString.substring("<detail>".length(), detailString.length() - "</detail>".length());
        }
        
        return detailString;
    }

    /**
     * Actual validation logic in this method.
     * 
     * @param receivedDetail received soap fault representation as string.
     * @param controlDetail control soap fault representation as string.
     * @param context
     * @param validationContext
     */
    protected abstract void validateFaultDetailString(String receivedDetail, String controlDetail, 
            TestContext context, ValidationContext validationContext) throws ValidationException;
}
