package fr.dila.solonepp.core.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IterableQueryResult;

import fr.dila.solonepp.api.constant.SolonEppConstant;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.dao.criteria.MessageCriteria;
import fr.dila.solonepp.api.domain.message.Message;
import fr.dila.solonepp.api.service.CorbeilleService;
import fr.dila.solonepp.api.service.rechercherMessage.RechercherMessageContext;
import fr.dila.solonepp.api.service.rechercherMessage.RechercherMessageDTO;
import fr.dila.solonepp.api.service.rechercherMessage.RechercherMessageRequest;
import fr.dila.solonepp.core.dao.MessageDao;
import fr.dila.st.api.constant.STQueryConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.dao.pagination.PageInfo;
import fr.dila.st.core.query.FlexibleQueryMaker;
import fr.dila.st.core.query.QueryUtils;

/**
 * Implémentation du service Corbeille de l'application SOLON EPP.
 * 
 * @author jtremeaux
 */
public class CorbeilleServiceImpl implements CorbeilleService {
    /**
     * UID.
     */
    private static final long serialVersionUID = -2392698015083550568L;
    
    private static final String QUERY_CORBEILLE_HAS_MESSAGE = "SELECT /*+ FIRST_ROWS(1) */ a.id as id "
    		+ " FROM (SELECT cslk.id FROM case_link cslk INNER JOIN cslk_corbeillelist corb ON cslk.id = corb.id "
    		+ " WHERE cslk.etatmessage IN ('NON_TRAITE', 'EN_COURS_REDACTION') AND corb.item = ?) a "
    		+ " WHERE ROWNUM = 1";
    
    private static final String VIRG = ", ";
    private static final String SEP = STQueryConstant.PREFIX_SEP;
    private static final String AS_SQL = STQueryConstant.AS;
    /* **************************************************************
     * Constantes des Evenements pour requêtes						*
     ****************************************************************/
    private static final String E_EVT_PREFIX = "e." + SolonEppSchemaConstant.EVENEMENT_SCHEMA_PREFIX + SEP;    
    private static final String EVT_ID_ALIAS = "idEvenement";
    private static final String EVT_ETAT_ALIAS = "EtatEvenement";
    private static final String EVT_EMETTEUR_ALIAS = "emetteur";
    private static final String EVT_DEST_ALIAS = "destinataire";
    private static final String EVT_COPIE_ALIAS = "copie";
    private static final String EVT_TYPE_ALIAS = "typeEvenement";
    
    /* **************************************************************
     * Constantes des Versions pour requêtes						*
     ****************************************************************/
    private static final String V_VER_PREFIX = "v." + SolonEppSchemaConstant.VERSION_SCHEMA_PREFIX + SEP;
    private static final String VER_ID_SENAT_ALIAS = "idSenat";
    private static final String VER_HOROD_ALIAS = "horodatage";
    private static final String VER_NIV_LEC_NUM_ALIAS = "niveauLectureNumero";
    private static final String VER_NIV_LEC_ALIAS = "niveauLecture";
    private static final String VER_OBJET_ALIAS = "objet";
    private static final String VER_PJ_PRESENTE_ALIAS = "pjPresente";
	private static final String VER_RUBRIQUE_ALIAS = "rubrique";
	private static final String VER_COMMENTAIRE_ALIAS = "commentaire";
    
    /* **************************************************************
     * Constantes des Dossiers pour requêtes						*
     ****************************************************************/
    private static final String DOS_ALIAS_PREFIX = "d.";
    private static final String D_DOS_PREFIX = STQueryConstant.D_DOS_PREFIX;
    private static final String DOS_ID_DOSSIER_ALIAS = "idDossier";
    private static final String DOS_DATE_DEP_TXT_ALIAS = "dateDepotTexte";
    private static final String DOS_NUM_DEP_TXT_ALIAS = "numeroDepotTexte";
    private static final String DOS_ALRT_DOS_ALIAS = "alerteDossier";
    
