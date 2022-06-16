package fr.dila.st.core.operation.utils;

import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.service.FonctionService;
import fr.dila.st.core.service.STServiceLocator;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.util.List;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.directory.BaseSession;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.ecm.directory.api.DirectoryService;
import org.nuxeo.ecm.platform.usermanager.UserManager;

public abstract class AbstractCreateFunctionOperation {

    @OperationMethod
    public void createFunctions() {
        getFunctions().forEach(this::createFunction);
    }

    private void createFunction(FunctionBean function) {
        FonctionService functionService = STServiceLocator.getFonctionService();
        if (functionService.getFonction(function.getGroupname()) == null) {
            DocumentModel fonctionModel = BaseSession.createEntryModel(
                null,
                STSchemaConstant.BASE_FUNCTION_SCHEMA,
                null,
                null
            );
            PropertyUtil.setProperty(
                fonctionModel,
                STSchemaConstant.BASE_FUNCTION_SCHEMA,
                STSchemaConstant.BASE_FUNCTION_GROUP_NAME_PROPERTY,
                function.getGroupname()
            );
            PropertyUtil.setProperty(
                fonctionModel,
                STSchemaConstant.BASE_FUNCTION_SCHEMA,
                STSchemaConstant.BASE_FUNCTION_DESCRIPTION_PROPERTY,
                function.getDescription()
            );
            try (
                Session fonctionsSession = ServiceUtil
                    .getRequiredService(DirectoryService.class)
                    .open(STConstant.ORGANIGRAMME_BASE_FUNCTION_DIR)
            ) {
                fonctionsSession.createEntry(fonctionModel);
            }

            function.getParentGroups().forEach(parent -> addFunctionToGroup(parent, function));
        }
    }

    private void addFunctionToGroup(String parentGroupName, FunctionBean function) {
        UserManager um = STServiceLocator.getUserManager();
        DocumentModel groupModel = um.getGroupModel(parentGroupName);
        List<String> functions = PropertyUtil.getStringListProperty(
            groupModel,
            STSchemaConstant.GROUP_SCHEMA,
            STSchemaConstant.DIRECTORY_GROUP_FUNCTIONS_PROPERTY
        );
        functions.add(function.getGroupname());
        PropertyUtil.setProperty(
            groupModel,
            STSchemaConstant.GROUP_SCHEMA,
            STSchemaConstant.DIRECTORY_GROUP_FUNCTIONS_PROPERTY,
            functions
        );
        um.updateGroup(groupModel);
    }

    protected abstract List<FunctionBean> getFunctions();
}
