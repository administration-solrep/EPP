package fr.dila.solonepp.core.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.query.sql.model.DateLiteral;
import org.nuxeo.ecm.core.query.sql.model.DoubleLiteral;
import org.nuxeo.ecm.core.query.sql.model.Expression;
import org.nuxeo.ecm.core.query.sql.model.FromClause;
import org.nuxeo.ecm.core.query.sql.model.Function;
import org.nuxeo.ecm.core.query.sql.model.HavingClause;
import org.nuxeo.ecm.core.query.sql.model.IntegerLiteral;
import org.nuxeo.ecm.core.query.sql.model.Literal;
import org.nuxeo.ecm.core.query.sql.model.LiteralList;
import org.nuxeo.ecm.core.query.sql.model.Operator;
import org.nuxeo.ecm.core.query.sql.model.OrderByClause;
import org.nuxeo.ecm.core.query.sql.model.OrderByExpr;
import org.nuxeo.ecm.core.query.sql.model.Reference;
import org.nuxeo.ecm.core.query.sql.model.SQLQuery;
import org.nuxeo.ecm.core.query.sql.model.StringLiteral;
import org.nuxeo.ecm.core.query.sql.model.WhereClause;
import org.nuxeo.ecm.core.storage.sql.jdbc.QueryMaker.QueryMakerException;

import fr.dila.solonepp.api.constant.SolonEppConstant;
import fr.dila.solonepp.api.constant.SolonEppLifecycleConstant;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.service.MailboxInstitutionService;
import fr.dila.solonepp.core.assembler.ws.NiveauLectureCodeAssembler;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.query.ufnxql.ClauseParams;
import fr.dila.st.core.query.ufnxql.parser.SDefaultQueryVisitor;
import fr.dila.st.core.query.ufnxql.parser.SGroupByClause;
import fr.dila.st.core.query.ufnxql.parser.UFNXQLQueryParser;
import fr.dila.st.rest.client.helper.CDataAdapter;
import fr.sword.xsd.solon.epp.EtatEvenement;
import fr.sword.xsd.solon.epp.EtatMessage;
import fr.sword.xsd.solon.epp.Institution;
import fr.sword.xsd.solon.epp.NiveauLectureCode;

/**
 * Parser pour transformer une requete "xsd" en ufnxql
 * 
 * @author admin
 * 
 */
