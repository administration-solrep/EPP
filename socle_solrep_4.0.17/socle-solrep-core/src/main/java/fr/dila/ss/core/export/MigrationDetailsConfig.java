package fr.dila.ss.core.export;

import fr.dila.ss.api.migration.MigrationDetailModel;
import fr.dila.ss.core.export.enums.SSExcelSheetName;
import fr.dila.st.core.export.AbstractEnumExportConfig;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;

public class MigrationDetailsConfig extends AbstractEnumExportConfig<MigrationDetailModel> {

    public MigrationDetailsConfig(List<MigrationDetailModel> migrationDetails) {
        super(migrationDetails, SSExcelSheetName.MIGRATION);
    }

    @Override
    protected String[] getDataCells(CoreSession session, MigrationDetailModel item) {
        String startDate = formatDateWithHour(item.getStartDate());
        String endDate = formatDateWithHour(item.getEndDate());

        return new String[] { item.getDetail(), startDate, endDate, item.getStatut() };
    }
}
