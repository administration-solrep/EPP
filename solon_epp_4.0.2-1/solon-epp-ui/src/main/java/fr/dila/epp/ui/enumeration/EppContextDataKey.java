package fr.dila.epp.ui.enumeration;

import fr.dila.epp.ui.th.bean.MessageListForm;
import fr.dila.epp.ui.th.bean.ProfilForm;
import fr.dila.epp.ui.th.bean.RapidSearchForm;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.domain.message.Message;
import fr.dila.st.ui.enums.ContextDataKey;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.DocumentModel;

public enum EppContextDataKey implements ContextDataKey {
    ACCEPTER(Boolean.class),
    COMMUNICATION_METADONNEES_MAP(Map.class),
    CURRENT_EVENEMENT_FOR_CREATION(DocumentModel.class),
    CURRENT_MESSAGE(Message.class),
    CURRENT_TYPE_EVENEMENT(String.class),
    CURRENT_TYPE_EVENEMENT_SUCCESSIF(String.class),
    CURRENT_VERSION(Version.class),
    CURRENT_VERSION_FOR_CREATION(DocumentModel.class),
    DESTINATAIRE(String.class),
    EMETTEUR(String.class),
    FULL_TABLEREF(Boolean.class),
    INPUT(String.class),
    JSON_SEARCH(String.class),
    MAP_SEARCH(Map.class),
    MESSAGE_LIST_FORM(MessageListForm.class),
    MODE_CREATION(String.class),
    PIECE_JOINTE_LIST(List.class),
    PROFIL_FORM(ProfilForm.class),
    PUBLIER(Boolean.class),
    RAPID_SEARCH_FORM(RapidSearchForm.class),
    SKIP_LOCK(Boolean.class),
    TABLE_REF(String.class),
    TYPE_EVENEMENT(String.class),
    TYPE_ORGANISME(String.class),
    VERSION_ID(String.class);

    private final Class<?> valueType;
    private final String specificKey;

    EppContextDataKey(Class<?> valueType) {
        this(valueType, null);
    }

    EppContextDataKey(Class<?> valueType, String specificKey) {
        this.valueType = valueType;
        this.specificKey = specificKey;
    }

    @Override
    public String getName() {
        return StringUtils.defaultIfBlank(specificKey, name());
    }

    @Override
    public Class<?> getValueType() {
        return valueType;
    }
}
