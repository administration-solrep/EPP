package fr.dila.ss.core.util;

import fr.dila.ss.api.constant.SSInjGvtColumnsEnum;
import fr.dila.ss.api.migration.MigrationDetailModel;
import fr.dila.ss.core.dto.admin.ExportJournalTechniqueListingDTO;
import fr.dila.ss.core.dto.supervision.SupervisionUserDTO;
import fr.dila.ss.core.export.JournalTechniqueConfig;
import fr.dila.ss.core.export.MigrationDetailsConfig;
import fr.dila.ss.core.export.SupervisionConfig;
import fr.dila.ss.core.export.UsersAdminConfig;
import fr.dila.ss.core.export.UsersSimpleConfig;
import fr.dila.st.core.service.STServiceLocator;
import java.util.List;
import java.util.Set;
import javax.activation.DataSource;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public final class SSExcelUtil {

    private SSExcelUtil() {
        // Default constructor
    }

    public static DataSource exportMigrationDetails(CoreSession session, List<MigrationDetailModel> detailDocs) {
        MigrationDetailsConfig migrationDetailsConfig = new MigrationDetailsConfig(detailDocs);
        return migrationDetailsConfig.getDataSource(session);
    }

    public static DataSource exportSupervision(CoreSession session, List<SupervisionUserDTO> users, boolean isPdf) {
        SupervisionConfig supervisionConfig = new SupervisionConfig(users, isPdf);
        return supervisionConfig.getDataSource(session);
    }

    public static DataSource exportJournalTechnique(
        CoreSession session,
        List<ExportJournalTechniqueListingDTO> listings
    ) {
        JournalTechniqueConfig journalTechniqueConfig = new JournalTechniqueConfig(listings);
        return journalTechniqueConfig.getDataSource(session);
    }

    public static HSSFCell createCell(HSSFRow row, SSInjGvtColumnsEnum column) {
        return row.createCell(column.getIndex());
    }

    public static Cell getCell(Row row, SSInjGvtColumnsEnum column) {
        return row.getCell(column.getIndex());
    }

    public static void setBorder(Cell cell, BorderStyle borderStyle, short borderColor) {
        CellStyle style = cell.getCellStyle();
        style.setBorderBottom(borderStyle);
        style.setBottomBorderColor(borderColor);
        style.setBorderLeft(borderStyle);
        style.setLeftBorderColor(borderColor);
        style.setBorderRight(borderStyle);
        style.setRightBorderColor(borderColor);
        style.setBorderTop(borderStyle);
        style.setTopBorderColor(borderColor);
        cell.setCellStyle(style);
    }

    public static DataSource exportResultUserSearch(
        CoreSession session,
        List<DocumentModel> usersDocs,
        boolean isAdmin,
        boolean isAdminMinisteriel,
        Set<String> adminMinisterielMinisteres
    ) {
        DataSource fichierExcelResultat;

        if (isAdmin || isAdminMinisteriel) {
            UsersAdminConfig usersAdminConfig = new UsersAdminConfig(
                usersDocs,
                isAdmin,
                isAdminMinisteriel,
                adminMinisterielMinisteres,
                STServiceLocator.getSTUserService().getMapUsernameDateDerniereConnexion(usersDocs)
            );
            fichierExcelResultat = usersAdminConfig.getDataSource(session);
        } else {
            UsersSimpleConfig usersSimpleConfig = new UsersSimpleConfig(usersDocs);
            fichierExcelResultat = usersSimpleConfig.getDataSource(session);
        }

        return fichierExcelResultat;
    }
}
