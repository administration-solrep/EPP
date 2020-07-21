package fr.dila.solonepp.core.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.core.api.model.PropertyNotFoundException;

import fr.dila.solonepp.api.constant.SolonEppConstant;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.descriptor.metadonnees.EvenementMetaDonneesDescriptor;
import fr.dila.solonepp.api.descriptor.metadonnees.MetaDonneesDescriptor;
import fr.dila.solonepp.api.descriptor.metadonnees.PropertyDescriptor;
import fr.dila.solonepp.api.descriptor.metadonnees.VersionMetaDonneesDescriptor;
import fr.dila.solonepp.api.domain.dossier.Dossier;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.service.DossierService;
import fr.dila.solonepp.api.service.EvenementService;
import fr.dila.solonepp.api.service.MetaDonneesService;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.query.QueryUtils;
import fr.dila.st.core.util.StringUtil;
import fr.dila.st.core.util.UnrestrictedCreateDocumentRunner;

/**
 * Implémentation du service permettant de gérer le cycle de vie du dossier SOLON EPP.
 * 
 * @author jtremeaux
 */
public class DossierServiceImpl implements DossierService {

    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Logger surcouche socle de log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(DossierServiceImpl.class);

    /**
     * Document racine des dossiers.
     */
    private static DocumentModel dossierRootDoc;

    private static final String GET_DOSSIER_QUERY = "SELECT d.ecm:uuid as id FROM " + SolonEppConstant.DOSSIER_DOC_TYPE + " AS d "
            + " WHERE d.dos:idDossier = ?";

    @Override
    public void deleteDossier(final CoreSession session, final DocumentModel dossierDoc) throws ClientException {
        LOGGER.info(session, STLogEnumImpl.DEL_DOSSIER_TEC, "Suppression du dossier: " + dossierDoc.getTitle() + ", UUID: " + dossierDoc.getId());
        session.removeDocument(dossierDoc.getRef());
    }

    @Override
    public DocumentModel getDossier(final CoreSession session, final String dossierId) throws ClientException {
        final Object[] param = new Object[] { dossierId };
        final List<DocumentModel> list = QueryUtils.doUFNXQLQueryAndFetchForDocuments(session, SolonEppConstant.DOSSIER_DOC_TYPE, GET_DOSSIER_QUERY,
                param, 1, 0);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.iterator().next();
    }

    @Override
    public List<DocumentModel> getDossierList(final CoreSession session, final Collection<String> ids) throws ClientException {
        if (ids == null || ids.isEmpty()) {
            return new DocumentModelListImpl();
        }

        final StringBuilder query = new StringBuilder("SELECT d.ecm:uuid as id FROM ");
        query.append(SolonEppConstant.DOSSIER_DOC_TYPE);
        query.append(" as d WHERE d.dos:idDossier IN (");
        query.append(StringUtil.getQuestionMark(ids.size()));
        query.append(") ");

        return QueryUtils.doUFNXQLQueryAndFetchForDocuments(session, SolonEppConstant.DOSSIER_DOC_TYPE, query.toString(), ids.toArray());
    }

    @Override
    public DocumentModel getDossierRoot(final CoreSession session) throws ClientException {
        if (dossierRootDoc != null) {
            return dossierRootDoc;
        }
        synchronized (this) {
            final StringBuilder sb = new StringBuilder("SELECT d.ecm:uuid as id FROM ").append(SolonEppConstant.DOSSIER_ROOT_DOC_TYPE)
                    .append(" AS d");
            final String[] params = new String[] {};
            final List<DocumentModel> list = QueryUtils.doUnrestrictedUFNXQLQueryAndFetchForDocuments(session,
                    SolonEppConstant.DOSSIER_ROOT_DOC_TYPE, sb.toString(), params);
            if (list == null || list.size() <= 0) {
                throw new ClientException("Racine des dossiers non trouvée");
            } else if (list.size() > 1) {
                throw new ClientException("Plusieurs racines des dossiers trouvées");
            }

            dossierRootDoc = list.get(0);
            return dossierRootDoc;
        }
    }

