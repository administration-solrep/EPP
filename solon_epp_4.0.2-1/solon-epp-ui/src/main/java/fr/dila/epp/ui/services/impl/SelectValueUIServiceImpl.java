package fr.dila.epp.ui.services.impl;

import static fr.dila.st.ui.enums.parlement.CommunicationMetadonneeEnum.DESTINATAIRE;
import static fr.dila.st.ui.enums.parlement.CommunicationMetadonneeEnum.EMETTEUR;
import static java.util.stream.Collectors.toList;

import fr.dila.epp.ui.enumeration.EppContextDataKey;
import fr.dila.epp.ui.services.SelectValueUIService;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.constant.SolonEppVocabularyConstant;
import fr.dila.solonepp.api.descriptor.evenementtype.DistributionElementDescriptor;
import fr.dila.solonepp.api.descriptor.evenementtype.EvenementTypeDescriptor;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.institution.InstitutionsEnum;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.api.constant.STVocabularyConstants;
import fr.dila.st.api.vocabulary.VocabularyEntry;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.nuxeo.ecm.core.api.DocumentModel;

public class SelectValueUIServiceImpl implements SelectValueUIService {

    @Override
    public List<SelectValueDTO> getAllAttributionsCommission() {
        return getSelectValues(SolonEppServiceLocator.getTableReferenceService().findAllAttributionCommission(null));
    }

    @Override
    public List<SelectValueDTO> getAllDecisionsProcAcc() {
        return getSelectValues(
            STServiceLocator.getVocabularyService().getAllEntry(SolonEppVocabularyConstant.DECISION_PROC_ACC)
        );
    }

    @Override
    public List<SelectValueDTO> getAllInstitutions() {
        return getSelectValues(
            new ArrayList<>(SolonEppSchemaConstant.INSTITUTIONS_VALUES),
            InstitutionsEnum::name,
            InstitutionsEnum::getLabel
        );
    }

    @Override
    public List<SelectValueDTO> getAllMotifsIrrecevabilite() {
        return getSelectValues(SolonEppServiceLocator.getTableReferenceService().findAllMotifIrrecevabilite(null));
    }

    @Override
    public List<SelectValueDTO> getAllNaturesLoi() {
        return getSelectValues(SolonEppServiceLocator.getTableReferenceService().findAllNatureLoi(null));
    }

    @Override
    public List<SelectValueDTO> getAllNaturesRapport() {
        return getSelectValues(SolonEppServiceLocator.getTableReferenceService().findAllNatureRapport(null));
    }

    @Override
    public List<SelectValueDTO> getAllNiveauxLecture() {
        return getSelectValues(SolonEppServiceLocator.getTableReferenceService().findAllNiveauLectureCode(null));
    }

    @Override
    public List<SelectValueDTO> getAllRapportsParlement() {
        return getSelectValues(SolonEppServiceLocator.getTableReferenceService().findAllRapportParlement(null));
    }

    @Override
    public List<SelectValueDTO> getAllResultatsCMP() {
        return getSelectValues(SolonEppServiceLocator.getTableReferenceService().findAllResultatCmp(null));
    }

    @Override
    public List<SelectValueDTO> getAllRubriques() {
        return getSelectValues(
            STServiceLocator.getVocabularyService().getAllEntry(SolonEppVocabularyConstant.RUBRIQUES_VOCABULARY),
            DocumentModel::getId,
            dm -> PropertyUtil.getStringProperty(dm, "vocabularyRubrique", STVocabularyConstants.COLUMN_LABEL)
        );
    }

    @Override
    public List<SelectValueDTO> getAllRubriquesForEmetteur(InstitutionsEnum emetteur) {
        return getSelectValues(
            SolonEppServiceLocator.getSolonEppVocabularyService().getRubriquesEntriesForEmetteur(emetteur),
            VocabularyEntry::getId,
            VocabularyEntry::getLabel
        );
    }

    @Override
    public List<SelectValueDTO> getAllSensAvis() {
        return getSelectValues(SolonEppServiceLocator.getTableReferenceService().findAllSensAvis(null));
    }

    @Override
    public List<SelectValueDTO> getAllSortsAdoption() {
        return getSelectValues(SolonEppServiceLocator.getTableReferenceService().findAllSortAdoption(null));
    }

    @Override
    public List<SelectValueDTO> getAllTypesActe() {
        return getSelectValues(SolonEppServiceLocator.getTableReferenceService().findAllTypeActe(null));
    }

    @Override
    public List<SelectValueDTO> getAllTypesLoi() {
        return getSelectValues(SolonEppServiceLocator.getTableReferenceService().findAllTypeLoi(null));
    }

    @Override
    public List<SelectValueDTO> getSelectableInstitutions(SpecificContext context) {
        String input = context.getFromContextData(EppContextDataKey.INPUT);
        Set<String> institutionIds;
        EvenementTypeDescriptor currentEvtTypeDescriptor = SolonEppServiceLocator
            .getEvenementTypeService()
            .getEvenementType(context.getCurrentDocument().getAdapter(Evenement.class).getTypeEvenement());
        DistributionElementDescriptor descriptor = null;
        EppPrincipal eppPrincipal = (EppPrincipal) context.getSession().getPrincipal();
        if (EMETTEUR.getName().equals(input)) {
            descriptor = currentEvtTypeDescriptor.getDistribution().getEmetteur();
            Set<String> userInstitutions = eppPrincipal.getInstitutionIdSet();
            institutionIds =
                descriptor
                    .getInstitution()
                    .keySet()
                    .stream()
                    .filter(institutionId -> userInstitutions.contains(institutionId))
                    .collect(Collectors.toSet());
            institutionIds.addAll(
                SolonEppSchemaConstant
                    .INSTITUTIONS_VALUES.stream()
                    .map(InstitutionsEnum::name)
                    .filter(institutionId -> InstitutionsEnum.isInstitutionAlwaysAccessible(institutionId))
                    .collect(Collectors.toSet())
            );
        } else if (DESTINATAIRE.getName().equals(input)) {
            descriptor = currentEvtTypeDescriptor.getDistribution().getDestinataire();
            institutionIds = descriptor.getInstitution().keySet();
        } else {
            institutionIds = eppPrincipal.getInstitutionIdSet();
        }
        return getSelectValues(
            new ArrayList<>(institutionIds),
            Function.identity(),
            id -> InstitutionsEnum.getLabelFromInstitutionKey(id)
        );
    }

    @Override
    public List<SelectValueDTO> getSelectValuesFromVocabulary(String vocabulary) {
        return getSelectValues(STServiceLocator.getVocabularyService().getAllEntry(vocabulary));
    }

    private static List<SelectValueDTO> getSelectValues(List<DocumentModel> entries) {
        return getSelectValues(
            entries,
            DocumentModel::getId,
            dm ->
                PropertyUtil.getStringProperty(dm, STVocabularyConstants.VOCABULARY, STVocabularyConstants.COLUMN_LABEL)
        );
    }

    private static <T> List<SelectValueDTO> getSelectValues(
        List<T> entries,
        Function<T, String> getId,
        Function<T, String> getLabel
    ) {
        return entries
            .stream()
            .map(entry -> new SelectValueDTO(getId.apply(entry), getLabel.apply(entry)))
            .sorted(Comparator.comparing(SelectValueDTO::getLabel))
            .collect(toList());
    }
}
