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

package com.consol.citrus.ws.message;


import com.consol.citrus.message.ReplyMessageReceiver;

/**
 * Reply message receiver implementation for receiving SOAP WebService messages. We introduced this special subclass
 * in order to separate message receiver definition for SOAP messages from other message protocols. This is 
 * because we have special SOAP validation capabilities that can be used e.g. in Java DSL.
 * 
 * @author Christoph Deppisch
 */
public class SoapReplyMessageReceiver extends ReplyMessageReceiver {

}
