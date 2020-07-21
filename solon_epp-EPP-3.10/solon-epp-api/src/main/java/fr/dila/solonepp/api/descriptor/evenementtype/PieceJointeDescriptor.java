package fr.dila.solonepp.api.descriptor.evenementtype;

import java.util.HashMap;
import java.util.Map;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XNodeMap;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 * Descripteur des pièces jointes.
 * 
 * @author jtremeaux
 */
@XObject("pieceJointe")
public class PieceJointeDescriptor {

    /**
     * Type de pièce jointe : TEXTE, TRAITE, AUTRE...
     */
    @XNode("@type")
    private String type;
    
    /**
     * Label de pièce jointe si on utilise pas le label du vocabulaire
     */
    @XNode("@label")
    private String label;
    
    /**
     * Type de pièce jointe obligatoire.
     */
    @XNode("@obligatoire")
    private boolean obligatoire;

    /**
     * Pièce jointe multivaluée.
     */
    @XNode("@multivalue")
    private boolean multivalue;

    @XNode("@order")
    private Integer order;
    
    @XNode("@displayUrl")
    private boolean displayUrl;
    
    @XNode("@displayTitle")
    private boolean displayTitle;
    
	/**
	 * Chaque valeur peut-elle contenir plusieurs documents (true par défaut)
	 */
	@XNode("@multiPj")
	private boolean multiPj;
	
	/**
	 * Initialisation de ce descripteur avec un premier élément ? Ce premier élément ne sera pas supprimable.
	 */
	@XNode("@initToOne")
	private boolean initToOne;

	/**
	 * Types de pièces jointes qui peuvent / doivent être fournies pour ce type
	 * d'événement.
	 */
	@XNodeMap(value = "mimetype", key = "@value", type = HashMap.class, componentType = MimetypeDescriptor.class)
	private Map<String, MimetypeDescriptor> mimetypes;

    /**
     * Constructeur par défaut de PieceJointeDescriptor.
     */
    public PieceJointeDescriptor() {
        displayUrl = true;
        displayTitle = true;

		multiPj = true;
		initToOne = false;
	}
    
    /**
     * Getter de type.
     *
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     * Setter de type.
     *
     * @param type type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Getter de obligatoire.
     *
     * @return obligatoire
     */
    public boolean isObligatoire() {
        return obligatoire;
    }

    /**
     * Setter de obligatoire.
     *
     * @param obligatoire obligatoire
     */
    public void setObligatoire(boolean obligatoire) {
        this.obligatoire = obligatoire;
    }

    /**
     * Getter de multivalue.
     *
     * @return multivalue
     */
    public boolean isMultivalue() {
        return multivalue;
    }

    /**
     * Setter de multivalue.
     *
     * @param multivalue multivalue
     */
    public void setMultivalue(boolean multivalue) {
        this.multivalue = multivalue;
    }

    /**
     * @return the order
     */
    public Integer getOrder() {
        return order;
    }

    /**
     * @param order the order to set
     */
    public void setOrder(Integer order) {
        this.order = order;
    }
    
    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    public boolean displayUrl() {
    	return this.displayUrl;
    }

    public void setDisplayUrl(boolean displayUrl) {
    	this.displayUrl = displayUrl;
    }

    public boolean displayTitle() {
    	return this.displayTitle;
    }

    public void setDisplayTitle(boolean displayTitle) {
    	this.displayTitle = displayTitle;
    }

	public boolean isMultiPj() {
		return multiPj;
	}

	public void setMultiPj(boolean multiPj) {
		this.multiPj = multiPj;
	}

	public boolean isInitToOne() {
		return initToOne;
	}

	public void setInitToOne(boolean initToOne) {
		this.initToOne = initToOne;
	}

	public Map<String, MimetypeDescriptor> getMimetypes() {
		return mimetypes;
	}

	public void setMimetypes(Map<String, MimetypeDescriptor> mimetypes) {
		this.mimetypes = mimetypes;
	}
}
