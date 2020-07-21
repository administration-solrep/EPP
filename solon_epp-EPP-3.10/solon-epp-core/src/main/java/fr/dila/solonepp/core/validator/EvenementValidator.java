package fr.dila.solonepp.core.validator;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.constant.SolonEppVocabularyConstant;
import fr.dila.solonepp.api.descriptor.evenementtype.EvenementTypeDescriptor;
import fr.dila.solonepp.api.descriptor.metadonnees.EvenementMetaDonneesDescriptor;
import fr.dila.solonepp.api.descriptor.metadonnees.MetaDonneesDescriptor;
import fr.dila.solonepp.api.descriptor.metadonnees.PropertyDescriptor;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.institution.InstitutionsEnum;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.EvenementService;
import fr.dila.solonepp.api.service.EvenementTypeService;
import fr.dila.solonepp.api.service.MetaDonneesService;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.api.constant.STSchemaConstant;

/**
 * Validateur des événements.
 * 
 * @author jtremeaux
 */
public class EvenementValidator {
    /**
     * Session.
     */
    private CoreSession session;
    
    /**
     * Constructeur de EvenementValidator.
     * 
     * @param session Session
     */
    public EvenementValidator(CoreSession session) {
        this.session = session;
    }
    
    /**
     * Vérifie que les données sont correctement renseignées pour un événement successif.
     * 
     * @param dossierDoc Document dossier
     * @param evenementDoc Document événement
     * @param versionDoc Document version
     * @throws ClientException
     */
    public void validateEvenementSuccessif(DocumentModel dossierDoc, DocumentModel evenementDoc, DocumentModel versionDoc) throws ClientException {
        Evenement evenement = evenementDoc.getAdapter(Evenement.class);
        
        // Vérifie si l'événement est successif
        String typeEvenement = evenement.getTypeEvenement();
        final EvenementTypeService evenementTypeService = SolonEppServiceLocator.getEvenementTypeService();
        if (!evenementTypeService.isTypeSuccessif(typeEvenement)) {
            throw new ClientException("Dossier existant : la communication doit être de type successif");
        }
        
        // Vérifie si l'événement parent existe
        String evenementParentId = evenement.getEvenementParent();
        final EvenementService evenementService = SolonEppServiceLocator.getEvenementService();
        DocumentModel evenementParentDoc = evenementService.getEvenement(session, evenementParentId);
        if (evenementParentDoc == null) {
            throw new ClientException("Communication parent non trouvé: " + evenementParentId);
        }
        
        // Vérifie si l'événement parent appartient au dossier
        Evenement evenementParent = evenementParentDoc.getAdapter(Evenement.class);
        if (!evenementParent.getDossier().equals(dossierDoc.getTitle())) {
            throw new ClientException("La communication parent doit appartenir au dossier: " + evenementParent.getDossier());
        }
        
        // Validations spécifiques aux types d'événements
        validateEvenementStandard(evenementDoc, evenementParentDoc);
        validateEvenementAlerte(evenementDoc, versionDoc);
    }
    
    /**
     * Validations spécifiques pour tous les types d'événements successifs sauf alerte.
     * 
     * @param evenementDoc Document événement à valider
     * @param evenementParentDoc Document événement parent
     * @throws ClientException
     */
    public void validateEvenementStandard(DocumentModel evenementDoc, DocumentModel evenementParentDoc) throws ClientException {
        Evenement evenement = evenementDoc.getAdapter(Evenement.class);
        final EvenementTypeService evenementTypeService = SolonEppServiceLocator.getEvenementTypeService();
        // Si l'événement est de type alerte ou générique : pas de vérifications à effectuer
        final String evenementType = evenement.getTypeEvenement();
        if (evenementTypeService.isEvenementTypeAlerte(evenementType) || evenementTypeService.isEvenementTypeGenerique(evenementType)) {
            return;
        }

        final EvenementService evenementService = SolonEppServiceLocator.getEvenementService();
        do {
            Evenement evenementParent = evenementParentDoc.getAdapter(Evenement.class);
            EvenementTypeDescriptor evtParentTypeDescriptor = evenementTypeService.getEvenementType(evenementParent.getTypeEvenement());
            EvenementTypeDescriptor evtTypeDescriptor = evenementTypeService.getEvenementType(evenement.getTypeEvenement());
            if (evenementTypeService.isEvenementTypeAlerte(evenementParent.getTypeEvenement())) {
                throw new ClientException("Une communication de type " + evenementType + " ne peut pas succéder à une communication de type Alerte");
            }
            //Si la procédure du parent n'est pas égale à celle de l'évenement à valider
            //ou si la procédure ne fait pas partie des procédures transverses 
            if (!evtParentTypeDescriptor.getProcedure().equals(evtTypeDescriptor.getProcedure()) && !evtTypeDescriptor.getProcedure().equals(SolonEppVocabularyConstant.CATEGORIE_EVENEMENT_DIVERS_VALUE)) {
                throw new ClientException("L'évènement à valider n'a pas une procédure valide (procédure de la communication précédente ou générique)");
            }
            String evenementParentId = evenementParent.getEvenementParent();
            if (StringUtils.isNotBlank(evenementParentId)) {
                evenementParentDoc = evenementService.getEvenement(session, evenementParentId);
            } else {
                evenementParentDoc = null;
            }
        } while (evenementParentDoc != null);
    }
    