public class XsdToUfnxqlParser {

    
    /**
     * Logger surcouche socle de log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(XsdToUfnxqlParser.class);  

    private static final String XSDTOUFNXQL_PROPERTIES_FILE = "parser/xsdtoufnxql.parser";
    private static final String EVENEMENT_PREFIX = "e.";
    private static final String DOSSIER_PREFIX = "d.";
    private static final String VERSION_PREFIX = "v.";
    private static final String ORGANISME_PREFIX = "o.";
    private static final String IDENTITE_PREFIX = "i.";
    private static final String MANDAT_PREFIX = "man.";
    private static final String PIECE_JOINTE_FICHIER_PREFIX = "f.";
    private static final String PIECE_JOINTE_PREFIX = "p.";
    
    private static Properties props;

    private static enum POS {
        UNDEFINED, IN_SELECT, IN_WHERE, IN_ORDER, IN_GROUP,
    };

    private static void load() throws IOException {
        if (props == null) {
            props = new Properties();
            InputStream in = XsdToUfnxqlParser.class.getClassLoader().getResourceAsStream(XSDTOUFNXQL_PROPERTIES_FILE);
            props.load(in);
        }
    }

    public String parse( String xsdQuery, List<Object> params, CoreSession session, String institutionId) throws ClientException {
        if (params == null) {
            throw new ClientException("La liste de paramètres ne peut pas être nulle.");
        }
    	
        try {
            load();
        } catch (IOException exc) {          
            LOGGER.error(session, STLogEnumImpl.PARSE_XSD_TO_UFNXQL_TEC);
            LOGGER.debug(session, STLogEnumImpl.PARSE_XSD_TO_UFNXQL_TEC, exc);
            throw new ClientException(exc.getMessage());
        }
        

        StringBuilder selectClause = new StringBuilder(" SELECT m.ecm:uuid as id FROM ");
        selectClause.append(SolonEppConstant.MESSAGE_DOC_TYPE);
        selectClause.append(" AS m ");

        StringBuilder whereClause = new StringBuilder(" WHERE ");

        if (StringUtils.isBlank(xsdQuery)) {
            throw new ClientException("La requête ne peut être vide.");
        }
        
        xsdQuery = xsdQuery.replaceAll(" {2,}ILIKE", " ILIKE");
        xsdQuery = xsdQuery.replaceAll("evt:contenu_pj ILIKE", "evt:contenu_pj =");

        String ufnxqlQuery = CDataAdapter.parse(xsdQuery);

        SQLQuery sqlQuery = UFNXQLQueryParser.parse("SELECT * FROM Message WHERE " + ufnxqlQuery);

        XSDQueryBuilder xSDQueryBuilder = new XSDQueryBuilder(parseParameter(params).toArray());
        sqlQuery.accept(xSDQueryBuilder);

        String clause = xSDQueryBuilder.getWhereClause().clause;

        params.clear();
        params.addAll(xSDQueryBuilder.getWhereClause().params);

        List<String> criteriaList = new ArrayList<String>();

        if (xSDQueryBuilder.isJoinDossier()) {
            // jointure sur Dossier
            selectClause.append(", ");
            selectClause.append(SolonEppConstant.DOSSIER_DOC_TYPE);
            selectClause.append(" AS d ");

            StringBuilder sb = new StringBuilder(" e.");
            sb.append(STSchemaConstant.ECM_SCHEMA_PREFIX);
            sb.append(":");
            sb.append(STSchemaConstant.ECM_PARENT_ID_PROPERTY);
            sb.append(" = d.");
            sb.append(STSchemaConstant.ECM_SCHEMA_PREFIX);
            sb.append(":");
            sb.append(STSchemaConstant.ECM_UUID_PROPERTY);

            criteriaList.add(sb.toString());
        }

        if (xSDQueryBuilder.isJoinEvenement()) {
            // jointure sur Evenement
            selectClause.append(", ");
            selectClause.append(SolonEppConstant.EVENEMENT_DOC_TYPE);
            selectClause.append(" AS e ");

            StringBuilder sb = new StringBuilder(" m.");
            sb.append(STSchemaConstant.CASE_LINK_SCHEMA_PREFIX);
            sb.append(":");
            sb.append(STSchemaConstant.CASE_LINK_CASE_DOCUMENT_ID_PROPERTY);
            sb.append(" = e.");
            sb.append(STSchemaConstant.ECM_SCHEMA_PREFIX);
            sb.append(":");
            sb.append(STSchemaConstant.ECM_UUID_PROPERTY);

            criteriaList.add(sb.toString());
        }

        if (xSDQueryBuilder.isJoinVersion()) {
            selectClause.append(", ");
            selectClause.append(SolonEppConstant.VERSION_DOC_TYPE);
            selectClause.append(" AS v ");

            StringBuilder sb = new StringBuilder(" m.");
            sb.append(STSchemaConstant.CASE_LINK_SCHEMA_PREFIX);
            sb.append(":");
            sb.append(SolonEppSchemaConstant.CASE_LINK_ACTIVE_VERSION_ID_PROPERTY);
            sb.append(" = v.");
            sb.append(STSchemaConstant.ECM_SCHEMA_PREFIX);
            sb.append(":");
            sb.append(STSchemaConstant.ECM_UUID_PROPERTY);

            criteriaList.add(sb.toString());
        }

          if (xSDQueryBuilder.isJoinjoinPiece()) {
            selectClause.append(", ");
            selectClause.append(SolonEppConstant.PIECE_JOINTE_DOC_TYPE);
            selectClause.append(" AS p ");
        }
        if (xSDQueryBuilder.isJoinjoinPieceFichier()) {
            selectClause.append(", ");
            selectClause.append(SolonEppConstant.PIECE_JOINTE_FICHIER_DOC_TYPE);
            selectClause.append(" AS f ");
    }
       
        if (xSDQueryBuilder.isJoinMandat()) {
            selectClause.append(", ");
            selectClause.append(SolonEppConstant.MANDAT_DOC_TYPE);
            selectClause.append(" AS man ");

        }

        if (xSDQueryBuilder.isJoinOrganisme()) {
            selectClause.append(", ");
            selectClause.append(SolonEppConstant.ORGANISME_DOC_TYPE);
            selectClause.append(" AS o ");

        }

        if (xSDQueryBuilder.isJoinIdentite()) {
            selectClause.append(", ");
            selectClause.append(SolonEppConstant.IDENTITE_DOC_TYPE);
            selectClause.append(" AS i ");

        }

        whereClause.append(clause);

        if (StringUtils.isNotBlank(ufnxqlQuery)) {
            whereClause.append(" AND ");
        }

        // pas de test direct sur les ACLs ça sert a rien, le parent du message est la mailbox de l'institution
        final MailboxInstitutionService mailboxInstitutionService = SolonEppServiceLocator.getMailboxInstitutionService();
        DocumentModel mailboxDoc = mailboxInstitutionService.getMailboxInstitution(session, institutionId);

        StringBuilder sb = new StringBuilder(" m.");
        sb.append(STSchemaConstant.ECM_SCHEMA_PREFIX);
        sb.append(":");
        sb.append(STSchemaConstant.ECM_PARENT_ID_PROPERTY);
        sb.append(" = ? ");

        criteriaList.add(sb.toString());
        params.add(mailboxDoc.getId());

        whereClause.append(StringUtils.join(criteriaList, " AND "));

        String orderClause = xSDQueryBuilder.getOrder();
        if (StringUtils.isNotBlank(orderClause)) {

            // replacement de "destinataireCopie" par sa denormalisation "destinataireCopieConcat"
            orderClause = orderClause.replaceAll(SolonEppSchemaConstant.EVENEMENT_DESTINATAIRE_COPIE_PROPERTY,
                    SolonEppSchemaConstant.EVENEMENT_DESTINATAIRE_COPIE_CONCAT_PROPERTY);

            return selectClause.toString() + whereClause.toString() + " ORDER BY " + orderClause;
        } else {
            return selectClause.toString() + whereClause.toString();
        }

    }

    /**
     * 
     * @param params ne peut pas être null (dans le cas où il l'est c'est intercepté dans la méthode appelante)
     * @return
     * @throws ClientException
     */
    private List<Serializable> parseParameter(List<Object> params) throws ClientException {
        List<Serializable> parsedParams = new ArrayList<Serializable>();
        for (Object object : params) {
            if (object instanceof EtatMessage) {
                // parse EtatMessage
                parsedParams.add(((EtatMessage) object).value());
            } else if (object instanceof EtatEvenement) {
                // Parse currentLifeCycleState
                String result = "*inconnu*";
                switch ((EtatEvenement) object) {
                case ANNULE:
                    result = SolonEppLifecycleConstant.EVENEMENT_ANNULE_STATE;
                    break;
                case EN_ATTENTE_DE_VALIDATION:
                    result = SolonEppLifecycleConstant.EVENEMENT_ATTENTE_VALIDATION_STATE;
                    break;
                case BROUILLON:
                    result = SolonEppLifecycleConstant.EVENEMENT_BROUILLON_STATE;
                    break;
                case EN_INSTANCE:
                    result = SolonEppLifecycleConstant.EVENEMENT_INSTANCE_STATE;
                    break;
                case PUBLIE:
                    result = SolonEppLifecycleConstant.EVENEMENT_PUBLIE_STATE;
                    break;
                }
                parsedParams.add(result);
            } else if (object instanceof NiveauLectureCode) {
                // parse NiveauLecture
                String niveauLectureCode = NiveauLectureCodeAssembler.assembleXsdToNiveauLectureCode((NiveauLectureCode) object);
                parsedParams.add(niveauLectureCode);
            } else if (object instanceof XMLGregorianCalendar) {
                // parse Calendar
                Calendar cal = Calendar.getInstance();
                cal.setTime(((XMLGregorianCalendar) object).toGregorianCalendar().getTime());
                parsedParams.add(cal);
            } else if (object instanceof String) {
                // remove CDATA
                String string = CDataAdapter.parse((String) object);
               if(EtatEvenement.EN_ATTENTE_DE_VALIDATION.toString().equals((String) string)) {
                   string = SolonEppLifecycleConstant.EVENEMENT_ATTENTE_VALIDATION_STATE;
                }else  if(EtatEvenement.EN_INSTANCE.toString().equals((String) string)) {
                    string = SolonEppLifecycleConstant.EVENEMENT_INSTANCE_STATE;
                }else  if(EtatEvenement.BROUILLON.toString().equals((String) string)) {
                    string = SolonEppLifecycleConstant.EVENEMENT_BROUILLON_STATE;
                }else  if(EtatEvenement.ANNULE.toString().equals((String) string)) {
                    string = SolonEppLifecycleConstant.EVENEMENT_ANNULE_STATE;
                }else  if(EtatEvenement.PUBLIE.toString().equals((String) string)) {
                    string = SolonEppLifecycleConstant.EVENEMENT_PUBLIE_STATE;
                }
                parsedParams.add(string);
            } else if (object instanceof Institution) {
                // parse Institution
                parsedParams.add((Institution) object);
            } else {
                parsedParams.add((Serializable) object);
            }
        }
        return parsedParams;
    }

