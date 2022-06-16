package fr.dila.solonepp.core.service;

import fr.dila.solonepg.rest.api.WSEpg;
import fr.dila.solonepp.api.constant.SolonEppParametreConstant;
import fr.dila.solonepp.api.constant.SolonEppVocabularyConstant;
import fr.dila.solonepp.api.domain.dossier.Dossier;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.PieceJointe;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.domain.piecejointe.PieceJointeFichier;
import fr.dila.solonepp.api.institution.InstitutionsEnum;
import fr.dila.solonepp.api.logging.enumerationCodes.EppLogEnumImpl;
import fr.dila.solonepp.api.service.InformationsParlementairesService;
import fr.dila.solonepp.api.service.SolonEppVocabularyService;
import fr.dila.solonepp.core.exception.EppNuxeoException;
import fr.dila.solonepp.core.validator.EvenementValidator;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.util.DateUtil;
import fr.dila.st.rest.client.WSProxyFactory;
import fr.dila.st.rest.client.WSProxyFactoryException;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import fr.sword.xsd.commons.TraitementStatut;
import fr.sword.xsd.solon.epg.Acte.Indexation;
import fr.sword.xsd.solon.epg.Acte.Responsable;
import fr.sword.xsd.solon.epg.ActeType;
import fr.sword.xsd.solon.epg.CreerDossierRequest;
import fr.sword.xsd.solon.epg.CreerDossierResponse;
import fr.sword.xsd.solon.epg.DossierEpg;
import fr.sword.xsd.solon.epg.DossierEpgWithFile;
import fr.sword.xsd.solon.epg.Fichier;
import fr.sword.xsd.solon.epg.InformationsParlementaires;
import fr.sword.xsd.solon.epg.ListeFichiers;
import fr.sword.xsd.solon.epg.ModifierDossierRequest;
import fr.sword.xsd.solon.epg.ModifierDossierResponse;
import fr.sword.xsd.solon.epg.PublicationIntOuExtType;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.PathRef;

public class InformationsParlementairesServiceImpl implements InformationsParlementairesService {
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 2676485218781198494L;

    private static final STLogger LOGGER = STLogFactory.getLog(InformationsParlementairesServiceImpl.class);

    private static final SimpleDateFormat SDF_DD_MM_YYYY = new SimpleDateFormat("dd/MM/yyyy");

    private static final String UNICITE_EVT45_QUERY =
        "SELECT v.ecm:uuid AS id FROM Version AS v, Evenement AS e WHERE v.ver:evenement != ? AND v.ver:evenement = e.evt:idEvenement AND v.ver:versionCourante = 1 AND v.ver:rubrique = ? AND v.ver:dateJo = ? AND e.evt:emetteur = ?";

    @Override
    public void callWsCreerDossierEpg(
        CoreSession session,
        Dossier dossier,
        Evenement evenement,
        Version version,
        boolean isPublier
    )
        throws Exception {
        LOGGER.debug(session, STLogEnumImpl.LOG_DEBUG_TEC, "###APPEL CREER DOSSIER####");
        // Valide les données de l'événement
        EvenementValidator evenementValidator = new EvenementValidator(session);
        evenementValidator.validateDistribution(evenement.getDocument(), isPublier);

        checkUniciteCreation(session, version.getRubrique(), version.getDateJo(), evenement.getEmetteur());

        WSEpg webservice = getWsEpg();
        if (webservice != null) {
            CreerDossierRequest request = new CreerDossierRequest();
            // Récupération des paramètres entite/institution
            DocumentModel params = session.getDocument(new PathRef(SolonEppParametreConstant.CREER_DOSSIER_PARAM_PATH));
            String emetteur = evenement.getEmetteur();
            String norDirection = InstitutionsEnum.getNorDirectionFromInstitution(emetteur, params);
            String norEntite = (String) params.getPropertyValue("parws:norEntite");
            request.setCodeEntite(norEntite);
            request.setCodeDirection(norDirection);
            request.setTypeActe(ActeType.INFORMATIONS_PARLEMENTAIRES);

            CreerDossierResponse response = webservice.creerDossier(request);

            if (TraitementStatut.OK.equals(response.getStatut())) {
                // renseignement côté epp du nor
                String nor = response.getDossier().getInformationsParlementaires().getNor();
                evenement.setDossier(nor);
                dossier.setTitle(nor);
                version.setNor(nor);
            } else {
                // statut ko on remonte l'erreur dans les logs
                LOGGER.error(
                    session,
                    EppLogEnumImpl.FAIL_GET_WS_EPG_TEC,
                    "Appel ws creer epg KO : " + response.getMessageErreur()
                );
                throw new EppNuxeoException("Retour KO de l'appel à CreerDossier");
            }
        } else {
            throw new EppNuxeoException("Aucun poste webservice disponible pour l'appel à CreerDossier");
        }
    }

