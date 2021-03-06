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

import java.io.*;

import org.easymock.EasyMock;
import org.springframework.core.io.Resource;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.consol.citrus.script.GroovyAction;

public class GroovyDefinitionTest {
    private Resource scriptResource = EasyMock.createMock(Resource.class);
    private Resource scriptTemplate = EasyMock.createMock(Resource.class);
    private File file = EasyMock.createMock(File.class);
            
    @Test
    public void testGroovyBuilderWithResource() throws IOException {
        MockBuilder builder = new MockBuilder() {
            @Override
            public void configure() {
                groovy(scriptResource)
                    .skipTemplate();
            }
        };
        
        reset(scriptResource);
        expect(scriptResource.getInputStream()).andReturn(new ByteArrayInputStream("someScript".getBytes())).once();
        replay(scriptResource);
        
        builder.run(null, null);
        
        Assert.assertEquals(builder.testCase().getActions().size(), 1);
        Assert.assertEquals(builder.testCase().getActions().get(0).getClass(), GroovyAction.class);
        
        GroovyAction action = (GroovyAction)builder.testCase().getActions().get(0);
        Assert.assertEquals(action.getScript(), "someScript");
        Assert.assertEquals(action.isUseScriptTemplate(), false);
        
        verify(scriptResource);
    }
    
    @Test
    public void testGroovyBuilderWithScript() {
        MockBuilder builder = new MockBuilder() {
            @Override
            public void configure() {
                groovy("println 'Groovy!'")
                    .skipTemplate();
            }
        };
        
        builder.run(null, null);
        
        Assert.assertEquals(builder.testCase().getActions().size(), 1);
        Assert.assertEquals(builder.testCase().getActions().get(0).getClass(), GroovyAction.class);
        
        GroovyAction action = (GroovyAction)builder.testCase().getActions().get(0);
        Assert.assertEquals(action.getScript(), "println 'Groovy!'");
        Assert.assertEquals(action.isUseScriptTemplate(), false);
    }
    
    @Test
    public void testGroovyBuilderWithTemplate() throws IOException {
        MockBuilder builder = new MockBuilder() {
            @Override
            public void configure() {
                groovy("println 'Groovy!'")
                    .template(scriptTemplate);
            }
        };
        
        reset(scriptTemplate, file);
        expect(scriptTemplate.getFile()).andReturn(file).once();
        expect(file.getAbsolutePath()).andReturn("classpath:some.file").once();
        replay(scriptTemplate, file);
        
        builder.run(null, null);
        
        Assert.assertEquals(builder.testCase().getActions().size(), 1);
        Assert.assertEquals(builder.testCase().getActions().get(0).getClass(), GroovyAction.class);
        
        GroovyAction action = (GroovyAction)builder.testCase().getActions().get(0);
        Assert.assertEquals(action.getScriptTemplatePath(), "classpath:some.file");
        Assert.assertEquals(action.isUseScriptTemplate(), true);
    }
    
    @Test
    public void testGroovyBuilderWithTemplatePath() {
        MockBuilder builder = new MockBuilder() {
            @Override
            public void configure() {
                groovy("println 'Groovy!'")
                    .template("classpath:script-template.groovy");
            }
        };
        
        builder.run(null, null);
        
        Assert.assertEquals(builder.testCase().getActions().size(), 1);
        Assert.assertEquals(builder.testCase().getActions().get(0).getClass(), GroovyAction.class);
        
        GroovyAction action = (GroovyAction)builder.testCase().getActions().get(0);
        Assert.assertNotNull(action.getScriptTemplatePath());
        Assert.assertEquals(action.isUseScriptTemplate(), true);
    }
}