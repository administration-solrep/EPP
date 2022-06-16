package fr.dila.ss.api.migration;

import java.io.Serializable;
import java.util.Date;

/**
 * Interface de log pour un changement de gouvernement
 *
 * @author asatre
 *
 */
public interface MigrationLoggerModel extends Serializable {
    long getId();

    void setId(long id);

    String getPrincipalName();

    void setPrincipalName(String principalName);

    void setStartDate(Date startDate);

    Date getStartDate();

    void setEndDate(Date endDate);

    Date getEndDate();

    long getElementsFils();

    void setElementsFils(long elementsFils);

    long getModeleFdr();

    void setModeleFdr(long modeleFdr);

    long getNorDossierLanceInite();

    void setNorDossierLanceInite(long norDossierLanceInite);

    long getNorDossierClos();

    void setNorDossierClos(long norDossierClos);

    long getBulletinOfficiel();

    void setBulletinOfficiel(long bulletinOfficiel);

    long getMotsCles();

    void setMotsCles(long motsCles);

    long getFdrStep();

    void setFdrStep(long fdrStep);

    long getCreatorPoste();

    void setCreatorPoste(long creatorPoste);

    long getMailboxPoste();

    void setMailboxPoste(long mailboxPoste);

    long getDeleteOld();

    void setDeleteOld(long deleteOld);

    long getModeleFdrCount();

    void setModeleFdrCount(long modeleFdrCount);

    long getNorDossierLanceIniteCount();

    void setNorDossierLanceIniteCount(long norDossierLanceIniteCount);

    long getNorDossierClosCount();

    void setNorDossierClosCount(long norDossierClosCount);

    long getBulletinOfficielCount();

    void setBulletinOfficielCount(long bulletinOfficielCount);

    long getMotsClesCount();

    void setMotsClesCount(long motsClesCount);

    long getFdrStepCount();

    void setFdrStepCount(long fdrStepCount);

    long getCreatorPosteCount();

    void setCreatorPosteCount(long creatorPosteCount);

    long getMailboxPosteCount();

    void setMailboxPosteCount(long mailboxPosteCount);

    long getModeleFdrCurrent();

    void setModeleFdrCurrent(long modeleFdrCurrent);

    long getNorDossierLanceIniteCurrent();

    void setNorDossierLanceIniteCurrent(long norDossierLanceIniteCurrent);

    long getNorDossierClosCurrent();

    void setNorDossierClosCurrent(long norDossierClosCurrent);

    long getBulletinOfficielCurrent();

    void setBulletinOfficielCurrent(long bulletinOfficielCurrent);

    long getMotsClesCurrent();

    void setMotsClesCurrent(long motsClesCurrent);

    long getFdrStepCurrent();

    void setFdrStepCurrent(long fdrStepCurrent);

    long getCreatorPosteCurrent();

    void setCreatorPosteCurrent(long creatorPosteCurrent);

    long getMailboxPosteCurrent();

    void setMailboxPosteCurrent(long mailboxPosteCurrent);

    long getTableRef();

    void setTableRef(long tableRef);

    long getTableRefCount();

    void setTableRefCount(long tableRefCount);

    long getTableRefCurrent();

    void setTableRefCurrent(long tableRefCurrent);

    String getTypeMigration();

    void setTypeMigration(String typeMigration);

    boolean isDeleteOldValue();

    void setDeleteOldValue(boolean deleteOldValue);

    String getOldElement();

    void setOldElement(String oldElement);

    String getNewElement();

    void setNewElement(String newElement);

    String getOldMinistere();

    void setOldMinistere(String oldMinistere);

    String getNewMinistere();

    void setNewMinistere(String newMinistere);

    void setStatus(String status);

    String getStatus();

    void assignMigrationInfo(MigrationInfo migrationInfo);

    boolean enCours();

    boolean terminee();

    boolean failed();

    Boolean isMigrationModeleFdr();

    void setMigrationModeleFdr(Boolean migrationModeleFdr);

    Boolean isMigrationWithDossierClos();

    void setMigrationWithDossierClos(Boolean migrationWithDossierClos);
}
