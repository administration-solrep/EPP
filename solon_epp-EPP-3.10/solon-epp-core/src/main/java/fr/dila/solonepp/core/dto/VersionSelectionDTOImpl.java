package fr.dila.solonepp.core.dto;

import java.util.Calendar;

import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.solonepp.api.constant.SolonEppLifecycleConstant;
import fr.dila.solonepp.api.dto.VersionSelectionDTO;
import fr.dila.st.core.util.DateUtil;

/**
 * DTO pour l'affichage et la selection des versions dans les corbeilles
 * @author asatre
 *
 */
public class VersionSelectionDTOImpl implements VersionSelectionDTO {
	
	private String id;
	private String title;
	private String description;
	private String etat;
	private Calendar dateAr;
	
    private DocumentModel version;
	
	private VersionSelectionDTOImpl(){
		//default private constructor
	}
	
	public VersionSelectionDTOImpl(String id, String title) {
		this();
		this.id = id;
		this.title = title;
	}
	
    public VersionSelectionDTOImpl(String id, String title, String description, String etat, Calendar dateAr) {
        super();
        this.id = id;
        this.title = title;
        this.description = description;
        this.etat = etat;
        this.dateAr = dateAr;
    }

    @Override
	public void setId(String id) {
		this.id = id;
	}
	@Override
	public String getId() {
		return id;
	}
	@Override
	public void setTitle(String title) {
		this.title = title;
	}
	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void setVersion(DocumentModel version) {
		this.version = version;
	}
	@Override
	public DocumentModel getVersion() {
		return version;
	} 
	
    /**
     * @return the description
     */
	@Override
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
	@Override
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the dateAr
     */
	@Override
    public Calendar getDateAr() {
        return dateAr;
    }

    /**
     * @param dateAr the dateAr to set
     */
	@Override
    public void setDateAr(Calendar dateAr) {
        this.dateAr = dateAr;
    }
	
	@Override
	public String getDateArAsString() {
	    if (dateAr != null) {
	        return DateUtil.formatDDMMYYYY(dateAr);
	    }
	    return null;
	}
	
    @Override
    public boolean isAccuserReception() {
        if (dateAr != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return the etat
     */
    public String getEtat() {
        return etat;
    }

    /**
     * @param etat the etat to set
     */
    public void setEtat(String etat) {
        this.etat = etat;
    }
    
    @Override
    public boolean isEtatRejete() {
        return SolonEppLifecycleConstant.VERSION_REJETE_STATE.equals(etat) || SolonEppLifecycleConstant.VERSION_ABANDONNE_STATE.equals(etat);
    }
}
