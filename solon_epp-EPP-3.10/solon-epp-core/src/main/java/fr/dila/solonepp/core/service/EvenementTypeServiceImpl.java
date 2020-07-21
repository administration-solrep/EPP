package fr.dila.solonepp.core.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

import fr.dila.solonepp.api.constant.SolonEppVocabularyConstant;
import fr.dila.solonepp.api.descriptor.evenementtype.DistributionDescriptor;
import fr.dila.solonepp.api.descriptor.evenementtype.DistributionElementDescriptor;
import fr.dila.solonepp.api.descriptor.evenementtype.EvenementTypeDescriptor;
import fr.dila.solonepp.api.descriptor.evenementtype.PieceJointeDescriptor;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.EvenementTypeService;

/**
 * Implémentation du service permettant de gérer les types d'événements.
 * 
 * @author jtremeaux
 */
public class EvenementTypeServiceImpl extends DefaultComponent implements EvenementTypeService {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Point d'extention des types d'événements.
     */
    public static final String EVENEMENT_TYPE_EXTENSION_POINT = "evenementType";

    /**
     * Tableau associatif des types d'événements contribués {evenementId:descriptor}.
     */
    private Map<String, EvenementTypeDescriptor> evenementTypeMap;

    /**
     * Tableau associatif des types d'événements contribués {categoryId:List<descriptor>}.
     */
    private Map<String, List<EvenementTypeDescriptor>> evenementTypeByCategoryMap;

    @Override
    public void activate(final ComponentContext context) {
        evenementTypeMap = new LinkedHashMap<String, EvenementTypeDescriptor>();
        evenementTypeByCategoryMap = new HashMap<String, List<EvenementTypeDescriptor>>();
    }

    @Override
    public void registerContribution(final Object contribution, final String extensionPoint, final ComponentInstance contributor) {
        if (extensionPoint.equals(EVENEMENT_TYPE_EXTENSION_POINT)) {
            final EvenementTypeDescriptor descriptor = (EvenementTypeDescriptor) contribution;

            // Classe les événements par type d'événement
            evenementTypeMap.put(descriptor.getName(), descriptor);

            // Classe les événements par catégorie
            List<EvenementTypeDescriptor> evenementByDescriptorList = evenementTypeByCategoryMap.get(descriptor.getProcedure());
            if (evenementByDescriptorList == null) {
                evenementByDescriptorList = new ArrayList<EvenementTypeDescriptor>();
                evenementTypeByCategoryMap.put(descriptor.getProcedure(), evenementByDescriptorList);
            }
            evenementByDescriptorList.add(descriptor);
        } else {
            throw new IllegalArgumentException("Unknown extension point: " + extensionPoint);
        }
    }

    @Override
    public EvenementTypeDescriptor getEvenementType(final String evenementType) throws ClientException {
        final EvenementTypeDescriptor evenementTypeDescriptor = evenementTypeMap.get(evenementType);
        if (evenementTypeDescriptor == null) {
            throw new ClientException("Type de communication inconnu : " + evenementType);
        }
        return evenementTypeDescriptor;
    }

    @Override
    public boolean isTypeCreateur(final String evenementType) throws ClientException {
        return getEvenementType(evenementType).isCreateur();
    }

    @Override
    public boolean isTypeSuccessif(final String evenementType) throws ClientException {
        return getEvenementType(evenementType).isSuccessif();
    }

    @Override
    public boolean isDemandeAr(final String evenementType) throws ClientException {
        return getEvenementType(evenementType).isDemandeAr();
    }

    @Override
    public boolean isEmetteurAutorise(final String evenementType, final String emetteur) throws ClientException {
        final DistributionDescriptor distribution = getEvenementType(evenementType).getDistribution();
        if (distribution == null) {
            throw new ClientException("La distribution n'est pas renseigné pour le type de communication : " + evenementType);
        }
        final DistributionElementDescriptor element = distribution.getEmetteur();
        final Map<String, String> emetteurMap = element.getInstitution();
        return emetteurMap != null && emetteurMap.containsKey(emetteur);
    }

