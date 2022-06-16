package fr.dila.st.core.operation.version;

import static org.assertj.core.api.Assertions.assertThat;

import fr.dila.st.core.operation.STApplication;
import org.junit.Test;

public class OperationsVersionExecuterOperationTest {

    @Test
    public void testIsEligible() {
        OperationsVersionExecuterOperation operation = new OperationsVersionExecuterOperation();

        assertThat(operation.isEligible(DummySpecificOperation.class, "4.0.0", STApplication.ANY)).isFalse();
        assertThat(operation.isEligible(DummySpecificOperation.class, "4.0.0", STApplication.REPONSES)).isTrue();
        assertThat(operation.isEligible(DummySpecificOperation.class, "4.0.1", STApplication.EPG)).isTrue();
        assertThat(operation.isEligible(DummySpecificOperation.class, "4.0.1", STApplication.EPP)).isFalse();
        assertThat(operation.isEligible(DummyReponsesOperation.class, "4.0.0", STApplication.REPONSES)).isTrue();
        assertThat(operation.isEligible(DummyReponsesOperation.class, "4.0.0", STApplication.EPP)).isFalse();
        assertThat(operation.isEligible(DummyTransverseOperation.class, "4.0.0", STApplication.ANY)).isTrue();
        assertThat(operation.isEligible(DummyTransverseOperation.class, "4.0.0", STApplication.REPONSES)).isTrue();
    }
}