    @Override
    public void callWsModifierDossierEpg(
        CoreSession session,
        Evenement evenement,
        Version version,
        PieceJointe pieceJointe
    )
        throws Exception {
        LOGGER.debug(session, STLogEnumImpl.LOG_DEBUG_TEC, "###APPEL MODIFIER DOSSIER####");

        checkUnicite(
            session,
            version.getRubrique(),
            version.getDateJo(),
            evenement.getEmetteur(),
            evenement.getIdEvenement()
        );

        WSEpg webservice = getWsEpg();
        if (webservice != null) {
            ModifierDossierRequest request = new ModifierDossierRequest();

            DossierEpgWithFile dossierEpgWithFile = new DossierEpgWithFile();
            DossierEpg dossierEpg = new DossierEpg();

            InformationsParlementaires infos = new InformationsParlementaires();
            infos.setNor(evenement.getDossier());
            String rubriqueLabel = SolonEppServiceLocator
                .getSolonEppVocabularyService()
                .getEntryLabel(SolonEppVocabularyConstant.RUBRIQUES_VOCABULARY, version.getRubrique());
            infos.setTitreActe(
                "JO-01 : Insertion d'informations parlementaires au JOLD - " +
                rubriqueLabel +
                " - " +
                SDF_DD_MM_YYYY.format(Calendar.getInstance().getTime())
            );
            infos.setPublicationIntegraleOuExtrait(PublicationIntOuExtType.EXTRAIT);
            infos.setModeParution("1");
            infos.setTypeActe(ActeType.INFORMATIONS_PARLEMENTAIRES);
            infos.setEmetteur(InstitutionsEnum.valueOf(evenement.getEmetteur()).getLabel());

            Responsable resp = new Responsable();
            resp.setNomReponsable(InstitutionsEnum.valueOf(evenement.getEmetteur()).getLabel());
            resp.setTelephoneResponsable("000");
            resp.setQualiteResponsable("Informations parlementaires");
            infos.setResponsable(resp);

            Indexation indexation = new Indexation();
            indexation.getRubriques().add("Informations parlementaires");
            infos.setIndexation(indexation);

            infos.setRubrique(rubriqueLabel);
            infos.setCommentaire(version.getDescription());
            infos.setDatePublicationSouhaitee(DateUtil.calendarToXMLGregorianCalendar(version.getDateJo()));
            infos.setTypeDemande("PUBLICATION_JO");
            infos.setIdEvenement(evenement.getIdEvenement());

            dossierEpg.setInformationsParlementaires(infos);

            Fichier fichierParapheur = new Fichier();
            DocumentModel pieceJointeFichierDoc = pieceJointe.getPieceJointeFichierDocList().get(0);
            PieceJointeFichier pieceJointeFichier = pieceJointeFichierDoc.getAdapter(PieceJointeFichier.class);
            Blob content = pieceJointeFichier.getContent();
            fichierParapheur.setContenu(content.getByteArray());
            fichierParapheur.setMimeType(pieceJointeFichier.getMimeType());
            fichierParapheur.setNom(pieceJointeFichier.getSafeFilename());
            fichierParapheur.setTailleFichier((int) content.getLength());
            fichierParapheur.setCheminFichier("Parapheur/Acte intégral/" + content.getFilename());
            ListeFichiers parapheur = new ListeFichiers();
            parapheur.getFichier().add(fichierParapheur);

            dossierEpgWithFile.setDossierEpg(dossierEpg);
            dossierEpgWithFile.setParapheur(parapheur);

            request.setDossier(dossierEpgWithFile);

            ModifierDossierResponse response = webservice.modifierDossier(request);

            if (TraitementStatut.OK.equals(response.getStatut())) {
                // Nothing to do
            } else {
                // statut ko on remonte l'erreur dans les logs
                LOGGER.error(
                    session,
                    EppLogEnumImpl.FAIL_GET_WS_EPG_TEC,
                    "Appel ws modifier epg KO : " + response.getMessageErreur()
                );
                throw new EppNuxeoException("Retour KO de l'appel à ModifierDossier : " + response.getMessageErreur());
            }
        } else {
            throw new EppNuxeoException("Aucun poste webservice disponible pour l'appel à ModifierDossier");
        }
    }

