package fr.dila.ss.ui.services;

import fr.dila.ss.ui.bean.fdr.ModeleFDRList;
import fr.dila.st.ui.th.model.SpecificContext;

public interface SSModeleFdrListUIService {
    ModeleFDRList getModelesFDRSubstitution(SpecificContext context);

    ModeleFDRList getModelesFDRSubstitution(ModeleFDRList modeleFDRList, SpecificContext context);
}
