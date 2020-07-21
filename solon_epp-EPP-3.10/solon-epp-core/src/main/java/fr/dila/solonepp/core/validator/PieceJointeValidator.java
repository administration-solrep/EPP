package fr.dila.solonepp.core.validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.solonepp.api.descriptor.evenementtype.EvenementTypeDescriptor;
import fr.dila.solonepp.api.descriptor.evenementtype.PieceJointeDescriptor;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.PieceJointe;
import fr.dila.solonepp.api.domain.piecejointe.PieceJointeFichier;
import fr.dila.solonepp.api.service.EvenementTypeService;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;

/**
 * Validateur des pièces jointes.
 * 
 * @author jtremeaux
 */
public class PieceJointeValidator {
    /**
     * Constructeur de PieceJointeValidator.
     * 
     * @param session Session
     */
    public PieceJointeValidator() {
    }
    
    /**
     * Valide l'ajout / modification / suppression de pièces jointes par rapport au type d'événement.
     * 
     * @param evenementDoc Document événement
     * @param previousPieceJointeDocMap Tableau associatif des titre / documents pièces jointes de la version publiée précédente
     * @param currentPieceJointeList Tableau associatif des titre / documents pièces jointes en cours
     * @param newPieceJointeDocList Document pièce jointes à ajouter / modifier
     * @param pieceJointeToRemoveIdSet Identifiants techniques des pièces jointes à supprimer
     * @param strictMode Interdit la suppression de pièce jointe, et la modification ou suppression de fichiers  
     * @param etatBrouillon test moins restrictif en cas de version brouillon
     * @throws ClientException
     */
    public void validatePieceJointeList(DocumentModel evenementDoc, Map<String, DocumentModel> previousPieceJointeDocMap, Map<String, DocumentModel> currentPieceJointeDocMap, List<DocumentModel> newPieceJointeDocList, Set<String> pieceJointeToRemoveIdSet, boolean strictMode, boolean etatBrouillon) throws ClientException {
        Evenement evenement = evenementDoc.getAdapter(Evenement.class);
        String typeEvenement = evenement.getTypeEvenement();
        final EvenementTypeService evenementTypeService = SolonEppServiceLocator.getEvenementTypeService();
        EvenementTypeDescriptor evenementTypeDescriptor = evenementTypeService.getEvenementType(evenement.getTypeEvenement());
        Map<String, PieceJointeDescriptor> pieceJointeDescriptorMap = evenementTypeDescriptor.getPieceJointe();

        // Classe les pièces jointes à créer / modifier par type
        Map<String, PieceJointe> pieceJointeMap = new HashMap<String, PieceJointe>();
        for (DocumentModel pieceJointeDoc : newPieceJointeDocList) {
            PieceJointe pieceJointe = pieceJointeDoc.getAdapter(PieceJointe.class);
            String typePieceJointe = pieceJointe.getTypePieceJointe();
            if (StringUtils.isEmpty(typePieceJointe) && !etatBrouillon) {
                throw new ClientException("Le type de pièce jointe est obligatoire");
            }

            pieceJointeMap.put(typePieceJointe, pieceJointe);
        }
        
        // Vérifie que les pièces spécifiées sont autorisées par le type
        for (PieceJointe pieceJointe : pieceJointeMap.values()) {
            String typePieceJointe = pieceJointe.getTypePieceJointe();
            PieceJointeDescriptor pieceJointeDescriptor = pieceJointeDescriptorMap.get(typePieceJointe);
            if (pieceJointeDescriptor == null) {
                throw new ClientException("Le type de pièce jointe " + typePieceJointe + " est interdit pour le type de communication " + typeEvenement);
            }
        }
        
        for (PieceJointeDescriptor pieceJointeDescriptor : pieceJointeDescriptorMap.values()) {
            // Vérifie que les pièce jointes obligatoires sont présentes
            String typePieceJointe = pieceJointeDescriptor.getType();
            PieceJointe pieceJointe = pieceJointeMap.get(typePieceJointe); 
            if (!etatBrouillon && pieceJointeDescriptor.isObligatoire() && (pieceJointe == null || pieceJointe.getPieceJointeFichierDocList() == null || pieceJointe.getPieceJointeFichierDocList().isEmpty())) {
                throw new ClientException("Le type de pièce jointe " + typePieceJointe + " est obligatoire pour le type de communication " + typeEvenement);
            }
            
            // Vérifie que les pièces jointes monovaluées sont renseignées une seule fois
            if (!etatBrouillon && pieceJointe != null && !pieceJointeDescriptor.isMultiPj()) {
                if (pieceJointe.getPieceJointeFichierDocList() != null && pieceJointe.getPieceJointeFichierDocList().size() > 1) {
                    throw new ClientException("La pièce jointe de type " + typePieceJointe + " doit être présente une seule fois");
                }
            }
        }
        
        // En mode complétion, il est interdit de supprimer des pièces jointes ogligatoire publiées
        if (strictMode && previousPieceJointeDocMap != null) {
        	Set<String> previousPieceJointeList = previousPieceJointeDocMap.keySet();
        	for (String pieceJointeId : pieceJointeToRemoveIdSet) {
        		DocumentModel pieceJointeDoc = currentPieceJointeDocMap.get(pieceJointeId);
        		PieceJointe pieceJointe = pieceJointeDoc.getAdapter(PieceJointe.class);
        		String pieceJointeType = pieceJointe.getTypePieceJointe();
        		if (evenementTypeService.isPieceJointeObligatoire(typeEvenement, pieceJointeType)) {
        			if (previousPieceJointeList.contains(pieceJointeId)) {
        				throw new ClientException("Il est interdit de supprimer des pièces jointes publiées. ID interne : " + pieceJointeId);
        			}
        		}
        	}
        }
        
        // Vérifie que les pièces jointes obligatoires ne sont pas supprimées
        for (String pieceJointeId : pieceJointeToRemoveIdSet) {
            DocumentModel pieceJointeDoc = currentPieceJointeDocMap.get(pieceJointeId);
            if (pieceJointeDoc == null) {
                throw new ClientException("Tentative de suppression de la pièce jointe " + pieceJointeId + " introuvable (ne devrait pas se produire)");
            }
            PieceJointe pieceJointe = pieceJointeDoc.getAdapter(PieceJointe.class);
            String pieceJointeType = pieceJointe.getTypePieceJointe();
            if (evenementTypeService.isPieceJointeObligatoire(typeEvenement, pieceJointeType) && !pieceJointeMap.containsKey(pieceJointeType)) {
                throw new ClientException("Le type de pièce jointe " + pieceJointeType + " est obligatoire pour le type de communication " + typeEvenement);
            }
        }
    }
    