    private static class XSDQueryBuilder extends SDefaultQueryVisitor {

        private static final long serialVersionUID = -7743149539458082583L;

        private Boolean joinDossier = Boolean.FALSE;
        private Boolean joinEvenement = Boolean.FALSE;
        private Boolean joinVersion = Boolean.FALSE;
        private Boolean joinPieceJointe = Boolean.FALSE;
        private Boolean joinPieceJointeFichier = Boolean.FALSE;
        private Boolean joinMandat = Boolean.FALSE;
        private Boolean joinIdentite = Boolean.FALSE;
        private Boolean joinOrganisme = Boolean.FALSE;

        private StringBuilder wherePart;
        private StringBuilder orderPart;
        private List<Serializable> whereParams;
        private Object[] availableParams;
        public int nbParamsRequired;

        public POS pos = POS.UNDEFINED;

        public XSDQueryBuilder(Object[] availableParams) {
        	super();
            this.wherePart = new StringBuilder();
            this.orderPart = new StringBuilder();

            this.whereParams = new ArrayList<Serializable>();
            this.nbParamsRequired = 0;
            this.availableParams = availableParams;
        }

        @Override
        public void visitDateLiteral(DateLiteral l) {
            if (isInWhere()) {
                wherePart.append('?');
                whereParams.add(l.toCalendar());
            } else {
                throw new QueryMakerException("Not supported operation : visitDateLiteral in " + pos);
            }
        }

