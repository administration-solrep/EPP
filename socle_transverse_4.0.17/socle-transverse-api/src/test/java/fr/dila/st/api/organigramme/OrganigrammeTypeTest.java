package fr.dila.st.api.organigramme;

import static fr.dila.st.api.organigramme.OrganigrammeType.DIRECTION;
import static fr.dila.st.api.organigramme.OrganigrammeType.INSTITUTION;
import static fr.dila.st.api.organigramme.OrganigrammeType.MINISTERE;
import static fr.dila.st.api.organigramme.OrganigrammeType.UNITE_STRUCTURELLE;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class OrganigrammeTypeTest {

    @Test
    public void getLstPotentialParent() {
        Assertions
            .assertThat(UNITE_STRUCTURELLE.getLstPotentialParent())
            .containsExactly(MINISTERE, INSTITUTION, UNITE_STRUCTURELLE, DIRECTION);
    }
}
