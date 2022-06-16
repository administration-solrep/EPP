package fr.dila.epp.ui.services.impl;

import fr.dila.epp.ui.services.EppCorbeilleMenuService;
import fr.dila.epp.ui.services.actions.SolonEppActionsServiceLocator;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.CorbeilleTypeService;
import fr.dila.solonepp.api.service.corbeilletype.CorbeilleNode;
import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.services.impl.FragmentServiceImpl;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class EppCorbeilleMenuServiceImpl extends FragmentServiceImpl implements EppCorbeilleMenuService {
    public static final String ACTIVE_KEY = "corbeilleActivated";

    public EppCorbeilleMenuServiceImpl() {
        super();
    }

    @Override
    public Map<String, Object> getData(SpecificContext context) {
        EppPrincipal principal = (EppPrincipal) context.getSession().getPrincipal();
        Map<String, Object> data = new HashMap<>();

        CorbeilleTypeService corbeilleTypeService = ServiceUtil.getRequiredService(CorbeilleTypeService.class);
        if (StringUtils.isNotBlank(principal.getInstitutionId())) {
            List<CorbeilleNode> corbeilles = corbeilleTypeService.getCorbeilleInstitutionTreeWithCount(
                principal.getInstitutionId(),
                context.getSession()
            );
            if (
                CollectionUtils.isNotEmpty(corbeilles) &&
                CollectionUtils.isNotEmpty(corbeilles.get(0).getCorbeilleNodeList())
            ) {
                corbeilles.get(0).setOpened(true);
            }
            data.put("corbeilles", corbeilles);
            data.put(ACTIVE_KEY, UserSessionHelper.getUserSessionParameter(context, ACTIVE_KEY, String.class));
        } else {
            context.getMessageQueue().addErrorToQueue("Pas de corbeille trouvée pour l'utilisateur");
        }

        // data du select (création com créatrice)
        List<SelectValueDTO> evenementTypes = SolonEppActionsServiceLocator
            .getEvenementTypeActionService()
            .getEvenementCreateurList(context);
        data.put("evenementTypes", evenementTypes);

        return data;
    }
}
