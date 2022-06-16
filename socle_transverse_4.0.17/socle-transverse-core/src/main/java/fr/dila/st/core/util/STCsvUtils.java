package fr.dila.st.core.util;

import com.opencsv.CSVWriter;
import com.opencsv.ICSVWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.nuxeo.ecm.core.api.NuxeoException;

public final class STCsvUtils {

    private STCsvUtils() {}

    public static File writeAllLines(String filename, List<String[]> dataLines) {
        File csvFile = FileUtils.getAppTmpFile(FileUtils.generateCompleteCsvFilename(filename));
        try (ICSVWriter writer = new CSVWriter(new FileWriter(csvFile))) {
            writer.writeAll(dataLines, false);
        } catch (IOException exception) {
            throw new NuxeoException(
                "Une erreur s'est produite lors de la création du fichier CSV : " + csvFile.getName(),
                exception
            );
        }
        return csvFile;
    }
}
