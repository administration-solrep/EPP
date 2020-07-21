package fr.dila.solonepp.core.assembler.ws;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import com.google.common.collect.LinkedHashMultimap;

import fr.dila.solonepp.api.assembler.Assembler;
import fr.dila.solonepp.api.constant.SolonEppConstant;
import fr.dila.solonepp.api.constant.SolonEppMetaConstant;
import fr.dila.solonepp.api.constant.SolonEppVocabularyConstant;
import fr.dila.solonepp.api.descriptor.evenementtype.EvenementTypeDescriptor;
import fr.dila.solonepp.api.descriptor.evenementtype.PieceJointeDescriptor;
import fr.dila.solonepp.api.descriptor.metadonnees.PropertyDescriptor;
import fr.dila.solonepp.api.domain.dossier.Dossier;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.DossierService;
import fr.dila.solonepp.api.service.EvenementService;
import fr.dila.solonepp.api.service.EvenementTypeService;
import fr.dila.solonepp.api.service.MetaDonneesService;
import fr.dila.solonepp.api.service.PieceJointeService;
import fr.dila.solonepp.api.service.SolonEppVocabularyService;
import fr.dila.solonepp.api.service.TableReferenceService;
import fr.dila.solonepp.api.service.VersionActionService;
import fr.dila.solonepp.api.service.VersionService;
import fr.dila.solonepp.core.assembler.factory.AssemblerFactory;
import fr.dila.solonepp.core.exception.EppClientException;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.api.service.VocabularyService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.DateUtil;
import fr.sword.xsd.solon.epp.Action;
import fr.sword.xsd.solon.epp.DeltaMetadonnee;
import fr.sword.xsd.solon.epp.DeltaPieceJointe;
import fr.sword.xsd.solon.epp.EppBaseEvenement;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EppEvtDelta;
import fr.sword.xsd.solon.epp.EtatEvenement;
import fr.sword.xsd.solon.epp.EtatVersion;
import fr.sword.xsd.solon.epp.EvenementType;
import fr.sword.xsd.solon.epp.Institution;
import fr.sword.xsd.solon.epp.ModeCreationVersion;
import fr.sword.xsd.solon.epp.Nature;
import fr.sword.xsd.solon.epp.PieceJointe;

/**
 * Assembleur des données des objets événement métier <-> Web service. Le document du Web Service contient l'aggrégation de plusieurs objets métiers Nuxeo.
 * 
 * @author jtremeaux
 */
public class EvenementAssembler {
    /**
     * Session.
     */
    private final CoreSession session;

    /**
     * Constructeur de EvenementAssembler.
     * 
     * @param session Session
     */
    public EvenementAssembler(final CoreSession session) {
        this.session = session;
    }

