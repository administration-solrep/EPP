package fr.dila.ss.ui.bean;

import static fr.dila.ss.ui.th.bean.JournalDossierForm.REF_DOSSIER_PARAM;

import fr.dila.ss.ui.th.bean.JournalDossierForm;
import fr.dila.st.ui.bean.ColonneInfo;

public class JournalTechniqueResultList extends JournalDossierResultList {

    @Override
    protected void buildColonnes(JournalDossierForm form) {
        super.buildColonnes(form);
        listeColonnes.add(
            ColonneInfo
                .builder("journal.label.content.header.refDossier", true, true, false, true)
                .sortName(REF_DOSSIER_PARAM)
                .sortId(getSortId(REF_DOSSIER_PARAM))
                .sortValue(form == null ? null : form.getReferenceDossier())
                .build()
        );
    }
}
