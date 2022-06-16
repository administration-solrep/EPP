package fr.dila.ss.ui.bean;

import fr.dila.st.ui.bean.ColonneInfo;
import fr.dila.st.ui.bean.DossierDTO;
import java.util.ArrayList;
import java.util.List;
import org.nuxeo.ecm.platform.actions.Action;

public class FondDTO {
    private List<ColonneInfo> lstColonnes = new ArrayList<>();
    private List<DossierDTO> dossiers = new ArrayList<>();
    private boolean entiteVisible;
    private List<String> acceptedFileTypes = new ArrayList<>();
    private Action fondExportAction;

    public FondDTO(boolean isEntiteVisible) {
        super();
        entiteVisible = isEntiteVisible;
        buildColonnes();
    }

    public List<ColonneInfo> getLstColonnes() {
        return lstColonnes;
    }

    public void setLstColonnes(List<ColonneInfo> lstColonnes) {
        this.lstColonnes = lstColonnes;
    }

    public List<DossierDTO> getDossiers() {
        return dossiers;
    }

    public void setDossiers(List<DossierDTO> dossiers) {
        this.dossiers = dossiers;
    }

    public boolean getEntiteVisible() {
        return entiteVisible;
    }

    public void setEntiteVisible(boolean entiteVisible) {
        this.entiteVisible = entiteVisible;
    }

    public List<String> getAcceptedFileTypes() {
        return acceptedFileTypes;
    }

    public void setAcceptedFileTypes(List<String> acceptedFileTypes) {
        this.acceptedFileTypes = acceptedFileTypes;
    }

    protected void buildColonnes() {
        lstColonnes.clear();

        lstColonnes.add(new ColonneInfo("fondDossier.column.header.fichiers", false, true, false, true));
        lstColonnes.add(new ColonneInfo("fondDossier.column.header.auteur", false, true, false, true));
        lstColonnes.add(new ColonneInfo("fondDossier.column.header.entite", false, entiteVisible, false, true));
        lstColonnes.add(new ColonneInfo("fondDossier.column.header.date", false, true, false, true));
        lstColonnes.add(new ColonneInfo("fondDossier.column.header.version", false, true, false, true));
    }

    public Action getFondExportAction() {
        return fondExportAction;
    }

    public void setFondExportAction(Action fondExportAction) {
        this.fondExportAction = fondExportAction;
    }
}