    /**
     * Assemble les données de l'événement XSD -> objets métiers Nuxeo.
     * 
     * @param eppEvtContainer Aggrégation des données du dossier / événement / version
     * @param creerVersionRequest Données assemblées
     * @param eppEvt25Bis
     * @throws ClientException
     */
    public void assembleXsdToEvenement(final EppEvtContainer eppEvtContainer,
            final fr.dila.solonepp.api.service.version.CreerVersionRequest creerVersionRequest) throws ClientException {
        final EvenementService evenementService = SolonEppServiceLocator.getEvenementService();
        final DossierService dossierService = SolonEppServiceLocator.getDossierService();

        final EppPrincipal principal = (EppPrincipal) session.getPrincipal();

        if (eppEvtContainer == null) {
            throw new EppClientException("Votre requête ne contient pas de commmunication");
        }

        // Crée le document événement
        final DocumentModel evenementDoc = evenementService.createBareEvenement(session);
        final Evenement evenement = evenementDoc.getAdapter(Evenement.class);

        // Crée le document version
        final DocumentModel versionDoc = session.createDocumentModel(SolonEppConstant.VERSION_DOC_TYPE);
        final Version version = versionDoc.getAdapter(Version.class);

        final List<PieceJointe> pieceJointeList = new ArrayList<PieceJointe>();

        final EvenementType evenementType = eppEvtContainer.getType();

        if (evenementType == null) {
            throw new EppClientException("Veuillez saisir un type de communication");
        }

        final Assembler assembler = AssemblerFactory.getXsdToObjectAssembler(eppEvtContainer, session, evenementType.value(), principal);
        assembler.buildObject(evenement, version);
        assembler.buildPieceJointe(pieceJointeList);
        evenement.setTypeEvenement(assembler.getEvenementType().value());
        final EppBaseEvenement eppBaseEvenement = assembler.getEppBaseEvenement();

        // Crée le document dossier
        DocumentModel dossierDoc = null;
        Dossier dossier = null;
        if (StringUtils.isNotEmpty(eppBaseEvenement.getIdDossier())) {
            dossierDoc = dossierService.createBareDossier(session);
            dossier = dossierDoc.getAdapter(Dossier.class);
            dossier.setTitle(eppBaseEvenement.getIdDossier());
        }

        // Renseigne l'événement
        if (eppBaseEvenement.getDestinataire() != null) {
            evenement.setDestinataire(eppBaseEvenement.getDestinataire().toString());
        }
        final List<String> destinataireCopie = new ArrayList<String>();
        if (eppBaseEvenement.getCopie() != null) {
            for (final Institution institution : eppBaseEvenement.getCopie()){
                if (institution != null) {
                    destinataireCopie.add(institution.toString());
                }
            }
        }
        evenement.setDestinataireCopie(destinataireCopie);
        evenement.setDossier(eppBaseEvenement.getIdDossier());
        if (eppBaseEvenement.getEmetteur() != null) {
            evenement.setEmetteur(eppBaseEvenement.getEmetteur().toString());
        }

        // Si un événement est spécifié : ajout / modification d'une version sur un événement existant
        if (StringUtils.isNotEmpty(eppBaseEvenement.getIdEvenement())) {
            evenement.setTitle(eppBaseEvenement.getIdEvenement());
            version.setEvenement(eppBaseEvenement.getIdEvenement());
        }

        if (StringUtils.isNotEmpty(eppBaseEvenement.getIdEvenementPrecedent())) {
            evenement.setEvenementParent(eppBaseEvenement.getIdEvenementPrecedent());
        }

        version.setObjet(eppBaseEvenement.getObjet());
        version.setDescription(eppBaseEvenement.getCommentaire());
        final fr.sword.xsd.solon.epp.Version versionCourante = eppBaseEvenement.getVersionCourante();
        if (versionCourante != null) {
            version.setMajorVersion(Long.valueOf(versionCourante.getMajeur()));
            version.setMinorVersion(Long.valueOf(versionCourante.getMineur()));
        }

        // Renseigne les pièces jointes
        if (eppBaseEvenement.getAutres() != null) {
            pieceJointeList.addAll(eppBaseEvenement.getAutres());
        }
        for (final PieceJointe pieceJointe : pieceJointeList) {
            final DocumentModel pieceJointeDoc = PieceJointeAssembler.toPieceJointeDoc(session, pieceJointe);
            if (pieceJointeDoc != null) {
                creerVersionRequest.getPieceJointeDocList().add(pieceJointeDoc);
            }
        }

        creerVersionRequest.setDossierDoc(dossierDoc);
        creerVersionRequest.setEvenementDoc(evenementDoc);
        creerVersionRequest.setVersionDoc(versionDoc);
    }

