package fr.dila.ss.ui.bean.fdr;

import fr.dila.ss.ui.enums.FeuilleRouteEtapeOrder;
import fr.dila.ss.ui.enums.FeuilleRouteTypeRef;
import fr.dila.st.ui.annot.SwBean;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteExecutionType;
import java.util.ArrayList;
import javax.ws.rs.FormParam;

@SwBean
public class CreationEtapeDTO {
    @FormParam("idBranch")
    private String idBranche; // id de la branche courante

    @FormParam("typeAjout")
    private String typeAjout; // ajout avant ou après

    @FormParam("typeCreation")
    private String typeCreation; // étape en série ou en parallèle

    @FormParam("lines[]")
    private ArrayList<CreationEtapeLineDTO> lines = new ArrayList<>();

    @FormParam("typeRef")
    private String typeRef; // branche ou étape

    public String getIdBranche() {
        return idBranche;
    }

    public void setIdBranche(String idBranche) {
        this.idBranche = idBranche;
    }

    public String getTypeAjout() {
        return typeAjout;
    }

    public void setTypeAjout(String typeAjout) {
        this.typeAjout = typeAjout;
    }

    public FeuilleRouteEtapeOrder getTypeAjoutEnum() {
        return FeuilleRouteEtapeOrder.fromString(this.typeAjout);
    }

    public void setTypeAjoutEnum(FeuilleRouteEtapeOrder order) {
        if (order != null) {
            this.typeAjout = order.getFrontValue();
        }
    }

    public FeuilleRouteTypeRef getTypeRefAjoutEnum() {
        return FeuilleRouteTypeRef.fromString(this.typeRef);
    }

    public void setTypeRefAjoutEnum(FeuilleRouteTypeRef typeRef) {
        if (typeRef != null) {
            this.typeRef = typeRef.getFrontValue();
        }
    }

    public String getTypeCreation() {
        return typeCreation;
    }

    public void setTypeCreation(String typeCreation) {
        this.typeCreation = typeCreation;
    }

    public ArrayList<CreationEtapeLineDTO> getLines() {
        return lines;
    }

    public void setLines(ArrayList<CreationEtapeLineDTO> lines) {
        this.lines = lines;
    }

    public String getTypeRef() {
        return typeRef;
    }

    public void setTypeRef(String typeRef) {
        this.typeRef = typeRef;
    }

    public FeuilleRouteExecutionType getExecutionType() {
        if ("parallele".equals(this.typeCreation)) {
            return FeuilleRouteExecutionType.parallel;
        } else if ("serie".equals(this.typeCreation) || "epreuve".equals(this.typeCreation)) {
            return FeuilleRouteExecutionType.serial;
        }

        return null;
    }
}
