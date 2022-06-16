package fr.dila.solonepp.core.service;

import fr.dila.solonepp.api.constant.SolonEppConstant;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.constant.SolonEppVocabularyConstant;
import fr.dila.solonepp.api.domain.tablereference.Acteur;
import fr.dila.solonepp.api.domain.tablereference.Circonscription;
import fr.dila.solonepp.api.domain.tablereference.Gouvernement;
import fr.dila.solonepp.api.domain.tablereference.Identite;
import fr.dila.solonepp.api.domain.tablereference.Mandat;
import fr.dila.solonepp.api.domain.tablereference.MembreGroupe;
import fr.dila.solonepp.api.domain.tablereference.Ministere;
import fr.dila.solonepp.api.domain.tablereference.Organisme;
import fr.dila.solonepp.api.domain.tablereference.Periode;
import fr.dila.solonepp.api.dto.TableReferenceDTO;
import fr.dila.solonepp.api.institution.InstitutionsEnum;
import fr.dila.solonepp.api.logging.enumerationCodes.EppLogEnumImpl;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.JetonService;
import fr.dila.solonepp.api.service.OrganigrammeService;
import fr.dila.solonepp.api.service.TableReferenceService;
import fr.dila.solonepp.core.assembler.TableReferenceAssembler;
import fr.dila.solonepp.core.dto.TableReferenceDTOImpl;
import fr.dila.solonepp.core.validator.TableReferenceValidator;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.InstitutionNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.service.VocabularyService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.DirectoryHelper;
import fr.dila.st.core.util.SolonDateConverter;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import fr.sword.naiad.nuxeo.ufnxql.core.query.FlexibleQueryMaker;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import fr.sword.xsd.solon.epp.ObjetType;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.IterableQueryResult;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.uidgen.UIDGeneratorService;
import org.nuxeo.ecm.core.uidgen.UIDSequencer;

/**
 * Implémentation du service des tables de références.
 *
 * @author jtremeaux
 * @author sly
 */
public class TableReferenceServiceImpl implements TableReferenceService {
    private static final String AS_D_WHERE_D = " AS d WHERE d.";
    private static final String AS_D = " AS d";
    private static final String SELECT_ID_FROM = "SELECT d.ecm:uuid as id FROM ";
    private static final String DOC_TYPE_INCONNU = "docType inconnu ";
    private static final String OBJET_NONTROUVE = "Objet %s non trouvé.";

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = -1202227712580404655L;

    /**
     * Logger surcouche socle de log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(TableReferenceServiceImpl.class);

    /**
     * Document racine des document de type Acteur
     */
    private static DocumentModel acteurRootDoc;

    /**
     * Document racine des document de type Circonscription
     */
    private static DocumentModel circonscriptionRootDoc;

    /**
     * Document racine des document de type Gouvernement
     */
    private static DocumentModel gouvernementRootDoc;

    /**
     * Document racine des document de type Identite
     */
    private static DocumentModel identiteRootDoc;

    /**
     * Document racine des document de type Mandat
     */
    private static DocumentModel mandatRootDoc;

    /**
     * Document racine des document de type MembreGroupe
     */
    private static DocumentModel membreGroupeRootDoc;

    /**
     * Document racine des document de type Ministere
     */
    private static DocumentModel ministereRootDoc;

    /**
     * Document racine des document de type Organisme
     */
    private static DocumentModel organismeRootDoc;

    /**
     * Document racine des document de type Periode
     */
    private static DocumentModel periodeRootDoc;

    /**
     * Requête sql select id from mandat
     */
    private static final String SQL_QUERY_MANDATS = "SELECT ID FROM MANDAT ";

    /**
     * clause order by sur identifiant : prend en compte la taille du champ pour
     * traiter le fait que le champ est sous la forme "MandatXXX" (champ varchar
     * donc 700 arrive après 3000)
     */
    private static final String SQL_ORDER_MAND_BY_IDENTIFIANTS = " ORDER BY length(IDENTIFIANT), IDENTIFIANT ";

    /**
     * "Impossible de modifier un objet n'appartenant pas à l'utilisateur."
     */
    private static final String CANT_UPDATE_DOC = "Impossible de modifier un objet n'appartenant pas à l'utilisateur.";
    private static final String[] NO_PARAMETERS = {};

    public TableReferenceServiceImpl() {
        // Default constructor
    }

    private String buildSelectQuery(String tableName) {
        return SELECT_ID_FROM + tableName + AS_D;
    }

    private List<DocumentModel> getDocumentModelsFromType(CoreSession session, String documentType) {
        return QueryUtils.doUFNXQLQueryAndFetchForDocuments(
            session,
            documentType,
            buildSelectQuery(documentType),
            NO_PARAMETERS
        );
    }

    private DocumentModel getDocumentModel(CoreSession session, DocumentModel doc, String docType, String docName) {
        if (doc != null) {
            return doc;
        }
        synchronized (this) {
            final List<DocumentModel> list = getDocumentModelsFromType(session, docType);
            if (CollectionUtils.isEmpty(list)) {
                throw new NuxeoException("Racine des " + docName + " non trouvée");
            } else if (list.size() > 1) {
                throw new NuxeoException("Plusieurs racines des " + docName + " trouvées");
            }
            return list.get(0);
        }
    }

    // *************************************************************
    // Table de référence "Acteur".
    // *************************************************************
    @Override
    public DocumentModel createBareActeurDoc(final CoreSession session) {
        return session.createDocumentModel(SolonEppConstant.ACTEUR_DOC_TYPE);
    }

    @Override
    public DocumentModel getActeurById(final CoreSession session, final String acteurId) {
        return getObjectById(
            session,
            acteurId,
            SolonEppConstant.ACTEUR_DOC_TYPE,
            SolonEppSchemaConstant.ACTEUR_SCHEMA_PREFIX
        );
    }

    @Override
    public boolean hasActeur(final CoreSession session, final String acteurId) {
        return hasObject(
            session,
            acteurId,
            SolonEppConstant.ACTEUR_DOC_TYPE,
            SolonEppSchemaConstant.ACTEUR_SCHEMA_PREFIX
        );
    }

    @Override
    public List<DocumentModel> findAllActeur(final CoreSession session) {
        return findAllObject(session, SolonEppConstant.ACTEUR_DOC_TYPE);
    }

    @Override
    public DocumentModel createActeur(final CoreSession session, final DocumentModel acteurDoc) {
        final Acteur acteur = acteurDoc.getAdapter(Acteur.class);

        LOGGER.info(session, STLogEnumImpl.CREATE_ACTEUR_TEC, "Acteur " + acteur.getIdentifiant());

        // Génère l'identifiant de l'acteur
        final String identifiant = SolonEppConstant.ACTEUR_DOC_TYPE.concat(
            getNextAsString(SolonEppConstant.ACTEUR_DOC_TYPE)
        );
        acteur.setIdentifiant(identifiant);

        // Renseigne le chemin et le nom du document
        acteurDoc.setPathInfo(getActeurRoot(session).getPathAsString(), identifiant);

        // Crée le document Acteur
        final DocumentModel acteurCreatedDoc = session.createDocument(acteurDoc);

        // Notifie la création d'un acteur
        final JetonService jetonService = SolonEppServiceLocator.getJetonService();
        jetonService.createNotificationObjetRefUpdate(session, ObjetType.ACTEUR.value(), acteurCreatedDoc, identifiant);

        return acteurCreatedDoc;
    }

    @Override
    public DocumentModel updateActeur(final CoreSession session, final DocumentModel acteurDoc) {
        final Acteur acteur = acteurDoc.getAdapter(Acteur.class);

        LOGGER.info(session, STLogEnumImpl.UPDATE_ACTEUR_TEC, acteurDoc);

        TableReferenceValidator.validateActeurForUpdate(acteur);

        // Vérification de l'existence et récupération de l'objet
        DocumentModel existingActeurDoc = getActeurById(session, acteur.getIdentifiant());
        if (existingActeurDoc == null) {
            throw new NuxeoException(String.format(OBJET_NONTROUVE, acteur.getIdentifiant()));
        }

        // Mise à jour des champs
        TableReferenceAssembler.assembleActeurForUpdate(acteurDoc, existingActeurDoc);

        // Sauvegarde le document Acteur
        existingActeurDoc = session.saveDocument(existingActeurDoc);

        // Notifie la modification d'un acteur
        final JetonService jetonService = SolonEppServiceLocator.getJetonService();
        jetonService.createNotificationObjetRefUpdate(
            session,
            ObjetType.ACTEUR.value(),
            existingActeurDoc,
            acteur.getIdentifiant()
        );

        return existingActeurDoc;
    }

    @Override
    public void disableAllActeur(final CoreSession session) {
        final List<DocumentModel> docs = findAllActeur(session);
        for (final DocumentModel doc : docs) {
            updateActeur(session, doc);
        }

        // Notifie le renouvellement des acteurs
        final JetonService jetonService = SolonEppServiceLocator.getJetonService();
        jetonService.createNotificationObjetRefReset(session, SolonEppConstant.ACTEUR_DOC_TYPE);
    }

    @Override
    public DocumentModel getActeurRoot(final CoreSession session) {
        return getDocumentModel(session, acteurRootDoc, SolonEppConstant.ACTEUR_ROOT_DOC_TYPE, "Acteurs");
    }

    // *************************************************************
    // Table de référence "Circonscription".
    // *************************************************************
    @Override
    public DocumentModel createBareCirconscriptionDoc(final CoreSession session) {
        return session.createDocumentModel(SolonEppConstant.CIRCONSCRIPTION_DOC_TYPE);
    }

