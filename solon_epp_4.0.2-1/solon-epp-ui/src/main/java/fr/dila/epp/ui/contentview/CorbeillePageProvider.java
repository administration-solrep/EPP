package fr.dila.epp.ui.contentview;

import fr.dila.epp.ui.bean.EppMessageDTO;
import fr.dila.epp.ui.helper.CorbeilleProviderHelper;
import fr.dila.solonepp.api.constant.SolonEppConstant;
import fr.dila.solonepp.api.domain.dossier.Dossier;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.domain.message.Message;
import fr.dila.solonepp.api.logging.enumerationCodes.EppLogEnumImpl;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.ui.contentview.AbstractDTOPageProvider;
import fr.sword.naiad.nuxeo.ufnxql.core.query.FlexibleQueryMaker;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public class CorbeillePageProvider extends AbstractDTOPageProvider {
    private static final long serialVersionUID = 1L;

    /**
     * Logger surcouche socle de log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(CorbeillePageProvider.class);

    @Override
    protected void fillCurrentPageMapList(CoreSession coreSession) {
        currentItems = new ArrayList<Map<String, Serializable>>();

        resultsCount = QueryUtils.doCountQuery(coreSession, FlexibleQueryMaker.KeyCode.UFXNQL.getKey() + query);
        // recupere la liste des ids triés
        // se baser sur ce tri pour retourner une liste de map triée dans le bon
        // ordre
        if (resultsCount > 0) {
            List<String> ids = QueryUtils.doQueryForIds(
                coreSession,
                FlexibleQueryMaker.KeyCode.UFXNQL.getKey() + query,
                getPageSize(),
                offset
            );
            populateFromMessageIds(coreSession, ids);
        }
    }

    protected void populateFromMessageIds(CoreSession session, List<String> ids) {
        if (!ids.isEmpty()) {
            List<DocumentModel> dml = QueryUtils.retrieveDocuments(
                session,
                SolonEppConstant.MESSAGE_DOC_TYPE,
                ids,
                true
            );

            Map<String, EppMessageDTO> mapVersionIdDTO = new HashMap<>();
            Map<String, EppMessageDTO> mapEvenementIdDTO = new HashMap<>();

            // Traitement des messages et initialisation des DTOs
            for (DocumentModel dm : dml) {
                if (dm != null) {
                    Message message = dm.getAdapter(Message.class);
                    EppMessageDTO messageDto = new EppMessageDTO();
                    CorbeilleProviderHelper.buildDTOFromMessage(messageDto, message, dm, session);

                    mapEvenementIdDTO.put(message.getCaseDocumentId(), messageDto);
                    mapVersionIdDTO.put(message.getActiveVersionId(), messageDto);

                    if (currentItems != null) {
                        currentItems.add(messageDto);
                    }
                }
            }

            // Récupération des Versions en une fois
            dml = QueryUtils.retrieveDocuments(session, SolonEppConstant.VERSION_DOC_TYPE, mapVersionIdDTO.keySet());
            if (dml.isEmpty()) {
                LOGGER.warn(
                    EppLogEnumImpl.FAIL_GET_MESSAGE_TEC,
                    "Aucune version n'a pu être récupérée avec l'ID : " + mapVersionIdDTO.keySet().toString()
                );
                return;
            }

            for (DocumentModel dm : dml) {
                String versionId = dm.getId();
                CorbeilleProviderHelper.buildDTOFromVersion(
                    mapVersionIdDTO.get(versionId),
                    dm.getAdapter(Version.class)
                );
            }

            // Récupération des Evenements en une fois
            dml =
                QueryUtils.retrieveDocuments(session, SolonEppConstant.EVENEMENT_DOC_TYPE, mapEvenementIdDTO.keySet());
            if (dml.isEmpty()) {
                LOGGER.warn(
                    EppLogEnumImpl.FAIL_GET_MESSAGE_TEC,
                    "Aucun événement n'a pu être récupéré avec l'ID : " + mapEvenementIdDTO.keySet().toString()
                );
                return;
            }

            Map<String, List<EppMessageDTO>> mapDossierIdDTO = new HashMap<>();

            for (DocumentModel dm : dml) {
                String evenementId = dm.getId();
                Evenement evenement = dm.getAdapter(Evenement.class);
                EppMessageDTO messageDto = mapEvenementIdDTO.get(evenementId);
                CorbeilleProviderHelper.buildDTOFromEvenement(messageDto, evenement);

                final String dossierId = evenement.getDossier();
                List<EppMessageDTO> dtoList = mapDossierIdDTO.get(dossierId);
                if (dtoList == null) {
                    dtoList = new ArrayList<>();
                }
                dtoList.add(messageDto);
                mapDossierIdDTO.put(dossierId, dtoList);
            }

            // Récupération des Dossiers
            if (!mapDossierIdDTO.isEmpty()) {
                dml = SolonEppServiceLocator.getDossierService().getDossierList(session, mapDossierIdDTO.keySet());

                for (final DocumentModel dm : dml) {
                    final Dossier dossier = dm.getAdapter(Dossier.class);
                    final List<EppMessageDTO> dtoList = mapDossierIdDTO.get(dossier.getTitle());
                    for (final EppMessageDTO messageDto : dtoList) {
                        messageDto.setDossierEnAlerte(dossier.isAlerte());
                    }
                }
            }
        }
    }
}
