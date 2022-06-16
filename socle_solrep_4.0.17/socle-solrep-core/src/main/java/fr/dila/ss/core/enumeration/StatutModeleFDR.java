package fr.dila.ss.core.enumeration;

import static java.util.stream.Collectors.toMap;

import fr.dila.ss.api.criteria.FeuilleRouteCriteria;
import fr.dila.ss.api.feuilleroute.SSFeuilleRoute;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteElement;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteElement.ElementLifeCycleState;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.nuxeo.ecm.core.api.DocumentModel;

public enum StatutModeleFDR {
    VALIDE(
        "modeleFDR.validee",
        FeuilleRouteElement::isValidated,
        criteria -> criteria.setCurrentLifeCycleState(ElementLifeCycleState.validated.name()),
        "icon--check-circle"
    ),
    VALIDATION_DEMANDEE(
        "modeleFDR.validationDemandee",
        SSFeuilleRoute::isDemandeValidation,
        criteria -> criteria.setDemandeValidation(true),
        "icon--stop-circle"
    ),
    BROUILLON(
        "modeleFDR.brouillon",
        FeuilleRouteElement::isDraft,
        criteria -> {
            criteria.setCurrentLifeCycleState(ElementLifeCycleState.draft.name());
            criteria.setDemandeValidation(false);
        },
        "icon--pause-circle"
    );

    private final String value;
    private final Predicate<SSFeuilleRoute> documentPredicate;
    private final Consumer<FeuilleRouteCriteria> updateFeuilleRouteCriteriaConsumer;
    private final String icon;

    StatutModeleFDR(
        String value,
        Predicate<SSFeuilleRoute> documentPredicate,
        Consumer<FeuilleRouteCriteria> updateFeuilleRouteCriteriaConsumer,
        String icon
    ) {
        this.value = value;
        this.documentPredicate = documentPredicate;
        this.updateFeuilleRouteCriteriaConsumer = updateFeuilleRouteCriteriaConsumer;
        this.icon = icon;
    }

    public String getValue() {
        return value;
    }

    public Predicate<SSFeuilleRoute> getDocumentPredicate() {
        return documentPredicate;
    }

    public String getIcon() {
        return icon;
    }

    public static String getStatutFromDoc(DocumentModel doc) {
        SSFeuilleRoute fdr = doc.getAdapter(SSFeuilleRoute.class);
        return Stream
            .of(values())
            .filter(statut -> statut.getDocumentPredicate().test(fdr))
            .findFirst()
            .map(Enum::name)
            .orElse(null);
    }

    public static Map<String, String> getLabelKeys() {
        return Stream
            .of(values())
            .collect(
                toMap(
                    StatutModeleFDR::name,
                    StatutModeleFDR::getValue,
                    (existing, replacing) -> existing,
                    LinkedHashMap::new
                )
            );
    }

    public static StatutModeleFDR fromValue(String name) {
        return Stream.of(values()).filter(statut -> Objects.equals(statut.name(), name)).findFirst().orElse(null);
    }

    public void updateFeuilleRouteCriteria(FeuilleRouteCriteria criteria) {
        updateFeuilleRouteCriteriaConsumer.accept(criteria);
    }
}
