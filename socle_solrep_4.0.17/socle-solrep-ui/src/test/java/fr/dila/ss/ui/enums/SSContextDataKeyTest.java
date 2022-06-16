package fr.dila.ss.ui.enums;

import static fr.dila.ss.ui.enums.SSContextDataKey.CORBEILLE_ACTIONS;
import static fr.dila.ss.ui.enums.SSContextDataKey.CREATION_ETAPE_DTO;
import static fr.dila.ss.ui.enums.SSContextDataKey.DIRECTION_MOVE_STEP;
import static fr.dila.ss.ui.enums.SSContextDataKey.ETAPE_ACTIONS;
import static fr.dila.ss.ui.enums.SSContextDataKey.FEUILLE_ROUTE;
import static fr.dila.ss.ui.enums.SSContextDataKey.ID_ETAPE;
import static fr.dila.ss.ui.enums.SSContextDataKey.ID_MODELE;
import static fr.dila.ss.ui.enums.SSContextDataKey.ID_MODELES;
import static fr.dila.ss.ui.enums.SSContextDataKey.ID_POSTE;
import static fr.dila.ss.ui.enums.SSContextDataKey.TYPE_ETAPE;
import static org.assertj.core.api.Assertions.assertThat;

import fr.dila.ss.ui.bean.actions.CorbeilleActionDTO;
import fr.dila.ss.ui.bean.actions.RoutingActionDTO;
import fr.dila.ss.ui.bean.fdr.CreationEtapeDTO;
import fr.dila.st.core.AbstractTestSortableEnum;
import java.util.List;
import org.junit.Test;
import org.nuxeo.ecm.core.api.DocumentModel;

public class SSContextDataKeyTest extends AbstractTestSortableEnum<SSContextDataKey> {

    @Override
    protected Class<SSContextDataKey> getEnumClass() {
        return SSContextDataKey.class;
    }

    @Test
    public void getName() {
        assertThat(CREATION_ETAPE_DTO.getName()).isEqualTo("CREATION_ETAPE_DTO");
        assertThat(DIRECTION_MOVE_STEP.getName()).isEqualTo("DIRECTION_MOVE_STEP");
        assertThat(ETAPE_ACTIONS.getName()).isEqualTo("etapeActions");
        assertThat(FEUILLE_ROUTE.getName()).isEqualTo("FEUILLE_ROUTE");
        assertThat(ID_MODELES.getName()).isEqualTo("ID_MODELES");
        assertThat(ID_POSTE.getName()).isEqualTo("ID_POSTE");
        assertThat(TYPE_ETAPE.getName()).isEqualTo("TYPE_ETAPE");
        assertThat(ID_ETAPE.getName()).isEqualTo("ID_ETAPE");
        assertThat(ID_MODELE.getName()).isEqualTo("ID_MODELE");
    }

    @Test
    public void getTypeValue() {
        assertThat(CREATION_ETAPE_DTO.getValueType()).isEqualTo(CreationEtapeDTO.class);
        assertThat(CORBEILLE_ACTIONS.getValueType()).isEqualTo(CorbeilleActionDTO.class);
        assertThat(DIRECTION_MOVE_STEP.getValueType()).isEqualTo(String.class);
        assertThat(ETAPE_ACTIONS.getValueType()).isEqualTo(RoutingActionDTO.class);
        assertThat(FEUILLE_ROUTE.getValueType()).isEqualTo(DocumentModel.class);
        assertThat(ID_MODELES.getValueType()).isEqualTo(List.class);
        assertThat(ID_POSTE.getValueType()).isEqualTo(String.class);
        assertThat(TYPE_ETAPE.getValueType()).isEqualTo(String.class);
        assertThat(ID_ETAPE.getValueType()).isEqualTo(String.class);
        assertThat(ID_MODELE.getValueType()).isEqualTo(String.class);
    }
}
