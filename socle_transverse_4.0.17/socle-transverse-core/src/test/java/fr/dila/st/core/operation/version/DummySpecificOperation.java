package fr.dila.st.core.operation.version;

import fr.dila.st.core.operation.STApplication;
import fr.dila.st.core.operation.STVersion;
import org.nuxeo.ecm.automation.core.annotations.Operation;

@Operation(id = "specificOperation")
@STVersion(version = "4.0.0", application = STApplication.REPONSES)
@STVersion(version = "4.0.1", application = STApplication.EPG)
public class DummySpecificOperation {}
