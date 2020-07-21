package fr.dila.solonepp.web.contentview;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.ClientRuntimeException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.SortInfo;
import org.nuxeo.ecm.platform.query.nxql.NXQLQueryBuilder;

import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.descriptor.evenementtype.EvenementTypeDescriptor;
import fr.dila.solonepp.api.domain.dossier.Dossier;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.domain.message.Message;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.DossierService;
import fr.dila.solonepp.api.service.EvenementTypeService;
import fr.dila.solonepp.api.service.VersionService;
import fr.dila.solonepp.api.service.corbeilletype.CorbeilleNode;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.solonepp.web.action.corbeille.CorbeilleTreeBean;
import fr.dila.solonepp.web.client.MessageDTO;
import fr.dila.solonepp.web.client.MessageDTOImpl;
import fr.dila.st.api.service.STLockService;
import fr.dila.st.core.query.FlexibleQueryMaker;
import fr.dila.st.core.query.QueryUtils;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.web.contentview.AbstractDTOPageProvider;
import fr.dila.st.web.contentview.CorePageProviderUtil;
import fr.dila.st.web.contentview.HiddenColumnPageProvider;

public class CorbeillePageProvider extends AbstractDTOPageProvider implements HiddenColumnPageProvider {

    // private static final String TOTAL_COUNT_PROPERTY = "totalCount";
    // private static final String USER_COLUMN_PROPERTY = "userColumn";

    /**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 81707229571958291L;

	/**
     * Paramètre du provider contenant la chaîne de la requête.
     */
    public static final String QUERY_STRING_PROPERTY = "queryString";

    /**
     * Paramètre du provider contenent les paramètres de la requête. Les paramètres peuvent être une collection, un tableau d'objet ou non renseignés.
     */
    public static final String QUERY_PARAMETER_PROPERTY = "parameters";

    /**
     * corbeille tree
     */
    public static final String CORBEILLE_TREE = "corbeilleTree";

    /**
     * Default constructor
     */
    public CorbeillePageProvider() {
    	super();
    }
    