    private static final String QUERY_EVT_INFOS = "SELECT e.ecm:uuid AS uuid, "
    		+ E_EVT_PREFIX + SolonEppSchemaConstant.ID_EVENEMENT + AS_SQL + EVT_ID_ALIAS + VIRG
    		+ "e." + STSchemaConstant.ECM_CURRENT_LIFE_CYCLE_STATE_XPATH + AS_SQL + EVT_ETAT_ALIAS + VIRG
    		+ E_EVT_PREFIX + SolonEppSchemaConstant.EVENEMENT_EMETTEUR_PROPERTY + AS_SQL + EVT_EMETTEUR_ALIAS + VIRG
    		+ E_EVT_PREFIX + SolonEppSchemaConstant.EVENEMENT_DESTINATAIRE_PROPERTY + AS_SQL + EVT_DEST_ALIAS + VIRG
    		+ E_EVT_PREFIX + SolonEppSchemaConstant.EVENEMENT_DESTINATAIRE_COPIE_CONCAT_PROPERTY + AS_SQL + EVT_COPIE_ALIAS + VIRG
    		+ E_EVT_PREFIX + SolonEppSchemaConstant.EVENEMENT_TYPE_EVENEMENT_PROPERTY + AS_SQL + EVT_TYPE_ALIAS
    		+ " FROM Evenement as e ";
    
    private static final String QUERY_VERSION_INFOS =  "SELECT v.ecm:uuid AS uuid, "
    		+ V_VER_PREFIX + SolonEppSchemaConstant.VERSION_SENAT_PROPERTY + AS_SQL + VER_ID_SENAT_ALIAS + VIRG
    		+ V_VER_PREFIX + SolonEppSchemaConstant.VERSION_HORODATAGE_PROPERTY + AS_SQL + VER_HOROD_ALIAS + VIRG
    		+ V_VER_PREFIX + SolonEppSchemaConstant.VERSION_NIVEAU_LECTURE_NUMERO_PROPERTY + AS_SQL + VER_NIV_LEC_NUM_ALIAS + VIRG
    		+ V_VER_PREFIX + SolonEppSchemaConstant.VERSION_NIVEAU_LECTURE_PROPERTY + AS_SQL + VER_NIV_LEC_ALIAS + VIRG
    		+ V_VER_PREFIX + SolonEppSchemaConstant.VERSION_OBJET_PROPERTY + AS_SQL + VER_OBJET_ALIAS + VIRG
			+ V_VER_PREFIX + SolonEppSchemaConstant.VERSION_PIECE_JOINTE_PRESENTE_PROPERTY + AS_SQL + VER_PJ_PRESENTE_ALIAS + VIRG
			+ V_VER_PREFIX + SolonEppSchemaConstant.VERSION_RUBRIQUE + AS_SQL + VER_RUBRIQUE_ALIAS + VIRG
			+ V_VER_PREFIX + SolonEppSchemaConstant.COMMENTAIRE + AS_SQL + VER_COMMENTAIRE_ALIAS
    		+ " FROM Version as v ";
    
    private static final String QUERY_DOSSIER = "SELECT "
    		+ DOS_ALIAS_PREFIX + STSchemaConstant.DUBLINCORE_SCHEMA_PREFIX + SEP 
    		+ STSchemaConstant.DUBLINCORE_TITLE_PROPERTY + AS_SQL + DOS_ID_DOSSIER_ALIAS + VIRG
    		+ D_DOS_PREFIX + SolonEppSchemaConstant.DOSSIER_DATE_DEPOT_TEXTE_PROPERTY + AS_SQL + DOS_DATE_DEP_TXT_ALIAS+ VIRG
    		+ D_DOS_PREFIX + SolonEppSchemaConstant.DOSSIER_NUMERO_DEPOT_TEXTE_PROPERTY + AS_SQL + DOS_NUM_DEP_TXT_ALIAS + VIRG
    		+ D_DOS_PREFIX + SolonEppSchemaConstant.DOSSIER_ALERTE_COUNT_PROPERTY + AS_SQL + DOS_ALRT_DOS_ALIAS
    		+ " FROM Dossier as d ";