    /**
     * Assemble les données de l'événement XSD -> objets métiers Nuxeo. Reporte les modification du delta sur le document version et les documents pièces jointes.
     * 
     * @param eppEvtDelta Delta des modifications à apporter
     * @param evenementDoc Document événement
     * @param versionDoc Document version à assembler (modifié par effet de bord)
     * @param pieceJointeDocList Liste des documents pièces jointes à assembler (modifiée par effet de bord)
     * @throws ClientException
     */
    public void assembleXsdToEvenementDelta(final EppEvtDelta eppEvtDelta, final DocumentModel evenementDoc, final DocumentModel versionDoc,
            final List<DocumentModel> pieceJointeDocList) throws ClientException {
        final Evenement evenement = evenementDoc.getAdapter(Evenement.class);
        final String evenementType = evenement.getTypeEvenement();

        // Assemble les métadonnées
        final List<DeltaMetadonnee> deltaMetadonneeList = eppEvtDelta.getMetadonnee();
        final MetaDonneesService metaDonneesService = SolonEppServiceLocator.getMetaDonneesService();
        final Map<String, PropertyDescriptor> propertyMap = metaDonneesService.getMapProperty(evenementType);
        if (deltaMetadonneeList != null) {
            for (final DeltaMetadonnee deltaMetadonnee : deltaMetadonneeList) {
                final String metadonneeKey = deltaMetadonnee.getKey();
                PropertyDescriptor propertyDescriptor = null;
                for (final String key : propertyMap.keySet()) {
                    final PropertyDescriptor propertyDescTmp = propertyMap.get(key);
                    if (metadonneeKey.equals(propertyDescTmp.getNameWS())) {
                        propertyDescriptor = propertyDescTmp;
                        break;
                    }
                }

                if (propertyDescriptor == null) {
                    throw new ClientException("La propriété " + metadonneeKey + " n'existe pas pour le type de communication " + evenementType);
                }
                assembleXsdToPropertyDelta(deltaMetadonnee, versionDoc, propertyDescriptor);
            }
        }

        // Assemble les pièces jointes
        final List<DeltaPieceJointe> deltaPieceJointeList = eppEvtDelta.getPieceJointe();
        final EvenementTypeService evenementTypeService = SolonEppServiceLocator.getEvenementTypeService();
        final EvenementTypeDescriptor evenementTypeDescriptor = evenementTypeService.getEvenementType(evenementType);
        final Map<String, PieceJointeDescriptor> pieceJointeDescriptorMap = evenementTypeDescriptor.getPieceJointe();
        if (deltaPieceJointeList != null) {
            // Classe les pièces jointes par type
            final LinkedHashMultimap<String, DocumentModel> pieceJointeDocByTypeMap = LinkedHashMultimap.create();
            for (final DocumentModel pieceJointeDoc : pieceJointeDocList) {
                final fr.dila.solonepp.api.domain.evenement.PieceJointe pieceJointe = pieceJointeDoc
                        .getAdapter(fr.dila.solonepp.api.domain.evenement.PieceJointe.class);
                pieceJointeDocByTypeMap.put(pieceJointe.getTypePieceJointe(), pieceJointeDoc);
            }

            // Ajoute / modifie / réinitialise les pièces jointes
            for (final DeltaPieceJointe deltaPieceJointe : deltaPieceJointeList) {
                final String pieceJointeType = deltaPieceJointe.getKey();
                final PieceJointeDescriptor pieceJointeDescriptor = pieceJointeDescriptorMap.get(pieceJointeType);
                if (pieceJointeDescriptor == null) {
                    throw new ClientException("La pièce jointe " + pieceJointeType + " n'existe pas pour le type de communication " + evenementType);
                }
                assemblePieceJointeDelta(deltaPieceJointe, pieceJointeDocByTypeMap, pieceJointeDescriptor);
            }

            // Renseigne les pièces jointes assemblées
            pieceJointeDocList.clear();
            pieceJointeDocList.addAll(pieceJointeDocByTypeMap.values());
        }
    }