    @Override
    public DocumentModel getCirconscriptionById(final CoreSession session, final String id) {
        return getObjectById(
            session,
            id,
            SolonEppConstant.CIRCONSCRIPTION_DOC_TYPE,
            SolonEppSchemaConstant.CIRCONSCRIPTION_SCHEMA_PREFIX
        );
    }

    @Override
    public boolean hasCirconscription(final CoreSession session, final String id) {
        return hasObject(
            session,
            id,
            SolonEppConstant.CIRCONSCRIPTION_DOC_TYPE,
            SolonEppSchemaConstant.CIRCONSCRIPTION_SCHEMA_PREFIX
        );
    }

    @Override
    public List<DocumentModel> findAllCirconscription(final CoreSession session, final boolean restricted) {
        if (restricted) {
            return findAllObjectWithProprietaire(
                session,
                SolonEppConstant.CIRCONSCRIPTION_DOC_TYPE,
                SolonEppSchemaConstant.CIRCONSCRIPTION_SCHEMA_PREFIX
            );
        }
        return findAllObject(session, SolonEppConstant.CIRCONSCRIPTION_DOC_TYPE);
    }

    @Override
    public DocumentModel createCirconscription(final CoreSession session, final DocumentModel circonscriptionDoc) {
        final Circonscription circonscription = circonscriptionDoc.getAdapter(Circonscription.class);

        LOGGER.info(session, EppLogEnumImpl.CREATE_CIRCONSCRIPTION_TEC, circonscriptionDoc);

        // Validation des données
        TableReferenceValidator.validateCirconscriptionForCreation(circonscription);

        // Génère l'identifiant de la circonscription
        final String identifiant = SolonEppConstant.CIRCONSCRIPTION_DOC_TYPE.concat(
            getNextAsString(SolonEppConstant.CIRCONSCRIPTION_DOC_TYPE)
        );
        circonscription.setIdentifiant(identifiant);

        // Renseigne l'institution dans le propriétaire
        final EppPrincipal principal = (EppPrincipal) session.getPrincipal();
        circonscription.setProprietaire(principal.getInstitutionId());

        // Renseigne le chemin et le nom du document
        circonscriptionDoc.setPathInfo(
            getCirconscriptionRoot(session).getPathAsString(),
            circonscription.getIdentifiant()
        );

        // Crée le document Circonscription
        final DocumentModel circonscriptionCreatedDoc = session.createDocument(circonscriptionDoc);

        // Notifie la création d'une circonscription
        final JetonService jetonService = SolonEppServiceLocator.getJetonService();
        jetonService.createNotificationObjetRefUpdate(
            session,
            ObjetType.CIRCONSCRIPTION.value(),
            circonscriptionCreatedDoc,
            identifiant
        );

        return circonscriptionCreatedDoc;
    }

    @Override
    public DocumentModel updateCirconscription(final CoreSession session, final DocumentModel circonscriptionDoc) {
        final Circonscription circonscription = circonscriptionDoc.getAdapter(Circonscription.class);

        LOGGER.info(session, EppLogEnumImpl.UPDATE_CIRCONSCRIPTION_TEC, circonscriptionDoc);

        // Validation des données
        TableReferenceValidator.validateCirconscriptionForUpdate(circonscription);

        // Vérification de l'existence et récupération de l'objet
        DocumentModel existingCirconscriptionDoc = getCirconscriptionById(session, circonscription.getIdentifiant());
        if (existingCirconscriptionDoc == null) {
            throw new NuxeoException(String.format(OBJET_NONTROUVE, circonscription.getIdentifiant()));
        }

        // Vérification de la propriété de l'objet
        final Circonscription existingCirconscription = existingCirconscriptionDoc.getAdapter(Circonscription.class);
        final EppPrincipal principal = (EppPrincipal) session.getPrincipal();
        if (!existingCirconscription.getProprietaire().equals(principal.getInstitutionId())) {
            throw new NuxeoException(CANT_UPDATE_DOC);
        }

        // Notifie la modification d'une circonscription
        if (!circonscription.equals(existingCirconscription)) {
            SolonEppServiceLocator
                .getJetonService()
                .createNotificationObjetRefUpdate(
                    session,
                    ObjetType.CIRCONSCRIPTION.value(),
                    existingCirconscriptionDoc,
                    circonscription.getIdentifiant()
                );
        }

        // Mise à jour des champs
        TableReferenceAssembler.assembleCirconscriptionForUpdate(circonscriptionDoc, existingCirconscriptionDoc);

        // Sauvegarde le document
        existingCirconscriptionDoc = session.saveDocument(existingCirconscriptionDoc);

        return existingCirconscriptionDoc;
    }

    @Override
    public void disableAllCirconscription(final CoreSession session) {
        final List<DocumentModel> docs = findAllCirconscription(session, true);
        for (final DocumentModel doc : docs) {
            final Circonscription adapted = doc.getAdapter(Circonscription.class);
            adapted.setDateFin(Calendar.getInstance());
            updateCirconscription(session, doc);
        }

        // Notifie le renouvellement des circonscriptions
        SolonEppServiceLocator
            .getJetonService()
            .createNotificationObjetRefReset(session, SolonEppConstant.CIRCONSCRIPTION_DOC_TYPE);
    }

    @Override
    public DocumentModel getCirconscriptionRoot(final CoreSession session) {
        return getDocumentModel(
            session,
            circonscriptionRootDoc,
            SolonEppConstant.CIRCONSCRIPTION_ROOT_DOC_TYPE,
            "Circonscription"
        );
    }

    // *************************************************************
    // Table de référence "Gouvernement".
    // *************************************************************

    @Override
    public DocumentModel createBareGouvernementDoc(final CoreSession session) {
        return session.createDocumentModel(SolonEppConstant.GOUVERNEMENT_DOC_TYPE);
    }

    @Override
    public DocumentModel getGouvernementById(final CoreSession session, final String id) {
        return getObjectById(
            session,
            id,
            SolonEppConstant.GOUVERNEMENT_DOC_TYPE,
            SolonEppSchemaConstant.GOUVERNEMENT_SCHEMA_PREFIX
        );
    }

    @Override
    public boolean hasGouvernement(final CoreSession session, final String id) {
        return hasObject(
            session,
            id,
            SolonEppConstant.GOUVERNEMENT_DOC_TYPE,
            SolonEppSchemaConstant.GOUVERNEMENT_SCHEMA_PREFIX
        );
    }

    @Override
    public List<DocumentModel> findAllGouvernement(final CoreSession session) {
        return findAllObject(session, SolonEppConstant.GOUVERNEMENT_DOC_TYPE);
    }

    @Override
    public DocumentModel createGouvernement(final CoreSession session, final DocumentModel gouvernementDoc) {
        // Validation des données
        final Gouvernement gouvernement = gouvernementDoc.getAdapter(Gouvernement.class);

        LOGGER.info(session, STLogEnumImpl.CREATE_GOUVERNEMENT_TEC, gouvernementDoc);

        TableReferenceValidator.validateGouvernementForCreation(gouvernement);

        // Génère l'identifiant de la gouvernement
        final String identifiant = SolonEppConstant.GOUVERNEMENT_DOC_TYPE.concat(
            getNextAsString(SolonEppConstant.GOUVERNEMENT_DOC_TYPE)
        );
        gouvernement.setIdentifiant(identifiant);

        // Renseigne le chemin et le nom du document
        gouvernementDoc.setPathInfo(getGouvernementRoot(session).getPathAsString(), gouvernement.getIdentifiant());

        // Crée le document Gouvernement
        final DocumentModel gouvernementCreatedDoc = session.createDocument(gouvernementDoc);

        // Notifie la création d'un gouvernement
        final JetonService jetonService = SolonEppServiceLocator.getJetonService();
        jetonService.createNotificationObjetRefUpdate(
            session,
            ObjetType.GOUVERNEMENT.value(),
            gouvernementCreatedDoc,
            identifiant
        );

        return gouvernementCreatedDoc;
    }

    @Override
    public DocumentModel updateGouvernement(final CoreSession session, final DocumentModel gouvernementDoc) {
        final Gouvernement gouvernement = gouvernementDoc.getAdapter(Gouvernement.class);

        LOGGER.info(session, STLogEnumImpl.UPDATE_GOUVERNEMENT_TEC, gouvernementDoc);
        // Validation des données
        TableReferenceValidator.validateGouvernementForUpdate(gouvernement);

        // Vérification de l'existence et récupération de l'objet
        DocumentModel existingGouvernementDoc = getGouvernementById(session, gouvernement.getIdentifiant());
        if (existingGouvernementDoc == null) {
            throw new NuxeoException(String.format(OBJET_NONTROUVE, gouvernement.getIdentifiant()));
        }
        final Gouvernement existingGouvernement = existingGouvernementDoc.getAdapter(Gouvernement.class);

        // Notifie la modification d'un gouvernement
        if (!gouvernement.equals(existingGouvernement)) {
            final JetonService jetonService = SolonEppServiceLocator.getJetonService();
            jetonService.createNotificationObjetRefUpdate(
                session,
                ObjetType.GOUVERNEMENT.value(),
                existingGouvernementDoc,
                gouvernement.getIdentifiant()
            );
        }

        // Mise à jour des champs
        TableReferenceAssembler.assembleGouvernementForUpdate(gouvernementDoc, existingGouvernementDoc);

        // Sauvegarde le document
        existingGouvernementDoc = session.saveDocument(existingGouvernementDoc);

        return existingGouvernementDoc;
    }

    @Override
    public void disableAllGouvernement(final CoreSession session) {
        final List<DocumentModel> docs = findAllGouvernement(session);
        for (final DocumentModel doc : docs) {
            final Gouvernement adapted = doc.getAdapter(Gouvernement.class);
            adapted.setDateFin(Calendar.getInstance());
            updateGouvernement(session, doc);
        }

        // Notifie le renouvellement des acteurs
        SolonEppServiceLocator
            .getJetonService()
            .createNotificationObjetRefReset(session, SolonEppConstant.GOUVERNEMENT_DOC_TYPE);
    }