    /**
     * Validations spécifiques pour les événements alerte.
     * 
     * @param evenementDoc Document événement à valider
     * @param versionDoc Document version à valider
     * @throws ClientException
     */
    public void validateEvenementAlerte(DocumentModel evenementDoc, DocumentModel versionDoc) throws ClientException {
        Evenement evenement = evenementDoc.getAdapter(Evenement.class);
        final EvenementTypeService evenementTypeService = SolonEppServiceLocator.getEvenementTypeService();
        // Si l'événement n'est pas de type alerte : pas de vérifications à effectuer
        if(!evenementTypeService.isEvenementTypeAlerte(evenement.getTypeEvenement())) {
            return;
        }
        
        final EvenementService evenementService = SolonEppServiceLocator.getEvenementService();
        String evenementParentId = evenement.getEvenementParent();
        DocumentModel evenementParentDoc = evenementService.getEvenement(session, evenementParentId);
        Evenement evenementParent = evenementParentDoc.getAdapter(Evenement.class);
        Version version = versionDoc.getAdapter(Version.class);
        
        String brancheAlerte = evenementParent.getBrancheAlerte();
        if (version.isPositionAlerte()) {
            // Interdit de poser un alerte(+) après un autre alerte(+) ou alerte(-)
            if (StringUtils.isNotEmpty(brancheAlerte)) {
                throw new ClientException("Une communication d'alerte (pose) ne peut pas être créé après une autre communication alerte");
            }
        } else {
            // Interdit de poser un alerte(-) sans un alerte(+) au dessus
            if (StringUtils.isEmpty(brancheAlerte)) {
                throw new ClientException("Une communication alerte (levée) doit succéder à une communication alerte (pose)");
            } else if (!SolonEppSchemaConstant.EVENEMENT_BRANCHE_ALERTE_POSEE_VALUE.equals(brancheAlerte)) {
                throw new ClientException("Une communication alerte (levée) ne peut pas être créé après une alerte déjà levée");
            }
        }
    }

