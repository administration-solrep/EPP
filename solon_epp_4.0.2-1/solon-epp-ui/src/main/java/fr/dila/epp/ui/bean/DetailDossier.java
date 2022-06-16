package fr.dila.epp.ui.bean;

import fr.dila.st.ui.bean.WidgetDTO;
import java.util.ArrayList;
import java.util.List;

public class DetailDossier {
    private List<WidgetDTO> lstWidgets = new ArrayList<>();

    public List<WidgetDTO> getLstWidgets() {
        return lstWidgets;
    }

    public void setLstWidgets(List<WidgetDTO> lstWidgets) {
        this.lstWidgets = lstWidgets;
    }
}
