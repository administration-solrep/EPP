package fr.dila.st.core.ufnxql.functions;

import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.DocumentData;
import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.Field;
import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.QueryBuilder;
import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.functions.TestAclFunction;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.query.QueryParseException;
import org.nuxeo.ecm.core.query.sql.model.Function;
import org.nuxeo.ecm.core.query.sql.model.Reference;
import org.nuxeo.ecm.core.query.sql.model.StringLiteral;
import org.nuxeo.ecm.core.storage.sql.Model;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.Join;

/**
 * Fonction pour tester les acls pour les applications Solon
 *
 * testAcl(id) = 1,
 *   test l'acces en lecture sur le document avec l'id id
 *
 * testAcl(id, 'READ') = 1,
 *
 *
 */
public class SolonTestAclFunction extends TestAclFunction {

    public SolonTestAclFunction() {
        super();
    }

    @Override
    public void visitFunction(final QueryBuilder queryBuilder, final Function func) {
        if (
            !(
                (
                    func.args.size() == 1 &&
                    func.args.get(0) instanceof Reference ||
                    func.args.size() == 2 &&
                    func.args.get(0) instanceof Reference &&
                    func.args.get(1) instanceof StringLiteral
                ) &&
                queryBuilder.isInWhere()
            ) &&
            func.args.size() == 1 &&
            func.args.get(0) instanceof Reference &&
            queryBuilder.isInWhere()
        ) {
            String functionName = getName();
            throw new QueryParseException(
                String.format(
                    "function %s expects one or two argument : %s(id) or %s(id, 'comma separated permissions')",
                    functionName,
                    functionName,
                    functionName
                )
            );
        }

        if (queryBuilder.getQueryFilter().getPrincipals() != null) {
            Serializable paramPrincipals = queryBuilder.getQueryFilter().getPrincipals();

            if (skipTestAcl(Arrays.asList((String[]) paramPrincipals))) {
                queryBuilder.getWherePart().append(QueryBuilder.DUMMY_EXPRESSION);
                return;
            }

            Serializable paramPermissions = queryBuilder.getQueryFilter().getPermissions();

            if (!queryBuilder.getSqlInfo().dialect.supportsArrays()) {
                paramPrincipals = StringUtils.join((String[]) paramPrincipals, '|');
                paramPermissions = StringUtils.join((String[]) paramPermissions, '|');
            }

            final Reference ref = (Reference) func.args.get(0);
            final Field field = queryBuilder.getFieldMap().get(ref.name);
            final String id = field.toSql();
            final String prefix = field.getPrefix();

            if (queryBuilder.getSqlInfo().dialect.supportsReadAcl() && func.args.size() == 1) {
                /* optimized read acl */
                final String readAclTableAlias = "HRACL" + prefix;

                queryBuilder
                    .getWherePart()
                    .append(
                        getReadAclsCheckSqlForOracle(
                            readAclTableAlias + "." + Model.HIER_READ_ACL_ACL_ID,
                            (String[]) paramPrincipals
                        )
                    );
                List<String> listPrincipals = Arrays.asList((String[]) paramPrincipals);
                addWhereParams(queryBuilder, listPrincipals);

                final Join securityJoinReadAcl = new Join(
                    Join.INNER,
                    Model.HIER_READ_ACL_TABLE_NAME,
                    readAclTableAlias,
                    null,
                    id,
                    readAclTableAlias + ".id"
                );

                final DocumentData docData = queryBuilder.getDocumentDataMap().get(field.getPrefix());
                docData.getJoins().add(securityJoinReadAcl);
            } else if (func.args.size() == 2) {
                queryBuilder.getWherePart().append(queryBuilder.getSqlInfo().dialect.getSecurityCheckSql(id));
                queryBuilder.getWhereParams().add(paramPrincipals);

                final String[] params = ((StringLiteral) func.args.get(1)).asString().replace(" ", "").split(",");

                if (!queryBuilder.getSqlInfo().dialect.supportsArrays()) {
                    paramPermissions = StringUtils.join(params, '|');
                } else {
                    paramPermissions = params;
                }

                queryBuilder.getWhereParams().add(paramPermissions);
            } else {
                queryBuilder.getWherePart().append(queryBuilder.getSqlInfo().dialect.getSecurityCheckSql(id));
                queryBuilder.getWhereParams().add(paramPrincipals);
                queryBuilder.getWhereParams().add(paramPermissions);
            }
        } else {
            queryBuilder.getWherePart().append(QueryBuilder.DUMMY_EXPRESSION);
        }
    }

    protected boolean skipTestAcl(List<String> paramPrincipals) { // NOSONAR parameter used in override
        return false;
    }

    protected String getReadAclsCheckSqlForOracle(String idColumnName, String[] principals) {
        String questionMarks = String.join(",", Collections.nCopies(principals.length, "?"));
        String queryAclsFmt =
            "EXISTS (select /*+ NL_SJ */ a.acl_id FROM %s a WHERE a.acl_id = %s AND a.USERGROUP IN (%s))";
        return String.format(queryAclsFmt, "SW_ACLR_USER_ACLID", idColumnName, questionMarks);
    }

    protected void addWhereParams(final QueryBuilder queryBuilder, List<String> listPrincipals) {
        queryBuilder.getWhereParams().addAll(listPrincipals);
    }
}