    /**
     * Assemble une propriété en mode delta.
     * 
     * @param deltaMetadonnee Propriété a assembler
     * @param versionDoc Document version (modifié par effet de bord)
     * @param propertyDescriptor Description de la propriété
     * @throws ClientException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected void assembleXsdToPropertyDelta(final DeltaMetadonnee deltaMetadonnee, final DocumentModel versionDoc,
            final PropertyDescriptor propertyDescriptor) throws ClientException {
        final Version version = versionDoc.getAdapter(Version.class);

        if (propertyDescriptor.isRenseignerEpp()) {
            throw new ClientException("La propriété " + propertyDescriptor.getName() + " de la communication" + version.getEvenement()
                    + " est non modifiable car renseignée par l'EPP");
        }

        final String schema = propertyDescriptor.getSchema();
        final String property = propertyDescriptor.getName();
        final List<String> sourceValueList = deltaMetadonnee.getValeur();
        final String propertyType = propertyDescriptor.getType();
        if (propertyDescriptor.isMultiValue()) {
            // Assemble les valeurs multivaluées
            List destValue = (List) versionDoc.getProperty(schema, property);
            if (destValue == null || deltaMetadonnee.isReset() != null && deltaMetadonnee.isReset()) {
                destValue = new ArrayList();
            }
            if (sourceValueList != null) {
                for (final String sourceValueString : sourceValueList) {
                    final Object sourceValue = convertValue(sourceValueString, propertyType);
                    if (sourceValue == null) {

                    } else if (deltaMetadonnee.isDelete() != null && deltaMetadonnee.isDelete()) {
                        destValue.remove(sourceValue);
                    } else {
                        destValue.add(sourceValue);
                    }
                }
            }
            versionDoc.setProperty(schema, property, destValue);
        } else {
            // Assembles les valeurs monovaluées
            String sourceValueString = null;
            if (sourceValueList != null) {
                if (!sourceValueList.isEmpty()) {
                    if (sourceValueList.size() > 1) {
                        throw new ClientException("La propriété " + propertyDescriptor.getName() + " de la communication " + version.getEvenement()
                                + " est monovaluée, veuillez transmettre une seule valeur");
                    }
                    sourceValueString = sourceValueList.iterator().next();
                }
            }
			// EVT45 : conversion de la valeur d'une rubrique en id du vocabulaire des rubriques
			if (property.equals("rubrique")) {
				final SolonEppVocabularyService eppVocabularyService = SolonEppServiceLocator
						.getSolonEppVocabularyService();
				String rubriqueId = eppVocabularyService.getRubriqueIdForLabel(sourceValueString);
				if (rubriqueId != null && !rubriqueId.isEmpty()) {
					sourceValueString = rubriqueId;
				}
			}
            final Object sourceValue = convertValue(sourceValueString, propertyType);
            versionDoc.setProperty(schema, property, sourceValue);
        }
    }

    /**
     * Assemble les données des pièces jointes en mode delta.
     * 
     * @param deltaPieceJointe Données à assembler
     * @param pieceJointeByTypeDocMap Données assemblées modifiées par effet de bord : documents pièce jointes classés par type
     * @param pieceJointeDescriptor Descripteur de la pièce jointe à assembler
     * @throws ClientException
     */
    protected void assemblePieceJointeDelta(final DeltaPieceJointe deltaPieceJointe,
            final LinkedHashMultimap<String, DocumentModel> pieceJointeByTypeDocMap, final PieceJointeDescriptor pieceJointeDescriptor)
            throws ClientException {
        // Classe les pièces jointes existantes par identifiant technique
        final String pieceJointeType = deltaPieceJointe.getKey();
        final Set<DocumentModel> pieceJointeDocSet = pieceJointeByTypeDocMap.get(pieceJointeType);
        final Map<String, DocumentModel> pieceJointeDocMap = new HashMap<String, DocumentModel>();
        if (deltaPieceJointe.isReset() == null || !deltaPieceJointe.isReset()) {
            for (final DocumentModel pieceJointeDoc : pieceJointeDocSet) {
                final fr.dila.solonepp.api.domain.evenement.PieceJointe pieceJointe = pieceJointeDoc
                        .getAdapter(fr.dila.solonepp.api.domain.evenement.PieceJointe.class);
                pieceJointeDocMap.put(pieceJointe.getTitle(), pieceJointeDoc);
            }
        }

        final List<DocumentModel> newPieceJointeList = new ArrayList<DocumentModel>();
        for (final PieceJointe pieceJointe : deltaPieceJointe.getValeur()) {
            final DocumentModel pieceJointeDoc = PieceJointeAssembler.toPieceJointeDoc(session, pieceJointe);
            final fr.dila.solonepp.api.domain.evenement.PieceJointe pieceJointeModel = pieceJointeDoc
                    .getAdapter(fr.dila.solonepp.api.domain.evenement.PieceJointe.class);
            final String pieceJointeTitle = pieceJointeModel.getTitle();
            if (StringUtils.isBlank(pieceJointeTitle)) {
                newPieceJointeList.add(pieceJointeDoc);
            } else {
                pieceJointeDocMap.put(pieceJointeModel.getTitle(), pieceJointeDoc);
            }
        }

        // Renseignes les nouvelles pièces jointes pour ce type
        pieceJointeDocSet.clear();
        newPieceJointeList.addAll(pieceJointeDocMap.values());
        pieceJointeByTypeDocMap.putAll(pieceJointeType, newPieceJointeList);
    }

