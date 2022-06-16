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

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import org.nuxeo.runtime.api.Framework;

import com.sun.tools.attach.VirtualMachine;

public class AgentLoader {

    public static final AgentLoader INSTANCE = new AgentLoader();

    protected Object agent = load();

    protected ObjectSizer sizer = AgentHandler.newHandler(ObjectSizer.class, agent);

    public ObjectSizer getSizer() {
        return sizer;
    }

    protected <I> I getAgent(Class<I> type) {
        return AgentHandler.newHandler(type, agent);
    }

    protected Object load() {
        try {
            loadAgentIfNeeded();
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | MalformedURLException | URISyntaxException cause) {
            throw new RuntimeException("Cannot load nuxeo agent jar in virtual machine", cause);
        }
        try {
            return loadInstance();
        } catch (ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException
                | IllegalAccessException cause) {
            throw new RuntimeException("Cannot load nuxeo agent", cause);
        }
    }

    protected Object loadInstance() throws ClassNotFoundException, NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException {
        Class<?> clazz = AgentLoader.class.getClassLoader().loadClass("org.nuxeo.runtime.javaagent.NuxeoAgent");
        Field agentField = clazz.getDeclaredField("agent");
        agentField.setAccessible(true);
        return agentField.get(null);
    }

    protected void loadAgentIfNeeded() throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, MalformedURLException, URISyntaxException {
        try {
            AgentLoader.class.getClassLoader().loadClass("org.nuxeo.runtime.javaagent.NuxeoAgent");
        } catch (ClassNotFoundException e) {
            loadAgent();
        }
    }

    protected void loadAgent() throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, MalformedURLException, URISyntaxException {
        String nameOfRunningVM = ManagementFactory.getRuntimeMXBean().getName();
        int p = nameOfRunningVM.indexOf('@');
        String pid = nameOfRunningVM.substring(0, p);

        File home;
        try {
            home = Framework.getRuntime().getHome().getCanonicalFile();
        } catch (IOException cause) {
            throw new RuntimeException("cannot normalize runtime home path", cause);
        }
        File jarParentFolder = new File(home.getParentFile(), "bin");
        String jarLocation = locateAgentJar(jarParentFolder);

        if (jarLocation == null) {
            boolean isUnderTest = Boolean.parseBoolean(Framework.getProperty("org.nuxeo.runtime.testing", "false"));
            if (!isUnderTest) {
                throw new RuntimeException("Cannot locate nuxeo agent jar in bin folder");
            }
            URL testResource = AgentLoader.class.getResource("/");
            File testClasses = Paths.get(testResource.toURI()).toFile();
            File mainProject = new File(testClasses.getParentFile().getParentFile().getParentFile(), "main");
            File target = new File(mainProject, "target");
            jarLocation = locateAgentJar(target);
            if (jarLocation == null) {
                throw new RuntimeException("Cannot locate nuxeo agent jar in target folder " + target
                        + ", did you forgot to run package target");
            }
        }
        try {
            VirtualMachine vm = VirtualMachine.attach(pid);
            vm.loadAgent(jarLocation, "");
            vm.detach();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected String locateAgentJar(File dir) {
        File[] jars = dir.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith("nuxeo-javaagent-main") && name.endsWith(".jar");
            }
        });
        if (jars.length == 0) {
            return null;
        }
        return jars[0].getPath();
    }
}
