package fr.dila.st.core.operation.utils;

/**
 * Informations permettant la création d'un nouveau paramètre via l'opération
 * AbstractCreateParametersOperation
 *
 * @author tlombard
 */
public class ParametreBean {
    private final String parameterName;
    private final String parameterTitre;
    private final String parameterDescription;
    private final String parameterUnit;
    private final String parameterValue;
    private final String parameterType;

    public ParametreBean(String parameterName) {
        this(parameterName, null, null, null, null);
    }

    public ParametreBean(String parameterName, String parameterTitre) {
        this(parameterName, parameterTitre, null, null, null);
    }

    public ParametreBean(
        String parameterName,
        String parameterTitre,
        String parameterDescription,
        String parameterUnit,
        String parameterValue
    ) {
        this(parameterName, parameterTitre, parameterDescription, parameterUnit, parameterValue, null);
    }

    public ParametreBean(
        String parameterName,
        String parameterTitre,
        String parameterDescription,
        String parameterUnit,
        String parameterValue,
        String type
    ) {
        super();
        this.parameterName = parameterName;
        this.parameterTitre = parameterTitre;
        this.parameterDescription = parameterDescription;
        this.parameterUnit = parameterUnit;
        this.parameterValue = parameterValue;
        this.parameterType = type;
    }

    /**
     * @return the parameterName
     */
    public String getParameterName() {
        return parameterName;
    }

    /**
     * @return the parameterTitre
     */
    public String getParameterTitre() {
        return parameterTitre;
    }

    /**
     * @return the parameterDescription
     */
    public String getParameterDescription() {
        return parameterDescription;
    }

    /**
     * @return the parameterUnit
     */
    public String getParameterUnit() {
        return parameterUnit;
    }

    /**
     * @return the parameterValue
     */
    public String getParameterValue() {
        return parameterValue;
    }

    public String getParameterType() {
        return parameterType;
    }
}
