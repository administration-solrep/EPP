package fr.dila.st.core.requeteur;

import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.constant.STRequeteurExpertConstants;
import fr.dila.st.core.smartquery.IncrementalSmartNXQLQuery;
import fr.dila.st.core.util.DateUtil;
import fr.dila.st.core.util.FullTextUtil;
import fr.dila.st.core.util.SolonDateConverter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Héritage de l'incremental smart query donnée par Nuxeo.
 *
 * @author jgomez
 */
public class STIncrementalSmartNXQLQuery extends IncrementalSmartNXQLQuery {
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 9205676547457702896L;

    private static final Log LOGGER = LogFactory.getLog(STIncrementalSmartNXQLQuery.class);

    /**
     * Un binding pour le poste. (Retraitement de la valeur en identifiant de mailbox poste-<valeur>.
     */
    private String posteValue;

    /**
     * Binding sur les manylistbox
     */
    private List<String> manyListbox;

    /**
     * Un binding pour 0 et 1
     */
    private String booleanAsStringValue;

    /**
     * Pour les recherches fulltext, la chaîne transformées avec des %
     */
    private String fulltextValue;

    /**
     * La catégorie sélectionnée, utilisée pour réduire le nombre de widgets presentés à l'utilisateur
     */

    private String selectedCategoryName;

    /**
     * Mis à DYNAMIC pour selectionner une date dynamique
     */
    private String dynDynamicDateSelector;
    private String dynDynamicDateOtherSelector;

    private Date dynDateStaticValue, dynDateStaticOtherValue;

    private String dynDateDynamicValue, dynDateDynamicOtherValue;

    public STIncrementalSmartNXQLQuery(String existingQueryPart) {
        super(existingQueryPart);
    }

    public static enum SPECIAL_OPERATORS {
        CONTAINS("CONTAINS", 1),
        BETWEEN("BETWEEN", 2),
        NOT_BETWEEN("NOT BETWEEN", 2),
        NOT_CONTAINS("NOT CONTAINS", 1),
        NOT_STARTSWITH("NOT STARTSWITH", 1),
        NULL("IS NULL", 0),
        NOT_NULL("IS NOT NULL", 0);

        private final String stringValue;

        private final Integer arity;

        SPECIAL_OPERATORS(String stringValue, Integer arity) {
            this.stringValue = stringValue;
            this.arity = arity;
        }

        public String getStringValue() {
            return stringValue;
        }

        public Integer getArity() {
            return arity;
        }
    }

