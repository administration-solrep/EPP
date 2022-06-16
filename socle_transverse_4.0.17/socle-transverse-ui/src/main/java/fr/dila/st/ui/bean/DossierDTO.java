package fr.dila.st.ui.bean;

import java.util.ArrayList;
import java.util.List;
import org.nuxeo.ecm.platform.actions.Action;

public class DossierDTO {
    private String id;
    private String nom;
    private List<DocumentDTO> lstDocuments = new ArrayList<>();
    private List<Action> lstActions = new ArrayList<>();

    public DossierDTO() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public List<DocumentDTO> getLstDocuments() {
        return lstDocuments;
    }

    public void setLstDocuments(List<DocumentDTO> lstDocuments) {
        this.lstDocuments = lstDocuments;
    }

    public List<Action> getLstActions() {
        return lstActions;
    }

    public void setLstActions(List<Action> lstActions) {
        this.lstActions = lstActions;
    }
}
