package fr.dila.epp.ui.services.impl;

import fr.dila.epp.ui.services.HistoriqueDossierUIService;
import fr.dila.solonepp.api.descriptor.evenementtype.EvenementTypeDescriptor;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.domain.message.Message;
import fr.dila.solonepp.api.service.EvenementService;
import fr.dila.solonepp.api.service.EvenementTypeService;
import fr.dila.solonepp.api.service.MessageService;
import fr.dila.solonepp.api.service.VersionService;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.ui.bean.DossierHistoriqueEPP;
import fr.dila.st.ui.bean.MessageVersion;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.nuxeo.ecm.core.api.DocumentModel;

public class HistoriqueDossierUIServiceImpl implements HistoriqueDossierUIService {

    @Override
    public DossierHistoriqueEPP getHistoriqueDossier(SpecificContext context) {
        DossierHistoriqueEPP historiqueDossier = new DossierHistoriqueEPP();

        Map<DocumentModel, DocumentModel> evtsRacinesDoc = getEvenementNodes(context, null);

        if (evtsRacinesDoc == null) {
            return historiqueDossier;
        }
        for (Entry<DocumentModel, DocumentModel> entry : evtsRacinesDoc.entrySet()) {
            buildMessageVersion(context, entry, historiqueDossier, true, null, null);
        }
        return historiqueDossier;
    }

    /**
     * Retourne les évènements enfants
     *
     * @param context
     * @param evtParent
     * @param verParent
     * @param historiqueDossier
     * @return
     */
    private List<MessageVersion> getChildren(
        SpecificContext context,
        Evenement evtParent,
        Version verParent,
        DossierHistoriqueEPP historiqueDossier
    ) {
        List<MessageVersion> children = new ArrayList<>();

        Map<DocumentModel, DocumentModel> evtSuccessifMap = getEvenementNodes(context, evtParent.getTitle());

        for (Entry<DocumentModel, DocumentModel> entry : evtSuccessifMap.entrySet()) {
            buildMessageVersion(context, entry, historiqueDossier, false, verParent, children);
        }
        return children;
    }

    private Map<DocumentModel, DocumentModel> getEvenementNodes(SpecificContext context, String parentNode) {
        final EvenementService evtService = SolonEppServiceLocator.getEvenementService();
        List<DocumentModel> evtSuccessifList = null;
        if (parentNode == null) {
            Evenement evtCourant = context.getCurrentDocument().getAdapter(Evenement.class);
            DocumentModel currentDossier = SolonEppServiceLocator
                .getDossierService()
                .getDossier(context.getSession(), evtCourant.getDossier());
            evtSuccessifList = evtService.getEvenementsRacineDuDossier(context.getSession(), currentDossier);
        } else {
            evtSuccessifList = evtService.findEvenementSuccessif(context.getSession(), parentNode);
        }
        if (evtSuccessifList.isEmpty()) {
            return Collections.emptyMap();
        }
        final MessageService messageService = SolonEppServiceLocator.getMessageService();
        Map<DocumentModel, DocumentModel> evenementMap = new LinkedHashMap<>();
        for (DocumentModel evtSuccessifDoc : evtSuccessifList) {
            DocumentModel messageDoc = messageService.getMessageByEvenementId(
                context.getSession(),
                evtSuccessifDoc.getTitle()
            );
            if (messageDoc == null) {
                // Si le message est null, c'est que l'utilisateur n'est pas autorisé à le voir.
                // Chercher parmis les evenements successif
                evenementMap.putAll(getEvenementNodes(context, evtSuccessifDoc.getTitle()));
            } else {
                evenementMap.put(evtSuccessifDoc, messageDoc);
            }
        }
        return evenementMap;
    }

    /**
     * Retourne le label de l'événement
     *
     * @param evenementType
     * @return
     */
    private String getEvenementTypeLabel(String evenementType) {
        final EvenementTypeService evtTypeService = SolonEppServiceLocator.getEvenementTypeService();
        EvenementTypeDescriptor evtTypeDesc = evtTypeService.getEvenementType(evenementType);
        return evtTypeDesc.getLabel();
    }

    /**
     * Ajoute un élémént à l'historique dans le bon niveau
     *
     * @param context
     * @param evtMsg
     *            Key : evenementDoc | Value : messageDoc
     * @param historiqueDossier
     *            utilisé si ajout à la racine
     * @param isRootNode
     *            vrai si l'événement est à la racine (pas successif)
     * @param versionParent
     *            peut être null si isRootNode est vrai
     * @param children
     *            peut être null si isRootNode est vrai
     */
    private void buildMessageVersion(
        SpecificContext context,
        Entry<DocumentModel, DocumentModel> evtMsg,
        DossierHistoriqueEPP historiqueDossier,
        Boolean isRootNode,
        Version versionParent,
        List<MessageVersion> children
    ) {
        DocumentModel evenementDoc = evtMsg.getKey();
        DocumentModel messageDoc = evtMsg.getValue();

        if (messageDoc != null) {
            final VersionService versionService = SolonEppServiceLocator.getVersionService();
            Message message = messageDoc.getAdapter(Message.class);
            DocumentModel versionDoc = versionService.getVersionActive(
                context.getSession(),
                evenementDoc,
                message.getMessageType()
            );
            Version version = null;
            if (isRootNode) {
                if (versionDoc == null) {
                    return;
                }
                version = versionDoc.getAdapter(Version.class);
            } else if (versionDoc != null && versionParent != null) {
                version = versionDoc.getAdapter(Version.class);

                isRootNode =
                    versionParent.getNiveauLecture() != null &&
                    version.getNiveauLecture() != null &&
                    (
                        !versionParent.getNiveauLecture().equals(version.getNiveauLecture()) ||
                        (
                            versionParent.getNiveauLectureNumero() != null &&
                            version.getNiveauLectureNumero() != null &&
                            !versionParent.getNiveauLectureNumero().equals(version.getNiveauLectureNumero())
                        )
                    );
            }

            Evenement evenement = evenementDoc.getAdapter(Evenement.class);
            MessageVersion messageVersion = new MessageVersion(
                messageDoc.getId(),
                getEvenementTypeLabel(evenement.getTypeEvenement()),
                evenement.getTypeEvenement(),
                evenementDoc.getId().equals(context.getCurrentDocument().getId()),
                evenement.isEtatAnnule()
            );
            if (isRootNode) {
                historiqueDossier.getLstVersions().add(messageVersion);
            } else {
                children.add(messageVersion);
            }
            messageVersion.setLstChilds(getChildren(context, evenement, version, historiqueDossier));
        }
    }
}
