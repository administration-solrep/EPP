package fr.dila.solonepp.core.assembler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import com.google.common.base.Objects;
import com.google.common.collect.LinkedHashMultiset;

import java.util.Arrays;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.descriptor.metadonnees.MetaDonneesDescriptor;
import fr.dila.solonepp.api.descriptor.metadonnees.PropertyDescriptor;
import fr.dila.solonepp.api.descriptor.metadonnees.VersionMetaDonneesDescriptor;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.MetaDonneesService;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;

/**
 * Assembleur des données des objets Version.
 * 
 * @author jtremeaux
 */
public class VersionAssembler {
    /**
     * Session.
     */
    private CoreSession session;
    
    /**
     * Constructeur de VersionAssembler.
     * 
     * @param session Session
     */
    public VersionAssembler(CoreSession session) {
        this.session = session;
    }
    
    /**
     * Assemble les propriétés d'une version pour modifier une version existante.
     * 
     * @param versionFromDoc Nouveau document version
     * @param versionToDoc Document version à modifier
     * @param evenementDoc Document événement
     * @param initial Mode modification de la version initiale (brouillon ou publication)
     * @param completion Mode complétion
     * @throws ClientException
     */
    public void assembleVersionForUpdate(DocumentModel versionFromDoc, DocumentModel versionToDoc, DocumentModel evenementDoc, boolean initial, boolean completion) throws ClientException {
        EppPrincipal principal = (EppPrincipal) session.getPrincipal();
        Evenement evenement = evenementDoc.getAdapter(Evenement.class);
        
        // Assemble les données de la version
        final MetaDonneesService metaDonneesService = SolonEppServiceLocator.getMetaDonneesService();
        MetaDonneesDescriptor metaDonneesDescriptor = metaDonneesService.getEvenementType(evenement.getTypeEvenement());
        VersionMetaDonneesDescriptor versionMetaDonneesDescriptor = metaDonneesDescriptor.getVersion();
        for (PropertyDescriptor propertyDescriptor : versionMetaDonneesDescriptor.getProperty().values()) {
            // Les attributs renseignés par l'EPP ne sont jamais modifiables
            if (propertyDescriptor.isRenseignerEpp()) {
                continue;
            }
            
            // Traite uniquement les attributs modifiables, ou tous les attributs pour une version non publiée
            if (!propertyDescriptor.isModifiable() && !initial) {
                continue;
            }
            
            // Détermine le schéma de la propriété
            String schema = propertyDescriptor.getSchema();
            String property = propertyDescriptor.getName();
            
            // L'attribut "senat" peut être modifié uniquement par l'institution SENAT
            if (SolonEppSchemaConstant.VERSION_SENAT_PROPERTY.equals(property)
                    && !principal.isInstitutionSenat()) {
                continue;
            }
            
            Object valueFrom = versionFromDoc.getProperty(schema, property);
            Object valueTo = versionToDoc.getProperty(schema, property);
            if (propertyDescriptor.isMultiValue()) {
                if (initial || !propertyDescriptor.isObligatoire() || !completion) {
                    // Autorise l'ajout / modification / suppression de valeurs
                    versionToDoc.setProperty(schema, property, assembleListValue(valueFrom, valueTo, schema, propertyDescriptor.getName(), false));
                } else {
                    // En mode complétion, autorise l'ajout d'éléments aux valeurs multivaluées obligatoires, et interdit la suppression
                    versionToDoc.setProperty(schema, property, assembleListValue(valueFrom, valueTo, schema, propertyDescriptor.getName(), true));
                }
            } else {
                if (initial || !propertyDescriptor.isObligatoire() || !completion) {
                    // Autorise l'ajout / modification / suppression des valeurs scalaires
                    versionToDoc.setProperty(schema, propertyDescriptor.getName(), valueFrom);
                } else if (completion) {
                    // En mode complétion, interdit la modification des valeurs scalaires obligatoires
                    boolean equals = false;
                    if (valueFrom == null && valueTo == null) {
                        equals = true;
                    } else if (valueFrom != null && valueTo != null) {
                        if (valueFrom instanceof Calendar && valueTo instanceof Calendar) {
                            equals = ((Calendar) valueFrom).compareTo((Calendar) valueTo) == 0;
                        } else {
                            equals = Objects.equal(valueFrom, valueTo);
                        }
                    }
                    if (!equals) {
                        StringBuilder errorMsg = new StringBuilder("La propriété obligatoire ")
                            .append(schema)
                            .append(":")
                            .append(property)
                            .append(" ne peut pas être modifiée en mode complétion, ancienne valeur: ")
                            .append(valueTo)
                            .append(" nouvelle valeur: ")
                            .append(valueFrom);
                        throw new ClientException(errorMsg.toString());
                    }
                }
            }
        }
    }

    /**
     * Modifie les éléments d'une collection.
     * 
     * @param valueFrom Nouvelles valeurs
     * @param valueTo Valeurs du document à modifier
     * @param schema Nom du schéma
     * @param property Nom de la propriété
     * @param addMode Mode ajout : ajoute les nouveaux éléments, sinon remplace la collection
     * @return Nouvelle collection
     * @throws ClientException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List assembleListValue(Object valueFrom, Object valueTo, String schema, String property, boolean addMode) throws ClientException {
        if (valueFrom instanceof Object[] && valueTo instanceof Object[]) {
            valueFrom = Arrays.asList((Object[]) valueFrom);
            valueTo = Arrays.asList((Object[]) valueTo);
        }
        
        if (valueFrom == null) {
            valueFrom = new ArrayList();
        } else {
            if (!(valueTo instanceof Collection)) {
                throw new ClientException("La propriété " + schema + ":" + property + " des données entrantes doit contenir une collection");
            }
        }
        
        LinkedHashMultiset valueToSet = LinkedHashMultiset.create();
        // En mode ajout, conserve les anciennes valeurs, sinon repart d'un ensemble de valeurs vides
        if (addMode) {
            if (valueTo != null) {
                if (!(valueTo instanceof Collection)) {
                    throw new ClientException("La propriété " + schema + ":" + property + " du document modifié entrantes doit être une collection");
                }
                valueToSet.addAll((Collection) valueTo);
            }
        }
        
        // Ajoute les nouvelles valeurs à la collection
        if (!addMode) {
            valueToSet.addAll((Collection) valueFrom);
        } else {
            LinkedHashMultiset valueFromSet = LinkedHashMultiset.create();
            valueFromSet.addAll((Collection) valueFrom);
            for (Iterator iter = ((Collection) valueFrom).iterator(); iter.hasNext();) {
                Object o = iter.next();
                if (valueToSet.count(o) < valueFromSet.count(o)) {
                    valueToSet.add(o);
                }
            }
            for (Object o : valueToSet) {
                if (valueFromSet.count(o) < valueToSet.count(o)) {
                    throw new ClientException("Il est interdit de retirer un élément de la propriété multivaluée " + schema + ":" + property + " valeur :" + o);
                }
            }
        }
        
        List newList = new ArrayList(valueToSet);
        return newList;
    }
}
