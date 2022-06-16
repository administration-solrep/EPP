package fr.dila.st.ui.services.actions.impl;

import static fr.dila.st.api.constant.STEventConstant.CATEGORY_BORDEREAU;
import static fr.dila.st.api.constant.STEventConstant.CATEGORY_FDD;
import static fr.dila.st.api.constant.STEventConstant.CATEGORY_FEUILLE_ROUTE;
import static fr.dila.st.api.constant.STEventConstant.CATEGORY_JOURNAL;
import static fr.dila.st.api.constant.STEventConstant.CATEGORY_PARAPHEUR;
import static fr.dila.st.core.service.STServiceLocator.getSTPostesService;
import static fr.dila.st.core.service.STServiceLocator.getUserManager;
import static fr.dila.st.core.util.ObjectHelper.requireNonNullElseGet;
import static java.lang.String.format;
import static java.lang.String.join;
import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.SPACE;

import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.security.principal.STPrincipal;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.services.actions.STJournalActionService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class STJournalActionServiceImpl implements STJournalActionService {

    @Override
    public String translate(String entry) {
        return ofNullable(entry)
            .map(s -> asList(s.split(SPACE)))
            .orElseGet(ArrayList::new)
            .stream()
            .map(ResourceHelper::getString)
            .collect(joining(SPACE));
    }

    @Override
    public String getPostesLabels(String idUser) {
        return ofNullable(idUser)
            .map(u -> getUserManager().getPrincipal(u))
            .map(STPrincipal.class::cast)
            .map(p -> requireNonNullElseGet(p.getPosteIdSet(), HashSet<String>::new))
            .map(this::toLabels)
            .map(l -> join(", ", l))
            .orElse(EMPTY);
    }

    private Set<String> toLabels(Set<String> posteIds) {
        return posteIds.stream().map(p -> getSTPostesService().getPoste(p)).map(this::getLabel).collect(toSet());
    }

    private String getLabel(PosteNode p) {
        return ofNullable(p).map(PosteNode::getLabel).orElseGet(() -> format("**poste (%s) inconnu**", p.getId()));
    }

    @Override
    public Set<String> getCategoryList() {
        return Stream
            .of(CATEGORY_FEUILLE_ROUTE, CATEGORY_BORDEREAU, CATEGORY_FDD, CATEGORY_PARAPHEUR, CATEGORY_JOURNAL)
            .collect(Collectors.toSet());
    }
}