    public CorbeilleServiceImpl() {
    	// Default constructor
    }

    @Override
    public void findMessage(CoreSession session, RechercherMessageContext rechercherMessageContext) throws ClientException {
        List<DocumentModel> messageDocList = getMessagesList(session, rechercherMessageContext);        

        if (messageDocList != null) {
        	Map<String, RechercherMessageDTO> mapEvenementIdDto = new HashMap<String, RechercherMessageDTO>();
            Map<String, RechercherMessageDTO> mapVersionIdDto = new HashMap<String, RechercherMessageDTO>();
            Map<String, List<RechercherMessageDTO>> mapDossierIdDto = new HashMap<String, List<RechercherMessageDTO>>();

            List<RechercherMessageDTO> messageList = rechercherMessageContext.getRechercherMessageResponse().getMessagetList();

            for (DocumentModel messageDoc : messageDocList) {
                mapMessageWithDocs(messageDoc, messageList, mapEvenementIdDto, mapVersionIdDto, mapDossierIdDto);
            }

            loadEvenementsForMessages(session, mapEvenementIdDto);
            loadVersionsForMessages(session, mapVersionIdDto);
            loadDossiersForMessages(session, mapDossierIdDto);
        }

    }

    @Override
    public long findMessageCount(CoreSession session, String corbeilleId) throws ClientException {
        MessageCriteria messageCriteria = new MessageCriteria();
        messageCriteria.setCorbeille(corbeilleId);
        messageCriteria.setCheckReadPermission(true);
        MessageDao messageDao = new MessageDao(session, messageCriteria);
        return messageDao.count();
    }

	@Override
	public boolean hasCommunicationNonTraites(CoreSession session, String corbeilleId) throws ClientException {		
		IterableQueryResult res = null;
		try {
			res = QueryUtils.doSqlQuery(session, new String[]{FlexibleQueryMaker.COL_ID}, 
					QUERY_CORBEILLE_HAS_MESSAGE, new Object[]{corbeilleId});
			if (res.iterator().hasNext()) {
				return true;
			}
		} finally {
			if (res != null) {
				res.close();
			}
		}		
		return false;
	}
	
