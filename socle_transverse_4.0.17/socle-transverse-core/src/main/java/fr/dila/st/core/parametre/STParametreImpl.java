package fr.dila.st.core.parametre;

import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.parametre.STParametre;
import fr.dila.st.core.schema.DublincoreSchemaUtils;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Adapter document Parametre de l'application
 *
 * @author Fabio Esposito
 *
 */
public class STParametreImpl implements STParametre {
    protected DocumentModel document;

    public STParametreImpl(DocumentModel doc) {
        this.document = doc;
    }

    @Override
    public DocumentModel getDocument() {
        return document;
    }

    @Override
    public void setDocument(DocumentModel document) {
        this.document = document;
    }

    @Override
    public String getUnit() {
        return PropertyUtil.getStringProperty(
            document,
            STSchemaConstant.PARAMETRE_SCHEMA,
            STSchemaConstant.PARAMETRE_UNIT
        );
    }

    @Override
    public void setUnit(String unit) {
        PropertyUtil.setProperty(document, STSchemaConstant.PARAMETRE_SCHEMA, STSchemaConstant.PARAMETRE_UNIT, unit);
    }

    @Override
    public String getValue() {
        return PropertyUtil.getStringProperty(
            document,
            STSchemaConstant.PARAMETRE_SCHEMA,
            STSchemaConstant.PARAMETRE_VALUE
        );
    }

    @Override
    public void setValue(String value) {
        PropertyUtil.setProperty(document, STSchemaConstant.PARAMETRE_SCHEMA, STSchemaConstant.PARAMETRE_VALUE, value);
    }

    @Override
    public String getDescription() {
        return DublincoreSchemaUtils.getDescription(document);
    }

    @Override
    public void setDescription(String description) {
        DublincoreSchemaUtils.setDescription(document, description);
    }

    @Override
    public String getType() {
        return PropertyUtil.getStringProperty(
            document,
            STSchemaConstant.PARAMETRE_SCHEMA,
            STSchemaConstant.PARAMETRE_TYPE
        );
    }

    @Override
    public void setType(String type) {
        PropertyUtil.setProperty(document, STSchemaConstant.PARAMETRE_SCHEMA, STSchemaConstant.PARAMETRE_TYPE, type);
    }
}