    @Override
    public DocumentModel createDossier(final CoreSession session, final DocumentModel dossierDoc) throws ClientException {

        LOGGER.info(session, STLogEnumImpl.CREATE_DOSSIER_TEC, "Création du dossier " + dossierDoc.getTitle());
        final Dossier dossier = dossierDoc.getAdapter(Dossier.class);

        // Valide les données
        final String dossierTitle = dossierDoc.getTitle();
        if (StringUtils.isEmpty(dossierTitle)) {
            throw new ClientException("L'identifiant du dossier est obligatoire");
        }

        // Vérifie l'unicité de l'identifiant de dossier
        final DocumentModel existingDossierDoc = getDossier(session, dossierTitle);
        if (existingDossierDoc != null) {
            return existingDossierDoc;
        }

        // Renseigne les données initiales du dossier
        dossier.setAlerteCount(0);

        // Renseigne le chemin et le nom du document
        dossierDoc.setPathInfo(getDossierRoot(session).getPathAsString(), dossierDoc.getTitle());

        // Crée le document Dossier
        return new UnrestrictedCreateDocumentRunner(session).saveDocument(dossierDoc);
    }

    @Override
    public DocumentModel createBareDossier(final CoreSession session) throws ClientException {
        final DocumentModel dossierDoc = session.createDocumentModel(SolonEppConstant.DOSSIER_DOC_TYPE);

        // Dossier dossier = dossierDoc.getAdapter(Dossier.class);

        return dossierDoc;
    }

    @Override
    public void updateFicheDossier(final CoreSession session, final DocumentModel versionDoc) throws ClientException {

        final Version version = versionDoc.getAdapter(Version.class);

        final String evenementId = version.getEvenement();
        final EvenementService evenementService = SolonEppServiceLocator.getEvenementService();
        final DocumentModel evenementDoc = evenementService.getEvenement(session, evenementId);
        final Evenement evenement = evenementDoc.getAdapter(Evenement.class);
        final String dossierId = evenement.getDossier();

        LOGGER.debug(session, STLogEnumImpl.UPDATE_DOSSIER_TEC, "Mise à jour de la fiche dossier : " + dossierId);

        final DocumentModel dossierDoc = getDossier(session, dossierId);

        final MetaDonneesService metadonneesService = SolonEppServiceLocator.getMetaDonneesService();

        final MetaDonneesDescriptor metadonneesDescriptor = metadonneesService.getEvenementType(evenement.getTypeEvenement());

        final EvenementMetaDonneesDescriptor evenementMetaDescriptor = metadonneesDescriptor.getEvenement();
        final VersionMetaDonneesDescriptor versionMetaDescriptor = metadonneesDescriptor.getVersion();

        // Copie des elements de la fiche dossier contenus dans l'événement
        for (final String name : evenementMetaDescriptor.getProperty().keySet()) {

            final PropertyDescriptor propertyDescriptor = evenementMetaDescriptor.getProperty().get(name);

            if (propertyDescriptor != null && propertyDescriptor.isFicheDossier() && !SolonEppSchemaConstant.EVENEMENT_DOSSIER_PROPERTY.equals(name)) {
                try {
                    dossierDoc.setProperty(SolonEppSchemaConstant.DOSSIER_SCHEMA, name,
                            evenementDoc.getProperty(SolonEppSchemaConstant.EVENEMENT_SCHEMA, name));
                } catch (final PropertyNotFoundException pe) {
                    LOGGER.error(session, STLogEnumImpl.FAIL_GET_PROPERTY_TEC, "in Event : " + name);
                }
            }
        }

        // Copie des elements de la fiche dossier contenus dans la version
        for (final String name : versionMetaDescriptor.getProperty().keySet()) {

            final PropertyDescriptor propertyDescriptor = versionMetaDescriptor.getProperty().get(name);

            if (propertyDescriptor != null && propertyDescriptor.isFicheDossier()) {
                final Object value = versionDoc.getProperty(SolonEppSchemaConstant.VERSION_SCHEMA, name);
                if (name.equals(SolonEppSchemaConstant.DOSSIER_URL_DOSSIER_SENAT_PROPERTY)
                        || name.equals(SolonEppSchemaConstant.DOSSIER_URL_DOSSIER_AN_PROPERTY)) {
                    if (!StringUtils.isEmpty((String) value)) {
                        try {
                            dossierDoc.setProperty(SolonEppSchemaConstant.DOSSIER_SCHEMA, name, value);
                        } catch (final PropertyNotFoundException pe) {
                            LOGGER.error(session, STLogEnumImpl.FAIL_GET_PROPERTY_TEC, "in Dossier : " + name);
                        }
                    }
                } else {
                    try {
                        dossierDoc.setProperty(SolonEppSchemaConstant.DOSSIER_SCHEMA, name, value);
                    } catch (final PropertyNotFoundException pe) {
                        LOGGER.error(session, STLogEnumImpl.FAIL_GET_PROPERTY_TEC, "in Dossier : " + name);
                    }
                }
            }
        }
        session.saveDocument(dossierDoc);
    }

