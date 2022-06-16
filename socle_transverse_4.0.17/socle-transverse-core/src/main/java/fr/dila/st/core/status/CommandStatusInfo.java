package fr.dila.st.core.status;

import fr.sword.idl.naiad.nuxeo.addons.status.api.model.ResultInfo;
import java.io.InputStream;
import org.apache.commons.lang3.StringUtils;

public interface CommandStatusInfo extends SolonStatusInfo {
    default void checkCommand(ResultInfo resultInfo, String cmd) {
        try {
            ProcessBuilder builder = new ProcessBuilder(cmd.split(StringUtils.SPACE));
            // make sure we have only one InputStream to read to avoid parallelism/deadlock issues
            builder.redirectErrorStream(true);
            Process process = builder.start();
            // close process input immediately
            process.getOutputStream().close();
            // consume all process output
            try (InputStream in = process.getInputStream()) {
                byte[] bytes = new byte[4096];
                while (in.read(bytes) != -1) {
                    // loop
                }
            }
            // wait for process termination
            int exitVal = process.waitFor();
            if (exitVal != 0) {
                setKo(resultInfo, String.format("La commande [%s] a échoué", cmd));
            }
        } catch (Exception e) {
            setKo(resultInfo, e.getMessage());
        }
    }
}
