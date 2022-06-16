package fr.dila.ss.api.service;

import fr.dila.solon.birt.common.BirtOutputFormat;
import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import org.nuxeo.ecm.core.api.Blob;

public interface SSBirtService extends Serializable {
    /**
     * Génération synchrone (car résultat retourné) d'un blob (rapport) suivant le format passé en paramètre
     * @param reportName le nom du rapport à générer
     * @param reportFile le fichier rptDesign utilisé
     * @param inputValues les valeurs à utiliser pour générer le rapport
     * @param outPutFormat le format dans lequel générer le rapport
     * @return le blob du rapport généré
     *
     */
    Blob generateReportResults(
        final File fileResult,
        final File imagesDir,
        final String reportName,
        final String reportFile,
        final Map<String, ? extends Serializable> inputValues,
        final BirtOutputFormat outPutFormat
    );

    /**
     * Génération synchrone (car résultat retourné) d'un blob (rapport) suivant le format passé en paramètre
     * @param reportName le nom du rapport à générer
     * @param reportFile le fichier rptDesign utilisé
     * @param inputValues les valeurs à utiliser pour générer le rapport
     * @param outPutFormat le format dans lequel générer le rapport
     * @return le blob du rapport généré
     */
    Blob generateReportResults(
        final String reportName,
        final String reportFile,
        final Map<String, ? extends Serializable> inputValues,
        final BirtOutputFormat outPutFormat
    );

    /**
     * Génération synchrone (car résultat retourné une série de blob suivant les formats passés en paramètre
     * @param fileResult le fichier résultat du rapport créé
     * @param imagesDir le repertoire des images du rapport
     * @param reportName le nom du rapport à générer
     * @param reportFile le fichier rptDesign utilisé
     * @param inputValues les valeurs à utiliser pour générer le rapport
     * @param outPutFormats la liste des formats dans lequel générer le rapport
     * @return Map<String, Blob> une map des blobs suivant leur format
     *
     */
    Map<BirtOutputFormat, Blob> generateReportResults(
        final File fileResult,
        final File imagesDir,
        final String reportName,
        final String reportFile,
        final Map<String, ? extends Serializable> inputValues,
        final List<BirtOutputFormat> outPutFormats
    );

    /**
     * Génération synchrone (car résultat retourné) une série de blob suivant les formats passés en paramètre
     * @param reportName le nom du rapport à générer
     * @param reportFile le fichier rptDesign utilisé
     * @param inputValues les valeurs à utiliser pour générer le rapport
     * @param outPutFormats la liste des formats dans lequel générer le rapport
     * @return Map<String, Blob> une map des blobs suivant leur format
     *
     */
    Map<BirtOutputFormat, Blob> generateReportResults(
        final String reportName,
        final String reportFile,
        final Map<String, ? extends Serializable> inputValues,
        final List<BirtOutputFormat> outPutFormats
    );

    /**
     * Génération synchrone (car résultat retourné) d'une série de File suivant les formats passés en paramètre
     * @param reportName le nom du rapport à générer
     * @param reportFile le fichier rptDesign utilisé
     * @param inputValues les valeurs à utiliser pour générer le rapport
     * @param outPutFormats la liste des formats dans lequel générer le rapport
     * @return Map<String, Blob> une map des blobs suivant leur format
     *
     */
    Map<BirtOutputFormat, File> generateReportFileResults(
        String reportName,
        String reportFile,
        Map<String, ? extends Serializable> inputValues,
        List<BirtOutputFormat> outPutFormats
    );
}