        @Override
        public void visitDoubleLiteral(DoubleLiteral l) {
            if (isInWhere()) {
                wherePart.append('?');
                whereParams.add(Double.valueOf(l.value));
            } else {
                throw new QueryMakerException("Not supported operation : visitDoubleLiteral in " + pos);
            }
        }

        @Override
        public void visitExpression(Expression expr) {
          
            LOGGER.trace(STLogEnumImpl.PARSE_XSD_TO_UFNXQL_TEC,"in visitExpression(" + expr + ")");
            if (isInWhere()) {
                processExpressionInWhere(expr);
            } else {
                throw new QueryMakerException("Not supported operation : visitExpression in " + pos);
            }
            
            LOGGER.trace(STLogEnumImpl.PARSE_XSD_TO_UFNXQL_TEC,"out visitExpression(" + expr + ")");
        }

        protected void processExpressionInWhere(Expression expr) {

            wherePart.append('(');

            super.visitExpression(expr);

            wherePart.append(')');
        }

        @Override
        public void visitFromClause(FromClause clause) {
            // do nothing
        }

        @Override
        public void visitFunction(Function func) {
            LOGGER.trace(STLogEnumImpl.PARSE_XSD_TO_UFNXQL_TEC, "in visitFunction(" + func + ")");
        }

        @Override
        public void visitHavingClause(HavingClause clause) {
            throw new QueryMakerException("visitHavingClause not supported");
        }

        @Override
        public void visitIntegerLiteral(IntegerLiteral l) {
            if (isInWhere()) {
                wherePart.append('?');
                whereParams.add(Long.valueOf(l.value));
            } else {
                throw new QueryMakerException("Not supported operation : visitIntegerLiteral in " + pos);
            }
        }