    @Override
    public boolean isDestinataireAutorise(final String evenementType, final String destinataire) throws ClientException {
        final DistributionDescriptor distribution = getEvenementType(evenementType).getDistribution();
        if (distribution == null) {
            throw new ClientException("La distribution n'est pas renseigné pour le type de communication : " + evenementType);
        }
        final DistributionElementDescriptor element = distribution.getDestinataire();
        final Map<String, String> destinataireMap = element.getInstitution();
        return destinataireMap != null && destinataireMap.containsKey(destinataire);
    }

    @Override
    public boolean isDestinataireCopieAutorise(final String evenementType, final String destinataireCopie) throws ClientException {
        final DistributionDescriptor distribution = getEvenementType(evenementType).getDistribution();
        if (distribution == null) {
            throw new ClientException("La distribution n'est pas renseigné pour le type de communication : " + evenementType);
        }
        final DistributionElementDescriptor element = distribution.getCopie();
        if (element == null) {
            return false;
        }
        final Map<String, String> destinataireCopieMap = element.getInstitution();
        return destinataireCopieMap != null && destinataireCopieMap.containsKey(destinataireCopie);
    }

    @Override
    public boolean isDestinataireCopieObligatoire(final String evenementType) throws ClientException {
        final DistributionDescriptor distribution = getEvenementType(evenementType).getDistribution();
        if (distribution == null) {
            throw new ClientException("La distribution n'est pas renseigné pour le type de communication : " + evenementType);
        }
        final DistributionElementDescriptor element = distribution.getCopie();
        if (element == null) {
            return false;
        }
        return element.isObligatoire();
    }

    @Override
    public boolean isPieceJointeObligatoire(final String evenementType, final String pieceJointeType) throws ClientException {
        final Map<String, PieceJointeDescriptor> pieceJointeDescriptorMap = getEvenementType(evenementType).getPieceJointe();
        final PieceJointeDescriptor pieceJointeDescriptor = pieceJointeDescriptorMap.get(pieceJointeType);
        if (pieceJointeDescriptor == null) {
            return false;
        }
        return pieceJointeDescriptor.isObligatoire();
    }

    @Override
    public boolean isCreerBrouillon(final String evenementType) throws ClientException {
        return getEvenementType(evenementType).isCreerBrouillon();
    }

    @Override
    public boolean isCompleter(final String evenementType) throws ClientException {
        return getEvenementType(evenementType).isCompleter();
    }

    @Override
    public boolean isRectifier(final String evenementType) throws ClientException {
        return getEvenementType(evenementType).isRectifier();
    }

    @Override
    public boolean isAnnuler(final String evenementType) throws ClientException {
        return getEvenementType(evenementType).isAnnuler();
    }

    @Override
    public List<EvenementTypeDescriptor> findEvenementByCategory(final String categoryId) {
        return evenementTypeByCategoryMap.get(categoryId);
    }

    @Override
    public List<EvenementTypeDescriptor> findEvenementTypeCreateur() {
        final List<EvenementTypeDescriptor> evenementCreateurList = new ArrayList<EvenementTypeDescriptor>();

        for (final String evenementType : evenementTypeMap.keySet()) {
            final EvenementTypeDescriptor evenementTypeDescriptor = evenementTypeMap.get(evenementType);
            if (evenementTypeDescriptor.isCreateur()) {
                evenementCreateurList.add(evenementTypeDescriptor);
            }
        }

        return evenementCreateurList;
    }

    @Override
    public List<EvenementTypeDescriptor> findEvenementTypeSuccessif() throws ClientException {

        final List<EvenementTypeDescriptor> evenementSuccessifList = new ArrayList<EvenementTypeDescriptor>();

        for (final String evtType : evenementTypeMap.keySet()) {
            final EvenementTypeDescriptor evenementTypeDescriptor = evenementTypeMap.get(evtType);
            if (evenementTypeDescriptor.isSuccessif()) {
                evenementSuccessifList.add(evenementTypeDescriptor);
            }
        }

        return evenementSuccessifList;
    }

