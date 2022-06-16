package fr.sword.idl.naiad.nuxeo.addons.status.api.model;

import java.util.HashMap;

public class ResultInfo extends HashMap<String, Object> {

    private static final long serialVersionUID = 409406341768703215L;

    public enum ResultEnum {
        OK,
        KO;
    }

    public ResultInfo() {
        setStatut(ResultEnum.OK);
        setDescription("-");
    }

    public String getDescription() {
        return (String) this.get("description");
    }

    public void setDescription(final String description) {
        this.put("description", description);
    }

    public void setParams(final String params) {
        this.put("params", params);
    }

    public ResultEnum getStatut() {
        return (ResultEnum) this.get("statut");
    }

    public void setStatut(final ResultEnum statut) {
        this.put("statut", statut);
    }

}
