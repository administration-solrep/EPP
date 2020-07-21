package fr.dila.solonepp.api.descriptor.metadonnees;

import java.util.List;

/**
 * Interface de description d'une propriété d'une métadonnée.
 * 
 * @author asatre
 */
public interface PropertyDescriptor {

    /**
     * Retourne le nom du schéma de la propriété.
     * 
     * @return Nom du schéma
     */
    String getSchema();

    String getName();
    
	void setName(String name);

	String getNameWS();
	
	void setNameWS(String nameWS);
	
    String getLabel();

    void setLabel(String label);

	boolean isObligatoire();

	void setObligatoire(boolean obligatoire);

	String getType();

	void setType(String type);

	boolean isModifiable();

	void setModifiable(boolean modifiable);

	boolean isMultiValue();

	void setMultiValue(boolean multiValue);
	
	void setFicheDossier(boolean ficheDossier);

	boolean isFicheDossier();
	
	void setRenseignerEpp(boolean renseignerEpp);

	boolean isRenseignerEpp();
	
	void setVisibility(boolean visibility);

	boolean isVisibility();
	
	void setInstitutions(String institutions);

	String getInstitutions();
	
	List<String> getListInstitutions();
	
	void setDefaultValue(DefaultValue defaultValue);

	DefaultValue getDefaultValue() ;
	
	void setMinDate(String minDate);
	
	String getMinDate();

	void setHidden(boolean hidden);
	
	boolean isHidden();
}
