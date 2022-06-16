package fr.dila.epp.ui.services;

import fr.dila.solonepp.api.institution.InstitutionsEnum;
import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;

public interface SelectValueUIService {
    List<SelectValueDTO> getAllAttributionsCommission();

    List<SelectValueDTO> getAllDecisionsProcAcc();

    List<SelectValueDTO> getAllInstitutions();

    List<SelectValueDTO> getAllMotifsIrrecevabilite();

    List<SelectValueDTO> getAllNaturesLoi();

    List<SelectValueDTO> getAllNaturesRapport();

    List<SelectValueDTO> getAllNiveauxLecture();

    List<SelectValueDTO> getAllRapportsParlement();

    List<SelectValueDTO> getAllResultatsCMP();

    List<SelectValueDTO> getAllRubriques();

    List<SelectValueDTO> getAllRubriquesForEmetteur(InstitutionsEnum emetteur);

    List<SelectValueDTO> getAllSensAvis();

    List<SelectValueDTO> getAllSortsAdoption();

    List<SelectValueDTO> getAllTypesActe();

    List<SelectValueDTO> getAllTypesLoi();

    List<SelectValueDTO> getSelectableInstitutions(SpecificContext context);

    List<SelectValueDTO> getSelectValuesFromVocabulary(String vocabulary);
}
