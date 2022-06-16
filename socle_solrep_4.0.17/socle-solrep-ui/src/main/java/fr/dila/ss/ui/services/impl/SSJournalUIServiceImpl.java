package fr.dila.ss.ui.services.impl;

import fr.dila.ss.ui.bean.JournalDossierListingDTO;
import fr.dila.ss.ui.bean.JournalDossierResultList;

public class SSJournalUIServiceImpl
    extends AbstractSSJournalUIServiceImpl<JournalDossierResultList, JournalDossierListingDTO> {

    public SSJournalUIServiceImpl() {
        super(JournalDossierResultList.class, JournalDossierListingDTO.class);
    }

    @Override
    protected String getProviderName() {
        return "JOURNAL_DOSSIER";
    }
}
