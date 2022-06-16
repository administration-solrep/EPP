package fr.dila.st.core.logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.nuxeo.common.utils.FileUtils;

public abstract class AbstractLogger {
    protected static final String FR_DILA_REPONSES_ABSTRACT_LOGGER = "fr.dila.st.abstract.logger";

    protected static final String MESSAGE_FORMATTER_FILE = "messageFormatter.properties";

    protected static final String LOG_DOSSIER_KEY = "log.dossier";

    protected static final String LOG_REPONSE_KEY = "log.reponse";

    protected static final String LOG_FDR_KEY = "log.feuilleRoute";

    protected static Properties properties = new Properties();

    protected void initLogger() {
        String filename = FileUtils.getResourcePathFromContext(MESSAGE_FORMATTER_FILE);
        if (filename != null && !filename.isEmpty()) {
            FileInputStream fos = null;

            try {
                fos = new FileInputStream(filename);
                properties.load(fos);
            } catch (FileNotFoundException e) {
                throw new IllegalArgumentException("Fichier formatter de log non trouvé");
            } catch (IOException e) {
                throw new IllegalArgumentException("Probleme d'acces au fichier formatter de log");
            } finally {
                try {
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    // NOP
                }
            }
        } else {
            InputStream stream = Thread
                .currentThread()
                .getContextClassLoader()
                .getResourceAsStream(MESSAGE_FORMATTER_FILE);
            try {
                properties.load(stream);
            } catch (FileNotFoundException e) {
                throw new IllegalArgumentException("Fichier formatter de log non trouvé");
            } catch (IOException e) {
                throw new IllegalArgumentException("Probleme d'acces au fichier formatter de log");
            } finally {
                try {
                    if (stream != null) {
                        stream.close();
                    }
                } catch (IOException e) {
                    // NOP
                }
            }
        }
    }

    /**
     * Check
     *
     * @param log
     * @return
     */
    protected boolean check(Log log) {
        if (properties == null) {
            throw new IllegalArgumentException("Fichier formatter de log non trouve");
        }
        if (log == null) {
            throw new IllegalArgumentException("Logger ne peut pas être nul");
        }
        return true;
    }

    public static long getDurationInMs(long startTime) {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    }
}
