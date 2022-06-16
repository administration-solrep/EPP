package fr.dila.ss.ui.services;

import fr.dila.ss.ui.bean.JournalTechniqueResultList;
import fr.dila.st.ui.th.model.SpecificContext;
import java.io.File;

public interface SSJournalAdminUIService extends SSJournalUIService<JournalTechniqueResultList> {
    File exportJournal(SpecificContext context);
}
