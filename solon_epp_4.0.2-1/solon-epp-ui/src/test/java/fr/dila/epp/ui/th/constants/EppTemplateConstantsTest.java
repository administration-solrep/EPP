package fr.dila.epp.ui.th.constants;

import fr.dila.st.ui.AbstractTestConstants;
import org.junit.Test;

public class EppTemplateConstantsTest extends AbstractTestConstants<EppTemplateConstants> {

    @Override
    protected Class<EppTemplateConstants> getConstantClass() {
        return EppTemplateConstants.class;
    }

    @Test
    public void constantsShouldNotHaveDuplicates() {
        verifyDuplicatesWithSTTemplateConstants();
    }
}
