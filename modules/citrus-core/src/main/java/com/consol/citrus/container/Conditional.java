/*
 * Copyright 2006-2011 the original author or authors.
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

package com.consol.citrus.container;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.consol.citrus.TestAction;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.util.BooleanExpressionParser;

/**
 * Class executes nested test actions if condition expression evaluates to true.
 *
 * See {@link com.consol.citrus.util.BooleanExpressionParser} for supported
 * boolean expressions that define the conditioning.
 *
 * @author Matthias Beil
 * @since 1.2
 */
public class Conditional extends AbstractActionContainer {

    /**
     * Logger
     */
    private static Logger log = LoggerFactory.getLogger(Conditional.class);

    /** Boolean expression string */
    protected String expression;

    @Override
    public void doExecute(final TestContext context) {

        final String conditionString = context.replaceDynamicContentInString(this.expression);

        if (BooleanExpressionParser.evaluate(conditionString)) {
            log.debug("Condition [ {} ] evaluates to true, executing nested actions", expression);

            for (final TestAction action : actions) {
                setLastExecutedAction(action);
                action.execute(context);
            }
        } else {
            log.debug("Condition [ {} ] evaluates to false, not executing nested actions", expression);
        }
    }

    /**
     * Condition which allows execution if true.
     *
     * @param conditionIn
     */
    public void setExpression(final String expressionIn) {
        this.expression = expressionIn;
    }

    /**
     * Gets the expression.
     *
     * @return the expression
     */
    public String getExpression() {
        return this.expression;
    }

}
