package fr.dila.ss.ui.services.impl;

import fr.dila.ss.api.actualite.Actualite;
import fr.dila.ss.api.constant.ActualiteConstant;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.ss.ui.bean.actualites.ActualiteConsultationDTO;
import fr.dila.ss.ui.bean.actualites.ActualiteDTO;
import fr.dila.ss.ui.bean.actualites.ActualiteRechercheForm;
import fr.dila.ss.ui.bean.actualites.ActualitesList;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.services.ActualiteUIService;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.mapper.MapDoc2Bean;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.platform.query.api.PageProvider;

public class ActualiteUIServiceImpl implements ActualiteUIService {

    @Override
    public DocumentModel toDocumentModel(SpecificContext context, ActualiteDTO dto) {
        DocumentModel actualiteDoc = context
            .getSession()
            .createDocumentModel(ActualiteConstant.ACTUALITE_DOCUMENT_TYPE);
        MapDoc2Bean.beanToDoc(dto, actualiteDoc);

        return actualiteDoc;
    }

    @Override
    public ActualiteConsultationDTO toActualiteForm(DocumentModel actualiteDoc) {
        return MapDoc2Bean.docToBean(actualiteDoc, ActualiteConsultationDTO.class);
    }

    @Override
    public List<ActualiteConsultationDTO> fetchUserActualitesNonLues(SpecificContext context) {
        CoreSession session = context.getSession();
        List<DocumentModel> docs = SSServiceLocator.getActualiteService().fetchUserActualitesNonLues(session);

        return docs.stream().map(this::toActualiteForm).collect(Collectors.toList());
    }

    @Override
    public void setActualiteLue(SpecificContext context) {
        String id = context.getFromContextData(STContextDataKey.ID);
        Objects.requireNonNull(id, "Un id depuis le context est requis");

        CoreSession session = context.getSession();

        DocumentModel actualiteDoc = session.getDocument(new IdRef(id));
        Actualite actualite = actualiteDoc.getAdapter(Actualite.class);

        List<String> lecteurs = actualite.getLecteurs();
        String principalName = session.getPrincipal().getName();
        if (!lecteurs.contains(principalName)) {
            lecteurs.add(principalName);
            actualite.setLecteurs(lecteurs);

            CoreInstance.doPrivileged(
                session,
                superSession -> {
                    superSession.saveDocument(actualiteDoc);
                }
            );
        }
    }

    @Override
    public ActualitesList getActualitesList(SpecificContext context) {
        CoreSession session = context.getSession();

        ActualiteRechercheForm form = context.getFromContextData(SSContextDataKey.ACTUALITE_RECHERCHE_FORM);
        Objects.requireNonNull(form, "Un object de type [ActualiteRechercheForm] du context est attendu");
        DocumentModel formDoc = session.createDocumentModel(ActualiteConstant.ACTUALITE_REQUETE_DOCUMENT_TYPE);
        MapDoc2Bean.beanToDoc(form, formDoc);

        PageProvider<DocumentModel> pageProvider = SSServiceLocator
            .getActualiteService()
            .getActualitesPageProvider(session, formDoc, form.getSortInfos(), form.getSize(), form.getPage() - 1L);
        List<DocumentModel> actualitesDocs = pageProvider.getCurrentPage();
        List<ActualiteConsultationDTO> actualites = actualitesDocs
            .stream()
            .map(this::toActualiteForm)
            .collect(Collectors.toList());

        ActualitesList actualitesList = new ActualitesList();
        actualitesList.setListe(actualites);
        actualitesList.setNbTotal(Math.toIntExact(pageProvider.getResultsCount()));
        actualitesList.buildColonnes(form);

        return actualitesList;
    }

    @Override
    public void removeActualites(SpecificContext context) {
        List<String> idActualites = context.getFromContextData(STContextDataKey.IDS);
        context.getSession().removeDocuments(idActualites.stream().map(IdRef::new).toArray(DocumentRef[]::new));
    }
}
