package fr.dila.st.core.requeteur;

import static fr.dila.st.api.constant.STSchemaConstant.USER_DATE_DEBUT;
import static fr.dila.st.api.constant.STSchemaConstant.USER_DATE_FIN;
import static fr.dila.st.api.constant.STSchemaConstant.USER_DELETED;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.joining;
import static org.nuxeo.ecm.core.query.sql.model.DateLiteral.dateTimeFormatter;

import com.google.common.collect.Lists;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.api.service.organigramme.STPostesService;
import fr.dila.st.api.service.organigramme.STUsAndDirectionService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.ObjectHelper;
import fr.dila.st.core.util.SolonDateConverter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.SortInfo;
import org.nuxeo.ecm.core.query.sql.model.Expression;
import org.nuxeo.ecm.core.query.sql.model.Literal;
import org.nuxeo.ecm.core.query.sql.model.MultiExpression;
import org.nuxeo.ecm.core.query.sql.model.Operand;
import org.nuxeo.ecm.core.query.sql.model.Operator;
import org.nuxeo.ecm.core.query.sql.model.OrderByExpr;
import org.nuxeo.ecm.core.query.sql.model.Predicate;
import org.nuxeo.ecm.core.query.sql.model.Predicates;
import org.nuxeo.ecm.core.query.sql.model.QueryBuilder;
import org.nuxeo.ecm.core.query.sql.model.Reference;
import org.nuxeo.ecm.platform.usermanager.UserManager;

/**
 * Le requeteur de recherche des utilisateurs.
 *
 * @author olejacques
 *
 */

public class RequeteurRechercheUtilisateurs {
    private static final String QUERY_ET = " ET ";

    private static final String ID_USER_POSTE = "user.poste";
    private static final String ID_USER_DIRECTION = "user.direction";
    private static final String ID_USER_MINISTERE = "user.ministere";
    private static final String ID_USER_PROFILS = "user.profils";

    private QueryBuilder queryBuilder;
    private String query;

    private List<OrderByExpr> orders = Arrays.asList(
        new OrderByExpr(new Reference(STSchemaConstant.USER_LAST_NAME), false),
        new OrderByExpr(new Reference(STSchemaConstant.USER_FIRST_NAME), false),
        new OrderByExpr(new Reference(STSchemaConstant.USER_USERNAME), false)
    );

    private List<String> directions = Collections.emptyList();
    private List<String> groups = Collections.emptyList();
    private List<String> ministeres = Collections.emptyList();
    private List<String> postes = Collections.emptyList();

    private Supplier<Set<String>> directionsUsernamesSupplier = () -> {
        STUsAndDirectionService usService = STServiceLocator.getSTUsAndDirectionService();
        return directions
            .stream()
            .flatMap(d -> usService.getUserFromUniteStructurelle(d).stream())
            .map(STUser::getUsername)
            .collect(Collectors.toSet());
    };

    private Supplier<Set<String>> groupsUsernamesSupplier = () -> {
        UserManager userManager = getUserManager();
        return groups.stream().flatMap(g -> userManager.getUsersInGroup(g).stream()).collect(Collectors.toSet());
    };

    private Supplier<Set<String>> ministeresUsernamesSupplier = () -> {
        STMinisteresService ministeresService = STServiceLocator.getSTMinisteresService();
        return ministeres
            .stream()
            .flatMap(m -> ministeresService.getUserFromMinistere(m).stream())
            .map(STUser::getUsername)
            .collect(Collectors.toSet());
    };

    private Supplier<Set<String>> postesUsernamesSupplier = () -> {
        STPostesService stPostesService = STServiceLocator.getSTPostesService();
        return postes
            .stream()
            .flatMap(p -> stPostesService.getUserFromPoste(p).stream())
            .map(STUser::getUsername)
            .collect(Collectors.toSet());
    };

    private List<Consumer<Map<String, DocumentModel>>> listFilters = Arrays.asList(
        mapUsersByUsername -> filterMapUsersByUsername(directions, mapUsersByUsername, directionsUsernamesSupplier),
        mapUsersByUsername -> filterMapUsersByUsername(groups, mapUsersByUsername, groupsUsernamesSupplier),
        mapUsersByUsername -> filterMapUsersByUsername(ministeres, mapUsersByUsername, ministeresUsernamesSupplier),
        mapUsersByUsername -> filterMapUsersByUsername(postes, mapUsersByUsername, postesUsernamesSupplier)
    );

    private RequeteurRechercheUtilisateurs() {
        // Instancier le requeteur avec le builder
    }

    protected UserManager getUserManager() {
        return STServiceLocator.getUserManager();
    }

