package fr.dila.st.ui.bean;

import java.util.ArrayList;
import java.util.List;

public class DetailCommunication {
    private List<WidgetDTO> lstWidgets = new ArrayList<>();
    private List<SelectValueDTO> lstComSuccessives = new ArrayList<>();

    public List<WidgetDTO> getLstWidgets() {
        return lstWidgets;
    }

    public void setLstWidgets(List<WidgetDTO> lstWidgets) {
        this.lstWidgets = lstWidgets;
    }

    public List<SelectValueDTO> getLstComSuccessives() {
        return lstComSuccessives;
    }

    public void setLstComSuccessives(List<SelectValueDTO> lstComSuccessives) {
        this.lstComSuccessives = lstComSuccessives;
    }
}
