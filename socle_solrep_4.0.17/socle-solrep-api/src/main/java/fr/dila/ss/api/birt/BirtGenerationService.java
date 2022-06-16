package fr.dila.ss.api.birt;

import fr.dila.solon.birt.common.BirtOutputFormat;
import java.io.File;
import java.io.Serializable;
import java.util.Map;

public interface BirtGenerationService {
    /**
     * Génération de fichier dans le format désiré à partir du nom du rapport et des
     * paramètres scalaires donnés en paramètres.
     *
     * @param reportName       nom du rapport (chemin complet)
     * @param format           format du fichier résultat souhaité
     * @param scalarParameters paramètres scalaires spécifiques pour cette
     *                         génération.
     * @param resultPathname   (optionnel) chemin complet et nom du fichier à
     *                         générer. Si null, on ne l'impose pas.
     * @param track            si true, supprime le fichier lorsque l'objet
     *                         correspondant est supprimé par le GC. Si false,
     *                         laisse le fichier en place.
     * @return le chemin complet du fichier généré.
     */
    File generate(
        String reportName,
        String reportFile,
        BirtOutputFormat format,
        Map<String, ? extends Serializable> scalarParameters,
        String resultPathname,
        boolean track
    );

    /**
     * Génération asynchrone dans le format désiré à partir du nom du rapport et des
     * paramètres scalaires donnés en paramètres. La gestion de la tâche asynchrone
     * est déléguée à un Worker Nuxeo.
     *
     * @param reportName       nom du rapport (chemin complet)
     * @param format           format du fichier résultat souhaité
     * @param scalarParameters paramètres scalaires spécifiques pour cette
     *                         génération.
     * @param resultPathname   (optionnel) chemin complet et nom du fichier à
     *                         générer. Si null, on ne l'impose pas.
     * @param track            si true, supprime le fichier lorsque l'objet
     *                         correspondant est supprimé par le GC. Si false,
     *                         laisse le fichier en place.
     */
    void generateAsync(
        String reportName,
        String reportFile,
        BirtOutputFormat format,
        Map<String, ? extends Serializable> scalarParameters,
        String resultPathname,
        boolean track
    );

    /**
     * Renvoie une liste contenant l'ensemble des rapports Birt connus dans
     * l'application.
     *
     * @return une liste d'objets BirtReport.
     */
    BirtReports getReports();

    /**
     * Renvoie le fichier contenant l'ensemble des rapports Birt connus dans l'application.
     *
     * @return un fichier
     */
    File getBirtReportsFile();

    /**
     * Renvoie le rapport Birt dont le nom l'id est renseigné
     *
     * @param reportId
     * @return objet BirtReport
     */
    BirtReport getReport(String reportId);
}