    public List<DocumentModel> execute() {
        queryBuilder.orders(orders);
        DocumentModelList foundUsers = STServiceLocator.getUserManager().searchUsers(queryBuilder);
        Map<String, DocumentModel> foundUsersByUsernames = foundUsers
            .stream()
            .collect(Collectors.toMap(doc -> doc.getAdapter(STUser.class).getUsername(), identity()));
        listFilters.forEach(filter -> filter.accept(foundUsersByUsernames));

        foundUsers.retainAll(foundUsersByUsernames.values());
        return foundUsers;
    }

    private void filterMapUsersByUsername(
        List<String> collection,
        Map<String, DocumentModel> mapUsersByUsername,
        Supplier<Set<String>> usernamesSupplier
    ) {
        if (!collection.isEmpty() && !mapUsersByUsername.isEmpty()) {
            CollectionUtils
                .disjunction(usernamesSupplier.get(), mapUsersByUsername.keySet())
                .forEach(mapUsersByUsername::remove);
        }
    }

    public static class Builder {
        private String username;
        private String firstName;
        private String lastName;
        private String email;
        private String telephoneNumber;
        private List<String> ministeres = Collections.emptyList();
        private List<String> postes = Collections.emptyList();
        private List<String> groups = Collections.emptyList();
        private List<String> directions = Collections.emptyList();
        private Date dateDebut;
        private Date dateFin;
        private Date dateDebutMax;
        private Date dateFinMax;
        private String postalAddress;
        private String postalCode;
        private String locality;
        private String query;
        private Boolean isLogout;

        private List<SortInfo> sortInfos = Collections.emptyList();

        public Builder() {
            // Default constructor
        }

        public Builder username(String val) {
            username = val;
            return this;
        }

        public Builder firstName(String val) {
            firstName = val;
            return this;
        }

        public Builder lastName(String val) {
            lastName = val;
            return this;
        }

        public Builder email(String val) {
            email = val;
            return this;
        }

        public Builder telephoneNumber(String val) {
            telephoneNumber = val;
            return this;
        }

        public Builder ministeres(List<String> val) {
            ministeres = ObjectHelper.requireNonNullElseGet(val, Collections::emptyList);
            return this;
        }

        public Builder postes(List<String> val) {
            postes = ObjectHelper.requireNonNullElseGet(val, Collections::emptyList);
            return this;
        }

        public Builder groups(List<String> val) {
            groups = ObjectHelper.requireNonNullElseGet(val, Collections::emptyList);
            return this;
        }

        public Builder directions(List<String> val) {
            directions = ObjectHelper.requireNonNullElseGet(val, Collections::emptyList);
            return this;
        }

        public Builder dateDebut(Date val) {
            dateDebut = val;
            return this;
        }

        public Builder dateDebut(String val) {
            if (StringUtils.isNotBlank(val)) {
                dateDebut = SolonDateConverter.DATE_SLASH.parseToDate(val);
            }
            return this;
        }

        public Builder dateFin(Date val) {
            dateFin = val;
            return this;
        }

        public Builder dateFin(String val) {
            if (StringUtils.isNotBlank(val)) {
                dateFin = SolonDateConverter.DATE_SLASH.parseToDate(val);
            }
            return this;
        }

        public Builder dateDebutMax(Date val) {
            dateDebutMax = val;
            return this;
        }

        public Builder dateDebutMax(String val) {
            if (StringUtils.isNotBlank(val)) {
                dateDebutMax = SolonDateConverter.DATE_SLASH.parseToDate(val);
            }
            return this;
        }

        public Builder dateFinMax(String val) {
            if (StringUtils.isNotBlank(val)) {
                dateFinMax = SolonDateConverter.DATE_SLASH.parseToDate(val);
            }
            return this;
        }

        public Builder dateFinMax(Date val) {
            dateFinMax = val;
            return this;
        }

        public Builder postalAddress(String val) {
            postalAddress = val;
            return this;
        }

        public Builder postalCode(String val) {
            postalCode = val;
            return this;
        }

        public Builder locality(String val) {
            locality = val;
            return this;
        }

        public Builder sortInfos(List<SortInfo> val) {
            sortInfos = ObjectHelper.requireNonNullElseGet(val, Collections::emptyList);
            return this;
        }

        public Builder query(String query) {
            this.query = query;
            return this;
        }

        public Builder isLogout(Boolean isLogout) {
            this.isLogout = isLogout;
            return this;
        }

        public RequeteurRechercheUtilisateurs build() {
            return new RequeteurRechercheUtilisateurs(this);
        }
    }

