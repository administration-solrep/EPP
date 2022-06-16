package fr.dila.st.core.operation.utils;

import static fr.dila.st.api.constant.STParametreConstant.ARCHIVAGE_TYPE;

public class ArchivageParametreBean extends ParametreBean {

    public ArchivageParametreBean(
        String parameterName,
        String parameterTitre,
        String parameterDescription,
        String parameterUnit,
        String parameterValue
    ) {
        super(parameterName, parameterTitre, parameterDescription, parameterUnit, parameterValue, ARCHIVAGE_TYPE);
    }

    public ArchivageParametreBean(String parameterName, String parameterTitre) {
        super(parameterName, parameterTitre, null, null, null, ARCHIVAGE_TYPE);
    }
}