    /**
     * Convertit une donnée transmise par le webservice (en string) en type Nuxeo.
     * 
     * @param value Valeur à convertir
     * @param propertyType Type de valeur
     * @return Valeur convertie
     * @throws ClientException
     */
    protected Object convertValue(final String value, final String propertyType) throws ClientException {
        if (value == null || StringUtils.isBlank(value)) {
            return null;
        }

        final TableReferenceService tableReferenceService = SolonEppServiceLocator.getTableReferenceService();
        final VocabularyService vocabularyService = STServiceLocator.getVocabularyService();

        if (SolonEppMetaConstant.META_TYPE_STRING.equals(propertyType)) {
            return value;
        } else if (SolonEppMetaConstant.META_TYPE_INTEGER.equals(propertyType)) {
            return Long.valueOf(value);
        } else if (SolonEppMetaConstant.META_TYPE_BOOLEAN.equals(propertyType)) {
            return Boolean.valueOf(value);
        } else if (SolonEppMetaConstant.META_TYPE_DATE.equals(propertyType)) {
            return DateUtil.stringToXMLGregorianCalendar(value).toGregorianCalendar();
        } else if (SolonEppMetaConstant.META_TYPE_ATTRIBUTION_COMMISSION.equals(propertyType)) {
            if (!vocabularyService.hasDirectoryEntry(SolonEppVocabularyConstant.ATTRIBUTION_COMMISSION_VOCABULARY, value)) {
                throw new ClientException("Commission d'attribution inconnue : " + value);
            }
            return value;
        } else if (SolonEppMetaConstant.META_TYPE_MOTIF_IRRECEVABILITE.equals(propertyType)) {
            if (!vocabularyService.hasDirectoryEntry(SolonEppVocabularyConstant.MOTIF_IRRECEVABILITE_VOCABULARY, value)) {
                throw new ClientException("Motif d'irrecevabilité inconnu : " + value);
            }
            return value;
        } else if (SolonEppMetaConstant.META_TYPE_NIVEAU_LECTURE.equals(propertyType)) {
            if (!vocabularyService.hasDirectoryEntry(SolonEppVocabularyConstant.NIVEAU_LECTURE_VOCABULARY, value)) {
                throw new ClientException("Niveau de lecture inconnu : " + value);
            }
            return value;
        } else if (SolonEppMetaConstant.META_TYPE_NATURE_LOI.equals(propertyType)) {
            if (!vocabularyService.hasDirectoryEntry(SolonEppVocabularyConstant.NATURE_LOI_VOCABULARY, value)) {
                throw new ClientException("Nature de loi inconnue : " + value);
            }
            return value;
        } else if (SolonEppMetaConstant.META_TYPE_NATURE_RAPPORT.equals(propertyType)) {
            if (!vocabularyService.hasDirectoryEntry(SolonEppVocabularyConstant.NATURE_RAPPORT_VOCABULARY, value)) {
                throw new ClientException("Nature de rapport inconnu : " + value);
            }
            return value;
        } else if (SolonEppMetaConstant.META_TYPE_TYPE_LOI.equals(propertyType)) {
            if (!vocabularyService.hasDirectoryEntry(SolonEppVocabularyConstant.TYPE_LOI_VOCABULARY, value)) {
                throw new ClientException("Type de loi inconnu : " + value);
            }
            return value;
        } else if (SolonEppMetaConstant.META_TYPE_SENS_AVIS.equals(propertyType)) {
            if (!vocabularyService.hasDirectoryEntry(SolonEppVocabularyConstant.SENS_AVIS_VOCABULARY, value)) {
                throw new ClientException("Sens d'avis inconnu : " + value);
            }
            return value;
        } else if (SolonEppMetaConstant.META_TYPE_SORT_ADOPTION.equals(propertyType)) {
            if (!vocabularyService.hasDirectoryEntry(SolonEppVocabularyConstant.SORT_ADOPTION_VOCABULARY, value)) {
                throw new ClientException("Sort d'adoption inconnu : " + value);
            }
            return value;
        } else if (SolonEppMetaConstant.META_TYPE_ACTEUR.equals(propertyType)) {
            final boolean exist = tableReferenceService.hasActeur(session, value);
            if (!exist) {
                throw new ClientException("Acteur inexistant: " + value);
            }
            return value;
        } else if (SolonEppMetaConstant.META_TYPE_CIRCONSCRIPTION.equals(propertyType)) {
            final Boolean exist = tableReferenceService.hasCirconscription(session, value);
            if (!exist) {
                throw new ClientException("Circonscription inexistante: " + value);
            }
            return value;
        } else if (SolonEppMetaConstant.META_TYPE_GOUVERNEMENT.equals(propertyType)) {
            final boolean exist = tableReferenceService.hasGouvernement(session, value);
            if (!exist) {
                throw new ClientException("Gouvernement inexistant: " + value);
            }
            return value;
        } else if (SolonEppMetaConstant.META_TYPE_IDENTITE.equals(propertyType)) {
            final boolean exist = tableReferenceService.hasIdentite(session, value);
            if (!exist) {
                throw new ClientException("Identité inexistante: " + value);
            }
            return value;
        } else if (SolonEppMetaConstant.META_TYPE_MANDAT.equals(propertyType)) {
            final boolean exist = tableReferenceService.hasMandat(session, value);
            if (!exist) {
                throw new ClientException("Mandat inexistant: " + value);
            }
            return value;
        } else if (SolonEppMetaConstant.META_TYPE_MEMBRE_GROUPE.equals(propertyType)) {
            final boolean exist = tableReferenceService.hasMembreGroupe(session, value);
            if (!exist) {
                throw new ClientException("Membre de groupe inexistant: " + value);
            }
            return value;
        } else if (SolonEppMetaConstant.META_TYPE_MINISTERE.equals(propertyType)) {
            final boolean exist = tableReferenceService.hasMinistere(session, value);
            if (!exist) {
                throw new ClientException("Ministère inexistant: " + value);
            }
            return value;
        } else if (SolonEppMetaConstant.META_TYPE_ORGANISME.equals(propertyType)) {
            final boolean exist = tableReferenceService.hasOrganisme(session, value);
            if (!exist) {
                throw new ClientException("Organisme inexistant: " + value);
            }
            return value;
        } else if (SolonEppMetaConstant.META_TYPE_PERIODE.equals(propertyType)) {
            final boolean exist = tableReferenceService.hasPeriode(session, value);
            if (!exist) {
                throw new ClientException("Période inexistante: " + value);
            }
            return value;
        } else {
            throw new ClientException("Type de métadonnées inconnu :" + propertyType);
        }
    }