    private static String replaceWildcards(String value) {
        return value.replace("%", "_").replace("*", "%");
    }

    private RequeteurRechercheUtilisateurs(Builder builder) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(Predicates.eq(USER_DELETED, STConstant.FALSE));

        buildFromQuery(builder, predicates);

        addPredicateWithNotBlankCondition(
            builder.username,
            predicates,
            () -> Predicates.ilike(STSchemaConstant.USER_USERNAME, replaceWildcards(builder.username))
        );
        addPredicateWithNotBlankCondition(
            builder.firstName,
            predicates,
            () -> Predicates.ilike(STSchemaConstant.USER_FIRST_NAME, replaceWildcards(builder.firstName))
        );
        addPredicateWithNotBlankCondition(
            builder.lastName,
            predicates,
            () -> Predicates.ilike(STSchemaConstant.USER_LAST_NAME, replaceWildcards(builder.lastName))
        );
        addPredicateWithNotBlankCondition(
            builder.postalAddress,
            predicates,
            () -> Predicates.ilike(STSchemaConstant.USER_POSTAL_ADRESS, builder.postalAddress)
        );
        addPredicateWithNotBlankCondition(
            builder.postalCode,
            predicates,
            () -> Predicates.ilike(STSchemaConstant.USER_POSTAL_CODE, builder.postalCode)
        );
        addPredicateWithNotBlankCondition(
            builder.locality,
            predicates,
            () -> Predicates.ilike(STSchemaConstant.USER_LOCALITY, builder.locality)
        );
        addPredicateWithNotBlankCondition(
            builder.telephoneNumber,
            predicates,
            () -> Predicates.ilike(STSchemaConstant.USER_TELEPHONE, replaceWildcards(builder.telephoneNumber))
        );
        addPredicateWithNotBlankCondition(
            builder.email,
            predicates,
            () -> Predicates.ilike(STSchemaConstant.USER_EMAIL, replaceWildcards(builder.email))
        );
        addPredicateWithNonNullCondition(
            builder.dateDebut,
            predicates,
            () -> Predicates.gte(STSchemaConstant.USER_DATE_DEBUT, builder.dateDebut)
        );
        addPredicateWithNonNullCondition(
            builder.dateDebutMax,
            predicates,
            () -> Predicates.lte(STSchemaConstant.USER_DATE_DEBUT, builder.dateDebutMax)
        );
        addPredicateWithNonNullCondition(
            builder.dateFin,
            predicates,
            () -> Predicates.gte(STSchemaConstant.USER_DATE_FIN, builder.dateFin)
        );
        addPredicateWithNonNullCondition(
            builder.dateFinMax,
            predicates,
            () -> Predicates.lte(STSchemaConstant.USER_DATE_FIN, builder.dateFinMax)
        );
        addPredicateWithNonNullCondition(
            builder.isLogout,
            predicates,
            () -> Predicates.eq(STSchemaConstant.USER_IS_LOGOUT, builder.isLogout)
        );

        if (!builder.sortInfos.isEmpty()) {
            orders =
                builder
                    .sortInfos.stream()
                    .map(s -> new OrderByExpr(new Reference(s.getSortColumn()), !s.getSortAscending()))
                    .collect(Collectors.toList());
        }

        queryBuilder = new QueryBuilder();
        queryBuilder.predicate(new MultiExpression(Operator.AND, predicates));
        buildQuery(predicates);

