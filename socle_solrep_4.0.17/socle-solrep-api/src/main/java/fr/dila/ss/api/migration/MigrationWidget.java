package fr.dila.ss.api.migration;

import java.util.function.BiFunction;

public enum MigrationWidget {
    delete_node_ministere(
        (loggerModel, status) ->
            new MigrationLoggerInfos(
                loggerModel.getDeleteOld(),
                loggerModel.getDeleteOld(),
                loggerModel.getDeleteOld(),
                status
            )
    ),
    delete_node_poste(
        (loggerModel, status) ->
            new MigrationLoggerInfos(
                loggerModel.getDeleteOld(),
                loggerModel.getDeleteOld(),
                loggerModel.getDeleteOld(),
                status
            )
    ),
    delete_node_unite_structurelle(
        (loggerModel, status) ->
            new MigrationLoggerInfos(
                loggerModel.getDeleteOld(),
                loggerModel.getDeleteOld(),
                loggerModel.getDeleteOld(),
                status
            )
    ),
    delete_node_direction(
        (loggerModel, status) ->
            new MigrationLoggerInfos(
                loggerModel.getDeleteOld(),
                loggerModel.getDeleteOld(),
                loggerModel.getDeleteOld(),
                status
            )
    ),
    rattachement_direction(
        (loggerModel, status) ->
            new MigrationLoggerInfos(
                loggerModel.getNorDossierClos(),
                loggerModel.getNorDossierClosCurrent(),
                loggerModel.getNorDossierClosCount(),
                status
            )
    ),
    reattribution_nor_direction(
        (loggerModel, status) ->
            new MigrationLoggerInfos(
                loggerModel.getNorDossierLanceInite(),
                loggerModel.getNorDossierLanceIniteCurrent(),
                loggerModel.getNorDossierLanceIniteCount(),
                status
            )
    ),
    migrer_mots_cles(
        (loggerModel, status) ->
            new MigrationLoggerInfos(
                loggerModel.getMotsCles(),
                loggerModel.getMotsClesCurrent(),
                loggerModel.getMotsClesCount(),
                status
            )
    ),
    migrer_bulletin_officiel(
        (loggerModel, status) ->
            new MigrationLoggerInfos(
                loggerModel.getBulletinOfficiel(),
                loggerModel.getBulletinOfficielCurrent(),
                loggerModel.getBulletinOfficielCount(),
                status
            )
    ),
    rattachement_ministere(
        (loggerModel, status) ->
            new MigrationLoggerInfos(
                loggerModel.getNorDossierClos(),
                loggerModel.getNorDossierClosCurrent(),
                loggerModel.getNorDossierClosCount(),
                status
            )
    ),
    reattribution_nor_ministere(
        (loggerModel, status) ->
            new MigrationLoggerInfos(
                loggerModel.getNorDossierLanceInite(),
                loggerModel.getNorDossierLanceIniteCurrent(),
                loggerModel.getNorDossierLanceIniteCount(),
                status
            )
    ),
    migre_fdr_modele(
        (loggerModel, status) ->
            new MigrationLoggerInfos(
                loggerModel.getModeleFdr(),
                loggerModel.getModeleFdrCurrent(),
                loggerModel.getModeleFdrCount(),
                status
            )
    ),
    update_mailboxes(
        (loggerModel, status) ->
            new MigrationLoggerInfos(
                loggerModel.getMailboxPoste(),
                loggerModel.getMailboxPosteCurrent(),
                loggerModel.getMailboxPosteCount(),
                status
            )
    ),
    update_creator_poste(
        (loggerModel, status) ->
            new MigrationLoggerInfos(
                loggerModel.getCreatorPoste(),
                loggerModel.getCreatorPosteCurrent(),
                loggerModel.getCreatorPosteCount(),
                status
            )
    ),
    migrer_fdr_etape(
        (loggerModel, status) ->
            new MigrationLoggerInfos(
                loggerModel.getFdrStep(),
                loggerModel.getFdrStepCurrent(),
                loggerModel.getFdrStepCount(),
                status
            )
    ),
    deplacer_element_fils(
        (loggerModel, status) ->
            new MigrationLoggerInfos(
                loggerModel.getElementsFils(),
                loggerModel.getElementsFils(),
                loggerModel.getElementsFils(),
                status
            )
    ),
    update_dossiers(
        (loggerModel, status) ->
            new MigrationLoggerInfos(
                loggerModel.getNorDossierLanceInite(),
                loggerModel.getNorDossierLanceIniteCurrent(),
                loggerModel.getNorDossierLanceIniteCount(),
                status
            )
    );

    private BiFunction<MigrationLoggerModel, String, MigrationLoggerInfos> migrationLoggerInfosGetter;

    MigrationWidget(BiFunction<MigrationLoggerModel, String, MigrationLoggerInfos> migrationLoggerInfosGetter) {
        this.migrationLoggerInfosGetter = migrationLoggerInfosGetter;
    }

    public MigrationLoggerInfos getMigrationLoggerInfos(MigrationLoggerModel migrationLoggerModel, String status) {
        return migrationLoggerInfosGetter.apply(migrationLoggerModel, status);
    }
}
