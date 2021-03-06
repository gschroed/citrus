/*
 * Copyright 2006-2012 the original author or authors.
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

package com.consol.citrus.dsl.definition;

import static org.easymock.EasyMock.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.easymock.EasyMock;
import org.springframework.core.io.Resource;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.consol.citrus.actions.TransformAction;

public class TransformDefinitionTest {
	private Resource xmlResource = EasyMock.createMock(Resource.class);
	private Resource xsltResource = EasyMock.createMock(Resource.class);
	
	@Test
	public void testTransformBuilderWithData() {
		MockBuilder builder = new MockBuilder() {
    		@Override
    		public void configure() {
    		    transform()
    				.source("<Test>XML</test>")
    				.xslt("XSLT")
    		        .result("result");
			}
		};
	
		builder.run(null, null);
		Assert.assertEquals(builder.testCase().getActions().size(), 1);
		Assert.assertEquals(builder.testCase().getActions().get(0).getClass(), TransformAction.class);
		
		TransformAction action = (TransformAction)builder.testCase().getActions().get(0);
		
		Assert.assertEquals(action.getName(), TransformAction.class.getSimpleName());
		Assert.assertEquals(action.getXmlData(), "<Test>XML</test>");
		Assert.assertEquals(action.getXsltData(), "XSLT");
		Assert.assertEquals(action.getTargetVariable(), "result");
	}
		
	@Test
	public void testTransformBuilderWithResource() throws IOException {
		MockBuilder builder = new MockBuilder() {
			@Override
			public void configure() {
				transform()
					.source(xmlResource)
					.xslt(xsltResource)
					.result("result");
						
			}
		};
		
		reset(xmlResource, xsltResource);
        expect(xmlResource.getInputStream()).andReturn(new ByteArrayInputStream("xmlData".getBytes())).once();
        expect(xsltResource.getInputStream()).andReturn(new ByteArrayInputStream("xsltSource".getBytes())).once();
        replay(xmlResource, xsltResource);
		
		builder.run(null, null);
		Assert.assertEquals(builder.testCase().getActions().size(), 1);
		Assert.assertEquals(builder.testCase().getActions().get(0).getClass(), TransformAction.class);
		
		TransformAction action = (TransformAction)builder.testCase().getActions().get(0);
		
		Assert.assertEquals(action.getName(), TransformAction.class.getSimpleName());
		Assert.assertEquals(action.getXmlData(), "xmlData");
		Assert.assertEquals(action.getXsltData(), "xsltSource");
		Assert.assertEquals(action.getTargetVariable(), "result");
		
		verify(xmlResource, xsltResource);
	}
}