        @Override
        public void visitLiteralList(LiteralList l) {
            LOGGER.trace(STLogEnumImpl.PARSE_XSD_TO_UFNXQL_TEC, "in visitLiteralList(" + l + ")");
            
            if (isInWhere()) {
                wherePart.append('(');
                for (Iterator<Literal> it = l.iterator(); it.hasNext();) {
                    it.next().accept(this);
                    if (it.hasNext()) {
                        wherePart.append(", ");
                    }
                }
                wherePart.append(')');
            } else {
                throw new QueryMakerException("No support for literal list in another part than where (" + pos + ")");
            }
            LOGGER.trace(STLogEnumImpl.PARSE_XSD_TO_UFNXQL_TEC, "out visitLiteralList(" + l + ")");
        }

        @Override
        public void visitOperator(Operator op) {
            if (isInWhere()) {
                wherePart.append(' ').append(op.toString()).append(' ');
            } else {
                throw new QueryMakerException("Not supported operation : visitOperator in " + pos);
            }
        }

        @Override
        public void visitOrderByClause(OrderByClause clause) {
            pos = POS.IN_ORDER;
            LOGGER.trace(STLogEnumImpl.PARSE_XSD_TO_UFNXQL_TEC, "in visitOrderByClause(" + clause + ")");

            super.visitOrderByClause(clause);

            LOGGER.trace(STLogEnumImpl.PARSE_XSD_TO_UFNXQL_TEC, "out visitOrderByClause(" + clause + ")");

            pos = POS.UNDEFINED;

        }

        @Override
        public void visitOrderByExpr(OrderByExpr expr) {

            LOGGER.trace(STLogEnumImpl.PARSE_XSD_TO_UFNXQL_TEC, "in visitOrderByExpr(" + expr + ")");

            expr.reference.accept(this);
            if (expr.isDescending) {
                orderPart.append(" DESC");
            }
            LOGGER.trace(STLogEnumImpl.PARSE_XSD_TO_UFNXQL_TEC, "out visitOrderByExpr(" + expr + ")");
        }

        @Override
        public void visitQuery(SQLQuery query) {
            LOGGER.trace(STLogEnumImpl.PARSE_XSD_TO_UFNXQL_TEC, "in visitQuery(" + query + ")");

            super.visitQuery(query);

            LOGGER.trace(STLogEnumImpl.PARSE_XSD_TO_UFNXQL_TEC, "out visitQuery(" + query + ")");
        }

        @Override
        public void visitReference(Reference ref) {

          LOGGER.trace(STLogEnumImpl.PARSE_XSD_TO_UFNXQL_TEC, "in visitReference(" + ref + ")");

            // super.visitReference(ref);
            String name = ref.name;

            String field = props.getProperty(name);
            if (StringUtils.isNotBlank(field)) {
                processReference(field);
            } else {
                if (isInWhere()) {
                    wherePart.append(field);
                } else if (isInOrder()) {
                    if (orderPart.length() > 0) {
                        orderPart.append(", ");
                    }
                    orderPart.append(field);
                }
            }
            
            LOGGER.trace(STLogEnumImpl.PARSE_XSD_TO_UFNXQL_TEC, "out visitReference(" + ref + ")");
        }

        protected void processReference(String field) {
            if (isInWhere()) {
                wherePart.append(field);
                processJoin(field);
            } else if (isInOrder()) {
                if (orderPart.length() > 0) {
                    orderPart.append(", ");
                }
                orderPart.append(field);
                processJoin(field);
            }
        }

