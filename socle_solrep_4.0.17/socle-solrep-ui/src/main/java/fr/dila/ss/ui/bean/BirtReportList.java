package fr.dila.ss.ui.bean;

import fr.dila.ss.api.birt.BirtReport;
import fr.dila.ss.ui.th.bean.BirtReportListForm;
import fr.dila.st.ui.bean.ColonneInfo;
import java.util.ArrayList;
import java.util.List;

public class BirtReportList {
    private final List<ColonneInfo> listeColonnes = new ArrayList<>();

    private List<BirtReport> liste = new ArrayList<>();

    private Integer nbTotal;

    public BirtReportList() {
        nbTotal = 0;
    }

    public List<BirtReport> getListe() {
        return liste;
    }

    public void setListe(List<BirtReport> liste) {
        this.liste = liste;
    }

    public Integer getNbTotal() {
        return nbTotal;
    }

    public void setNbTotal(Integer nbTotal) {
        this.nbTotal = nbTotal;
    }

    public List<ColonneInfo> getListeColonnes() {
        return listeColonnes;
    }

    public void buildColonnes(BirtReportListForm form) {
        listeColonnes.clear();

        if (form != null) {
            listeColonnes.add(
                new ColonneInfo("label.birt.reporting.report.title", true, "titre", form.getTitre(), false, true)
            );
        }
    }
}