    @Override
    public List<EvenementTypeDescriptor> findEvenementTypeSuccessifWithSameProcedure(final String currentEvenementType) {
        final List<EvenementTypeDescriptor> evenementSuccessifList = new ArrayList<EvenementTypeDescriptor>();

        final EvenementTypeDescriptor currentEvtTypeDescriptor = evenementTypeMap.get(currentEvenementType);
        if(currentEvtTypeDescriptor.isLimiteSuccesseur()) {
          return evenementSuccessifList;
        }
        for (final String evtType : evenementTypeMap.keySet()) {
            final EvenementTypeDescriptor evenementTypeDescriptor = evenementTypeMap.get(evtType);
            if (evenementTypeDescriptor.isSuccessif()
                    && (currentEvtTypeDescriptor.getProcedure().equals(SolonEppVocabularyConstant.CATEGORIE_EVENEMENT_DIVERS_VALUE)
                            || evenementTypeDescriptor.getProcedure().equals(currentEvtTypeDescriptor.getProcedure()) || evenementTypeDescriptor
                            .getProcedure().equals(SolonEppVocabularyConstant.CATEGORIE_EVENEMENT_DIVERS_VALUE))) {
                evenementSuccessifList.add(evenementTypeDescriptor);
            }
        }

        return evenementSuccessifList;
    }

    @Override
    public List<EvenementTypeDescriptor> findEvenementTypeSuccessif(final String evenementType) throws ClientException {

        List<String> evtSuccessifIdList = null;
        final List<EvenementTypeDescriptor> evenementSuccessifList = new ArrayList<EvenementTypeDescriptor>();

        final EvenementTypeDescriptor evtTypeDescriptor = evenementTypeMap.get(evenementType);
        evtSuccessifIdList = evtTypeDescriptor.getEvenementSuccessifList();

        for (final String evtType : evenementTypeMap.keySet()) {
            final EvenementTypeDescriptor evenementTypeDescriptor = evenementTypeMap.get(evtType);
            if (evenementTypeDescriptor.isSuccessif() && evtSuccessifIdList.contains(evenementTypeDescriptor.getName())) {
                evenementSuccessifList.add(evenementTypeDescriptor);
            }
        }

        return evenementSuccessifList;
    }

    @Override
    public List<EvenementTypeDescriptor> findEvenementTypeSuccessif(final CoreSession session, final String evenementType) throws ClientException {

        final EppPrincipal eppPrincipal = (EppPrincipal) session.getPrincipal();

        List<String> evtSuccessifIdList = null;
        final List<EvenementTypeDescriptor> evenementSuccessifList = new ArrayList<EvenementTypeDescriptor>();

        final EvenementTypeDescriptor evtTypeDescriptor = evenementTypeMap.get(evenementType);
        evtSuccessifIdList = evtTypeDescriptor.getEvenementSuccessifList();

        // ajoute les événements successifs, appartenant à la liste des événements successifs pour le type d'événement parent (evenementType)
        // et dont l'utilisateur peut être emetteur
        for (final String evtType : evenementTypeMap.keySet()) {
            final EvenementTypeDescriptor evenementTypeDescriptor = evenementTypeMap.get(evtType);
            if (evenementTypeDescriptor.isSuccessif() && evtSuccessifIdList.contains(evenementTypeDescriptor.getName())
                    && evenementTypeDescriptor.getDistribution().getEmetteur().getInstitution().keySet().contains(eppPrincipal.getInstitutionId())) {
                evenementSuccessifList.add(evenementTypeDescriptor);
            }
        }

        return evenementSuccessifList;
    }

    @Override
    public List<EvenementTypeDescriptor> findEvenementType() {
        return new ArrayList<EvenementTypeDescriptor>(evenementTypeMap.values());
    }

    @Override
    public boolean isEvenementTypeAlerte(final String evenementType) {
        return evenementType != null && evenementType.toUpperCase().startsWith("ALERTE");
    }

    @Override
    public boolean isEvenementTypeGenerique(final String evenementType) {
        return evenementType != null && evenementType.toUpperCase().startsWith("GENERIQUE");
    }
}