    /**
     * Assemble les données d'un événement Objets métiers Nuxeo -> XSD.
     * 
     * @param dossierDoc Document dossier source
     * @param evenementDoc Document événement source
     * @param versionDoc Document version source
     * @param pieceJointeDocList Liste de documents pièces jointes (facultatives)
     * @param messageType Type de message (EMETTEUR, DESTINATAIRE, COPIE)
     * @return Document événement assemblé
     * @throws ClientException
     * @throws IOException
     */
    public EppEvtContainer assembleEvenementToXsd(final DocumentModel dossierDoc, final DocumentModel evenementDoc, final DocumentModel versionDoc,
            final List<DocumentModel> pieceJointeDocList, final String messageType) throws ClientException {
        return assembleEvenementToXsd(dossierDoc, evenementDoc, versionDoc, pieceJointeDocList, messageType, false);
    }

    /**
     * Assemble les données d'un événement Objets métiers Nuxeo -> XSD.
     * 
     * @param dossierDoc Document dossier source
     * @param evenementDoc Document événement source
     * @param versionDoc Document version source
     * @param pieceJointeDocList Liste de documents pièces jointes (facultatives)
     * @param messageType Type de message (EMETTEUR, DESTINATAIRE, COPIE)
     * @param addPjContent boolean pour ajouter ou non le contenu des pièces jointes
     * @return Document événement assemblé
     * @throws ClientException
     */
    public EppEvtContainer assembleEvenementToXsd(final DocumentModel dossierDoc, final DocumentModel evenementDoc, final DocumentModel versionDoc,
            final List<DocumentModel> pieceJointeDocList, final String messageType, final boolean addPjContent) throws ClientException {
        if (dossierDoc == null) {
            throw new ClientException("Impossible de récupérer les données du dossier");
        }
        if (evenementDoc == null) {
            throw new ClientException("Impossible de récupérer les données de l'évènement");
        }
        if (versionDoc == null) {
            throw new ClientException("Impossible de récupérer les données de la version");
        }
        final EppPrincipal principal = (EppPrincipal) session.getPrincipal();
        final Dossier dossier = dossierDoc.getAdapter(Dossier.class);
        final Evenement evenement = evenementDoc.getAdapter(Evenement.class);
        final Version version = versionDoc.getAdapter(Version.class);

        final Assembler assembler = AssemblerFactory.getXsdToObjectAssembler(null, session, evenement.getTypeEvenement(), principal);
        assembler.buildXsd(evenement, version);
        assembler.buildPieceJointeXsd(pieceJointeDocList, addPjContent);

        final EppEvtContainer eppEvtContainerResponse = new EppEvtContainer();
        assembler.setEvtInContainer(eppEvtContainerResponse);

        final EppBaseEvenement eppBaseEvenement = assembler.getEppBaseEvenement();

        // Renseigne les données du dossier
        if(eppBaseEvenement.getIdDossier() == null || eppBaseEvenement.getIdDossier().isEmpty()){
            eppBaseEvenement.setIdDossier(dossier.getTitle());    
        }        

        // Renseigne les données de l'événement
        eppBaseEvenement.setIdEvenement(evenement.getTitle());
        eppBaseEvenement.setIdEvenementPrecedent(evenement.getEvenementParent());
        if (StringUtils.isNotEmpty(evenementDoc.getId())) {

            // lors de initialiserEvenement les versions et les evenements ne sont pas enregistées
            // et ces propriétés ne seront pas settées lors de initialiserEvenement

            if (evenement.isEtatAnnule()) {
                eppBaseEvenement.setEtat(EtatEvenement.ANNULE);
            } else if (evenement.isEtatAttenteValidation()) {
                eppBaseEvenement.setEtat(EtatEvenement.EN_ATTENTE_DE_VALIDATION);
            } else if (evenement.isEtatBrouillon()) {
                eppBaseEvenement.setEtat(EtatEvenement.BROUILLON);
            } else if (evenement.isEtatInstance()) {
                eppBaseEvenement.setEtat(EtatEvenement.EN_INSTANCE);
            } else if (evenement.isEtatPublie()) {
                eppBaseEvenement.setEtat(EtatEvenement.PUBLIE);
            } else {
                throw new ClientException("Etat de la communication inconnu: " + evenementDoc.getCurrentLifeCycleState());
            }
        }
        eppBaseEvenement.setEmetteur(Institution.valueOf(evenement.getEmetteur()));

        if (StringUtils.isNotBlank(evenement.getDestinataire())) {
            // lors de initialiserEvenement cette propriété peut être null
            eppBaseEvenement.setDestinataire(Institution.valueOf(evenement.getDestinataire()));
        }

        final List<String> destinataireCopieReponse = evenement.getDestinataireCopie();
        if (destinataireCopieReponse != null && !destinataireCopieReponse.isEmpty()) {
            for (final String dest : destinataireCopieReponse) {
                eppBaseEvenement.getCopie().add(Institution.valueOf(dest));
            }
        }

        // Renseigne les données de la version
        final Calendar horodatage = version.getHorodatage();
        if (horodatage != null) {
            final XMLGregorianCalendar xmlHorodatage = DateUtil.calendarToXMLGregorianCalendar(horodatage);
            eppBaseEvenement.setHorodatage(xmlHorodatage);
        }
        final Calendar dateAr = version.getDateAr();
        if (dateAr != null) {
            final XMLGregorianCalendar xmlDateAr = DateUtil.calendarToXMLGregorianCalendar(dateAr);
            eppBaseEvenement.setDateAr(xmlDateAr);
        }
        eppBaseEvenement.setObjet(version.getObjet());
        eppBaseEvenement.setCommentaire(version.getDescription());

        // FEV319
        if (StringUtils.isNotBlank(version.getNature())) {
            eppBaseEvenement.getNature().add(Nature.fromValue(version.getNature()));
        }
        if (version.isVersionCourante()) {
            eppBaseEvenement.getNature().add(Nature.VERSION_COURANTE);
        }
        
        // Renseigne les données de la version en cours
        final fr.sword.xsd.solon.epp.Version versionEnCoursWS = assembleVersionToVersionXsd(versionDoc);
        eppBaseEvenement.setVersionCourante(versionEnCoursWS);

        if (StringUtils.isNotEmpty(versionDoc.getId())) {

            // lors de initialiserEvenement les versions et les evenements ne sont pas enregistées
            // et ces propriétés ne seront pas settées lors de initialiserEvenement

            // Renseigne la liste des versions visibles
            final List<fr.sword.xsd.solon.epp.Version> versionList = eppBaseEvenement.getVersionDisponible();
            final VersionService versionService = SolonEppServiceLocator.getVersionService();
            final List<DocumentModel> versionVisibleDocList = versionService.findVersionVisible(session, evenementDoc, messageType);
            for (final DocumentModel versionVisibleDoc : versionVisibleDocList) {
                final fr.sword.xsd.solon.epp.Version versionVisibleWS = assembleVersionToVersionXsd(versionVisibleDoc);
                versionList.add(versionVisibleWS);
            }
        }

        final List<String> metaModifieeList = assembleMetadonneeModifieeList(version, evenement.getTypeEvenement());
        eppBaseEvenement.getMetadonneeModifiee().addAll(metaModifieeList);

        //Pieces jointes Ajouter et supprimer
        PieceJointeService pieceJointeService = SolonEppServiceLocator.getPieceJointeService();
        if(evenement.getTitle()!=null){
          eppBaseEvenement.getPieceJointeAjoutee().addAll(pieceJointeService.getPieceJointeAjouteeListe(session, versionDoc, evenementDoc));
          eppBaseEvenement.getPieceJointeSupprimee().addAll(pieceJointeService.getPieceJointeSupprimeeListe(session, versionDoc, evenementDoc));
        }
        
        // Renseigne les pièces jointes
        if (pieceJointeDocList != null) {
            for (final DocumentModel pieceJointeDoc : pieceJointeDocList) {
                final PieceJointe pieceJointe = PieceJointeAssembler.toPieceJointeXsd(pieceJointeDoc, addPjContent);
                switch (pieceJointe.getType()) {
                case AUTRE:
                    eppBaseEvenement.getAutres().add(pieceJointe);
                    break;
                }
            }
        }
        eppBaseEvenement.setHasContenuPj(addPjContent);

        // Renseigne les evenements suivants
        final EvenementTypeService evenementTypeService = SolonEppServiceLocator.getEvenementTypeService();
        final List<EvenementTypeDescriptor> evtSuccessifList = evenementTypeService.findEvenementTypeSuccessif(session, evenement.getTypeEvenement());
        for (final EvenementTypeDescriptor evtDescriptor : evtSuccessifList) {
            eppBaseEvenement.getEvenementSuivant().add(EvenementType.fromValue(evtDescriptor.getName()));
        }

        if (StringUtils.isNotEmpty(evenementDoc.getId())) {
            // lors de initialiserEvenement les versions et les evenements ne sont pas enregistées
            // et ces propriétés ne seront pas settées lors de initialiserEvenement

            // Renseigne la liste des actions disponibles
            final VersionActionService versionActionService = SolonEppServiceLocator.getVersionActionService();
            final DocumentModel messageDoc = SolonEppServiceLocator.getMessageService().getMessageByEvenementId(session, evenementDoc.getTitle());
            if (messageDoc == null) {
                throw new ClientException("Communication non trouvé: " + evenementDoc.getTitle());
            }
            final fr.dila.solonepp.api.domain.message.Message message = messageDoc.getAdapter(fr.dila.solonepp.api.domain.message.Message.class);

            final List<String> actionPossibleList = versionActionService.findActionPossible(session, evenementDoc, versionDoc, messageType,
                    message.getEtatMessage());
            for (final String actionPossible : actionPossibleList) {
                final Action action = ActionAssembler.assembleActionToXsd(actionPossible);
                eppBaseEvenement.getActionSuivante().add(action);
            }
        }

        return eppEvtContainerResponse;
    }

