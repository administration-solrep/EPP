package fr.dila.ss.ui.services;

import fr.dila.ss.api.birt.BirtReport;
import fr.dila.ss.api.birt.ReportProperty;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.ui.bean.BirtReportList;
import fr.dila.ss.ui.th.bean.BirtReportListForm;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.Collection;
import java.util.Map;
import org.nuxeo.ecm.core.api.Blob;

public interface SSStatistiquesUIService {
    /**
     * Renvoie la liste accessible dans la page des statistiques
     *
     * @param ssPrincipal
     * @param form
     * @return
     */
    BirtReportList getStatistiqueList(SSPrincipal ssPrincipal, BirtReportListForm form);

    /**
     * Renvoie un rapport Birt à partir de son id
     *
     * @param id
     * @return
     */
    BirtReport getBirtReport(String id);

    /**
     * Commande la génération du fichier résultat du rapport Birt indiqué en param
     * et renvoie le chemin du fichier généré. Les paramètres scalaires sont
     * renseignés à partir des paramètres indiqués.
     *
     * @param birtReport  rapport Birt
     * @param params : paramètres passés à l'application
     * @return le path
     */
    String generateReport(BirtReport birtReport, Map<String, String> params);

    /**
     * Vérifie si l'ensemble des paramètres scalaires nécessaires pour ce rapport
     * est renseigné.
     *
     * @param birtReport  rapport Birt
     * @param params
     * @return true ssi l'ensemble des params scalaires de ce rapport est renseigné.
     */
    boolean hasRequiredParameters(BirtReport birtReport, Map<String, String> params);

    /**
     * A partir du nom du répertoire, trouve le fichier html qui se trouve dedans et
     * renvoie le contenu du fichier, null si aucun fichier trouvé.
     *
     * @param reportDirectoryName nom du répertoire
     * @return
     */
    String getHtmlReportContent(SpecificContext context, String reportDirectoryName);

    /**
     * Renvoie la liste des propriétés de type scalaire nécessaires pour ce rapport.
     * Si la la liste est vide, le rapport peut être généré directement. Sinon, on a
     * besoin de connaitre les valeurs pour chaque propriété avant de générer un
     * rapport.
     *
     * @param birtReport
     * @return
     */
    Collection<ReportProperty> getScalarProperties(BirtReport birtReport);

    /**
     * Reconstitue et renvoie l'URL d'accès direct à la stat (paramètres inclus).
     *
     * @param birtReport
     * @param ministereId
     * @return l'URL (relative, reconstituée par Thymeleaf !)
     */
    String getReportUrl(BirtReport birtReport, Map<String, String> params);

    Blob generateReport(SpecificContext context);
}