    /**
     * On a des problèmes avec la quote simple qui entoure les valeurs, donc on surcharge la méthode pour mettre des
     * double quotes.
     *
     */
    @Override
    public String buildQuery() {
        StringBuilder builder = new StringBuilder();
        if (existingQueryPart != null) {
            builder.append(existingQueryPart);
            builder.append(" ");
        }
        // perform simple check before changing query
        if (leftExpression != null && conditionalOperator != null) {
            if (logicalOperator != null) {
                builder.append(logicalOperator);
                builder.append(" ");
            }
            if (Boolean.TRUE.equals(openParenthesis)) {
                builder.append("(");
            }
            if (
                Boolean.TRUE.equals(addNotOperator) ||
                SPECIAL_OPERATORS.NOT_STARTSWITH.getStringValue().equals(conditionalOperator)
            ) {
                builder.append("NOT ");
            }

            builder.append(leftExpression);
            builder.append(' ');

            if (SPECIAL_OPERATORS.CONTAINS.getStringValue().equals(conditionalOperator)) {
                builder.append("LIKE");
            } else if (SPECIAL_OPERATORS.NOT_CONTAINS.getStringValue().equals(conditionalOperator)) {
                builder.append("NOT LIKE");
            } else if (SPECIAL_OPERATORS.NOT_STARTSWITH.getStringValue().equals(conditionalOperator)) {
                // negation already added above
                builder.append("STARTSWITH");
            } else if (SPECIAL_OPERATORS.NULL.getStringValue().equals(conditionalOperator)) {
                builder.append("IS NULL");
            } else if (SPECIAL_OPERATORS.NOT_NULL.getStringValue().equals(conditionalOperator)) {
                builder.append("IS NOT NULL");
            } else {
                builder.append(conditionalOperator);
            }
            builder.append(' ');

            if (value != null) {
                // Traitement du widget de date dynamique
                LOGGER.info(
                    "PPP : Paramètres du widget dynamique : " +
                    dynDynamicDateSelector +
                    ";" +
                    dynDateDynamicValue +
                    ";" +
                    dynDateStaticValue +
                    "\n" +
                    dynDynamicDateOtherSelector +
                    ";" +
                    dynDateStaticOtherValue +
                    ";" +
                    dynDateDynamicOtherValue
                );
                if (booleanValue != null) {
                    if (Boolean.TRUE.equals(booleanValue)) {
                        builder.append(1);
                    } else {
                        builder.append(0);
                    }
                } else if (stringValue != null) {
                    if (
                        SPECIAL_OPERATORS.CONTAINS.getStringValue().equals(conditionalOperator) ||
                        SPECIAL_OPERATORS.NOT_CONTAINS.getStringValue().equals(conditionalOperator)
                    ) {
                        builder.append("\"%");
                        if (leftExpression.equals("d.dos:numeroNor") && stringValue.contains(";")) {
                            stringValue = stringValue.replace(";", "%;%");
                        }
                        if (Boolean.TRUE.equals(escapeValue)) {
                            builder.append(String.format("%s", escaper.escape(stringValue)));
                        } else {
                            builder.append(stringValue);
                        }
                        builder.append("%\"");
                    } else {
                        if (Boolean.TRUE.equals(escapeValue)) {
                            builder.append(String.format("\"%s\"", escaper.escape(stringValue)));
                        } else {
                            builder.append(String.format("\"%s\"", stringValue));
                        }
                    }
                } else if (posteValue != null) {
                    // Gestion du poste
                    builder.append(String.format("\"%s\"", STConstant.PREFIX_POSTE + posteValue));
                } else if (booleanAsStringValue != null) {
                    builder.append(String.format("%s", booleanAsStringValue));
                } else if (stringListValue != null) {
                    String[] values = new String[stringListValue.size()];
                    values = stringListValue.toArray(values);
                    if (Boolean.TRUE.equals(escapeValue)) {
                        for (int i = 0; i < values.length; i++) {
                            values[i] = String.format("\"%s\"", escaper.escape(values[i]));
                        }
                    } else {
                        for (int i = 0; i < values.length; i++) {
                            values[i] = String.format("\"%s\"", values[i]);
                        }
                    }
                    builder.append(String.format("(%s)", StringUtils.join(values, ",")));
                } else if (stringArrayValue != null) {
                    String[] values = new String[stringArrayValue.length];
                    if (Boolean.TRUE.equals(escapeValue)) {
                        for (int i = 0; i < stringArrayValue.length; i++) {
                            values[i] = String.format("\"%s\"", escaper.escape(stringArrayValue[i]));
                        }
                    } else {
                        for (int i = 0; i < stringArrayValue.length; i++) {
                            values[i] = String.format("\"%s\"", stringArrayValue[i]);
                        }
                    }
                    builder.append(String.format("(%s)", StringUtils.join(values, ",")));
                } else if (datetimeValue != null) {
                    builder.append(
                        String.format(
                            "TIMESTAMP '%s'",
                            SolonDateConverter.DATETIME_DASH_REVERSE_SECOND_COLON.format(datetimeValue)
                        )
                    );
                    if (otherDatetimeValue != null) {
                        builder.append(" AND ");

                        // On ajoute 24h pour l'intervalle de fin afin d'inclure la dernière journée
                        Calendar otherDatetime = DateUtil.toCalendarFromNotNullDate(otherDatetimeValue);
                        DateUtil.setDateToEndOfDay(otherDatetime);
                        otherDatetimeValue = otherDatetime.getTime();
                        builder.append(
                            String.format(
                                "TIMESTAMP \'%s\'",
                                SolonDateConverter.DATETIME_DASH_REVERSE_SECOND_COLON.format(otherDatetimeValue)
                            )
                        );
                    }
                } else if (dateValue != null) {
                    builder.append(
                        String.format("DATE \"%s\"", SolonDateConverter.DATE_DASH_REVERSE.format(dateValue))
                    );
                    if (otherDateValue != null) {
                        builder.append(" AND ");
                        // On ajoute 24h pour l'intervalle de fin afin d'inclure la dernière journée
                        Calendar otherDate = DateUtil.toCalendarFromNotNullDate(otherDateValue);
                        otherDate.add(Calendar.DAY_OF_MONTH, 1);
                        otherDateValue = otherDate.getTime();
                        builder.append(
                            String.format("DATE \"%s\"", SolonDateConverter.DATE_DASH_REVERSE.format(otherDateValue))
                        );
                    }
                } else if (dynDynamicDateSelector != null) {
                    if ("STATIC".equals(dynDynamicDateSelector) && dynDateStaticValue != null) {
                        builder.append(
                            String.format(
                                "DATE \"%s\"",
                                SolonDateConverter.DATE_DASH_REVERSE.format(dynDateStaticValue)
                            )
                        );
                    }
                    if ("DYNAMIC".equals(dynDynamicDateSelector) && dynDateDynamicValue != null) {
                        builder.append(String.format("%s", dynDateDynamicValue));
                    }
                    if (dynDynamicDateOtherSelector != null) {
                        if ("STATIC".equals(dynDynamicDateOtherSelector) && dynDateStaticOtherValue != null) {
                            builder.append(" AND ");
                            // On ajoute 24h pour l'intervalle de fin afin d'inclure la dernière journée
                            Calendar dynDate = DateUtil.toCalendarFromNotNullDate(dynDateStaticOtherValue);
                            dynDate.add(Calendar.DAY_OF_MONTH, 1);
                            dynDateStaticOtherValue = dynDate.getTime();
                            builder.append(
                                String.format(
                                    "DATE \"%s\"",
                                    SolonDateConverter.DATE_DASH_REVERSE.format(dynDateStaticOtherValue)
                                )
                            );
                        }
                        if ("DYNAMIC".equals(dynDynamicDateSelector) && dynDateDynamicOtherValue != null) {
                            builder.append(" AND");
                            builder.append(String.format(" %s", dynDateDynamicOtherValue));
                        }
                    }
                } else if (integerValue != null) {
                    builder.append(integerValue);
                } else if (floatValue != null) {
                    builder.append(floatValue);
                } else if (manyListbox != null) {
                    builder.append(String.format("('%s')", StringUtils.join(manyListbox, "','")));
                } else {
                    // value type not supported
                    builder.append(value.toString());
                }
            }
            if (Boolean.TRUE.equals(closeParenthesis)) {
                builder.append(")");
            }
        }
        String newValue = builder.toString().trim();
        clear();
        existingQueryPart = newValue;
        return existingQueryPart;
    }

