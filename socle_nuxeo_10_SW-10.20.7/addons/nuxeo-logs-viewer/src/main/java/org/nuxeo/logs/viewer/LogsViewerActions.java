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
 *     Thomas Roger
 */

package org.nuxeo.logs.viewer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationChain;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.jsf.operations.DownloadFile;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.Blobs;
import org.nuxeo.launcher.config.ConfigurationGenerator;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.services.config.ConfigurationService;

/**
 * @since 6.0
 */
@Scope(ScopeType.CONVERSATION)
@Name("logsViewerActions")
@Install(precedence = Install.FRAMEWORK)
public class LogsViewerActions implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String LOG_MAX_LINES_COUNT_KEY = "nuxeo.logs.viewer.max.lines.count";

    public static final int LOG_MAX_LINES_COUNT = 1500;

    protected long logMaxLinesCount = -1;

    protected List<String> logFiles;

    protected String selectedLogFile;

    protected long selectedLogFileLastModified = -1;

    protected List<LogLine> initialLogLines;

    protected long bytesRead = 0;

    public long getLogMaxLinesCount() {
        if (logMaxLinesCount == -1) {
            ConfigurationService cs = Framework.getService(ConfigurationService.class);
            logMaxLinesCount = Integer.parseInt(
                    cs.getProperty(LOG_MAX_LINES_COUNT_KEY, String.valueOf(LOG_MAX_LINES_COUNT)), 10);
        }
        return logMaxLinesCount;
    }

    public List<String> getLogFiles() {
        if (logFiles == null) {
            ConfigurationGenerator configurationGenerator = new ConfigurationGenerator();
            configurationGenerator.init();

            logFiles = new ArrayList<>(configurationGenerator.getLogFiles());

            // Add nuxeoctl log file
            File nuxeoctlLog = new File(configurationGenerator.getLogDir(), "nuxeoctl.log");
            if (nuxeoctlLog.exists()) {
                logFiles.add(nuxeoctlLog.getAbsolutePath());
            }
            // Add console log file
            File consoleLog = new File(configurationGenerator.getLogDir(), "console.log");
            if (consoleLog.exists()) {
                logFiles.add(consoleLog.getAbsolutePath());
            }
        }
        return logFiles;
    }

    public String getSelectedLogFile() {
        if (selectedLogFile == null) {
            List<String> logFiles = getLogFiles();
            if (!logFiles.isEmpty()) {
                selectedLogFile = logFiles.get(0);
                selectedLogFileLastModified = -1;
            }
        }
        return selectedLogFile;
    }

    public void setSelectedLogFile(String selectedLogFile) {
        flushCache();
        this.selectedLogFile = selectedLogFile;
    }

    public String getFileName(String logFile) {
        return FilenameUtils.getName(logFile);
    }

    public List<LogLine> getInitialLogLines() throws IOException {
        if (initialLogLines == null) {
            initialLogLines = new ArrayList<>();
            String selectedLogFile = getSelectedLogFile();
            if (selectedLogFile != null) {
                File logFile = new File(selectedLogFile);
                if (logFile.exists()) {
                    try (ReversedLinesFileReader reversedLinesFileReader = new ReversedLinesFileReader(new File(
                            getSelectedLogFile()))) {
                        for (int i = 0; i < getLogMaxLinesCount(); i++) {
                            String line = reversedLinesFileReader.readLine();
                            if (line != null) {
                                initialLogLines.add(0, new LogLine(line));
                            } else {
                                break;
                            }
                        }
                    }
                    bytesRead = logFile.length();
                    selectedLogFileLastModified = logFile.lastModified();
                }
            }
        }
        return initialLogLines;
    }

    @Factory(value = "newLogLines", scope = ScopeType.EVENT)
    public List<LogLine> getNewLogLines() throws IOException {
        List<LogLine> logLines = new ArrayList<>();
        String selectedLogFile = getSelectedLogFile();
        if (selectedLogFile != null) {
            File logFile = new File(selectedLogFile);
            if (logFile.exists() && logFile.lastModified() > selectedLogFileLastModified) {
                if (bytesRead > logFile.length()) {
                    // log rotation
                    bytesRead = 0;
                }

                try (BufferedReader in = new BufferedReader(new FileReader(logFile))) {
                    in.skip(bytesRead);
                    String line;
                    while ((line = in.readLine()) != null) {
                        logLines.add(new LogLine(line));
                    }
                }
                bytesRead = logFile.length();
                selectedLogFileLastModified = logFile.lastModified();
            }
        }
        return logLines;
    }

    public void downloadLogFile() throws IOException, OperationException {
        String selectedLogFile = getSelectedLogFile();
        if (selectedLogFile == null) {
            return;
        }

        File logFile = new File(selectedLogFile);
        if (logFile.exists()) {
            Blob blob = Blobs.createBlob(logFile);
            OperationChain chain = new OperationChain("DownloadServerLogFile");
            chain.add(DownloadFile.ID);
            try (OperationContext ctx = new OperationContext()) {
                ctx.setInput(blob);
                Framework.getService(AutomationService.class).run(ctx, chain);
            }
        }
    }

    public void flushCache() {
        initialLogLines = null;
        selectedLogFileLastModified = -1;
        bytesRead = 0;
    }

    public static class LogLine {

        public enum Status {
            INFO, DEBUG, WARN, ERROR, UNKNOWN
        }

        protected String line;

        protected Status status;

        public LogLine(String line) {
            this.line = line;
            status = computeStatus(line);
        }

        public String getLine() {
            return line;
        }

        public Status getStatus() {
            return status;
        }

        protected static Status computeStatus(String line) {
            Status status = Status.UNKNOWN;
            if (line.matches(".* INFO .*")) {
                status = Status.INFO;
            } else if (line.matches(".* DEBUG .*")) {
                status = Status.DEBUG;
            } else if (line.matches(".* WARN .*")) {
                status = Status.WARN;
            } else if (line.matches(".* ERROR .*")) {
                status = Status.ERROR;
            }
            return status;
        }

    }

}
