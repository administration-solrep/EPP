package fr.dila.st.core.util;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.nuxeo.ecm.core.api.CoreSession;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.factory.STLogFactory;

/**
 * Classe utilitaires pour la gestion des fichiers excel
 * 
 */
public class STExcelUtil {

	private static final STLogger	LOGGER						= STLogFactory.getLog(STExcelUtil.class);

	private static final short		SIZE_FONTS_HEIGHT			= 11;

	private static final String		IDENTIFIANTS_SHEET_NAME		= "Resultats_identifiant";
	private static final String[]	IDENTIFIANTS_HEADER			= { "Identifiant", "Titre", "Prénom", "Nom",
			"Adresse mél."										};
	private static final int		NB_COLONNES_IDENTIFIANTS	= IDENTIFIANTS_HEADER.length;

	protected STExcelUtil() {
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
		addCellToRow(currentRow, 0, header);

		return workbook;
	}

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
	public static void addCellToRow(HSSFRow row, int numColonneStart, String... dataCells) {
		int numColonne = numColonneStart;
		for (String dataCell : dataCells) {
			row.createCell(numColonne++).setCellValue(dataCell);
		}
	}

	/**
	 * formatte l'en tête de la feuille de calcul <br />
	 * (attention : les colonnes sont redimensionnées en fonction du contenu, donc autant appeler cette méthode une fois
	 * que toutes les données ont été mise dans la feuille)
	 * 
	 * @param workbook
	 * @param sheetName
	 * @param nbColonnes
	 */
	public static void formatStyle(HSSFWorkbook workbook, String sheetName, int nbColonnes) {
		// Font et style de la ligne de titre
		HSSFFont font = workbook.createFont();
		font.setFontHeightInPoints(SIZE_FONTS_HEIGHT);
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

		HSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		cellStyle.setFont(font);

		HSSFSheet sheet = workbook.getSheet(sheetName);
		for (int i = 0; i < nbColonnes; i++) {
			sheet.getRow(0).getCell(i).setCellStyle(cellStyle);
			sheet.autoSizeColumn(i);
		}
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
	 * @param session
	 * @param users
	 * 
	 * @return fichier Excel sous forme de Datasource (afin d'envoyer le fichier comme pièce jointe dans un mail).
	 */
	public static DataSource creationListUtilisateurExcel(CoreSession session, List<STUser> users) {
		DataSource fichierExcelResultat = null;
		try {
			// création du fichier Excel
			HSSFWorkbook workbook = initExcelFile(IDENTIFIANTS_SHEET_NAME, IDENTIFIANTS_HEADER);
			HSSFSheet sheet = workbook.getSheet(IDENTIFIANTS_SHEET_NAME);

			int numRow = 1;
			for (STUser user : users) {
				if (user != null) {
					numRow = numRow + 1;
					HSSFRow currentRow = sheet.createRow(numRow);
					addCellToRow(currentRow, 0, user.getUsername(), user.getTitle(), user.getFirstName(),
							user.getLastName(), user.getEmail());
				}
			}

			formatStyle(workbook, IDENTIFIANTS_SHEET_NAME, NB_COLONNES_IDENTIFIANTS);

			fichierExcelResultat = convertExcelToDataSource(workbook);
		} catch (IOException exc) {
			LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_EXCEL_TEC, exc);
		}
		return fichierExcelResultat;
	}

	/**
	 * utilisé pour récupérer la dernière ligne du fichier excel à créer
	 * 
	 * @param sheet
	 * @return retourne le numero de la dernière ligne qui a été utilisée dans le fichier (attention 0 != aucune ligne ;
	 *         Soit aucune ligne dans la feuille, soit une à la position zéro)
	 */
	protected static int getLastLine(HSSFSheet sheet) {
		return sheet.getLastRowNum();
	}

	/**
	 * @author FLT Create an Excel File based on the given content.
	 * 
	 * @param session
	 * @param sheetName
	 * @param header
	 * @param content
	 * @return
	 */
	public static DataSource createExcelFile(CoreSession session, String sheetName, String[] header,
			List<String[]> content) {
		DataSource fichierExcelResultat = null;
		try {
			HSSFWorkbook wb = initExcelFile(sheetName, header);
			HSSFSheet sheet = wb.getSheet(sheetName);
			int numRow = 1;
			HSSFRow currentRow;
			for (String[] values : content) {
				numRow++;
				currentRow = sheet.createRow(numRow);
				addCellToRow(currentRow, 0, values);
			}

			formatStyle(wb, sheetName, header.length);

			fichierExcelResultat = convertExcelToDataSource(wb);
		} catch (Exception exc) {
			LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_EXCEL_TEC, exc);
		}

		return fichierExcelResultat;
	}

	public static DataSource exportTable2Pdf(CoreSession session, String sheetName, String[] header,
			List<String[]> content) throws FileNotFoundException {

		ByteArrayOutputStream pdfFile = new ByteArrayOutputStream();

		try {

			Iterator<String[]> rowIterator = content.iterator();
			// Document pdf
			Document iTextXls2Pdf = new Document();
			PdfWriter.getInstance(iTextXls2Pdf, pdfFile);
			iTextXls2Pdf.open();
			Chunk c = new Chunk(sheetName);
			c.setUnderline(0.1f, -2f);
			c.setFont(FontFactory.getFont(FontFactory.COURIER, 14));
			Paragraph p = new Paragraph(c);
			p.setAlignment(Paragraph.ALIGN_CENTER);
			p.setSpacingBefore(50);
			iTextXls2Pdf.add(p);
			// Prend en paramètre le nombre de colonnes du tableau
			PdfPTable myTable = new PdfPTable(header.length);
			myTable.setSpacingBefore(50f);
			// Pour la gestion des différentes cellules, vu qu'on a fixé le nombre de colonnes, elles se positionnent
			// correctement
			PdfPCell tableCell;

			// Permet de déclarer la 1ère ligne comme header (si le tableau est trop grand par défaut, le header sera
			// reporté sur la page suivante)
			myTable.setHeaderRows(1);
			for (String colHeader : header) {
				c = new Chunk(colHeader);
				c.setUnderline(0.1f, -2f);
				c.setFont(FontFactory.getFont(FontFactory.COURIER, 14));
				PdfPCell cell = new PdfPCell(new Phrase(c));
				cell.setBackgroundColor(Color.LIGHT_GRAY);
				myTable.addCell(cell);
			}

			// Remplissage du tableau
			while (rowIterator.hasNext()) {
				String[] row = rowIterator.next();

				for (String cell : row) {
					tableCell = new PdfPCell(new Phrase(cell));
					myTable.addCell(tableCell);
				}
			}

			// On rajoute le tableau au document
			iTextXls2Pdf.add(myTable);
			iTextXls2Pdf.close();

		} catch (Exception exc) {
			LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_EXCEL_TEC, exc);
		}

		byte[] bytes = pdfFile.toByteArray();
		return new ByteArrayDataSource(bytes, "application/pdf");
	}

}