    private WSEpg getWsEpg() throws WSProxyFactoryException {
        final List<PosteNode> posteNodeList = SolonEppServiceLocator
            .getOrganigrammeService()
            .getPosteFromInstitution(InstitutionsEnum.GOUVERNEMENT.name());
        String url = null;
        String username = null;
        String value = null;
        String keyAlias = null;

        for (final PosteNode posteNode : posteNodeList) {
            url = posteNode.getWsUrl();
            if (
                StringUtils.isNotBlank(url) &&
                !posteNode.getDeleted() &&
                posteNode.isActive() &&
                url.contains("/site/solonepg")
            ) {
                username = posteNode.getWsUser();
                value = posteNode.getWsPassword();
                keyAlias = posteNode.getWsKeyAlias();
                break;
            } else {
                url = null;
            }
        }
        if (url == null) {
            return null;
        }

        WSProxyFactory factory = new WSProxyFactory(url, null, username, keyAlias);
        return factory.getService(WSEpg.class, value);
    }

    /**
     * Vérifie l'unicité de la communication
     * @param session
     * @param rubrique
     * @param dateJo
     * @param emetteur
     * @param idEvenement Identifiant de la communication en cours pour qu'elle soit exclue du contrôle d'unicité
     */
    private void checkUnicite(
        final CoreSession session,
        final String rubrique,
        final Calendar dateJo,
        final String emetteur,
        final String idEvenement
    ) {
        DocumentRef[] existingDoc = QueryUtils.doUFNXQLQueryForIds(
            session,
            UNICITE_EVT45_QUERY,
            new Object[] { idEvenement, rubrique, dateJo, emetteur },
            1,
            0
        );
        if (existingDoc != null && existingDoc.length == 1) {
            SolonEppVocabularyService eppService = SolonEppServiceLocator.getSolonEppVocabularyService();
            String labelEmetteur = InstitutionsEnum.valueOf(emetteur).getLabel();
            String labelRubrique = eppService.getEntryLabel(SolonEppVocabularyConstant.RUBRIQUES_VOCABULARY, rubrique);
            throw new EppNuxeoException(
                String.format(
                    "L'unicité de la communication pour l'émetteur %s, la rubrique %s et la date %s n'est pas validée. Une communication existe déjà avec ces données.",
                    labelEmetteur,
                    labelRubrique,
                    SDF_DD_MM_YYYY.format(dateJo.getTime())
                )
            );
        }
    }

    /**
     * Appelle la vérification d'unicité pour une communication en cours de création qui n'a donc pas d'idEvenement
     * @param session
     * @param rubrique
     * @param dateJo
     * @param emetteur
     */
    private void checkUniciteCreation(
        final CoreSession session,
        final String rubrique,
        final Calendar dateJo,
        final String emetteur
    ) {
        checkUnicite(session, rubrique, dateJo, emetteur, " ");
    }
}