        private void processJoin(String field) {
            // joiture sur Dossier
            if (field.contains(DOSSIER_PREFIX)) {
                joinDossier = Boolean.TRUE;
                joinEvenement = Boolean.TRUE;
            }

            // joiture sur Evenement
            if (field.contains(EVENEMENT_PREFIX)) {
                joinEvenement = Boolean.TRUE;
            }

            // joiture sur Version
            if (field.contains(VERSION_PREFIX)) {
                joinVersion = Boolean.TRUE;
            }

            // joiture sur Mandat
            if (field.contains(MANDAT_PREFIX)) {
                joinMandat = Boolean.TRUE;
            }

            // joiture sur Identite
            if (field.contains(IDENTITE_PREFIX)) {
                joinIdentite = Boolean.TRUE;
            }

            // joiture sur Organisme
            if (field.contains(ORGANISME_PREFIX)) {
                joinOrganisme = Boolean.TRUE;
            }
            if (field.contains(PIECE_JOINTE_FICHIER_PREFIX)) {
                joinVersion = Boolean.TRUE;
                joinPieceJointe = Boolean.TRUE;
                joinPieceJointeFichier =Boolean.TRUE;
            }
            if (field.contains(PIECE_JOINTE_PREFIX)) {
                joinVersion = Boolean.TRUE;
                joinPieceJointe = Boolean.TRUE;
            }
        }

        @Override
        public void visitStringLiteral(StringLiteral l) {
            processStringLiteral(l.value);
        }

        public void processStringLiteral(String str) {
            if (wherePart.toString().endsWith("f.ecm:fulltext = ")) {
                wherePart.append("'");
                wherePart.append(str);
                wherePart.append("'");
            } else {
                wherePart.append('?');
                whereParams.add(str);
            }
        }

        @Override
        public void visitWhereClause(WhereClause clause) {
            
            LOGGER.trace(STLogEnumImpl.PARSE_XSD_TO_UFNXQL_TEC, "in visitWhereClause(" + clause + ")");


            pos = POS.IN_WHERE;
            super.visitWhereClause(clause);
            pos = POS.UNDEFINED;

            LOGGER.trace(STLogEnumImpl.PARSE_XSD_TO_UFNXQL_TEC, "out visitWhereClause(" + clause + ")");

        }

        @Override
        public void visitSGroupByClause(SGroupByClause clause) {

            LOGGER.trace(STLogEnumImpl.PARSE_XSD_TO_UFNXQL_TEC, "in visitSGroupByClause(" + clause + ")");

            pos = POS.IN_GROUP;
            super.visitSGroupByClause(clause);
            pos = POS.UNDEFINED;

            LOGGER.trace(STLogEnumImpl.PARSE_XSD_TO_UFNXQL_TEC, "out visitSGroupByClause(" + clause + ")");

        }

        @Override
        public void visitSParamLiteral() {
            if (isInWhere()) {
                // le champs f.ecm:fulltext n'accepte pas le passage de parametre par ?
                if (!(wherePart.toString().endsWith("f.ecm:fulltext = "))) {
                wherePart.append("?");
                if (availableParams == null) {
                    throw new QueryMakerException("No parameter in entry");
                }
                if (nbParamsRequired >= availableParams.length) {
                    throw new QueryMakerException("Not enough parameter in entry [available=" + availableParams.length + "]");
                }
                whereParams.add((Serializable) availableParams[nbParamsRequired]);
                } else{
                    wherePart.append("'");
                    wherePart.append((String)availableParams[nbParamsRequired]);
                    wherePart.append("'");
                }
                nbParamsRequired++;
            } else {
                throw new QueryMakerException("param supported only in where part");
            }
        }

        protected boolean isInOrder() {
            return POS.IN_ORDER.equals(pos);
        }

        protected boolean isInWhere() {
            return POS.IN_WHERE.equals(pos);
        }

        public ClauseParams getWhereClause() {
            return new ClauseParams(wherePart.toString(), whereParams);
        }

        public String getOrder() {
            return orderPart.toString();
        }

        public Boolean isJoinDossier() {
            return joinDossier;
        }

        public Boolean isJoinEvenement() {
            return joinEvenement;
        }

        public Boolean isJoinVersion() {
            return joinVersion;
        }

        public Boolean isJoinjoinPiece() {
            return joinPieceJointe;
        }
        
        public Boolean isJoinjoinPieceFichier() {
            return joinPieceJointeFichier;
        }
        
        public Boolean isJoinMandat() {
            return joinMandat;
        }

        public Boolean isJoinIdentite() {
            return joinIdentite;
        }

        public Boolean isJoinOrganisme() {
            return joinOrganisme;
        }

    }

}