    @Override
    protected void fillCurrentPageMapList(final CoreSession coreSession) throws ClientException {
        final EvenementTypeService evenementTypeService = SolonEppServiceLocator.getEvenementTypeService();
        final STLockService lockService = STServiceLocator.getSTLockService();

        currentItems = new ArrayList<Map<String, Serializable>>();

        resultsCount = QueryUtils.doCountQuery(coreSession, FlexibleQueryMaker.KeyCode.UFXNQL.key + query, getParam());

        final List<DocumentModel> messageDocList = QueryUtils.doUFNXQLQueryAndFetchForDocuments(coreSession, null,
                FlexibleQueryMaker.KeyCode.UFXNQL.key + query, getParam(), getPageSize(), offset);

        final Map<String, MessageDTO> mapEvenementIdDto = new HashMap<String, MessageDTO>();
        final Map<String, MessageDTO> mapVersionIdDto = new HashMap<String, MessageDTO>();
        final Map<String, List<MessageDTO>> mapDossierIdDto = new HashMap<String, List<MessageDTO>>();
        // chargement des infos du message
        for (final DocumentModel messageDoc : messageDocList) {

        	// messageDoc.refresh();
            final Message message = messageDoc.getAdapter(Message.class);

            final MessageDTO messageDto = new MessageDTOImpl();

            if (message.getDate() != null) {
                messageDto.setDate(message.getDate().getTime());
            }

            messageDto.setIdMessage(messageDoc.getId());

            if (message.isTypeDestinataire() || message.isTypeCopie()) {
                messageDto.setTypeEmetteur(false);
            } else {
                messageDto.setTypeEmetteur(true);
            }

            messageDto.setEtatMessage(getEtatMessage(message, coreSession));

            // Renseigne les infos sur le locks
            final Map<String, String> lockDetail = lockService.getLockDetails(coreSession, messageDoc);
            if (lockDetail != null) {
                final String locker = lockDetail.get(STLockService.LOCKER);
                final String lockTime = lockDetail.get(STLockService.LOCK_TIME);

                messageDto.setLocker(locker);
                messageDto.setLockTime(lockTime);
            }

            final String versionId = message.getActiveVersionId();
            messageDto.setModeCreationVersion(versionId);
            mapVersionIdDto.put(versionId, messageDto);

            mapEvenementIdDto.put(message.getCaseDocumentId(), messageDto);

            currentItems.add(messageDto);
        }

        // Chargement des infos contenues dans la version
        if (!mapVersionIdDto.isEmpty()) {
            final DocumentModelList dml = QueryUtils.retrieveDocuments(coreSession, "Version", mapVersionIdDto.keySet());
            for (final DocumentModel dm : dml) {
                final String versionId = dm.getId();
                final Version version = dm.getAdapter(Version.class);
                final MessageDTO messageDto = mapVersionIdDto.get(versionId);
                messageDto.setObjetDossier(version.getObjet());
                messageDto.setNiveauLecture(version.getNiveauLecture());
                if (version.getNiveauLectureNumero() != null) {
                    messageDto.setNiveauLectureNumero("" + version.getNiveauLectureNumero());
                }
                messageDto.setModeCreationVersion(version.getModeCreation());
                final String numeroVersion = version.getNumeroVersion().toString();
                messageDto.setNumeroVersion(numeroVersion);
                messageDto.setPieceJointe(version.isPieceJointePresente());
                messageDto.setIdSenat(version.getSenat());
            }
        }

        // chargement des infos de l'événement
        if (!mapEvenementIdDto.isEmpty()) {
            final DocumentModelList dml = QueryUtils.retrieveDocuments(coreSession, "Evenement", mapEvenementIdDto.keySet());
            for (final DocumentModel dm : dml) {
                final String evenementId = dm.getId();
                final Evenement evenement = dm.getAdapter(Evenement.class);
                final MessageDTO messageDto = mapEvenementIdDto.get(evenementId);

                messageDto.setIdEvenement(evenement.getTitle());
                messageDto.setUidEvenement(evenement.getDocument().getId());
                messageDto.setEmetteur(evenement.getEmetteur());
                messageDto.setDestinataire(evenement.getDestinataire());
                if (evenement.getDestinataireCopie() != null && !evenement.getDestinataireCopie().isEmpty()) {
                    messageDto.setCopie(evenement.getDestinataireCopie().get(0));
                }

                messageDto.setEtatEvenement(getEtatEvenement(evenement, messageDto.getModeCreationVersion()));

                final EvenementTypeDescriptor evtType = evenementTypeService.getEvenementType(evenement.getTypeEvenement());
                if (evtType != null) {
                    messageDto.setEvenement(evtType.getLabel());
                    if (evenementTypeService.isEvenementTypeAlerte(evtType.getName())) {
                        messageDto.setAlerte(true);
                    }
                }

                final String dossierId = evenement.getDossier();
                messageDto.setIdDossier(dossierId);

                List<MessageDTO> dtoList = mapDossierIdDto.get(dossierId);
                if (dtoList == null) {
                    dtoList = new ArrayList<MessageDTO>();
                }
                dtoList.add(messageDto);
                mapDossierIdDto.put(dossierId, dtoList);
            }
        }
        // chargement des infos du dossier
        if (!mapDossierIdDto.isEmpty()) {
            final DossierService dossierService = SolonEppServiceLocator.getDossierService();
            final List<DocumentModel> dml = dossierService.getDossierList(coreSession, mapDossierIdDto.keySet());

            for (final DocumentModel dm : dml) {
                final Dossier dossier = dm.getAdapter(Dossier.class);
                final List<MessageDTO> dtoList = mapDossierIdDto.get(dossier.getTitle());
                for (final MessageDTO messageDto : dtoList) {
                    if (dossier.isAlerte()) {
                        messageDto.setEtatDossier(MessageDTO.EN_ALERTE);
                    } else {
                        messageDto.setEtatDossier(MessageDTO.EN_INSTANCE);
                    }
                }
            }
        }
    }

    @Override
    public Boolean isHiddenColumn(final String isHidden) throws ClientException {
        if (!StringUtils.isEmpty(isHidden)) {
            final Map<String, Serializable> props = getProperties();

            if ("SENAT".equals(isHidden)) {
                final CoreSession session = (CoreSession) props.get(CORE_SESSION_PROPERTY);
                final EppPrincipal principal = (EppPrincipal) session.getPrincipal();
                final Set<String> institutions = principal.getInstitutionIdSet();
                if (institutions != null && !institutions.isEmpty()) {
                    if (!institutions.contains(isHidden)) {
                        return Boolean.TRUE;
                    }
                }
            } else {
                final CorbeilleTreeBean corbeilleTree = (CorbeilleTreeBean) props.get(CORBEILLE_TREE);
                if (corbeilleTree != null) {
                    final CorbeilleNode corbeilleNode = corbeilleTree.getCurrentItem();
                    if (corbeilleNode != null) {
                        final List<String> hiddenColumns = corbeilleNode.getHiddenColumnList();
                        if (hiddenColumns != null) {
                            if (hiddenColumns.contains(isHidden)) {
                                return Boolean.TRUE;
                            }
                        }
                    }
                }
            }
        }
        return Boolean.FALSE;
    }

