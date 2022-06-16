package fr.dila.ss.api.migration;

import java.io.Serializable;
import java.util.Date;

public interface MigrationDetailModel extends Serializable {
    long getId();

    void setId(long id);

    String getDetail();

    void setDetail(String detail);

    MigrationLoggerModel getMigration();

    void setMigration(MigrationLoggerModel migration);

    String getStatut();

    void setStatut(String statut);

    Date getStartDate();

    void setStartDate(Date startDate);

    Date getEndDate();

    void setEndDate(Date endDate);
}
