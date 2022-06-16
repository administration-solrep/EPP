package fr.dila.st.api.service.organigramme;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import fr.dila.st.api.service.organigramme.OrganigrammeNodeDeletionProblem.ProblemScope;
import fr.dila.st.api.service.organigramme.OrganigrammeNodeDeletionProblem.ProblemType;
import org.junit.Before;
import org.junit.Test;

public class OrganigrammeNodeDeletionProblemTest {
    private OrganigrammeNodeDeletionProblem me, other;

    @Before
    public void setUp() {
        me = new OrganigrammeNodeDeletionProblem(ProblemType.DOSSIER_ATTACHED_TO_DIRECTION, "blockedObjIdentification");
        me.setBlockingDossierInfo("blockingDossierInfo");
        me.setPosteInfo("posteInfo");
        me.setBlockingObjIdentification("blockingObjIdentification");

        other =
            new OrganigrammeNodeDeletionProblem(ProblemType.DOSSIER_ATTACHED_TO_DIRECTION, "blockedObjIdentification");
        other.setBlockingDossierInfo("blockingDossierInfo");
        other.setPosteInfo("posteInfo");
        other.setBlockingObjIdentification("blockingObjIdentification");
    }

    @Test
    public void testEquals() {
        assertTrue(me.equals(other));
    }

    @Test
    public void testEquals_problemType() {
        other.setProblemType(ProblemType.INSTANCE_FDR_ATTACHED_TO_POSTE);
        assertFalse(me.equals(other));
    }

    @Test
    public void testEquals_blockedObjIdentification() {
        other.setBlockedObjIdentification("otherBlockedObjIdentification");
        assertFalse(me.equals(other));
    }

    @Test
    public void testEquals_blockingObjType() {
        other.setBlockingObjType(ProblemScope.MODELE_FDR);
        assertFalse(me.equals(other));
    }

    @Test
    public void testEquals_blockingObjIdentification() {
        other.setBlockingObjIdentification("otherBlockingObjIdentification");
        assertFalse(me.equals(other));
    }

    @Test
    public void testEquals_blockingDossierInfo() {
        other.setBlockingDossierInfo("otherBlockingDossierInfo");
        assertFalse(me.equals(other));
    }

    @Test
    public void testEquals_posteInfo() {
        other.setPosteInfo("otherPosteInfo");
        assertFalse(me.equals(other));
    }
}
