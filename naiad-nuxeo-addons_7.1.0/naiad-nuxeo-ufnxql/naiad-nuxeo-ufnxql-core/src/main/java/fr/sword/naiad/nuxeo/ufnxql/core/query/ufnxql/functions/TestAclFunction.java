package fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.functions;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.query.QueryParseException;
import org.nuxeo.ecm.core.query.sql.model.Expression;
import org.nuxeo.ecm.core.query.sql.model.Function;
import org.nuxeo.ecm.core.query.sql.model.IntegerLiteral;
import org.nuxeo.ecm.core.query.sql.model.Operator;
import org.nuxeo.ecm.core.query.sql.model.Reference;
import org.nuxeo.ecm.core.query.sql.model.StringLiteral;
import org.nuxeo.ecm.core.storage.sql.Model;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.Join;

import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.DocumentData;
import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.Field;
import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.QueryBuilder;
import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.UFNXQLConstants;

/**
 * Fonction pour tester les acls
 * 
 * testAcl(id) = 1,
 *   test l'acces en lecture sur le document avec l'id id
 *  
 * testAcl(id, 'READ') = 1,
 * 
 * 
 */
public class TestAclFunction implements UfnxqlFunction {

    /**
     * Default constructor
     */
    public TestAclFunction() {
        // do nothing
    }

    @Override
    public String getName() {
        return UFNXQLConstants.TEST_ACL_FUNCTION;
    }

    @Override
    public String getExpression() {
        return UFNXQLConstants.TEST_ACL_FUNCTION + "(id) = 1 or " + UFNXQLConstants.TEST_ACL_FUNCTION
                + "(id, 'comma separated permissions') = 1";
    }

    @Override
    public boolean checkExpression(final QueryBuilder queryBuilder, final Expression expr) {
        return Operator.EQ.equals(expr.operator) && expr.rvalue instanceof IntegerLiteral
                && ((IntegerLiteral) expr.rvalue).value == 1;
    }

    @Override
    public void visitFunction(final QueryBuilder queryBuilder, final Function func) {
        if (!((func.args.size() == 1 && func.args.get(0) instanceof Reference || func.args.size() == 2
                && func.args.get(0) instanceof Reference && func.args.get(1) instanceof StringLiteral) && queryBuilder
                    .isInWhere())) {
            if (func.args.size() == 1 && func.args.get(0) instanceof Reference && queryBuilder.isInWhere()) {
                throw new QueryParseException(
                        "function testAcl expects one or two argument : testAcl(id) or testAcl(id, 'comma separated permissions')");
            }
        }

        if (queryBuilder.getQueryFilter().getPrincipals() != null) {

            Serializable paramPrincipals = queryBuilder.getQueryFilter().getPrincipals();
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
                final String aclUserMapAlias = "ACLUSERMAP" + prefix;

                queryBuilder.getWherePart().append(
                        queryBuilder.getSqlInfo().dialect.getReadAclsCheckSql(aclUserMapAlias + "."
                                + Model.ACLR_USER_MAP_USER_ID));
                queryBuilder.getWhereParams().add(paramPrincipals);

                final Join securityJoinReadAcl = new Join(Join.INNER, Model.HIER_READ_ACL_TABLE_NAME,
                        readAclTableAlias, null, id, readAclTableAlias + ".id");

                final Join securityJoinAclUserMap = new Join(Join.INNER, Model.ACLR_USER_MAP_TABLE_NAME,
                        aclUserMapAlias, null, readAclTableAlias + "." + Model.HIER_READ_ACL_ACL_ID, aclUserMapAlias
                                + "." + Model.ACLR_USER_MAP_ACL_ID);

                final DocumentData docData = queryBuilder.getDocumentDataMap().get(field.getPrefix());
                docData.getJoins().add(securityJoinReadAcl);
                docData.getJoins().add(securityJoinAclUserMap);

            } else if (func.args.size() == 2) {
                queryBuilder.getWherePart().append(queryBuilder.getSqlInfo().dialect.getSecurityCheckSql(id));
                queryBuilder.getWhereParams().add(paramPrincipals);

                final String[] params = ((StringLiteral) func.args.get(1)).asString().replaceAll(" ", "").split(",");

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

}