    /**
     * Vérifie que les données de distribution de l'événement (émetteur, destinataire, copie) sont valides
     * pour sauvegarder l'événement avant ou lors de sa première publication.
     * 
     * @param evenementDoc Document evénément
     * @throws ClientException
     */
    public void validateDistribution(DocumentModel evenementDoc, boolean publie) throws ClientException {
        Evenement evenement = evenementDoc.getAdapter(Evenement.class);
        
        // Vérifie l'émetteur
        final String emetteur = evenement.getEmetteur();
        if (StringUtils.isBlank(emetteur)) {
            throw new ClientException("Émetteur est obligatoire");
        }
        
        final EvenementTypeService evenementTypeService = SolonEppServiceLocator.getEvenementTypeService();
        String evenementType = evenement.getTypeEvenement();
        if (!evenementTypeService.isEmetteurAutorise(evenementType, emetteur)) {
            throw new ClientException("Vous n'êtes pas autorisé à émettre le type de communication " + evenementType);
        }

        if (publie) {
            // Vérifie le destinataire
            final String destinataire = evenement.getDestinataire();
            if (StringUtils.isBlank(destinataire)) {
                throw new ClientException("Destinataire est obligatoire");
            }
            
            if (destinataire.equals(emetteur)) {
                throw new ClientException("Le destinataire " + destinataire + " doit être différent de l'émetteur " + emetteur);
            }
    
            if (!evenementTypeService.isDestinataireAutorise(evenementType, destinataire)) {
                throw new ClientException("Le destinataire " + destinataire + " n'est pas autorisé pour le type de communication " + evenementType);
            }
        
            // Vérifie les destinataires en copie
            final List<String> destinataireCopieList = evenement.getDestinataireCopie();
            if (destinataireCopieList == null || destinataireCopieList.isEmpty()) {
                if (evenementTypeService.isDestinataireCopieObligatoire(evenementType)) {
                    throw new ClientException("Le destinataire en copie est obligatoire pour le type de communication " + evenementType);
                }
            } else {
                for (String destinataireCopie : destinataireCopieList) {
                    if (destinataireCopie.equals(emetteur)) {
                        throw new ClientException("Le destinataire en copie " + destinataireCopie + " doit être différent de l'émetteur " + emetteur);
                    }
    
                    if (destinataireCopie.equals(destinataire)) {
                        throw new ClientException("Le destinataire en copie " + destinataireCopie + " doit être différent du destinataire " + destinataire);
                    }
    
                    if (!evenementTypeService.isDestinataireCopieAutorise(evenementType, destinataireCopie)) {
                        throw new ClientException("Le destinataire en copie " + destinataireCopie + " n'est pas autorisé pour le type de communication " + evenementType);
                    }
                }
            }
        }
    }
    
    /**
     * Vérifie que l'utilisateur connecté fait partie de l'institution émettrice de l'événement.
     * 
     * @param evenementDoc Document événement à valider
     */
    public void validateInstitutionEmettrice(DocumentModel evenementDoc) throws ClientException {
        Evenement evenement = evenementDoc.getAdapter(Evenement.class);
        EppPrincipal principal = (EppPrincipal) session.getPrincipal();
        if (!principal.getInstitutionIdSet().contains(evenement.getEmetteur()) && !InstitutionsEnum.isInstitutionAlwaysAccessible(evenement.getEmetteur())) {
            throw new ClientException("Seule l'institution émettrice peut modifier cette communication");
        }
    }

    /**
     * Vérifie que l'utilisateur connecté fait partie de l'institution destinataire de l'événement.
     * 
     * @param evenementDoc Document événement à valider
     */
    public void validateInstitutionDestinataire(DocumentModel evenementDoc) throws ClientException {
        Evenement evenement = evenementDoc.getAdapter(Evenement.class);
        EppPrincipal principal = (EppPrincipal) session.getPrincipal();
        if (!principal.getInstitutionIdSet().contains(evenement.getDestinataire())) {
            throw new ClientException("Seule l'institution destinataire peut modifier cette communication");
        }
    }

    /**
     * Vérifie que l'événement est dans un des états spécifiés. 
     * 
     * @param evenementDoc Document événement à valider
     * @param etatSet Ensemble des états autorisés
     */
    public void validateEtatEvenement(DocumentModel evenementDoc, Set<String> etatSet) throws ClientException {
        if (!etatSet.contains(evenementDoc.getCurrentLifeCycleState())) {
            throw new ClientException("La communication doit être à l'état " + StringUtils.join(etatSet, " ou "));
        }
    }

    /**
     * Vérifie si le type d'événement peut être créé à l'état brouillon (pour initialisation).
     * 
     * @param evenementDoc Document événement à valider
     * @throws ClientException
     */
    public void validateCreerBrouillon(DocumentModel evenementDoc) throws ClientException {
        final EvenementTypeService evenementTypeService = SolonEppServiceLocator.getEvenementTypeService();
        Evenement evenement = evenementDoc.getAdapter(Evenement.class);
        String evenementType = evenement.getTypeEvenement();
        if (!evenementTypeService.isCreerBrouillon(evenementType)) {
            throw new ClientException("Vous n'êtes pas autorisé à créer une version brouillon pour le type de communication " + evenementType);
        }
    }

