/*
 * (C) Copyright 2014 Nuxeo SA (http://nuxeo.com/) and others.
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
 *
 * Contributors:
 *     Stephane Lacoin
 */
package org.nuxeo.runtime.javaagent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class LoadAgentRule implements MethodRule {

    protected final AgentLoader loader = new AgentLoader();

    protected ObjectSizer sizer = loader.getAgent(ObjectSizer.class);

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface InjectSizer {

    }

    @Override
    public Statement apply(Statement base, FrameworkMethod method, Object target) {
        injectSizer(target);
        return base;
    }

    protected void injectSizer(Object target) {
        Field[] fields = target.getClass().getDeclaredFields();
        for (Field each : fields) {
            if (!ObjectSizer.class.equals(each.getType())) {
                continue;
            }
            InjectSizer inject = each.getAnnotation(InjectSizer.class);
            if (inject == null) {
                continue;
            }
            each.setAccessible(true);
            try {
                each.set(target, sizer);
            } catch (IllegalArgumentException | IllegalAccessException cause) {
                throw new RuntimeException("Cannot innject object sizer in fixture", cause);
            }
        }
    }

}
