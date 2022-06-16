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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class AgentHandler implements InvocationHandler {

    public static <I> I newHandler(Class<I> type, Object agent) {
        return type.cast(Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class<?>[] { type }, new AgentHandler(agent)));
    }

    protected AgentHandler(Object agent) {
        this.agent = agent;
        type = agent.getClass();
    }

    protected final Class<?> type;

    protected final Object agent;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("humanReadable".equals(method.getName())) {
            return humanReadable((long) args[0]);
        }
        return agentMethod(method).invoke(agent, args);
    }

    protected static String[] units = { "b", "Kb", "Mb" };

    protected String humanReadable(long size) {
        String unit = "b";
        double dSize = size;
        for (String eachUnit : units) {
            unit = eachUnit;
            if (dSize < 1024) {
                break;
            }
            dSize /= 1024;
        }

        return dSize + unit;
    }

    protected final Map<Method, Method> agentMethods = new HashMap<>();

    protected Method agentMethod(Method bridgeMethod) throws NoSuchMethodException, SecurityException {
        Method agentMethod = agentMethods.get(bridgeMethod);
        if (agentMethod == null) {
            agentMethod = type.getDeclaredMethod(bridgeMethod.getName(), bridgeMethod.getParameterTypes());
            agentMethods.put(bridgeMethod, agentMethod);
        }
        return agentMethod;
    }

}