    public void setPosteValue(String posteValue) {
        this.posteValue = posteValue;
        setValue(posteValue);
    }

    public String getPosteValue() {
        return posteValue;
    }

    public void setBooleanAsStringValue(String booleanAsStringValue) {
        this.booleanAsStringValue = booleanAsStringValue;
        setValue(booleanAsStringValue);
    }

    public String getBooleanAsStringValue() {
        return booleanAsStringValue;
    }

    /**
     * Clears all value bindings.
     */
    @Override
    protected void clearValues() {
        super.clearValues();
        posteValue = null;
        booleanAsStringValue = null;
        this.selectedCategoryName = STRequeteurExpertConstants.REQUETEUR_EXPERT_ALL_CATEGORY;
        dynDateDynamicOtherValue = null;
        dynDateDynamicValue = null;
        dynDateStaticOtherValue = null;
        dynDateStaticValue = null;
        dynDynamicDateSelector = null;
        dynDynamicDateOtherSelector = null;
        if (manyListbox != null) {
            manyListbox.clear();
        }
    }

    // On ne montre pas les parenthèses.
    @Override
    public boolean getShowCloseParenthesis() {
        return false;
    }

    @Override
    public boolean getShowOpenParenthesis() {
        return false;
    }

    public void setSelectedCategoryName(String selectedCategoryName) {
        this.selectedCategoryName = selectedCategoryName;
    }

