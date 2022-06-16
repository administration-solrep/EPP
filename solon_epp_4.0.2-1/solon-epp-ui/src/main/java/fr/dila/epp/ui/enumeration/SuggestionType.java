package fr.dila.epp.ui.enumeration;

import fr.dila.epp.ui.services.SolonEppUIServiceLocator;
import fr.dila.epp.ui.services.actions.SolonEppActionsServiceLocator;
import fr.dila.solonepp.api.constant.SolonEppConstant;
import fr.dila.solonepp.api.institution.InstitutionsEnum;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.bean.SuggestionDTO;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;

public enum SuggestionType {
    AUTEUR("auteur", SuggestionType::getSuggestionsFromIdentite),
    COAUTEUR("coauteur", SuggestionType::getSuggestionsFromIdentite),
    COMMISSIONS("commissions", SuggestionType::getSuggestionsFromOrganisme),
    COMMISSION_SAISIE("commissionSaisie", SuggestionType::getSuggestionsFromOrganisme),
    COMMISSION_SAISIE_AUF_OND("commissionSaisieAuFond", SuggestionType::getSuggestionsFromOrganisme),
    COMMISSION_SAISIE_POUR_AVIS("commissionSaisiePourAvis", SuggestionType::getSuggestionsFromOrganisme),
    GROUPE_PARLEMENTAIRE("groupeParlementaire", SuggestionType::getSuggestionsForGroupeParlementaire),
    ORGANISME("organisme", SuggestionType::getSuggestionsFromAuditionOrganisme),
    PARLEMENTAIRES_SUPPLEANTS("parlementaireSuppleantList", SuggestionType::getSuggestionsFromIdentite),
    PARLEMENTAIRES_TITULAIRES("parlementaireTitulaire", SuggestionType::getSuggestionsFromIdentite),
    PARLEMENTAIRES_TITULAIRES_LIST("parlementaireTitulaireList", SuggestionType::getSuggestionsFromIdentite),
    PROFILS("Profils", SuggestionType::getSuggestionsForProfils),
    RAPPORTEURS("rapporteurList", SuggestionType::getSuggestionsFromIdentite),
    RUBRIQUE("rubrique", SuggestionType::getSuggestionsForRubrique),
    DEFAULT("", SuggestionType::getDefaultSuggestion);

    private static final int MAX_SUGGESTION = 10;

    private final String value;
    private final Function<SpecificContext, List<SuggestionDTO>> suggestions;

    SuggestionType(String value, Function<SpecificContext, List<SuggestionDTO>> suggestions) {
        this.value = value;
        this.suggestions = suggestions;
    }

    public String getValue() {
        return value;
    }

    public List<SuggestionDTO> getSuggestions(SpecificContext context) {
        return suggestions.apply(context);
    }

    public static SuggestionType fromValue(String value) {
        return Stream
            .of(values())
            .filter(suggestion -> Objects.equals(suggestion.getValue(), value))
            .findFirst()
            .orElse(SuggestionType.DEFAULT);
    }

    private static List<SuggestionDTO> getSuggestionsFromIdentite(SpecificContext context) {
        context.putInContextData(EppContextDataKey.TABLE_REF, SolonEppConstant.IDENTITE_DOC_TYPE);
        return SolonEppActionsServiceLocator.getMetadonneesActionService().getSuggestions(context);
    }

    private static List<SuggestionDTO> getDefaultSuggestion(SpecificContext context) {
        return new ArrayList<>();
    }

    private static List<SuggestionDTO> getSuggestionsForGroupeParlementaire(SpecificContext context) {
        context.putInContextData(EppContextDataKey.TYPE_ORGANISME, "GROUPE_PARLEMENTAIRE");
        return getSuggestionsFromOrganisme(context);
    }

    private static List<SuggestionDTO> getSuggestionsFromAuditionOrganisme(SpecificContext context) {
        context.putInContextData(EppContextDataKey.TYPE_ORGANISME, "AUDITION");
        return getSuggestionsFromOrganisme(context);
    }

    private static List<SuggestionDTO> getSuggestionsFromOrganisme(SpecificContext context) {
        context.putInContextData(EppContextDataKey.TABLE_REF, SolonEppConstant.ORGANISME_DOC_TYPE);
        return SolonEppActionsServiceLocator.getMetadonneesActionService().getSuggestions(context);
    }

    private static List<SuggestionDTO> getSuggestionsForRubrique(SpecificContext context) {
        String input = context.getFromContextData(EppContextDataKey.INPUT);
        String emetteur = context.getFromContextData(EppContextDataKey.EMETTEUR);
        return StringUtils.isNotBlank(emetteur)
            ? SolonEppUIServiceLocator
                .getSelectValueUIService()
                .getAllRubriquesForEmetteur(InstitutionsEnum.valueOf(emetteur))
                .stream()
                .map(selectValue -> new SuggestionDTO(selectValue.getKey(), selectValue.getValue()))
                .sorted(
                    Comparator.<SuggestionDTO, Boolean>comparing(
                        suggestion -> !suggestion.getLabel().toLowerCase().contains(input.toLowerCase())
                    )
                )
                .collect(Collectors.toList())
            : Collections.emptyList();
    }

    private static List<SuggestionDTO> getSuggestionsForProfils(SpecificContext context) {
        return STServiceLocator
            .getSTDirectoryService()
            .getSuggestions(
                context.getFromContextData(EppContextDataKey.INPUT),
                STConstant.ORGANIGRAMME_PROFILE_DIR,
                "groupname"
            )
            .stream()
            .filter(profil -> !"administrators".equals(profil))
            .map(profil -> new SuggestionDTO(profil, profil))
            .limit(MAX_SUGGESTION)
            .collect(Collectors.toList());
    }
}
