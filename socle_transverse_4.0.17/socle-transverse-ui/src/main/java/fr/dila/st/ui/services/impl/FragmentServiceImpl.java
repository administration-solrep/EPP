package fr.dila.st.ui.services.impl;

import fr.dila.st.ui.services.FragmentService;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.Map;

public abstract class FragmentServiceImpl implements FragmentService {

    public abstract Map<String, Object> getData(SpecificContext context);
}