    @Override
    public DocumentModel getGouvernementRoot(final CoreSession session) {
        return getDocumentModel(
            session,
            gouvernementRootDoc,
            SolonEppConstant.GOUVERNEMENT_ROOT_DOC_TYPE,
            "Gouvernement"
        );
    }

    // *************************************************************
    // Table de référence "Identité".
    // *************************************************************

    @Override
    public DocumentModel createBareIdentiteDoc(final CoreSession session) {
        return session.createDocumentModel(SolonEppConstant.IDENTITE_DOC_TYPE);
    }

    @Override
    public DocumentModel getIdentiteById(final CoreSession session, final String identiteId) {
        return getObjectById(
            session,
            identiteId,
            SolonEppConstant.IDENTITE_DOC_TYPE,
            SolonEppSchemaConstant.IDENTITE_SCHEMA_PREFIX
        );
    }

    @Override
    public boolean hasIdentite(final CoreSession session, final String identiteId) {
        return hasObject(
            session,
            identiteId,
            SolonEppConstant.IDENTITE_DOC_TYPE,
            SolonEppSchemaConstant.IDENTITE_SCHEMA_PREFIX
        );
    }

    @Override
    public List<DocumentModel> findAllIdentite(final CoreSession session) {
        return findAllObject(session, SolonEppConstant.IDENTITE_DOC_TYPE);
    }

    @Override
    public List<DocumentModel> findAllIdentiteByDescription(
        final CoreSession session,
        final String nom,
        final String prenom,
        final Calendar dateNaissance
    ) {
        String query =
            SELECT_ID_FROM +
            SolonEppConstant.IDENTITE_DOC_TYPE +
            AS_D_WHERE_D +
            SolonEppSchemaConstant.IDENTITE_SCHEMA_PREFIX +
            ':' +
            SolonEppSchemaConstant.IDENTITE_NOM_PROPERTY +
            " = ? " +
            " AND d." +
            SolonEppSchemaConstant.IDENTITE_SCHEMA_PREFIX +
            ':' +
            SolonEppSchemaConstant.IDENTITE_PRENOM_PROPERTY +
            " = ? " +
            " AND d." +
            SolonEppSchemaConstant.IDENTITE_SCHEMA_PREFIX +
            ':' +
            SolonEppSchemaConstant.IDENTITE_DATE_NAISSANCE_PROPERTY +
            " = ? ";

        return QueryUtils.doUFNXQLQueryAndFetchForDocuments(
            session,
            SolonEppConstant.IDENTITE_DOC_TYPE,
            query,
            new Object[] { nom, prenom, dateNaissance }
        );
    }

    @Override
    public DocumentModel createIdentite(final CoreSession session, final DocumentModel identiteDoc) {
        LOGGER.info(session, STLogEnumImpl.CREATE_IDENTITE_TEC);
        // Validation des données
        final Identite identite = identiteDoc.getAdapter(Identite.class);
        TableReferenceValidator.validateIdentiteForCreation(session, identite);

        // Génère l'identifiant de l'identite
        final String identifiant = SolonEppConstant.IDENTITE_DOC_TYPE.concat(
            getNextAsString(SolonEppConstant.IDENTITE_DOC_TYPE)
        );
        identite.setIdentifiant(identifiant);

        // Renseigne l'institution dans le propriétaire
        final EppPrincipal principal = (EppPrincipal) session.getPrincipal();
        identite.setProprietaire(principal.getInstitutionId());

        // Renseigne le chemin et le nom du document
        identiteDoc.setPathInfo(getIdentiteRoot(session).getPathAsString(), identite.getIdentifiant());

        // Crée le document Identite
        final DocumentModel identiteCreatedDoc = session.createDocument(identiteDoc);

        // Notifie la création d'une identité
        final JetonService jetonService = SolonEppServiceLocator.getJetonService();
        jetonService.createNotificationObjetRefUpdate(
            session,
            ObjetType.IDENTITE.value(),
            identiteCreatedDoc,
            identifiant
        );

        return identiteCreatedDoc;
    }

    @Override
    public DocumentModel updateIdentite(final CoreSession session, final DocumentModel identiteDoc) {
        LOGGER.info(session, STLogEnumImpl.UPDATE_IDENTITE_TEC, identiteDoc);

        final Identite identite = identiteDoc.getAdapter(Identite.class);

        // Validation des données
        TableReferenceValidator.validateIdentiteForUpdate(session, identite);

        // Vérification de l'existence et récupération de l'objet
        DocumentModel existingIdentiteDoc = getIdentiteById(session, identite.getIdentifiant());
        if (existingIdentiteDoc == null) {
            throw new NuxeoException(String.format(OBJET_NONTROUVE, identite.getIdentifiant()));
        }

        // Vérification de la propriété de l'objet
        final Identite existingIdentite = existingIdentiteDoc.getAdapter(Identite.class);
        final EppPrincipal principal = (EppPrincipal) session.getPrincipal();
        if (!existingIdentite.getProprietaire().equals(principal.getInstitutionId())) {
            throw new NuxeoException(CANT_UPDATE_DOC);
        }

        // Notifie la modification d'une identité
        if (!identite.equals(existingIdentite)) {
            final JetonService jetonService = SolonEppServiceLocator.getJetonService();
            jetonService.createNotificationObjetRefUpdate(
                session,
                ObjetType.IDENTITE.value(),
                existingIdentiteDoc,
                identite.getIdentifiant()
            );
        }

        // Mise à jour des champs
        TableReferenceAssembler.assembleIdentiteForUpdate(identiteDoc, existingIdentiteDoc);

        // Sauvegarde le document
        existingIdentiteDoc = session.saveDocument(existingIdentiteDoc);

        return existingIdentiteDoc;
    }

    @Override
    public void disableAllIdentite(final CoreSession session) {
        final List<DocumentModel> docs = findAllIdentite(session);
        for (final DocumentModel doc : docs) {
            final Identite adapted = doc.getAdapter(Identite.class);
            adapted.setDateFin(Calendar.getInstance());
            updateIdentite(session, doc);
        }

        // Notifie le renouvellement des identités
        SolonEppServiceLocator
            .getJetonService()
            .createNotificationObjetRefReset(session, SolonEppConstant.IDENTITE_DOC_TYPE);
    }

    @Override
    public DocumentModel getIdentiteRoot(final CoreSession session) {
        return getDocumentModel(session, identiteRootDoc, SolonEppConstant.IDENTITE_ROOT_DOC_TYPE, "Identites");
    }

    // *************************************************************
    // Table de référence "Mandat".
    // *************************************************************

    @Override
    public DocumentModel createBareMandatDoc(final CoreSession session) {
        return session.createDocumentModel(SolonEppConstant.MANDAT_DOC_TYPE);
    }

    @Override
    public DocumentModel getMandatById(final CoreSession session, final String id) {
        return getObjectById(
            session,
            id,
            SolonEppConstant.MANDAT_DOC_TYPE,
            SolonEppSchemaConstant.MANDAT_SCHEMA_PREFIX
        );
    }

    @Override
    public List<DocumentModel> getMandatsByNor(final CoreSession session, final String nor, final boolean activeOnly) {
        List<DocumentModel> list = new ArrayList<>();
        if (StringUtils.isNotBlank(nor)) {
            final StringBuilder query = new StringBuilder(SQL_QUERY_MANDATS);
            query.append(" WHERE NOR = ?");
            final List<Object> paramList = new ArrayList<>();
            paramList.add(nor);
            if (activeOnly) {
                query.append(" AND (DATEFIN > ? OR DATEFIN IS NULL) ");
                paramList.add(Calendar.getInstance());
            }

            query.append(SQL_ORDER_MAND_BY_IDENTIFIANTS);
            list = getDocsBySqlQuery(session, query.toString(), paramList.toArray());
        }
        return list;
    }

    @Override
    public boolean hasMandat(final CoreSession session, final String id) {
        return hasObject(session, id, SolonEppConstant.MANDAT_DOC_TYPE, SolonEppSchemaConstant.MANDAT_SCHEMA_PREFIX);
    }

    private boolean hasObject(
        final CoreSession session,
        final String id,
        final String docType,
        final String schemaPrefix
    ) {
        String query =
            SELECT_ID_FROM +
            docType +
            AS_D_WHERE_D +
            schemaPrefix +
            ':' +
            SolonEppSchemaConstant.TABLE_REFERENCE_IDENTIFIANT_PROPERTY +
            " = ? ";

        final Long count = QueryUtils.doCountQuery(session, QueryUtils.ufnxqlToFnxqlQuery(query), new Object[] { id });

        return count == 1;
    }

    @Override
    public List<DocumentModel> findAllMandat(final CoreSession session, final boolean restricted) {
        final StringBuilder query = new StringBuilder(SQL_QUERY_MANDATS);
        final List<Object> paramList = new ArrayList<>();
        if (restricted) {
            query.append(" WHERE PROPRIETAIRE = ? ");
            final String institutionId = ((EppPrincipal) session.getPrincipal()).getInstitutionId();
            paramList.add(institutionId);
        }
        query.append(SQL_ORDER_MAND_BY_IDENTIFIANTS);
        return getDocsBySqlQuery(session, query.toString(), paramList.toArray());
    }

