package fr.dila.st.core.operation.utils;

import java.util.List;
import org.nuxeo.ecm.automation.OperationContext;

public abstract class AbstractParametreOperation {

    protected abstract OperationContext getContext();

    protected abstract List<ParametreBean> getParametreBeans();
}