    /**
     * Retourne la liste des métadonnées modifiée dans la version
     * 
     * @param version
     * @param evenementType
     * @return
     * @throws ClientException
     */
    protected List<String> assembleMetadonneeModifieeList(final Version version, final String evenementType) throws ClientException {

        final List<String> metaListXsd = new ArrayList<String>();
        final List<String> metaVersionList = version.getModifiedMetaList();

        final MetaDonneesService metaDonneesService = SolonEppServiceLocator.getMetaDonneesService();
        final Map<String, PropertyDescriptor> propertyMap = metaDonneesService.getMapProperty(evenementType);
        for (final String meta : metaVersionList) {
            final String[] metaTab = meta.split(":");
            final String name = metaTab[metaTab.length - 1];
            final PropertyDescriptor propertyDescriptor = propertyMap.get(name);
            if (propertyDescriptor != null) {
                if (propertyDescriptor.getNameWS() != null) {
                    metaListXsd.add(propertyDescriptor.getNameWS());
                }
            }
        }
        return metaListXsd;
    }

    /**
     * Assemble les données de la version WS.
     * 
     * @param versionDoc Document version
     * @return Version WS
     * @throws ClientException
     */
    protected fr.sword.xsd.solon.epp.Version assembleVersionToVersionXsd(final DocumentModel versionDoc) throws ClientException {
        final fr.sword.xsd.solon.epp.Version versionXsdResponse = new fr.sword.xsd.solon.epp.Version();
        final Version version = versionDoc.getAdapter(Version.class);
        final Calendar horodatage = version.getHorodatage();
        if (horodatage != null) {
            final XMLGregorianCalendar xmlHorodatage = DateUtil.calendarToXMLGregorianCalendar(horodatage);
            versionXsdResponse.setHorodatage(xmlHorodatage);
        }
        final Calendar dateAr = version.getDateAr();
        if (dateAr != null) {
            final XMLGregorianCalendar xmlDateAr = DateUtil.calendarToXMLGregorianCalendar(dateAr);
            versionXsdResponse.setDateAr(xmlDateAr);
        }

        final String modeCreation = version.getModeCreation();
        if (StringUtils.isNotEmpty(modeCreation)) {
            versionXsdResponse.setModeCreation(ModeCreationVersion.fromValue(modeCreation));
        }

        if (StringUtils.isNotEmpty(versionDoc.getId())) {
            // lors de initialiserEvenement les versions et les evenements ne sont pas enregistées
            // et ces propriétés ne seront pas settées lors de initialiserEvenement

            versionXsdResponse.setMajeur(version.getMajorVersion().intValue());
            versionXsdResponse.setMineur(version.getMinorVersion().intValue());

            final EtatVersion etatVersion = VersionEtatAssembler.assembleEtatVersionToXsd(versionDoc.getCurrentLifeCycleState());
            versionXsdResponse.setEtat(etatVersion);
        }

        return versionXsdResponse;
    }

}