    @Override
    public DocumentModel createMandat(final CoreSession session, final DocumentModel mandatDoc) {
        // Validation des données
        final Mandat mandat = mandatDoc.getAdapter(Mandat.class);

        TableReferenceValidator.validateMandatForCreation(session, mandat);

        // Génère l'identifiant de l'identite
        final String identifiant = SolonEppConstant.MANDAT_DOC_TYPE.concat(
            getNextAsString(SolonEppConstant.MANDAT_DOC_TYPE)
        );
        mandat.setIdentifiant(identifiant);

        // Renseigne l'institution dans le propriétaire
        final EppPrincipal principal = (EppPrincipal) session.getPrincipal();
        mandat.setProprietaire(principal.getInstitutionId());

        // Renseigne le chemin et le nom du document
        mandatDoc.setPathInfo(getMandatRoot(session).getPathAsString(), mandat.getIdentifiant());

        // Crée le document Mandat
        final DocumentModel mandatCreatedDoc = session.createDocument(mandatDoc);

        // Notifie la création d'un mandat
        SolonEppServiceLocator
            .getJetonService()
            .createNotificationObjetRefUpdate(session, ObjetType.MANDAT.value(), mandatCreatedDoc, identifiant);

        return mandatCreatedDoc;
    }

    @Override
    public DocumentModel updateMandat(final CoreSession session, final DocumentModel mandatDoc) {
        final Mandat mandat = mandatDoc.getAdapter(Mandat.class);

        LOGGER.info(session, STLogEnumImpl.UPDATE_MANDAT_TEC, mandatDoc);

        // Validation des données
        TableReferenceValidator.validateMandatForUpdate(session, mandat);

        // Vérification de l'existence et récupération de l'objet
        DocumentModel existingMandatDoc = getMandatById(session, mandat.getIdentifiant());
        if (existingMandatDoc == null) {
            throw new NuxeoException(String.format(OBJET_NONTROUVE, mandat.getIdentifiant()));
        }

        // Vérification de la propriété de l'objet
        final Mandat existingMandat = existingMandatDoc.getAdapter(Mandat.class);
        final EppPrincipal principal = (EppPrincipal) session.getPrincipal();
        if (!existingMandat.getProprietaire().equals(principal.getInstitutionId())) {
            throw new NuxeoException(CANT_UPDATE_DOC);
        }

        // Notifie la modification d'un mandat
        if (!mandat.equals(existingMandat)) {
            SolonEppServiceLocator
                .getJetonService()
                .createNotificationObjetRefUpdate(
                    session,
                    ObjetType.MANDAT.value(),
                    existingMandatDoc,
                    mandat.getIdentifiant()
                );
        }

        // Mise à jour des champs
        TableReferenceAssembler.assembleMandatForUpdate(mandatDoc, existingMandatDoc);

        // Sauvegarde le document
        existingMandatDoc = session.saveDocument(existingMandatDoc);

        return existingMandatDoc;
    }

    @Override
    public void disableAllMandat(final CoreSession session) {
        final List<DocumentModel> docs = findAllMandat(session, true);
        for (final DocumentModel doc : docs) {
            final Mandat adapted = doc.getAdapter(Mandat.class);
            adapted.setDateFin(new GregorianCalendar());
            updateMandat(session, doc);
        }

        // Notifie le renouvellement des mandats
        SolonEppServiceLocator
            .getJetonService()
            .createNotificationObjetRefReset(session, SolonEppConstant.MANDAT_DOC_TYPE);
    }

    @Override
    public DocumentModel getMandatRoot(final CoreSession session) {
        return getDocumentModel(session, mandatRootDoc, SolonEppConstant.MANDAT_ROOT_DOC_TYPE, "Mandats");
    }

    // *************************************************************
    // Table de référence "Membre de groupe".
    // *************************************************************

    @Override
    public DocumentModel createBareMembreGroupeDoc(final CoreSession session) {
        return session.createDocumentModel(SolonEppConstant.MEMBRE_GROUPE_DOC_TYPE);
    }

    @Override
    public DocumentModel getMembreGroupeById(final CoreSession session, final String membreGroupId) {
        return getObjectById(
            session,
            membreGroupId,
            SolonEppConstant.MEMBRE_GROUPE_DOC_TYPE,
            SolonEppSchemaConstant.MEMBRE_GROUPE_SCHEMA_PREFIX
        );
    }

    @Override
    public boolean hasMembreGroupe(final CoreSession session, final String membreGroupId) {
        return hasObject(
            session,
            membreGroupId,
            SolonEppConstant.MEMBRE_GROUPE_DOC_TYPE,
            SolonEppSchemaConstant.MEMBRE_GROUPE_SCHEMA_PREFIX
        );
    }

    @Override
    public List<DocumentModel> findAllMembreGroupe(final CoreSession session) {
        return findAllObject(session, SolonEppConstant.MEMBRE_GROUPE_DOC_TYPE);
    }

    @Override
    public DocumentModel createMembreGroupe(final CoreSession session, final DocumentModel membreGroupeDoc) {
        // Validation des données
        final MembreGroupe membreGroupe = membreGroupeDoc.getAdapter(MembreGroupe.class);

        LOGGER.info(session, EppLogEnumImpl.CREATE_MEMBRE_TEC, membreGroupeDoc);

        TableReferenceValidator.validateMembreGroupeForCreation(session, membreGroupe);

        // Génère l'identifiant du membreGroupe
        final String identifiant = SolonEppConstant.MEMBRE_GROUPE_DOC_TYPE.concat(
            getNextAsString(SolonEppConstant.MEMBRE_GROUPE_DOC_TYPE)
        );
        membreGroupe.setIdentifiant(identifiant);

        // Renseigne le chemin et le nom du document
        membreGroupeDoc.setPathInfo(getMembreGroupeRoot(session).getPathAsString(), membreGroupe.getIdentifiant());

        // Crée le document MembreGroupe
        final DocumentModel membreGroupeCreatedDoc = session.createDocument(membreGroupeDoc);

        // Notifie la création d'un membre de groupe
        SolonEppServiceLocator
            .getJetonService()
            .createNotificationObjetRefUpdate(
                session,
                ObjetType.MEMBRE_GROUPE.value(),
                membreGroupeCreatedDoc,
                identifiant
            );

        return membreGroupeCreatedDoc;
    }

    @Override
    public DocumentModel updateMembreGroupe(final CoreSession session, final DocumentModel membreGroupeDoc) {
        final MembreGroupe membreGroupe = membreGroupeDoc.getAdapter(MembreGroupe.class);

        LOGGER.info(session, EppLogEnumImpl.UPDATE_MEMBRE_TEC, membreGroupeDoc);

        // Validation des données
        TableReferenceValidator.validateMembreGroupeForUpdate(session, membreGroupe);

        // Vérification de l'existence et récupération de l'objet
        DocumentModel existingMembreGroupeDoc = getMembreGroupeById(session, membreGroupe.getIdentifiant());
        if (existingMembreGroupeDoc == null) {
            throw new NuxeoException(String.format(OBJET_NONTROUVE, membreGroupe.getIdentifiant()));
        }
        final MembreGroupe existingMembreGroupe = existingMembreGroupeDoc.getAdapter(MembreGroupe.class);

        // Notifie la modification d'un membre de groupe
        if (!membreGroupe.equals(existingMembreGroupe)) {
            SolonEppServiceLocator
                .getJetonService()
                .createNotificationObjetRefUpdate(
                    session,
                    ObjetType.MEMBRE_GROUPE.value(),
                    existingMembreGroupeDoc,
                    membreGroupe.getIdentifiant()
                );
        }

        // Mise à jour des champs
        TableReferenceAssembler.assembleMembreGroupeForUpdate(membreGroupeDoc, existingMembreGroupeDoc);

        // Sauvegarde le document
        existingMembreGroupeDoc = session.saveDocument(existingMembreGroupeDoc);

        return existingMembreGroupeDoc;
    }

    @Override
    public void disableAllMembreGroupe(final CoreSession session) {
        final List<DocumentModel> docs = findAllMembreGroupe(session);
        for (final DocumentModel doc : docs) {
            final MembreGroupe adapted = doc.getAdapter(MembreGroupe.class);
            adapted.setDateFin(new GregorianCalendar());
            updateMembreGroupe(session, doc);
        }

        // Notifie le renouvellement des membres de groupes
        final JetonService jetonService = SolonEppServiceLocator.getJetonService();
        jetonService.createNotificationObjetRefReset(session, SolonEppConstant.MEMBRE_GROUPE_DOC_TYPE);
    }

    @Override
    public DocumentModel getMembreGroupeRoot(final CoreSession session) {
        return getDocumentModel(
            session,
            membreGroupeRootDoc,
            SolonEppConstant.MEMBRE_GROUPE_ROOT_DOC_TYPE,
            "Membres groupe"
        );
    }

    // *************************************************************
    // Table de référence "Ministère".
    // *************************************************************

    @Override
    public DocumentModel createBareMinistereDoc(final CoreSession session) {
        return session.createDocumentModel(SolonEppConstant.MINISTERE_DOC_TYPE);
    }

    @Override
    public DocumentModel getMinistereById(final CoreSession session, final String ministereId) {
        return getObjectById(
            session,
            ministereId,
            SolonEppConstant.MINISTERE_DOC_TYPE,
            SolonEppSchemaConstant.MINISTERE_SCHEMA_PREFIX
        );
    }

    @Override
    public boolean hasMinistere(final CoreSession session, final String ministereId) {
        return hasObject(
            session,
            ministereId,
            SolonEppConstant.MINISTERE_DOC_TYPE,
            SolonEppSchemaConstant.MINISTERE_SCHEMA_PREFIX
        );
    }

    @Override
    public List<DocumentModel> findAllMinistere(final CoreSession session) {
        return findAllObject(session, SolonEppConstant.MINISTERE_DOC_TYPE);
    }