        directions = filterWithNotBlankValues(builder.directions.stream());
        addToQuery(ID_USER_DIRECTION, directions);
        groups = filterWithNotBlankValues(builder.groups.stream());
        addToQuery(ID_USER_PROFILS, groups);
        ministeres = filterWithNotBlankValues(builder.ministeres.stream());
        addToQuery(ID_USER_MINISTERE, ministeres);
        postes = filterWithNotBlankValues(builder.postes.stream());
        addToQuery(ID_USER_POSTE, postes);
    }

    private static List<String> filterWithNotBlankValues(Stream<String> streamToFilter) {
        return streamToFilter.filter(StringUtils::isNotBlank).collect(Collectors.toList());
    }

    private void addToQuery(String lvalue, List<String> list) {
        if (!list.isEmpty()) {
            query += QUERY_ET + lvalue + "=" + String.join(",", list);
        }
    }

    private void buildQuery(List<Predicate> predicates) {
        query =
            predicates.stream().map(this::convertPredicateToString).filter(Objects::nonNull).collect(joining(QUERY_ET));
    }

    private String convertPredicateToString(Predicate pred) {
        if (USER_DELETED.equals(pred.lvalue.toString())) {
            return null; // don't store the deleted predicate
        }
        return predicateToString(pred);
    }

    /**
     * Override of {@link Expression#toString()} to call
     * {@link Literal#toString()} of the predicate right value
     *
     * @param pred
     *            the predicate
     * @return the predicate as String
     */
    private String predicateToString(Predicate pred) {
        Operand rvalue = pred.rvalue;
        if (rvalue == null) {
            if (pred.isSuffix()) {
                return pred.lvalue.toString() + ' ' + pred.operator.toString();
            } else {
                return pred.operator.toString() + ' ' + pred.lvalue.toString();
            }
        } else {
            String rvalueStr = rvalue.toString();
            if (rvalue instanceof Literal) {
                rvalueStr = ((Literal) rvalue).asString();
            }
            return pred.lvalue.toString() + ' ' + pred.operator.toString() + ' ' + rvalueStr;
        }
    }

    /**
     * Transformation du requeteur en {@link String} pour le stocker dans les favoris de recherche
     *
     * @return le user requeteur en String
     */
    public String getQuery() {
        return query;
    }

    private <T> void addPredicateIfCondition(
        java.util.function.Predicate<T> condition,
        T val,
        List<Predicate> predicates,
        Supplier<Predicate> predicate
    ) {
        if (condition.test(val)) {
            predicates.add(predicate.get());
        }
    }

    private <T> void addPredicateWithNonNullCondition(
        T val,
        List<Predicate> predicates,
        Supplier<Predicate> predicate
    ) {
        addPredicateIfCondition(Objects::nonNull, val, predicates, predicate);
    }

    private void addPredicateWithNotBlankCondition(
        String val,
        List<Predicate> predicates,
        Supplier<Predicate> predicate
    ) {
        addPredicateIfCondition(StringUtils::isNotBlank, val, predicates, predicate);
    }

    private void buildFromQuery(Builder builder, List<Predicate> predicates) {
        String queryString = builder.query;
        if (queryString == null) {
            return;
        }
        // Le but est de séparer chaque bout de requete avec les différents test, celui qui est bon donne donc 2
        // éléments, les autres ne faisant aucune séparation
        Stream
            .of(queryString.split(QUERY_ET))
            .filter(StringUtils::isNotEmpty)
            .map(condition -> toPredicate(condition, builder))
            .filter(Objects::nonNull)
            .forEach(predicates::add);
    }

    /**
     * {@link String} to {@link Predicate} mapper
     *
     * @param condition where clause condition as String
     * @param builder {@link Builder}
     * @return {@link Predicate}
     */
    private Predicate toPredicate(String condition, Builder builder) {
        String[] elem = condition.split(Operator.GTEQ.toString()); // >=
        if (elem.length == 2) {
            String elem0 = elem[0].trim();
            String elem1 = elem[1].trim();
            // on traite la date qui est cencé être le seul cas avec des >=
            if (USER_DATE_DEBUT.equals(elem0) || USER_DATE_FIN.equals(elem0)) {
                return Predicates.gte(elem0, dateTimeFormatter.parseDateTime(elem1));
            }
        }
        elem = condition.split(Operator.LTEQ.toString()); // <=
        if (elem.length == 2) {
            // idem, seul un des champs date est cencé passé ici, on
            // ignore donc les autres
            String elem0 = elem[0].trim();
            String elem1 = elem[1].trim();
            if (USER_DATE_DEBUT.equals(elem0) || USER_DATE_FIN.equals(elem0)) {
                return Predicates.lte(elem0, dateTimeFormatter.parseDateTime(elem1));
            }
        }
        elem = condition.split(Operator.EQ.toString()); // =
        if (elem.length != 2) {
            elem = condition.split(Operator.ILIKE.toString());
        }
        if (elem.length == 2) {
            String elem0 = elem[0].trim();
            String elem1 = elem[1].trim();
            if (USER_DELETED.equals(elem0)) {
                // le predicate est déjà dans la liste
            } else if (ID_USER_MINISTERE.equals(elem0)) {
                builder.ministeres = toList(elem1);
            } else if (ID_USER_DIRECTION.equals(elem0)) {
                builder.directions = toList(elem1);
            } else if (ID_USER_POSTE.equals(elem0)) {
                builder.postes = toList(elem1);
            } else if (ID_USER_PROFILS.equals(elem0)) {
                builder.groups = toList(elem1);
            } else {
                return Predicates.ilike(elem0, elem1);
            }
        }
        return null;
    }

    private List<String> toList(String str) {
        return Lists.newArrayList(str.split(","));
    }
}
