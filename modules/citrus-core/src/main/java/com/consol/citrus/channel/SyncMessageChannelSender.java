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

package com.consol.citrus.channel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.Message;

import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.message.ReplyMessageCorrelator;
import com.consol.citrus.message.ReplyMessageHandler;

/**
 * Synchronous message channel sender. After sending message action will listen for reply message. A
 * {@link ReplyMessageHandler} may ask for this reply message and continue with message validation.
 * 
 * @author Christoph Deppisch
 */
public class SyncMessageChannelSender extends MessageChannelSender {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(SyncMessageChannelSender.class);
    
    /** Reply message handler */
    private ReplyMessageHandler replyMessageHandler;
    
    /** Time to wait for reply message to arrive */
    private long replyTimeout = 5000L;
    
    /** Reply message correlator */
    private ReplyMessageCorrelator correlator = null;
    
    /**
     * @see com.consol.citrus.message.MessageSender#send(org.springframework.integration.Message)
     * @throws CitrusRuntimeException
     */
    public void send(Message<?> message) {
        String destinationChannelName = getDestinationChannelName();
        
        log.info("Sending message to channel: '" + destinationChannelName + "'");

        if (log.isDebugEnabled()) {
            log.debug("Message to sent is:\n" + message.toString());
        }

        getMessagingTemplate().setReceiveTimeout(replyTimeout);
        Message<?> replyMessage;
        
        replyMessage = getMessagingTemplate().sendAndReceive(getDestinationChannel(), message);
        
        if (replyMessage == null) {
            throw new CitrusRuntimeException("Reply timed out after " + 
                    replyTimeout + "ms. Did not receive reply message on reply channel");
        }
        
        log.info("Message was successfully sent to channel: '" + destinationChannelName + "'");
        
        if (replyMessageHandler != null) {
            if (correlator != null) {
                replyMessageHandler.onReplyMessage(replyMessage,
                    correlator.getCorrelationKey(message));
            } else {
                replyMessageHandler.onReplyMessage(replyMessage);
            }
        }
    }
    
    /**
     * Set the reply message handler
     * @param replyMessageHandler the replyMessageHandler to set
     */
    public void setReplyMessageHandler(ReplyMessageHandler replyMessageHandler) {
        this.replyMessageHandler = replyMessageHandler;
    }

    /**
     * Get the reply message handler.
     * @return the replyMessageHandler
     */
    public ReplyMessageHandler getReplyMessageHandler() {
        return replyMessageHandler;
    }

    /**
     * Set the reply timeout.
     * @param replyTimeout the replyTimeout to set
     */
    public void setReplyTimeout(long replyTimeout) {
        this.replyTimeout = replyTimeout;
    }

    /**
     * Get the reply timeout.
     * @return the replyTimeout
     */
    public long getReplyTimeout() {
        return replyTimeout;
    }

    /**
     * Set the reply message correlator.
     * @param correlator the correlator to set
     */
    public void setCorrelator(ReplyMessageCorrelator correlator) {
        this.correlator = correlator;
    }

    /**
     * Get the reply message correlator.
     * @return the correlator
     */
    public ReplyMessageCorrelator getCorrelator() {
        return correlator;
    }
    
}