    @Override
    public DocumentModel createMinistere(final CoreSession session, final DocumentModel ministereDoc) {
        // Validation des données
        final Ministere ministere = ministereDoc.getAdapter(Ministere.class);

        LOGGER.info(session, STLogEnumImpl.CREATE_MINISTERE_TEC, ministereDoc);

        TableReferenceValidator.validateMinistereForCreation(session, ministere);

        // Génère l'identifiant du membreGroupe
        final String identifiant = SolonEppConstant.MINISTERE_DOC_TYPE.concat(
            getNextAsString(SolonEppConstant.MINISTERE_DOC_TYPE)
        );
        ministere.setIdentifiant(identifiant);

        // Renseigne le chemin et le nom du document
        ministereDoc.setPathInfo(getMinistereRoot(session).getPathAsString(), ministere.getIdentifiant());

        // Crée le document Ministere
        final DocumentModel ministereCreatedDoc = session.createDocument(ministereDoc);

        // Notifie la création d'un ministère
        SolonEppServiceLocator
            .getJetonService()
            .createNotificationObjetRefUpdate(session, ObjetType.MINISTERE.value(), ministereCreatedDoc, identifiant);

        return ministereCreatedDoc;
    }

    @Override
    public DocumentModel updateMinistere(final CoreSession session, final DocumentModel ministereDoc) {
        final Ministere ministere = ministereDoc.getAdapter(Ministere.class);

        LOGGER.info(session, STLogEnumImpl.UPDATE_MINISTERE_TEC, ministereDoc);
        // Validation des données
        TableReferenceValidator.validateMinistereForUpdate(session, ministere);

        // Vérification de l'existence et récupération de l'objet
        DocumentModel existingMinistereDoc = getMinistereById(session, ministere.getIdentifiant());
        if (existingMinistereDoc == null) {
            throw new NuxeoException(String.format(OBJET_NONTROUVE, ministere.getIdentifiant()));
        }
        final Ministere existingMinistere = existingMinistereDoc.getAdapter(Ministere.class);

        // Notifie la modification d'un ministère
        if (!ministere.equals(existingMinistere)) {
            SolonEppServiceLocator
                .getJetonService()
                .createNotificationObjetRefUpdate(
                    session,
                    ObjetType.MINISTERE.value(),
                    existingMinistereDoc,
                    ministere.getIdentifiant()
                );
        }

        // Mise à jour des champs
        TableReferenceAssembler.assembleMinistereForUpdate(ministereDoc, existingMinistereDoc);

        // Sauvegarde le document
        existingMinistereDoc = session.saveDocument(existingMinistereDoc);

        return existingMinistereDoc;
    }

    @Override
    public void disableAllMinistere(final CoreSession session) {
        final List<DocumentModel> docs = findAllMinistere(session);
        for (final DocumentModel doc : docs) {
            final Ministere adapted = doc.getAdapter(Ministere.class);
            adapted.setDateFin(Calendar.getInstance());
            updateMinistere(session, doc);
        }

        // Notifie le renouvellement des ministères
        SolonEppServiceLocator
            .getJetonService()
            .createNotificationObjetRefReset(session, SolonEppConstant.MINISTERE_DOC_TYPE);
    }

    @Override
    public DocumentModel getMinistereRoot(final CoreSession session) {
        return getDocumentModel(session, ministereRootDoc, SolonEppConstant.MINISTERE_ROOT_DOC_TYPE, "Ministeres");
    }

    // *************************************************************
    // Table de référence "Organisme".
    // *************************************************************

    @Override
    public DocumentModel createBareOrganismeDoc(final CoreSession session) {
        return session.createDocumentModel(SolonEppConstant.ORGANISME_DOC_TYPE);
    }

    @Override
    public DocumentModel getOrganismeById(final CoreSession session, final String id) {
        return getObjectById(
            session,
            id,
            SolonEppConstant.ORGANISME_DOC_TYPE,
            SolonEppSchemaConstant.ORGANISME_SCHEMA_PREFIX
        );
    }

    @Override
    public boolean hasOrganisme(final CoreSession session, final String id) {
        return hasObject(
            session,
            id,
            SolonEppConstant.ORGANISME_DOC_TYPE,
            SolonEppSchemaConstant.ORGANISME_SCHEMA_PREFIX
        );
    }

    @Override
    public List<DocumentModel> findAllOrganisme(final CoreSession session, final boolean restricted) {
        if (restricted) {
            return findAllObjectWithProprietaire(
                session,
                SolonEppConstant.ORGANISME_DOC_TYPE,
                SolonEppSchemaConstant.ORGANISME_SCHEMA_PREFIX
            );
        }
        return findAllObject(session, SolonEppConstant.ORGANISME_DOC_TYPE);
    }

    @Override
    public DocumentModel createOrganisme(final CoreSession session, final DocumentModel organismeDoc) {
        // Validation des données
        final Organisme organisme = organismeDoc.getAdapter(Organisme.class);

        LOGGER.info(session, EppLogEnumImpl.CREATE_ORGANISME_TEC, "Organisme " + organisme.getIdentifiant());

        TableReferenceValidator.validateOrganismeForCreation(organisme);

        // Génère l'identifiant du membreGroupe
        final String identifiant = SolonEppConstant.ORGANISME_DOC_TYPE.concat(
            getNextAsString(SolonEppConstant.ORGANISME_DOC_TYPE)
        );
        organisme.setIdentifiant(identifiant);

        // Renseigne l'institution dans le propriétaire
        final EppPrincipal principal = (EppPrincipal) session.getPrincipal();
        organisme.setProprietaire(principal.getInstitutionId());

        // Renseigne le chemin et le nom du document
        organismeDoc.setPathInfo(getOrganismeRoot(session).getPathAsString(), organisme.getIdentifiant());

        // Crée le document Organisme
        final DocumentModel organismeCreatedDoc = session.createDocument(organismeDoc);

        // Notifie la création d'un organisme
        SolonEppServiceLocator
            .getJetonService()
            .createNotificationObjetRefUpdate(session, ObjetType.ORGANISME.value(), organismeCreatedDoc, identifiant);

        return organismeCreatedDoc;
    }

    @Override
    public DocumentModel updateOrganisme(final CoreSession session, final DocumentModel organismeDoc) {
        final Organisme organisme = organismeDoc.getAdapter(Organisme.class);

        LOGGER.info(session, EppLogEnumImpl.UPDATE_ORGANISME_TEC, organismeDoc);

        // Validation des données
        TableReferenceValidator.validateOrganismeForUpdate(organisme);

        // Vérification de l'existence et récupération de l'objet
        DocumentModel existingOrganismeDoc = getOrganismeById(session, organisme.getIdentifiant());
        if (existingOrganismeDoc == null) {
            throw new NuxeoException(String.format(OBJET_NONTROUVE, organisme.getIdentifiant()));
        }

        // Vérification de la propriété de l'objet
        final Organisme existingOrganisme = existingOrganismeDoc.getAdapter(Organisme.class);
        final EppPrincipal principal = (EppPrincipal) session.getPrincipal();
        if (!existingOrganisme.getProprietaire().equals(principal.getInstitutionId())) {
            throw new NuxeoException(CANT_UPDATE_DOC);
        }

        // Notifie la modification d'un organisme
        if (!organisme.equals(existingOrganisme)) {
            SolonEppServiceLocator
                .getJetonService()
                .createNotificationObjetRefUpdate(
                    session,
                    ObjetType.ORGANISME.value(),
                    existingOrganismeDoc,
                    organisme.getIdentifiant()
                );
        }

        // Mise à jour des champs
        TableReferenceAssembler.assembleOrganismeForUpdate(organismeDoc, existingOrganismeDoc);

        // Sauvegarde le document
        existingOrganismeDoc = session.saveDocument(existingOrganismeDoc);

        return existingOrganismeDoc;
    }

    @Override
    public void disableAllOrganisme(final CoreSession session) {
        final List<DocumentModel> docs = findAllOrganisme(session, true);
        for (final DocumentModel doc : docs) {
            final Organisme adapted = doc.getAdapter(Organisme.class);
            adapted.setDateFin(Calendar.getInstance());
            updateOrganisme(session, doc);
        }

        // Notifie le renouvellement des organismes
        SolonEppServiceLocator
            .getJetonService()
            .createNotificationObjetRefReset(session, SolonEppConstant.ORGANISME_DOC_TYPE);
    }

    @Override
    public DocumentModel getOrganismeRoot(final CoreSession session) {
        return getDocumentModel(session, organismeRootDoc, SolonEppConstant.ORGANISME_ROOT_DOC_TYPE, "Organismes");
    }

    // *************************************************************
    // Table de référence "Période".
    // *************************************************************

    @Override
    public DocumentModel createBarePeriodeDoc(final CoreSession session) {
        return session.createDocumentModel(SolonEppConstant.PERIODE_DOC_TYPE);
    }

    @Override
    public DocumentModel getPeriodeById(final CoreSession session, final String id) {
        return getObjectById(
            session,
            id,
            SolonEppConstant.PERIODE_DOC_TYPE,
            SolonEppSchemaConstant.PERIODE_SCHEMA_PREFIX
        );
    }

    @Override
    public boolean hasPeriode(final CoreSession session, final String id) {
        return hasObject(session, id, SolonEppConstant.PERIODE_DOC_TYPE, SolonEppSchemaConstant.PERIODE_SCHEMA_PREFIX);
    }

    @Override
    public List<DocumentModel> findAllPeriode(final CoreSession session, final boolean restricted) {
        if (restricted) {
            return findAllObjectWithProprietaire(
                session,
                SolonEppConstant.PERIODE_DOC_TYPE,
                SolonEppSchemaConstant.PERIODE_SCHEMA_PREFIX
            );
        }
        return findAllObject(session, SolonEppConstant.PERIODE_DOC_TYPE);
    }