	/**
	 * Récupère les messages en fonction des critères passés dans la requête
	 * @param session
	 * @param rechercherMessageContext
	 * @return
	 * @throws ClientException
	 */
	private List<DocumentModel> getMessagesList(CoreSession session, RechercherMessageContext rechercherMessageContext) throws ClientException {
		final RechercherMessageRequest rechercherMessageRequest = rechercherMessageContext.getRechercherMessageRequest();

        MessageCriteria messageCriteria = rechercherMessageRequest.getMessageCriteria();
        String queryString = rechercherMessageRequest.getQueryString();
        String parametrizedQuery = rechercherMessageRequest.getParametrizedQuery();

        List<DocumentModel> messageDocList = null;
        
        if (messageCriteria == null && queryString == null && parametrizedQuery == null) {
        	throw new ClientException("Mode de recherche de message non spécifié");
        }
        
        if (messageCriteria == null) {
        	long pageSize = 0;
            long offset = 0;
            PageInfo pageInfo = rechercherMessageRequest.getPageInfo();
            if (pageInfo != null) {
                pageSize = pageInfo.getPageSize();
                offset = pageInfo.getOffset();
            }            
        	if (queryString == null) {
        		if (parametrizedQuery != null) {
            		messageDocList = QueryUtils.doUFNXQLQueryAndFetchForDocuments(session, SolonEppConstant.MESSAGE_DOC_TYPE, parametrizedQuery,
                            rechercherMessageRequest.getParamList().toArray(), pageSize, offset);
        		}
        	} else {
        		messageDocList = QueryUtils.doUFNXQLQueryAndFetchForDocuments(session, SolonEppConstant.MESSAGE_DOC_TYPE, queryString, new Object[0],
                        pageSize, offset);
        	}
        } else {
            MessageDao messageDao = new MessageDao(session, messageCriteria, rechercherMessageRequest.getPageInfo());
            messageDocList = messageDao.list();
        } 
        
        return messageDocList;
	}
	/**
	 * Renseigne les mapEvenement, mapVersion et mapDossierId avec le message récupéré
	 * @param messageDoc
	 * @param messageList
	 * @param mapEvenementIdDto
	 * @param mapVersionIdDto
	 * @param mapDossierIdDto
	 */
	private void mapMessageWithDocs(DocumentModel messageDoc, List<RechercherMessageDTO> messageList,
			Map<String, RechercherMessageDTO> mapEvenementIdDto , Map<String, RechercherMessageDTO> mapVersionIdDto,
			Map<String, List<RechercherMessageDTO>> mapDossierIdDto) {
		
		RechercherMessageDTO messageEpp = new RechercherMessageDTO();
        messageList.add(messageEpp);

        // Renseigne les données du message
        messageEpp.setMessageDoc(messageDoc);
        Message message = messageDoc.getAdapter(Message.class);
        mapEvenementIdDto.put(message.getCaseDocumentId(), messageEpp);
        mapVersionIdDto.put(message.getActiveVersionId(), messageEpp);

        String dossierId = message.getIdDossier();

        List<RechercherMessageDTO> dtoList = mapDossierIdDto.get(dossierId);
        if (dtoList == null) {
            dtoList = new ArrayList<RechercherMessageDTO>();
        }
        dtoList.add(messageEpp);
        mapDossierIdDto.put(dossierId, dtoList);
	}
	
	/**
	 * charge les evenements pour la recherche sur message
	 * @param session
	 * @param mapEvenementIdDTO
	 * @throws ClientException
	 */
	private void loadEvenementsForMessages(CoreSession session, Map<String, RechercherMessageDTO> mapEvenementIdDTO) throws ClientException {
		// chargement de l'événement
        if (!mapEvenementIdDTO.isEmpty()) {
            StringBuilder queryEvt = new StringBuilder(QUERY_EVT_INFOS)
                .append(STQueryConstant.WHERE)
                .append(QueryUtils.getQuestionMarkQueryWithColumn("e.ecm:uuid", mapEvenementIdDTO.keySet().size()));

            IterableQueryResult res = null;

            try {
                res = QueryUtils.doUFNXQLQuery(session, queryEvt.toString(), mapEvenementIdDTO.keySet().toArray());
                Iterator<Map<String, Serializable>> iteratorEvt = res.iterator();
                while (iteratorEvt.hasNext()) {
                    Map<String, Serializable> row = iteratorEvt.next();

                    String evenementId = (String) row.get("uuid");
                    RechercherMessageDTO rechercherMessageElement = mapEvenementIdDTO.get(evenementId);
                    rechercherMessageElement.setIdEvenement((String) row.get(EVT_ID_ALIAS));
                    rechercherMessageElement.setEmetteur((String) row.get(EVT_EMETTEUR_ALIAS));
                    rechercherMessageElement.setEtatEvenement((String) row.get(EVT_ETAT_ALIAS));
                    rechercherMessageElement.setDestinataire((String) row.get(EVT_DEST_ALIAS));
                    rechercherMessageElement.setDestinataireCopie((String) row.get(EVT_COPIE_ALIAS));
                    rechercherMessageElement.setTypeEvenement((String) row.get(EVT_TYPE_ALIAS));
                }
            } finally {
                if (res != null) {
                    res.close();
                }
            }

        }
	}
	
