package fr.dila.st.ui.bean;

import java.util.ArrayList;
import java.util.List;
import org.nuxeo.ecm.platform.actions.Action;

public class DocumentDTO {
    private String id;
    private String nom;
    private String auteur;
    private String entite;
    private String date;
    private String version;
    private String link;
    private String extension;
    private List<Action> lstActions = new ArrayList<>();

    public DocumentDTO() {
        super();
    }

    public DocumentDTO(
        String id,
        String nom,
        String auteur,
        String date,
        String version,
        String link,
        String extension
    ) {
        super();
        setId(id);
        setNom(nom);
        setAuteur(auteur);
        setEntite(entite);
        setDate(date);
        setVersion(version);
        setLink(link);
        setExtension(extension);
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

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public String getEntite() {
        return entite;
    }

    public void setEntite(String entite) {
        this.entite = entite;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getExtension() {
        return extension;
    }

    public String getFullName() {
        return nom + extension;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public List<Action> getLstActions() {
        return lstActions;
    }

    public void setLstActions(List<Action> lstActions) {
        this.lstActions = lstActions;
    }
}