    @Override
    public DocumentModel createPeriode(final CoreSession session, final DocumentModel periodeDoc) {
        // Validation des données
        final Periode periode = periodeDoc.getAdapter(Periode.class);

        LOGGER.info(session, EppLogEnumImpl.CREATE_PERIODE_TEC, periodeDoc);

        TableReferenceValidator.validatePeriodeForCreation(periode);

        // Génère l'identifiant du membreGroupe
        final String identifiant = SolonEppConstant.PERIODE_DOC_TYPE.concat(
            getNextAsString(SolonEppConstant.PERIODE_DOC_TYPE)
        );
        periode.setIdentifiant(identifiant);

        // Renseigne l'institution dans le propriétaire
        final EppPrincipal principal = (EppPrincipal) session.getPrincipal();
        periode.setProprietaire(principal.getInstitutionId());

        // Renseigne le chemin et le nom du document
        periodeDoc.setPathInfo(getPeriodeRoot(session).getPathAsString(), periode.getIdentifiant());

        // Crée le document Periode
        final DocumentModel periodeCreatedDoc = session.createDocument(periodeDoc);

        // Notifie la création d'une période
        SolonEppServiceLocator
            .getJetonService()
            .createNotificationObjetRefUpdate(session, ObjetType.PERIODE.value(), periodeCreatedDoc, identifiant);

        return periodeCreatedDoc;
    }

    @Override
    public DocumentModel updatePeriode(final CoreSession session, final DocumentModel periodeDoc) {
        final Periode periode = periodeDoc.getAdapter(Periode.class);

        LOGGER.info(session, EppLogEnumImpl.UPDATE_PERIODE_TEC, periodeDoc);

        // Validation des données
        TableReferenceValidator.validatePeriodeForUpdate(periode);

        // Vérification de l'existence et récupération de l'objet
        DocumentModel existingPeriodeDoc = getPeriodeById(session, periode.getIdentifiant());
        if (existingPeriodeDoc == null) {
            throw new NuxeoException(String.format(OBJET_NONTROUVE, periode.getIdentifiant()));
        }

        // Vérification de la propriété de l'objet
        final Periode existingPeriode = existingPeriodeDoc.getAdapter(Periode.class);
        final EppPrincipal principal = (EppPrincipal) session.getPrincipal();
        if (!existingPeriode.getProprietaire().equals(principal.getInstitutionId())) {
            throw new NuxeoException(CANT_UPDATE_DOC);
        }

        // Notifie la modification d'une période
        if (!periode.equals(existingPeriode)) {
            SolonEppServiceLocator
                .getJetonService()
                .createNotificationObjetRefUpdate(
                    session,
                    ObjetType.PERIODE.value(),
                    existingPeriodeDoc,
                    periode.getIdentifiant()
                );
        }

        // Mise à jour des champs
        TableReferenceAssembler.assemblePeriodeForUpdate(periodeDoc, existingPeriodeDoc);

        // Sauvegarde le document
        existingPeriodeDoc = session.saveDocument(existingPeriodeDoc);

        return existingPeriodeDoc;
    }

    @Override
    public void disableAllPeriode(final CoreSession session) {
        final List<DocumentModel> docs = findAllPeriode(session, true);
        for (final DocumentModel doc : docs) {
            final Periode adapted = doc.getAdapter(Periode.class);
            adapted.setDateFin(Calendar.getInstance());
            updatePeriode(session, doc);
        }

        // Notifie le renouvellement des périodes
        SolonEppServiceLocator
            .getJetonService()
            .createNotificationObjetRefReset(session, SolonEppConstant.PERIODE_DOC_TYPE);
    }

    @Override
    public DocumentModel getPeriodeRoot(final CoreSession session) {
        return getDocumentModel(session, periodeRootDoc, SolonEppConstant.PERIODE_ROOT_DOC_TYPE, "Periodes");
    }

    // *************************************************************
    // Tools
    // *************************************************************

    protected DocumentModel getObjectById(
        final CoreSession session,
        final String id,
        final String docType,
        final String schemaPrefix
    ) {
        String query =
            SELECT_ID_FROM +
            docType +
            AS_D_WHERE_D +
            schemaPrefix +
            ':' +
            SolonEppSchemaConstant.TABLE_REFERENCE_IDENTIFIANT_PROPERTY +
            " = ? ";

        final List<DocumentModel> list = QueryUtils.doUFNXQLQueryAndFetchForDocuments(
            session,
            docType,
            query,
            new Object[] { id }
        );
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        return list.iterator().next();
    }

    protected List<DocumentModel> findAllObject(final CoreSession session, final String docType) {
        String query = SELECT_ID_FROM + docType + AS_D;

        return QueryUtils.doUFNXQLQueryAndFetchForDocuments(session, docType, query, NO_PARAMETERS);
    }

    protected List<DocumentModel> findAllObjectWithProprietaire(
        final CoreSession session,
        final String docType,
        final String schemaPrefix
    ) {
        String query =
            SELECT_ID_FROM +
            docType +
            AS_D_WHERE_D +
            schemaPrefix +
            ':' +
            SolonEppSchemaConstant.TABLE_REFERENCE_PROPRIETAIRE_PROPERTY +
            " = ? ";

        final String institutionId = ((EppPrincipal) session.getPrincipal()).getInstitutionId();

        return QueryUtils.doUFNXQLQueryAndFetchForDocuments(session, docType, query, new Object[] { institutionId });
    }

    @Override
    public List<TableReferenceDTO> searchTableReference(
        final CoreSession session,
        final String searchPattern,
        final String docType,
        final String emetteur,
        final boolean fullTableRef,
        String typeOrganisme
    ) {
        final List<TableReferenceDTO> result = new ArrayList<>();
        final List<Object> paramList = new ArrayList<>();

        if (SolonEppConstant.IDENTITE_DOC_TYPE.equals(docType)) {
            final StringBuilder query = defaultStartQueryRechercheIdentite(
                fullTableRef ? null : Calendar.getInstance(),
                emetteur,
                null,
                paramList
            );

            query.append(" AND t.");
            query.append(SolonEppSchemaConstant.IDENTITE_SCHEMA_PREFIX);
            query.append(':');
            query.append(SolonEppSchemaConstant.IDENTITE_NOM_PROPERTY);
            query.append(" ILIKE ? ");

            paramList.add(searchPattern + "%");

            buildIdentiteRechercheResponse(session, result, paramList, query);
        } else if (SolonEppConstant.ORGANISME_DOC_TYPE.equals(docType)) {
            final boolean emitParSenat = InstitutionsEnum.SENAT.name().equals(emetteur);
            final boolean emitParAN = InstitutionsEnum.ASSEMBLEE_NATIONALE.name().equals(emetteur);
            final boolean emitParGouvernement = InstitutionsEnum.GOUVERNEMENT.name().equals(emetteur);

            final StringBuilder query = defaultStartQueryRechercheOrganisme(
                fullTableRef ? null : Calendar.getInstance(),
                emetteur,
                null,
                paramList
            );

            String schemaPrefix = SolonEppSchemaConstant.ORGANISME_SCHEMA_PREFIX;

            query.append(" AND t.");
            query.append(schemaPrefix);
            query.append(':');
            query.append(SolonEppSchemaConstant.ORGANISME_NOM_PROPERTY);
            query.append(" ILIKE ? ");
            paramList.add("%" + searchPattern + "%");

            if (StringUtils.isNotBlank(emetteur)) {
                query.append(" AND t.");
                query.append(schemaPrefix);
                query.append(':');
                query.append(SolonEppSchemaConstant.ORGANISME_TYPE_ORGANISME_PROPERTY);
                query.append(" LIKE ? ");

                if (StringUtils.isBlank(typeOrganisme)) {
                    if (emitParSenat) {
                        paramList.add("ORGANE_SENAT");
                    } else if (emitParAN) {
                        paramList.add("ORGANE_AN");
                    } else if (emitParGouvernement) {
                        paramList.add("ORGANE%");
                    }
                } else {
                    // Pour Groupe Parlementaire
                    if (typeOrganisme.equals("GROUPE_PARLEMENTAIRE")) {
                        if (emitParSenat) {
                            paramList.add("GROUPE_SENAT");
                        } else if (emitParAN) {
                            paramList.add("GROUPE_AN");
                        } else if (emitParGouvernement) {
                            paramList.add("GROUPE%");
                        }
                    } else {
                        paramList.add(typeOrganisme);
                    }
                }
            }
            buildOrganismeRechercheResponse(session, result, paramList, query);
        } else {
            throw new NuxeoException(DOC_TYPE_INCONNU + docType);
        }

        return result;
    }

    private void buildOrganismeRechercheResponse(
        final CoreSession session,
        final List<TableReferenceDTO> result,
        final List<Object> paramList,
        final StringBuilder sb
    ) {
        try (IterableQueryResult res = QueryUtils.doUFNXQLQuery(session, sb.toString(), paramList.toArray())) {
            StreamSupport
                .stream(res.spliterator(), false)
                .forEach(
                    row -> {
                        final String idMandat = (String) row.get("id");
                        final String nom = (String) row.get("nom");
                        final Calendar dateDebut = (Calendar) row.get("dateDebut");
                        final Calendar dateFin = (Calendar) row.get("dateFin");

                        String date = "";
                        if (dateDebut != null && dateFin != null) {
                            date =
                                " (du " +
                                SolonDateConverter.DATE_SLASH.format(dateDebut) +
                                " au " +
                                SolonDateConverter.DATE_SLASH.format(dateFin) +
                                ")";
                        }
                        result.add(new TableReferenceDTOImpl(idMandat, nom + date));
                    }
                );
        }
    }

