package fr.dila.solonepp.core.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.nuxeo.ecm.core.api.CoreSession;

import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;

/**
 * Classe utilitaire pour créér des Documents Excel.
 * 
 * @author arolin
 */
public class ExcelUtil {
    
    /**
     * Logger formalisé en surcouche du logger apache/log4j 
     */
    private static final STLogger LOGGER = STLogFactory.getLog(ExcelUtil.class);
    
    private static short SIZE_FONTS_HEIGHT = 11;
    private static String SHEET_NAME = "Liste dossiers";
    public static final int NB_COLONNES = 5;
    
    /**
     * Créé un fichier Excel contenant les informations souhaitées pour une liste d'utilisateurs
     * 
     * @param session
     * @param users
     * 
     * @return fichier Excel sous forme de Datasource (afin d'envoyer le fichier comme pièce jointe dans un mail).
     */
    public static DataSource creationListUtilisateurExcel(CoreSession session, List<STUser> users) {
        DataSource fichierExcelResultat = null;
        try {
            // création du fichier Excel
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet();
            wb.setSheetName(0, "Resultats_identifiant");
            // création des colonnes header
            int numRow = 0;
            HSSFRow currentRow = sheet.createRow(numRow);
            int numCol = 0;
            currentRow.createCell(numCol++).setCellValue("Identifiant");
            currentRow.createCell(numCol++).setCellValue("Titre");
            currentRow.createCell(numCol++).setCellValue("Prénom");
            currentRow.createCell(numCol++).setCellValue("Nom");
            currentRow.createCell(numCol++).setCellValue("Adresse mèl");

            int nbCol = numCol;

            for (STUser user : users) {
                numCol = 0;
                if (user != null) {
                    numRow = numRow + 1;

                    currentRow = sheet.createRow(numRow);
                    currentRow.createCell(numCol++).setCellValue(user.getUsername());
                    currentRow.createCell(numCol++).setCellValue(user.getTitle());
                    currentRow.createCell(numCol++).setCellValue(user.getFirstName());
                    currentRow.createCell(numCol++).setCellValue(user.getLastName());
                    currentRow.createCell(numCol++).setCellValue(user.getEmail());
                }
            }

            // Font et style de la ligne de titre
            HSSFFont font = wb.createFont();
            font.setFontHeightInPoints((short) 11);
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

            HSSFCellStyle cellStyle = wb.createCellStyle();
            cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            cellStyle.setFont(font);

            for (int i = 0; i < nbCol; i++) {
                sheet.getRow(0).getCell(i).setCellStyle(cellStyle);
                sheet.autoSizeColumn(i);
            }

            // récupération du fichier dans un outPutStream
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            wb.write(baos);
            baos.close();
            // récupération du fichier Excel en tant que DataSource
            byte[] bytes = baos.toByteArray();
            fichierExcelResultat = new ByteArrayDataSource(bytes, "application/vnd.ms-excel");
        } catch (Exception exc) {
            LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_EXCEL_TEC, exc);
        }
        return fichierExcelResultat;
    }
}