    public String getSelectedCategoryName() {
        if (StringUtils.isBlank(selectedCategoryName)) {
            return STRequeteurExpertConstants.REQUETEUR_EXPERT_NO_CATEGORY;
        }
        return selectedCategoryName;
    }

    public void setFulltextValue(String fulltextValue) {
        this.fulltextValue = fulltextValue;
        if (fulltextValue != null) {
            setStringValue(FullTextUtil.replaceStarByPercent(fulltextValue));
        } else {
            setStringValue(null);
        }
    }

    public String getFulltextValue() {
        return fulltextValue;
    }

    protected SPECIAL_OPERATORS lookupSpecialOperator(String operatorStringValue) {
        if (operatorStringValue == null) {
            return null;
        }
        for (SPECIAL_OPERATORS op : SPECIAL_OPERATORS.values()) {
            if (operatorStringValue.equals(op.getStringValue())) {
                return op;
            }
        }
        return null;
    }

    public Integer getOperatorArity() {
        if (conditionalOperator == null) {
            return 0;
        }
        SPECIAL_OPERATORS op = lookupSpecialOperator(conditionalOperator);
        return (op != null) ? op.getArity() : 1;
    }

    public String getDynDynamicDateSelector() {
        return dynDynamicDateSelector;
    }

    public void setDynDynamicDateSelector(String dynDynamicDateSelector) {
        this.dynDynamicDateSelector = dynDynamicDateSelector;
    }

    public String getDynDynamicDateOtherSelector() {
        return dynDynamicDateOtherSelector;
    }

    public void setDynDynamicDateOtherSelector(String dynDynamicDateOtherSelector) {
        this.dynDynamicDateOtherSelector = dynDynamicDateOtherSelector;
    }

    public Date getDynDateStaticValue() {
        return dynDateStaticValue;
    }

    public void setDynDateStaticValue(Date dynDateStaticValue) {
        this.dynDateStaticValue = dynDateStaticValue;
        setValue(dynDateStaticValue);
    }

    public Date getDynDateStaticOtherValue() {
        return dynDateStaticOtherValue;
    }

    public void setDynDateStaticOtherValue(Date dynDateStaticOtherValue) {
        this.dynDateStaticOtherValue = dynDateStaticOtherValue;
        setValue(dynDateStaticOtherValue);
    }

    public String getDynDateDynamicValue() {
        return dynDateDynamicValue;
    }

    public void setDynDateDynamicValue(String dynDateDynamicValue) {
        this.dynDateDynamicValue = dynDateDynamicValue;
        setValue(dynDateDynamicValue);
    }

    public String getDynDateDynamicOtherValue() {
        return dynDateDynamicOtherValue;
    }

    public void setDynDateDynamicOtherValue(String dynDateDynamicOtherValue) {
        this.dynDateDynamicOtherValue = dynDateDynamicOtherValue;
        setValue(dynDateDynamicOtherValue);
    }

    /**
     * @param manyListbox
     *            the manyListbox to set
     */
    public void setManyListbox(List<String> modeParutionValue) {
        this.manyListbox = modeParutionValue;
        setValue(modeParutionValue);
    }

    /**
     * @return the manyListbox
     */
    public List<String> getManyListbox() {
        return manyListbox;
    }
}
