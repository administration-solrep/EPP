package fr.dila.st.api.service;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public interface STExcelService {
    /**
     * Créé un fichier excel avec son en tête
     *
     * @return le nombre de colonne créées par la méthode
     */
    HSSFWorkbook initExcelFile(String sheetName, String... header);

    /**
     * parcours le tableau de string datacell pour ajouter les données dans les cellules d'une ligne
     *
     * @param row
     *            la ligne sur laquelle ajouter les données
     * @param numColonneStart
     *            la première colonne où les données seront ajoutées
     * @param dataCells
     *            tableau de string des données
     */
    void addCellToRow(HSSFRow row, int numColonneStart, String... dataCells);
}
