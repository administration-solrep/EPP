package fr.dila.ss.ui.bean.fdr;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.nuxeo.ecm.platform.actions.Action;

public class NoteEtapeDTO {
    private String id;
    private String auteur;
    private Date date;
    private String content;
    private String parentId;
    private List<Action> actions = new ArrayList<>();
    private List<NoteEtapeDTO> reponses = new ArrayList<>();

    public NoteEtapeDTO() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public List<NoteEtapeDTO> getReponses() {
        return reponses;
    }

    public void setReponses(List<NoteEtapeDTO> reponses) {
        this.reponses = reponses;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
