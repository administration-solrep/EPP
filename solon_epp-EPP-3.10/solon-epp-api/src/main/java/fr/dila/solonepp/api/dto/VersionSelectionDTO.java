package fr.dila.solonepp.api.dto;

import java.util.Calendar;

import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * DTO pour l'affichage et la selection des versions dans les corbeilles
 * 
 * @author asatre
 * 
 */
public interface VersionSelectionDTO {

	void setId(String id);

	String getId();

	void setTitle(String title);

	String getTitle();

	void setVersion(DocumentModel version);

	DocumentModel getVersion();

    String getDescription();

    void setDescription(String description);

    Calendar getDateAr();

    void setDateAr(Calendar dateAr);

    String getDateArAsString();

    boolean isAccuserReception();

    boolean isEtatRejete();
    
}