	/**
	 * charge les versions pour la recherche sur message
	 * @param session
	 * @param mapVersionIdDto
	 * @throws ClientException
	 */
	private void loadVersionsForMessages(CoreSession session, Map<String, RechercherMessageDTO> mapVersionIdDto) throws ClientException {
		// Chargement de la version
        if (!mapVersionIdDto.isEmpty()) {
            
            StringBuilder queryVersion = new StringBuilder(QUERY_VERSION_INFOS)
            .append(STQueryConstant.WHERE)
            .append(QueryUtils.getQuestionMarkQueryWithColumn("v.ecm:uuid", mapVersionIdDto.keySet().size()));

            IterableQueryResult res = QueryUtils.doUFNXQLQuery(session, queryVersion.toString(), mapVersionIdDto.keySet().toArray());
            
            try {
                Iterator<Map<String, Serializable>> iteratorVersion = res.iterator();
                while (iteratorVersion.hasNext()) {
                    Map<String, Serializable> row = iteratorVersion.next();

                    String uuid = (String) row.get("uuid");
                    RechercherMessageDTO rechercherMessageElement = mapVersionIdDto.get(uuid);
                    rechercherMessageElement.setIdSenat((String) row.get(VER_ID_SENAT_ALIAS));
                    rechercherMessageElement.setHorodatage((Calendar) row.get(VER_HOROD_ALIAS));
                    rechercherMessageElement.setObjet((String) row.get(VER_OBJET_ALIAS));
                    rechercherMessageElement.setNiveauLectureNumero((Long) row.get(VER_NIV_LEC_NUM_ALIAS));
                    rechercherMessageElement.setNiveauLecture((String) row.get(VER_NIV_LEC_ALIAS));
                    rechercherMessageElement.setPieceJointePresente((Boolean) row.get(VER_PJ_PRESENTE_ALIAS));
					rechercherMessageElement.setRubrique((String) row.get(VER_RUBRIQUE_ALIAS));
					rechercherMessageElement.setCommentaire((String) row.get(VER_COMMENTAIRE_ALIAS));
                    
                }
            } finally {
                res.close();
            }
            
        }
	}
	
	/**
	 * Charge les dossiers pour la recherche sur message
	 * @param session
	 * @param mapDossierIdDto
	 * @throws ClientException
	 */
	private void loadDossiersForMessages(CoreSession session, Map<String, List<RechercherMessageDTO>> mapDossierIdDto) throws ClientException {
		 // chargement du dossier
        if (!mapDossierIdDto.isEmpty()) {
                          
            StringBuilder queryDossier = new StringBuilder(QUERY_DOSSIER)
            .append(STQueryConstant.WHERE)
            .append(QueryUtils.getQuestionMarkQueryWithColumn(DOS_ALIAS_PREFIX + STSchemaConstant.DUBLINCORE_SCHEMA_PREFIX + SEP + STSchemaConstant.DUBLINCORE_TITLE_PROPERTY, mapDossierIdDto.keySet().size()));

            IterableQueryResult res = QueryUtils.doUFNXQLQuery(session, queryDossier.toString(), mapDossierIdDto.keySet().toArray());
            
            try {
                Iterator<Map<String, Serializable>> iteratorDossier = res.iterator();
                while (iteratorDossier.hasNext()) {
                    Map<String, Serializable> row = iteratorDossier.next();

                    String idDossier = (String) row.get(DOS_ID_DOSSIER_ALIAS);
                    List<RechercherMessageDTO> rechercherMessageList = mapDossierIdDto.get(idDossier);
                    
                    for (RechercherMessageDTO rechercherMessageElement : rechercherMessageList) {
                        rechercherMessageElement.setIdDossier(idDossier);
                        rechercherMessageElement.setDateDepotTexte((Calendar) row.get(DOS_DATE_DEP_TXT_ALIAS));
                        rechercherMessageElement.setNumeroDepotTexte((String) row.get(DOS_NUM_DEP_TXT_ALIAS));
                        rechercherMessageElement.setAlerteDossier((Long) row.get(DOS_ALRT_DOS_ALIAS));
                    }
                }
            } finally {
                res.close();
            }
            
        }
	}
}