    @Override
    public void setSearchDocumentModel(final DocumentModel searchDocumentModel) {
        if (this.searchDocumentModel != searchDocumentModel) {
            this.searchDocumentModel = searchDocumentModel;
            refresh();
        }
    }

    @Override
    public void setSortInfos(final List<SortInfo> sortInfo) {
        this.sortInfos = sortInfo;
        refresh();
    }

    @Override
    protected void buildQuery() {
        try {
            SortInfo[] sortArray = null;

            if (sortInfos != null) {
                sortArray = CorePageProviderUtil.getSortinfoForQuery(sortInfos);
            }

            final Map<String, Serializable> props = getProperties();
            final Object queryStringProp = props.get(QUERY_STRING_PROPERTY);
            if (queryStringProp == null || !(queryStringProp instanceof String)) {
                throw new ClientRuntimeException("La propriété \"queryString\" doit être renseignée");
            }
            final String queryString = (String) queryStringProp;
            final String newQuery = NXQLQueryBuilder.getQuery(queryString, null, false, false, sortArray);

            if (newQuery != null && !newQuery.equals(query)) {
                // query has changed => refresh
                refresh();
                query = newQuery;
            }
        } catch (final ClientException e) {
            throw new ClientRuntimeException(e);
        }
    }

    protected Object[] getParam() {
        Object[] params = null;
        final Map<String, Serializable> props = getProperties();
        final Object queryParameterProp = props.get(QUERY_PARAMETER_PROPERTY);
        if (queryParameterProp != null) {
            if (queryParameterProp instanceof Collection) {
                params = ((Collection<?>) queryParameterProp).toArray();
            } else if (queryParameterProp instanceof Object[]) {
                params = (Object[]) queryParameterProp;
            } else {
                throw new ClientRuntimeException("La propriété \"parameters\" doit être un tableau d'objets");
            }
        }
        return params;
    }

    protected String getEtatMessage(final Message message, final CoreSession coreSession) throws ClientException {

        if (message.isTypeEmetteur()) {
            if (message.isEtatNonTraite()) {
                return MessageDTO.EN_COURS_REDACTION;
            } else if (message.isEtatTraite()) {
                if (!message.isArNecessaire()) {
                    return MessageDTO.EMIS;
                } else {
                    if (message.getArNonDonneCount() > 0) {
                        return MessageDTO.EN_ATTENTE_AR;
                    } else {

                        final VersionService versionService = SolonEppServiceLocator.getVersionService();
                        final DocumentModel versionDoc = versionService.getLastVersion(coreSession, message.getIdEvenement());
                        final Version version = versionDoc.getAdapter(Version.class);

                        if (version.isEtatRejete()) {
                            return MessageDTO.AR_RECU_VERSION_REJETE;
                        } else {
                            return MessageDTO.AR_RECU;
                        }
                    }
                }
            } else {
                throw new ClientException("Etat du message inconnu");
            }
        } else if (message.isTypeDestinataire() || message.isTypeCopie()) {
            if (message.isEtatNonTraite()) {
                return MessageDTO.NON_TRAITE;
            } else if (message.isEtatEnCours()) {
                return MessageDTO.EN_COURS_TRAITEMENT;
            } else if (message.isEtatTraite()) {
                return MessageDTO.TRAITE;
            } else {
                throw new ClientException("Etat du message inconnu");
            }
        } else {
            throw new ClientException("Type du message inconnu: " + message.getMessageType());
        }
    }

    protected String getEtatEvenement(final Evenement evenement, final String modeCreationVersion) {
        if (evenement.isEtatAnnule()) {
            return MessageDTO.ANNULER;
        } else if (evenement.isEtatAttenteValidation()
                && !modeCreationVersion.equals(SolonEppSchemaConstant.VERSION_MODE_CREATION_PUBLIE_DEMANDE_ANNULATION_VALUE)) {
            return MessageDTO.EN_ATTENTE_VALIDATION;
        } else if (evenement.isEtatAttenteValidation()
                && modeCreationVersion.equals(SolonEppSchemaConstant.VERSION_MODE_CREATION_PUBLIE_DEMANDE_ANNULATION_VALUE)) {
            return MessageDTO.EN_ATTENTE_VALIDATION_ANNULATION;
        } else if (evenement.isEtatPublie()) {
            return MessageDTO.PUBLIE;
        } else if (evenement.isEtatBrouillon()) {
            return MessageDTO.BROUILLON;
        } else if (evenement.isEtatInstance()) {
            return MessageDTO.EN_INSTANCE;
        }
        return null;
    }
}