    private void buildIdentiteRechercheResponse(
        final CoreSession session,
        final List<TableReferenceDTO> result,
        final List<Object> paramList,
        final StringBuilder sb
    ) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        try (IterableQueryResult res = QueryUtils.doUFNXQLQuery(session, sb.toString(), paramList.toArray())) {
            StreamSupport
                .stream(res.spliterator(), false)
                .forEach(
                    row -> {
                        final String idMandat = (String) row.get("id");
                        final String civilite = (String) row.get("civ");
                        final String nom = (String) row.get("nom");
                        final String prenom = (String) row.get("prenom");
                        final Calendar dateDebut = (Calendar) row.get("dateDebut");
                        final Calendar dateFin = (Calendar) row.get("dateFin");

                        String date = "";
                        if (dateDebut != null && dateFin != null) {
                            date =
                                " (du " +
                                simpleDateFormat.format(dateDebut.getTime()) +
                                " au " +
                                simpleDateFormat.format(dateFin.getTime()) +
                                ")";
                        }

                        result.add(new TableReferenceDTOImpl(idMandat, civilite + " " + nom + " " + prenom + date));
                    }
                );
        }
    }

    private StringBuilder defaultStartQueryRechercheOrganisme(
        final Calendar cal,
        final String emetteur,
        final String identifiant,
        final List<Object> paramList
    ) {
        final StringBuilder query = new StringBuilder(
            "SELECT t.org:identifiant AS id, t.org:nom as nom, t.org:dateDebut as dateDebut, t.org:dateFin as dateFin FROM "
        );
        query.append(SolonEppConstant.ORGANISME_DOC_TYPE);
        query.append(" AS t ");

        final List<String> criterialist = new ArrayList<>();

        if (StringUtils.isNotBlank(identifiant)) {
            String criteria =
                " t." +
                SolonEppSchemaConstant.ORGANISME_SCHEMA_PREFIX +
                ':' +
                SolonEppSchemaConstant.TABLE_REFERENCE_IDENTIFIANT_PROPERTY +
                " = ? ";
            criterialist.add(criteria);
            paramList.add(identifiant);
        }

        if (cal != null) {
            String criteria =
                " t." +
                SolonEppSchemaConstant.ORGANISME_SCHEMA_PREFIX +
                ':' +
                SolonEppSchemaConstant.TABLE_REFERENCE_DATE_DEBUT_PROPERTY +
                " < ? " +
                " AND (t." +
                SolonEppSchemaConstant.ORGANISME_SCHEMA_PREFIX +
                ':' +
                SolonEppSchemaConstant.TABLE_REFERENCE_DATE_FIN_PROPERTY +
                " IS NULL OR t." +
                SolonEppSchemaConstant.ORGANISME_SCHEMA_PREFIX +
                ':' +
                SolonEppSchemaConstant.TABLE_REFERENCE_DATE_FIN_PROPERTY +
                " > ?)";
            criterialist.add(criteria);
            paramList.add(cal);
            paramList.add(cal);
        }

        if (StringUtils.isNotBlank(emetteur)) {
            final boolean emitParGouvernement = InstitutionsEnum.GOUVERNEMENT.name().equals(emetteur);
            if (!emitParGouvernement) {
                // non prise en compte de l'emetteur s'il est vide
                String criteria =
                    " t." +
                    SolonEppSchemaConstant.ORGANISME_SCHEMA_PREFIX +
                    ':' +
                    SolonEppSchemaConstant.ORGANISME_PROPRIETAIRE_PROPERTY +
                    " = ? ";
                criterialist.add(criteria);
                paramList.add(emetteur);
            }
        }

        if (CollectionUtils.isNotEmpty(criterialist)) {
            query.append(" WHERE ").append(StringUtils.join(criterialist, " AND "));
        }

        return query;
    }

    private StringBuilder defaultStartQueryRechercheIdentite(
        final Calendar cal,
        final String emetteur,
        final String identifiant,
        final List<Object> paramList
    ) {
        final StringBuilder query = new StringBuilder(
            "SELECT m.man:identifiant AS id, t.idt:civilite as civ, t.idt:nom as nom, t.idt:prenom as prenom, m.man:dateDebut as dateDebut, m.man:dateFin as dateFin FROM "
        );
        query.append(SolonEppConstant.MANDAT_DOC_TYPE);
        query.append(" as m, ");
        query.append(SolonEppConstant.IDENTITE_DOC_TYPE);
        query.append(" AS t WHERE m.");
        query.append(SolonEppSchemaConstant.MANDAT_SCHEMA_PREFIX);
        query.append(':');
        query.append(SolonEppSchemaConstant.MANDAT_IDENTITE_PROPERTY);
        query.append(" = t.");
        query.append(SolonEppSchemaConstant.IDENTITE_SCHEMA_PREFIX);
        query.append(':');
        query.append(SolonEppSchemaConstant.TABLE_REFERENCE_IDENTIFIANT_PROPERTY);

        if (StringUtils.isNotBlank(identifiant)) {
            query.append(" AND m.");
            query.append(SolonEppSchemaConstant.MANDAT_SCHEMA_PREFIX);
            query.append(':');
            query.append(SolonEppSchemaConstant.TABLE_REFERENCE_IDENTIFIANT_PROPERTY);
            query.append(" = ? ");
        }

        if (cal != null) {
            query.append(" AND m.");
            query.append(SolonEppSchemaConstant.MANDAT_SCHEMA_PREFIX);
            query.append(':');
            query.append(SolonEppSchemaConstant.TABLE_REFERENCE_DATE_DEBUT_PROPERTY);
            query.append(" < ? ");
            query.append(" AND (m.");
            query.append(SolonEppSchemaConstant.MANDAT_SCHEMA_PREFIX);
            query.append(':');
            query.append(SolonEppSchemaConstant.TABLE_REFERENCE_DATE_FIN_PROPERTY);
            query.append(" IS NULL OR m.");
            query.append(SolonEppSchemaConstant.MANDAT_SCHEMA_PREFIX);
            query.append(':');
            query.append(SolonEppSchemaConstant.TABLE_REFERENCE_DATE_FIN_PROPERTY);
            query.append(" > ?)");
        }

        if (StringUtils.isNotBlank(emetteur)) {
            // non prise en compte de l'emetteur s'il est vide
            query.append(" AND m.");
            query.append(SolonEppSchemaConstant.MANDAT_SCHEMA_PREFIX);
            query.append(':');
            query.append(SolonEppSchemaConstant.TABLE_REFERENCE_PROPRIETAIRE_PROPERTY);
            query.append(" = ? ");
        }

        if (StringUtils.isNotBlank(identifiant)) {
            paramList.add(identifiant);
        }

        if (cal != null) {
            paramList.add(cal);
            paramList.add(cal);
        }
        if (StringUtils.isNotBlank(emetteur)) {
            // non prise en compte de l'emetteur s'il est vide
            paramList.add(emetteur);
        }

        return query;
    }

    @Override
    public TableReferenceDTO findTableReferenceByIdAndType(
        final CoreSession session,
        final String identifiant,
        final String docType,
        final Calendar dateRecherche
    ) {
        final List<Object> paramList = new ArrayList<>();
        final List<TableReferenceDTO> result = new ArrayList<>();

        if (SolonEppConstant.IDENTITE_DOC_TYPE.equals(docType)) {
            // on force l'emmetteur a null pour afficher toutes les identites
            // même si l'emetteur n'est pas le
            // propriétaire
            final StringBuilder sb = defaultStartQueryRechercheIdentite(dateRecherche, null, identifiant, paramList);

            buildIdentiteRechercheResponse(session, result, paramList, sb);
        } else if (SolonEppConstant.ORGANISME_DOC_TYPE.equals(docType)) {
            // on force l'emmetteur a null pour afficher toutes les organismes
            // même si l'emetteur n'est pas le
            // propriétaire
            final StringBuilder sb = defaultStartQueryRechercheOrganisme(dateRecherche, null, identifiant, paramList);

            buildOrganismeRechercheResponse(session, result, paramList, sb);
        } else {
            throw new NuxeoException(DOC_TYPE_INCONNU + docType);
        }
        if (result.isEmpty()) {
            return null;
        } else if (result.size() == 1) {
            return result.iterator().next();
        } else {
            throw new NuxeoException("Plusieurs elements trouvés pour " + identifiant);
        }
    }

    @Override
    public DocumentModel getAttributionCommissionById(final CoreSession session, final String id) {
        return DirectoryHelper.getEntry(SolonEppVocabularyConstant.ATTRIBUTION_COMMISSION_VOCABULARY, id);
    }

    @Override
    public List<DocumentModel> findAllAttributionCommission(final CoreSession session) {
        final VocabularyService vocService = STServiceLocator.getVocabularyService();
        return vocService.getAllEntry(SolonEppVocabularyConstant.ATTRIBUTION_COMMISSION_VOCABULARY);
    }

    @Override
    public DocumentModel getNatureLoiById(final CoreSession session, final String id) {
        return DirectoryHelper.getEntry(SolonEppVocabularyConstant.NATURE_LOI_VOCABULARY, id);
    }

    @Override
    public List<DocumentModel> findAllNatureLoi(final CoreSession session) {
        final VocabularyService vocService = STServiceLocator.getVocabularyService();
        return vocService.getAllEntry(SolonEppVocabularyConstant.NATURE_LOI_VOCABULARY);
    }

    @Override
    public DocumentModel getNatureRapportById(final CoreSession session, final String id) {
        return DirectoryHelper.getEntry(SolonEppVocabularyConstant.NATURE_RAPPORT_VOCABULARY, id);
    }

    @Override
    public List<DocumentModel> findAllNatureRapport(final CoreSession session) {
        final VocabularyService vocService = STServiceLocator.getVocabularyService();
        return vocService.getAllEntry(SolonEppVocabularyConstant.NATURE_RAPPORT_VOCABULARY);
    }

    @Override
    public DocumentModel getTypeLoiById(final CoreSession session, final String id) {
        return DirectoryHelper.getEntry(SolonEppVocabularyConstant.TYPE_LOI_VOCABULARY, id);
    }

    @Override
    public List<DocumentModel> findAllTypeLoi(final CoreSession session) {
        final VocabularyService vocService = STServiceLocator.getVocabularyService();
        return vocService.getAllEntry(SolonEppVocabularyConstant.TYPE_LOI_VOCABULARY);
    }

    @Override
    public DocumentModel getSortAdoptionById(final CoreSession session, final String id) {
        return DirectoryHelper.getEntry(SolonEppVocabularyConstant.SORT_ADOPTION_VOCABULARY, id);
    }

    @Override
    public List<DocumentModel> findAllSortAdoption(final CoreSession session) {
        final VocabularyService vocService = STServiceLocator.getVocabularyService();
        return vocService.getAllEntry(SolonEppVocabularyConstant.SORT_ADOPTION_VOCABULARY);
    }

    @Override
    public DocumentModel getMotifIrrecevabiliteById(final CoreSession session, final String id) {
        return DirectoryHelper.getEntry(SolonEppVocabularyConstant.MOTIF_IRRECEVABILITE_VOCABULARY, id);
    }

    @Override
    public List<DocumentModel> findAllMotifIrrecevabilite(final CoreSession session) {
        final VocabularyService vocService = STServiceLocator.getVocabularyService();
        return vocService.getAllEntry(SolonEppVocabularyConstant.MOTIF_IRRECEVABILITE_VOCABULARY);
    }

    @Override
    public DocumentModel getSensAvisById(final CoreSession session, final String id) {
        return DirectoryHelper.getEntry(SolonEppVocabularyConstant.SENS_AVIS_VOCABULARY, id);
    }

    @Override
    public List<DocumentModel> findAllSensAvis(final CoreSession session) {
        final VocabularyService vocService = STServiceLocator.getVocabularyService();
        return vocService.getAllEntry(SolonEppVocabularyConstant.SENS_AVIS_VOCABULARY);
    }

    @Override
    public DocumentModel getNiveauLectureCodeById(final CoreSession session, final String id) {
        return DirectoryHelper.getEntry(SolonEppVocabularyConstant.NIVEAU_LECTURE_VOCABULARY, id);
    }

    @Override
    public List<DocumentModel> findAllNiveauLectureCode(final CoreSession session) {
        final VocabularyService vocService = STServiceLocator.getVocabularyService();
        return vocService.getAllEntry(SolonEppVocabularyConstant.NIVEAU_LECTURE_VOCABULARY);
    }

    @Override
    public DocumentModel getRapportParlementById(final CoreSession session, final String id) {
        return DirectoryHelper.getEntry(SolonEppVocabularyConstant.RAPPORT_PARLEMENT_VOCABULARY, id);
    }

    @Override
    public List<DocumentModel> findAllRapportParlement(final CoreSession session) {
        final VocabularyService vocService = STServiceLocator.getVocabularyService();
        return vocService.getAllEntry(SolonEppVocabularyConstant.RAPPORT_PARLEMENT_VOCABULARY);
    }

    @Override
    public DocumentModel getResultatCmpById(final CoreSession session, final String id) {
        return DirectoryHelper.getEntry(SolonEppVocabularyConstant.RESULTAT_CMP_VOCABULARY, id);
    }

    @Override
    public List<DocumentModel> findAllResultatCmp(final CoreSession session) {
        final VocabularyService vocService = STServiceLocator.getVocabularyService();
        return vocService.getAllEntry(SolonEppVocabularyConstant.RESULTAT_CMP_VOCABULARY);
    }

    @Override
    public OrganigrammeNode getInstitutionById(final CoreSession session, final String id) {
        final OrganigrammeService organigrammeService = SolonEppServiceLocator.getOrganigrammeService();
        return organigrammeService.getInstitution(id);
    }

    @Override
    public List<InstitutionNode> findAllInsitution(final CoreSession session) {
        final OrganigrammeService organigrammeService = SolonEppServiceLocator.getOrganigrammeService();
        return organigrammeService.getAllInstitutions();
    }

    @Override
    public DocumentModel getTypeActeById(final CoreSession session, final String id) {
        return DirectoryHelper.getEntry(SolonEppVocabularyConstant.TYPE_ACTE_VOCABULARY, id);
    }

    @Override
    public List<DocumentModel> findAllTypeActe(final CoreSession session) {
        final VocabularyService vocService = STServiceLocator.getVocabularyService();
        return vocService.getAllEntry(SolonEppVocabularyConstant.TYPE_ACTE_VOCABULARY);
    }

    @Override
    public List<DocumentModel> findAllMinistereByGov(final CoreSession session, final String govId) {
        String query =
            "SELECT t.ecm:uuid AS id FROM " +
            SolonEppConstant.MINISTERE_DOC_TYPE +
            " AS t" +
            " WHERE t." +
            SolonEppSchemaConstant.MINISTERE_SCHEMA_PREFIX +
            ":" +
            SolonEppSchemaConstant.MINISTERE_GOUVERNEMENT_PROPERTY +
            " = ? ";

        return QueryUtils.doUFNXQLQueryAndFetchForDocuments(
            session,
            SolonEppConstant.MINISTERE_DOC_TYPE,
            query,
            new Object[] { govId }
        );
    }

    @Override
    public List<DocumentModel> findAllMandatByMin(final CoreSession session, final String minId) {
        String query = SQL_QUERY_MANDATS + " WHERE MINISTERE = ? " + SQL_ORDER_MAND_BY_IDENTIFIANTS;

        return getDocsBySqlQuery(session, query, new Object[] { minId });
    }

    @Override
    public List<DocumentModel> findAllMandatWithoutMin(final CoreSession session) {
        String query =
            SQL_QUERY_MANDATS + " WHERE MINISTERE IS NULL AND PROPRIETAIRE = ? " + SQL_ORDER_MAND_BY_IDENTIFIANTS;

        return getDocsBySqlQuery(session, query, new Object[] { InstitutionsEnum.GOUVERNEMENT.name() });
    }

    /**
     * Retourne une liste de document model à partir d'une requête sql passée en
     * paramètre
     *
     * @param session
     * @param query
     *            : la requête sql - Attention, le select doit se faire sur la
     *            colonne ID
     * @param params
     *            : les paramètres utiles à la clause where
     * @return
     */
    private List<DocumentModel> getDocsBySqlQuery(
        final CoreSession session,
        final String query,
        final Object[] params
    ) {
        try (
            IterableQueryResult res = QueryUtils.doSqlQuery(
                session,
                new String[] { FlexibleQueryMaker.COL_ID },
                query,
                params
            )
        ) {
            return session.getDocuments(
                StreamSupport
                    .stream(res.spliterator(), false)
                    .map(row -> new IdRef((String) row.get(FlexibleQueryMaker.COL_ID)))
                    .toArray(DocumentRef[]::new)
            );
        }
    }

    @Override
    public List<DocumentModel> findAllIdentiteByMin(final CoreSession session, final String minId) {
        String query =
            "SELECT t.ecm:uuid AS id FROM " +
            SolonEppConstant.IDENTITE_DOC_TYPE +
            " AS t, " +
            SolonEppConstant.MANDAT_DOC_TYPE +
            " as m " +
            " WHERE t." +
            SolonEppSchemaConstant.IDENTITE_SCHEMA_PREFIX +
            ":" +
            SolonEppSchemaConstant.TABLE_REFERENCE_IDENTIFIANT_PROPERTY +
            " = m." +
            SolonEppSchemaConstant.MANDAT_SCHEMA_PREFIX +
            ":" +
            SolonEppSchemaConstant.MANDAT_IDENTITE_PROPERTY +
            " AND m." +
            SolonEppSchemaConstant.MANDAT_SCHEMA_PREFIX +
            ":" +
            SolonEppSchemaConstant.MANDAT_MINISTERE_PROPERTY +
            " = ? ";

        return QueryUtils.doUFNXQLQueryAndFetchForDocuments(
            session,
            SolonEppConstant.IDENTITE_DOC_TYPE,
            query,
            new Object[] { minId }
        );
    }

    @Override
    public List<String> findAllNodeHasChild(final CoreSession session, final String type, final String parentId) {
        final StringBuilder query = new StringBuilder("select distinct ");
        final List<Object> paramList = new ArrayList<>();
        if (ObjetType.GOUVERNEMENT.value().equals(type)) {
            query.append("m.gouvernement from Ministere m ");
        } else if (ObjetType.MINISTERE.value().equals(type)) {
            if (StringUtils.isNotBlank(parentId)) {
                query.append(
                    "mn.identifiant from mandat m, ministere mn where mn.gouvernement = ? and m.ministere = mn.identifiant "
                );
                paramList.add(parentId);
            } else {
                query.append("mn.identifiant from mandat m, ministere mn where m.ministere = mn.identifiant ");
            }
        } else {
            return null;
        }

        try (
            IterableQueryResult res = QueryUtils.doSqlQuery(
                session,
                new String[] { "min:identifiant" },
                query.toString(),
                paramList.toArray()
            )
        ) {
            return StreamSupport
                .stream(res.spliterator(), false)
                .map(row -> (String) row.get("min:identifiant"))
                .collect(Collectors.toList());
        }
    }

    private String getNextAsString(String seqName) {
        final UIDGeneratorService uidGeneratorService = ServiceUtil.getRequiredService(UIDGeneratorService.class);
        final UIDSequencer sequencer = uidGeneratorService.getSequencer();
        return Long.toString(sequencer.getNextLong(seqName));
    }
}