    /**
     * Vérifie si le type d'événement peut faire l'objet d'une complétion.
     * 
     * @param evenementDoc Document événement à valider
     * @throws ClientException
     */
    public void validateCompleter(DocumentModel evenementDoc) throws ClientException {
        final EvenementTypeService evenementTypeService = SolonEppServiceLocator.getEvenementTypeService();
        Evenement evenement = evenementDoc.getAdapter(Evenement.class);
        String evenementType = evenement.getTypeEvenement();
        if (!evenementTypeService.isCompleter(evenementType)) {
            throw new ClientException("Vous n'êtes pas autorisé à compléter pour le type de communication " + evenementType);
        }
    }

    /**
     * Vérifie si le type d'événement peut faire l'objet d'une rectification.
     * 
     * @param evenementDoc Document événement à valider
     * @throws ClientException
     */
    public void validateRectifier(DocumentModel evenementDoc) throws ClientException {
        final EvenementTypeService evenementTypeService = SolonEppServiceLocator.getEvenementTypeService();
        Evenement evenement = evenementDoc.getAdapter(Evenement.class);
        String evenementType = evenement.getTypeEvenement();
        if (!evenementTypeService.isRectifier(evenementType)) {
            throw new ClientException("Vous n'êtes pas autorisé à rectifier le type de communication " + evenementType);
        }
    }

    /**
     * Vérifie si le type d'événement peut faire l'objet d'une annulation.
     * 
     * @param evenementDoc Document événement à valider
     * @throws ClientException
     */
    public void validateAnnuler(DocumentModel evenementDoc) throws ClientException {
        final EvenementTypeService evenementTypeService = SolonEppServiceLocator.getEvenementTypeService();
        Evenement evenement = evenementDoc.getAdapter(Evenement.class);
        String evenementType = evenement.getTypeEvenement();
        if (!evenementTypeService.isAnnuler(evenementType)) {
            throw new ClientException("Vous n'êtes pas autorisé à annuler le type de communication " + evenementType);
        }
    }
    
    
    /**
     * Valide les métadonnées obligatoires de la evenement.
     * 
     * @param evenementDoc Document evenement à valider
     * @throws ClientException
     */
    @SuppressWarnings("rawtypes")
    public void validateMetaObligatoire(DocumentModel evenementDoc, boolean publie) throws ClientException {
        Evenement evenement = evenementDoc.getAdapter(Evenement.class);
        
        // Assemble les données de la version
        final MetaDonneesService metaDonneesService = SolonEppServiceLocator.getMetaDonneesService();
        MetaDonneesDescriptor metaDonneesDescriptor = metaDonneesService.getEvenementType(evenement.getTypeEvenement());
        EvenementMetaDonneesDescriptor evtMetaDonneesDescriptor = metaDonneesDescriptor.getEvenement();
        for (PropertyDescriptor propertyDescriptor : evtMetaDonneesDescriptor.getProperty().values()) {
            // Les attributs renseignés par l'EPP ne sont jamais validés
            if (propertyDescriptor.isRenseignerEpp()) {
                continue;
            }
            
            final String property = propertyDescriptor.getName();
            if (!publie && (SolonEppSchemaConstant.EVENEMENT_DESTINATAIRE_PROPERTY.equals(property)
                    || SolonEppSchemaConstant.EVENEMENT_DESTINATAIRE_COPIE_PROPERTY.equals(property))) {
                continue;
            }
            // Vérifie les attributs obligatoires
            if (propertyDescriptor.isObligatoire()) {
                // Détermine le schéma et la propriété
                
                String schema = SolonEppSchemaConstant.EVENEMENT_SCHEMA;
                if (STSchemaConstant.DUBLINCORE_DESCRIPTION_PROPERTY.equals(property)) {
                    schema = STSchemaConstant.DUBLINCORE_SCHEMA;
                }
                
                Object value = evenementDoc.getProperty(schema, property);
                if (value == null) {
                    throw new ClientException("La propriété " + schema + ":" + property + " est obligatoire");
                } else if ( value instanceof String && "".equals(value)) {
                    throw new ClientException("La propriété " + schema + ":" + property + " est obligatoire");
                }
                if (propertyDescriptor.isMultiValue()) {
                    if (!(value instanceof Collection)) {
                        throw new ClientException("La propriété " + schema + ":" + property + " des données entrantes doit contenir une collection");
                    }
                    if (((Collection) value).isEmpty()) {
                        throw new ClientException("La collection " + schema + ":" + property + " doit contenir des éléments");
                    }
                }
            }
        }
    }
}
