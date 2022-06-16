package fr.dila.ss.core.service;

import static fr.dila.st.api.constant.STSchemaConstant.USER_DATE_DERNIERE_CONNEXION;
import static fr.dila.st.api.constant.STSchemaConstant.USER_FIRST_NAME;
import static fr.dila.st.api.constant.STSchemaConstant.USER_IS_LOGOUT;
import static fr.dila.st.api.constant.STSchemaConstant.USER_LAST_NAME;
import static fr.dila.st.api.constant.STSchemaConstant.USER_USERNAME;
import static fr.dila.st.core.service.STServiceLocator.getUserManager;
import static fr.dila.st.core.util.ObjectHelper.requireNonNullElseGet;
import static java.util.Arrays.asList;
import static java.util.Comparator.nullsLast;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections4.ComparatorUtils.chainedComparator;
import static org.nuxeo.ecm.core.query.sql.model.Operator.OR;

import com.google.common.collect.ImmutableMap;
import fr.dila.ss.api.service.SSUserService;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STUserServiceImpl;
import fr.dila.st.core.util.DateUtil;
import java.text.Collator;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.nuxeo.ecm.core.api.SortInfo;
import org.nuxeo.ecm.core.query.sql.model.MultiExpression;
import org.nuxeo.ecm.core.query.sql.model.OrderByExpr;
import org.nuxeo.ecm.core.query.sql.model.Predicate;
import org.nuxeo.ecm.core.query.sql.model.Predicates;
import org.nuxeo.ecm.core.query.sql.model.Reference;
import org.nuxeo.ecm.platform.usermanager.UserManager;

public class SSUserServiceImpl extends STUserServiceImpl implements SSUserService {
    private static final long serialVersionUID = 5812529885871879740L;

    private static final STLogger LOG = STLogFactory.getLog(SSUserServiceImpl.class);

    @Override
    public List<STUser> getAllUserConnected(List<SortInfo> sortInfos) {
        List<OrderByExpr> orderExps = sortInfos
            .stream()
            .map(sort -> new OrderByExpr(new Reference(sort.getSortColumn()), !sort.getSortAscending()))
            .collect(Collectors.toList());

        return getActiveUsers(orderExps, Predicates.eq(USER_IS_LOGOUT, false));
    }

    @Override
    public List<STUser> getAllUserConnected() {
        return getActiveUsers(Predicates.eq(USER_IS_LOGOUT, false));
    }

    @Override
    public List<STUser> setLogoutTrueForAllUsers() {
        List<STUser> users = getAllUserConnected();
        users.forEach(u -> setLogoutTrue(u, getUserManager()));
        return users;
    }

    private void setLogoutTrue(STUser user, UserManager userManager) {
        user.setLogout(true);
        userManager.updateUser(user.getDocument());
        LOG.info(STLogEnumImpl.UPDATE_USER_TEC, user.getUsername());
    }

    public List<STUser> getListUserNotConnectedSince(final Date dateDeConnexion) {
        Predicate isNull = Predicates.isnull(USER_DATE_DERNIERE_CONNEXION);
        Predicate ltDate = Predicates.lt(
            USER_DATE_DERNIERE_CONNEXION,
            DateUtil.toCalendarFromNotNullDate(dateDeConnexion)
        );
        return getActiveUsers(new MultiExpression(OR, asList(isNull, ltDate)));
    }

    @Override
    public Collection<STUser> getListUserNotConnectedSince(final Date dateDeConnexion, List<SortInfo> sortInfos) {
        List<STUser> activeUsersSince = getListUserNotConnectedSince(dateDeConnexion);

        // Sort users
        if (CollectionUtils.isNotEmpty(sortInfos)) {
            List<Comparator<STUser>> comps = sortInfos
                .stream()
                .map(this::getComparator)
                .filter(Objects::nonNull)
                .collect(toList());
            Collections.sort(activeUsersSince, chainedComparator(comps));
        }

        return activeUsersSince;
    }

    private Comparator<STUser> getComparator(SortInfo sortInfo) {
        return Optional
            .ofNullable(SortComparators.COMPARATORS.get(sortInfo.getSortColumn()))
            .map(comp -> sortInfo.getSortAscending() ? comp : comp.reversed())
            .orElse(null);
    }

    private static final class SortComparators {
        private static final Comparator<STUser> USERNAME_COMPARATOR = stringComp(STUser::getUsername);
        private static final Comparator<STUser> FIRST_NAME_COMPARATOR = stringComp(STUser::getFirstName);
        private static final Comparator<STUser> LAST_NAME_COMPARATOR = stringComp(STUser::getLastName);
        private static final Comparator<STUser> DATE_CONNECTION_COMPARATOR = comp(
            STUser::getDateDerniereConnexion,
            null
        );

        private static Comparator<STUser> stringComp(Function<STUser, String> func) {
            Collator collator = Collator.getInstance();
            collator.setStrength(Collator.NO_DECOMPOSITION); // ignore accents
            return comp(func, collator::compare);
        }

        private static <T extends Comparable<T>> Comparator<STUser> comp(Function<STUser, T> func, Comparator<T> comp) {
            Comparator<T> comparator = requireNonNullElseGet(comp, Comparator::naturalOrder);
            return (u1, u2) -> Objects.compare(func.apply(u1), func.apply(u2), nullsLast(comparator));
        }

        private static final Map<String, Comparator<STUser>> COMPARATORS = ImmutableMap.of(
            USER_USERNAME,
            USERNAME_COMPARATOR,
            USER_FIRST_NAME,
            FIRST_NAME_COMPARATOR,
            USER_LAST_NAME,
            LAST_NAME_COMPARATOR,
            USER_DATE_DERNIERE_CONNEXION,
            DATE_CONNECTION_COMPARATOR
        );
    }
}
