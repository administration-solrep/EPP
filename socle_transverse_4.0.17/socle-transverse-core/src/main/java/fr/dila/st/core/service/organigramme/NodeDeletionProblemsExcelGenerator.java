package fr.dila.st.core.service.organigramme;

import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.service.organigramme.OrganigrammeNodeDeletionProblem;
import fr.dila.st.core.util.FileUtils;
import fr.dila.st.core.util.ResourceHelper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.nuxeo.ecm.core.api.NuxeoException;

/**
 * Classe dédiée à la génération du fichier Excel reprenant les erreurs
 * identifiées lors de la validation pré-suppression d'un noeud de
 * l'organigramme.
 *
 * @author tlombard
 */
public class NodeDeletionProblemsExcelGenerator {
    /**
     * Le noeud de l'organigramme qu'on tente de supprimer.
     */
    private OrganigrammeNode node;

    /**
     * La liste des problèmes rencontrés.
     */
    private Collection<OrganigrammeNodeDeletionProblem> problems;

    private HSSFWorkbook workbook = null;

    public NodeDeletionProblemsExcelGenerator(
        OrganigrammeNode node,
        Collection<OrganigrammeNodeDeletionProblem> problems
    ) {
        this.node = node;
        this.problems = problems;
    }

    /**
     * Génère à la demande le fichier Excel correspondant.
     *
     * @return un objet HSSFWorkbook
     */
    public HSSFWorkbook generateWorkbook() {
        if (workbook == null) {
            workbook = new HSSFWorkbook();

            HSSFSheet sheet = workbook.createSheet("Problèmes");

            // Génération du header
            HSSFRow headerRow = sheet.createRow(0);
            HSSFCell cell = headerRow.createCell(0);
            cell.setCellValue(ResourceHelper.getString("organigramme.error.delete.excel.header.errorType"));
            cell = headerRow.createCell(1);
            cell.setCellValue(ResourceHelper.getString("organigramme.error.delete.excel.header.elementName"));
            cell = headerRow.createCell(2);
            cell.setCellValue(ResourceHelper.getString("organigramme.error.delete.excel.header.objectType"));
            cell = headerRow.createCell(3);
            cell.setCellValue(ResourceHelper.getString("organigramme.error.delete.excel.header.objectIdentification"));
            cell = headerRow.createCell(4);
            cell.setCellValue(ResourceHelper.getString("organigramme.error.delete.excel.header.posteIdentification"));

            // Génération des lignes suivantes
            int rowNum = 1;
            for (OrganigrammeNodeDeletionProblem problem : problems) {
                HSSFRow problemRow = sheet.createRow(rowNum);
                cell = problemRow.createCell(0);
                cell.setCellValue(ResourceHelper.getString(problem.getProblemType().getPropertyKey()));
                cell = problemRow.createCell(1);
                cell.setCellValue(problem.getBlockedObjIdentification());
                cell = problemRow.createCell(2);
                cell.setCellValue(ResourceHelper.getString(problem.getProblemType().getScope().getPropertyKey()));
                cell = problemRow.createCell(3);
                cell.setCellValue(problem.getBlockingObjIdentification());
                cell = problemRow.createCell(4);
                cell.setCellValue(problem.getPosteInfo());

                rowNum++;
            }
        }
        return workbook;
    }

    /**
     * Sauvegarde le fichier généré sur le serveur.
     *
     * @return le fichier généré
     */
    public File saveToDisk() {
        if (workbook == null) {
            workbook = generateWorkbook();
        }

        String fileName = FileUtils.getAppTmpFilePath("suppression_" + node.getId() + ".xls");
        try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
            workbook.write(fileOut);
            fileOut.flush();
        } catch (IOException e) {
            throw new NuxeoException(e);
        }

        return new File(fileName);
    }
}