    @Override
    public Map<String, List<PropertyDescriptor>> getFicheDossierFields(final CoreSession session, final DocumentModel dossierDoc)
            throws ClientException {
        final Set<String> typeEvenementSet = new HashSet<String>();
        Set<PropertyDescriptor> descriptorSet = new HashSet<PropertyDescriptor>();
        final Map<String, List<PropertyDescriptor>> descriptorList = new HashMap<String, List<PropertyDescriptor>>();

        final EvenementService evtService = SolonEppServiceLocator.getEvenementService();
        final List<DocumentModel> evtRacineDocList = evtService.getEvenementDossierList(session, dossierDoc);

        for (final DocumentModel evtDoc : evtRacineDocList) {
            final Evenement evtRacine = evtDoc.getAdapter(Evenement.class);
            typeEvenementSet.add(evtRacine.getTypeEvenement());
        }

        final MetaDonneesService metaService = SolonEppServiceLocator.getMetaDonneesService();

        for (final String evtType : typeEvenementSet) {

            final MetaDonneesDescriptor metaDecriptor = metaService.getEvenementType(evtType);

            final Map<String, PropertyDescriptor> mapPropertyEvt = metaDecriptor.getEvenement().getProperty();

            for (final String name : mapPropertyEvt.keySet()) {
                final PropertyDescriptor descriptor = mapPropertyEvt.get(name);
                if (descriptor.isFicheDossier()) {
                    descriptorSet.add(descriptor);
                }
            }

            descriptorList.put(SolonEppSchemaConstant.EVENEMENT_SCHEMA, new ArrayList<PropertyDescriptor>(descriptorSet));

            descriptorSet = new HashSet<PropertyDescriptor>();
            final Map<String, PropertyDescriptor> mapPropertyVersion = metaDecriptor.getEvenement().getProperty();

            for (final String name : mapPropertyVersion.keySet()) {
                final PropertyDescriptor descriptor = mapPropertyEvt.get(name);
                if (descriptor.isFicheDossier()) {
                    descriptorSet.add(descriptor);
                }
            }

            descriptorList.put(SolonEppSchemaConstant.VERSION_SCHEMA, new ArrayList<PropertyDescriptor>(descriptorSet));

        }

        return descriptorList;
    }

    @Override
    public Set<String> getEvenementTypeDossierList(final CoreSession session, final DocumentModel dossierDoc) throws ClientException {
        final Set<String> typeEvenementSet = new HashSet<String>();

        final EvenementService evtService = SolonEppServiceLocator.getEvenementService();
        final List<DocumentModel> evtRacineDocList = evtService.getEvenementDossierList(session, dossierDoc);

        for (final DocumentModel evtDoc : evtRacineDocList) {
            final Evenement evtRacine = evtDoc.getAdapter(Evenement.class);
            typeEvenementSet.add(evtRacine.getTypeEvenement());
        }

        return typeEvenementSet;
    }
}
