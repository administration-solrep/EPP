package fr.dila.ss.ui.th.constants;

import fr.dila.st.ui.AbstractTestConstants;
import org.junit.Test;

public class SSTemplateConstantsTest extends AbstractTestConstants<SSTemplateConstants> {

    @Override
    protected Class<SSTemplateConstants> getConstantClass() {
        return SSTemplateConstants.class;
    }

    @Test
    public void constantsShouldNotHaveDuplicates() {
        verifyDuplicatesWithSTTemplateConstants();
    }
}
