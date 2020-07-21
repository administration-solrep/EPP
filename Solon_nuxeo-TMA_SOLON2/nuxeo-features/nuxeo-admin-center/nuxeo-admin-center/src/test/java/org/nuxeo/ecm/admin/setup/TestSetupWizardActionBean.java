/*
 * (C) Copyright 2010 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Julien Carsique
 *
 * $Id$
 */

package org.nuxeo.ecm.admin.setup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.nuxeo.common.Environment;
import org.nuxeo.common.utils.FileUtils;
import org.nuxeo.launcher.config.ConfigurationGenerator;

/**
 * @author jcarsique
 *
 */
public class TestSetupWizardActionBean {

    private SetupWizardActionBean setupWizardActionBean;

    private Map<String, String> parameters, advancedParameters;

    private File nuxeoHome, nuxeoConf, expectedNuxeoConf;

    @Before
    public void setUp() throws Exception {
        nuxeoHome = File.createTempFile("nuxeo", null);
        nuxeoHome.delete();
        nuxeoHome.mkdirs();
        System.setProperty(ConfigurationGenerator.NUXEO_HOME,
                nuxeoHome.getPath());

        // Properties required by ConfigurationGenerator
        System.setProperty(Environment.NUXEO_DATA_DIR, new File(nuxeoHome,
                "data").getPath());
        System.setProperty(Environment.NUXEO_LOG_DIR,
                new File(nuxeoHome, "log").getPath());

        nuxeoConf = new File(nuxeoHome, "bin");
        nuxeoConf.mkdirs();
        nuxeoConf = new File(nuxeoConf, "nuxeo.conf");
        FileUtils.copy(
                FileUtils.getResourceFileFromContext("configurator/nuxeo.conf"),
                nuxeoConf);
        System.setProperty(ConfigurationGenerator.NUXEO_CONF,
                nuxeoConf.getPath());

        FileUtils.copy(FileUtils.getResourceFileFromContext("templates/jboss"),
                new File(nuxeoHome, "templates"));
        System.setProperty("jboss.home.dir", nuxeoHome.getPath());

        setupWizardActionBean = new SetupWizardActionBean();
    }

    @Test
    public void testReadParameters() {
        parameters = setupWizardActionBean.getParameters();
        advancedParameters = setupWizardActionBean.getAdvancedParameters();
        assertEquals("0.0.0.0", parameters.get("nuxeo.bind.address"));
        assertNull(advancedParameters.get("nuxeo.bind.address"));
        assertNull(parameters.get("test.nuxeo.conf"));
        assertEquals("true", advancedParameters.get("test.nuxeo.conf"));
        assertNull(parameters.get("test.root.nuxeo.defaults"));
        assertEquals("true", advancedParameters.get("test.root.nuxeo.defaults"));
        assertNull(parameters.get("test.default.nuxeo.defaults"));
        assertEquals("true",
                advancedParameters.get("test.default.nuxeo.defaults"));
    }

    @Test
    public void testSaveParameters() throws IOException {
        parameters = setupWizardActionBean.getParameters();
        advancedParameters = setupWizardActionBean.getAdvancedParameters();
        parameters.put("nuxeo.bind.address", "127.0.0.1");
        advancedParameters.put("test.default.nuxeo.defaults", "false");
        setupWizardActionBean.saveParameters();
        expectedNuxeoConf = FileUtils.getResourceFileFromContext("configurator/nuxeo.conf.expected");
        BufferedReader bfNew = new BufferedReader(new FileReader(nuxeoConf));
        BufferedReader bfExp = new BufferedReader(new FileReader(
                expectedNuxeoConf));
        String newStr, expStr;
        while ((newStr = bfNew.readLine()) != null) {
            expStr = bfExp.readLine();
            if (!newStr.startsWith(ConfigurationGenerator.BOUNDARY_BEGIN)) {
                assertEquals(newStr, expStr);
            }
        }
        expStr = bfExp.readLine();
        assertNull(expStr);
        bfNew.close();
        bfExp.close();
    }

}
