package fr.dila.st.ui.th.bean;

import fr.dila.st.ui.annot.SwBean;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.FormParam;

@SwBean
public class OrganigrammeNorDirectionsForm {

    public OrganigrammeNorDirectionsForm() {}

    @FormParam("norDirections[]")
    private ArrayList<OrganigrammeNorDirectionForm> norDirections;

    public List<OrganigrammeNorDirectionForm> getNorDirections() {
        return norDirections;
    }

    public void setNorDirections(ArrayList<OrganigrammeNorDirectionForm> norDirections) {
        this.norDirections = norDirections;
    }
}
