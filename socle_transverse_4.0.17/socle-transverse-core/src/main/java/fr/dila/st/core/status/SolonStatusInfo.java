package fr.dila.st.core.status;

import fr.sword.idl.naiad.nuxeo.addons.status.api.model.ResultInfo;
import fr.sword.idl.naiad.nuxeo.addons.status.api.model.ResultInfo.ResultEnum;
import fr.sword.idl.naiad.nuxeo.addons.status.api.services.StatusInfo;

public interface SolonStatusInfo extends StatusInfo {
    default void setKo(ResultInfo resultInfo, String description) {
        resultInfo.setStatut(ResultEnum.KO);
        resultInfo.setDescription(description);
    }
}
