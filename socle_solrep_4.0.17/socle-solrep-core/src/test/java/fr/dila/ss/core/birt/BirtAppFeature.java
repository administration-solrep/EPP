package fr.dila.ss.core.birt;

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.mockito.invocation.InvocationOnMock;
import org.nuxeo.ecm.platform.commandline.executor.api.CmdParameters;
import org.nuxeo.ecm.platform.commandline.executor.api.ExecResult;
import org.nuxeo.runtime.test.runner.RunnerFeature;

public class BirtAppFeature implements RunnerFeature {
    private File testFile;

    /**
     * Crée un objet ExecResult pour lequel le output est le fichier temporaire de
     * test dans lequel on aura préalablement mis le contenu du paramètre indiqué de
     * la commande.
     *
     * @param invocation
     * @param paramName
     * @return
     * @throws IOException
     */
    public ExecResult answerExecResult(InvocationOnMock invocation, String paramName) throws IOException {
        removeTestFile();

        testFile = new File("/tmp/test.txt");

        String commandLine = (String) invocation.getArguments()[0];
        CmdParameters cmdParams = (CmdParameters) invocation.getArguments()[1];
        String param = cmdParams.getParameter(paramName);

        Files.write(param.getBytes(), testFile);

        return new ExecResult(commandLine, Arrays.asList(testFile.getAbsolutePath()), 0L, 0);
    }

    public void removeTestFile() {
        if (testFile != null && testFile.exists()) {
            testFile.delete();
        }
    }

    public String readFirstLine(File file) throws IOException {
        return Files.asCharSource(file, StandardCharsets.UTF_8).readFirstLine();
    }
}