    /**
     * Valide les données des fichiers de la pièce jointe.
     * 
     * @param pieceJointeDoc Document pièce jointe
     * @param etatBrouillon 
     * @throws ClientException
     */
    public void validatePieceJointe(DocumentModel pieceJointeDoc, boolean etatBrouillon, String evenementType) throws ClientException {
    	validatePiecesJointes(Collections.singletonList(pieceJointeDoc), etatBrouillon, evenementType);
    }
    
    private void validatePieceJointe(PieceJointe pieceJointe, PieceJointeDescriptor descriptor, boolean etatBrouillon) throws ClientException {
        
        // Vérifie les données de la pièce jointe
        if (StringUtils.isEmpty(pieceJointe.getTypePieceJointe())) {
            throw new ClientException("Le type de pièce jointe est obligatoire");
        }
        
        // Vérifie si la pièce jointe contient des fichiers
        List<DocumentModel> pieceJointeFichierDocList = pieceJointe.getPieceJointeFichierDocList();
        if (!etatBrouillon && (pieceJointeFichierDocList == null || pieceJointeFichierDocList.isEmpty())) {
            throw new ClientException("La pièce jointe " + pieceJointe.getTypePieceJointe() + " doit contenir des fichiers");
        } else if (etatBrouillon && pieceJointeFichierDocList == null) {
            return;
        }

        if (descriptor.isObligatoire() && (pieceJointeFichierDocList == null || pieceJointeFichierDocList.isEmpty())) {
        	throw new ClientException("La pièce jointe " + pieceJointe.getTypePieceJointe() + " est obligatoire");
        }
        
        if (!descriptor.isMultiPj() && pieceJointeFichierDocList.size() > 1) {
        	throw new ClientException("La pièce jointe " + pieceJointe.getTypePieceJointe() + " ne peut pas avoir plusieurs fichiers");
        }
        // Vérifie les données des fichiers
        Set<String> filenameList = new HashSet<String>();
        List<String> allowedMimetypes = new ArrayList<String>(descriptor.getMimetypes().keySet());
        for (DocumentModel pieceJointeFichierDoc : pieceJointeFichierDocList) {
            // Vérifie le nom du fichier
            PieceJointeFichierValidator pieceJointeFichierValidator = new PieceJointeFichierValidator();
            pieceJointeFichierValidator.validatePieceJointeFichier(pieceJointe.getDocument(), pieceJointeFichierDoc, allowedMimetypes);
            
            // Vérifie l'absence de doublons
            PieceJointeFichier pieceJointeFichier = pieceJointeFichierDoc.getAdapter(PieceJointeFichier.class);
            String filename = pieceJointeFichier.getFilename();
            if (filenameList.contains(filename)) {
                throw new ClientException("Le fichier " + filename + " doit être fourni une seule fois");
            }
            filenameList.add(filename);
        }
    }
    
    /**
     * Valide les données des fichiers des pièces jointes.
     * 
     * @param pieceJointeDoc Document pièce jointe
     * @param etatBrouillon 
     * @throws ClientException
     */
    public void validatePiecesJointes(List<DocumentModel> piecesJointesDocs, boolean etatBrouillon, String evenementType) throws ClientException {
    	// Récupération du descriptor
        final EvenementTypeService evenementTypeService = SolonEppServiceLocator.getEvenementTypeService();
        EvenementTypeDescriptor evenementTypeDescriptor = evenementTypeService.getEvenementType(evenementType);
        Map<String, PieceJointeDescriptor> pieceJointeDescriptorMap = evenementTypeDescriptor.getPieceJointe();
        
    	for (DocumentModel pieceJointeDoc : piecesJointesDocs) {
    		PieceJointe pieceJointe = pieceJointeDoc.getAdapter(PieceJointe.class);
    		PieceJointeDescriptor descriptor = pieceJointeDescriptorMap.get(pieceJointe.getTypePieceJointe());
    		validatePieceJointe(pieceJointe, descriptor, etatBrouillon);
    	}
    }
}
