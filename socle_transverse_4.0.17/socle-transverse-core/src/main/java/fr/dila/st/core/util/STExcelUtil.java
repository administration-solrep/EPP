package fr.dila.st.core.util;

import fr.dila.st.api.user.STUser;
import fr.dila.st.core.export.AdminUsersSearchConfig;
import fr.dila.st.core.export.UsersConfig;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Classe utilitaires pour la gestion des fichiers excel
 */
public final class STExcelUtil {
    private static final short SIZE_FONTS_HEIGHT = 11;
    public static final int XLS_ROW_LIMIT = 65536;

    private STExcelUtil() {
        // Default constructor
    }

    /**
     * Créé un fichier excel avec son en tête
     *
     * @return le nombre de colonne créées par la méthode
     */
    public static HSSFWorkbook initExcelFile(String sheetName, String... header) {
        // création du fichier Excel
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();
        workbook.setSheetName(0, sheetName);
        // création des colonnes header
        HSSFRow currentRow = sheet.createRow(0);
        addCellToRow(currentRow, 0, Arrays.stream(header).map(ResourceHelper::getString).toArray(String[]::new));

        return workbook;
    }

    /**
     * parcours le tableau de string datacell pour ajouter les données dans les cellules d'une ligne
     *
     * @param row             la ligne sur laquelle ajouter les données
     * @param numColonneStart la première colonne où les données seront ajoutées
     * @param dataCells       tableau de string des données
     */
    public static void addCellToRow(HSSFRow row, int numColonneStart, String... dataCells) {
        int numColonne = numColonneStart;
        if (Arrays.stream(dataCells).anyMatch(Objects::nonNull)) {
            for (String dataCell : dataCells) {
                row.createCell(numColonne++).setCellValue(dataCell);
            }
        }
    }

    /**
     * formatte l'en tête de la feuille de calcul <br />
     * (attention : les colonnes sont redimensionnées en fonction du contenu, donc autant appeler cette méthode une fois
     * que toutes les données ont été mise dans la feuille)
     */
    public static void formatStyle(HSSFWorkbook workbook, String sheetName, int nbColonnes) {
        formatStyle(workbook, sheetName, nbColonnes, new Integer[nbColonnes], false);
    }

    public static void formatStyle(
        HSSFWorkbook workbook,
        String sheetName,
        int nbColonnes,
        Integer[] columnWitdth,
        boolean landscape
    ) {
        // Font et style de la ligne de titre
        HSSFFont font = workbook.createFont();
        font.setFontHeightInPoints(SIZE_FONTS_HEIGHT);
        font.setBold(true);

        HSSFCellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setFont(font);

        HSSFSheet sheet = workbook.getSheet(sheetName);
        for (int i = 0; i < nbColonnes; i++) {
            Integer width = columnWitdth.length > i ? columnWitdth[i] : null;
            sheet.getRow(0).getCell(i).setCellStyle(headerStyle); // Header style

            if (width == null) {
                sheet.autoSizeColumn(i);
            } else {
                sheet.setColumnWidth(i, width);
            }
        }
        if (landscape) {
            sheet.getPrintSetup().setLandscape(true);
        }
    }

    public static <T> HSSFWorkbook createExcelFile(
        String sheetName,
        String[] headers,
        List<T> exportItems,
        Consumer<ImmutableTriple<HSSFSheet, T, Integer>> addRow
    ) {
        HSSFWorkbook workbook = initExcelFile(sheetName, headers);
        HSSFSheet sheet = workbook.getSheet(sheetName);
        int numRow = 1;

        formatStyle(workbook, sheetName, headers.length);
        for (T item : exportItems) {
            addRow.accept(ImmutableTriple.of(sheet, item, numRow++));
        }

        return workbook;
    }

    /**
     * Converti un workbook (fichier excel) en un datasource pour envoyer le fichier
     *
     * @param workbook
     * @return retourne l'objet datasource correspondant au workbook
     * @throws IOException
     */
    public static DataSource convertExcelToDataSource(HSSFWorkbook workbook) throws IOException {
        // récupération du fichier dans un outPutStream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        baos.close();
        // récupération du fichier Excel en tant que DataSource
        byte[] bytes = baos.toByteArray();
        return new ByteArrayDataSource(bytes, "application/vnd.ms-excel");
    }

    /**
     * Créé un fichier Excel contenant les informations souhaitées pour une liste d'utilisateurs
     *
     * @return fichier Excel sous forme de Datasource (afin d'envoyer le fichier comme pièce jointe dans un mail).
     */
    public static DataSource creationListUtilisateurExcel(CoreSession session, List<STUser> users) {
        UsersConfig usersConfig = new UsersConfig(users);
        return usersConfig.getDataSource(session);
    }

    public static DataSource exportResultUserSearch(CoreSession session, List<DocumentModel> usersDocs) {
        AdminUsersSearchConfig adminUsersSearchConfig = new AdminUsersSearchConfig(usersDocs);
        return adminUsersSearchConfig.getDataSource(session);
    }
}
