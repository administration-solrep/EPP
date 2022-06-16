package fr.dila.ss.ui.services.impl;

import static fr.dila.ss.core.service.SSServiceLocator.getSSUserService;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import fr.dila.solon.birt.common.BirtOutputFormat;
import fr.dila.ss.core.dto.supervision.SupervisionUserDTO;
import fr.dila.ss.core.util.ExportUtils;
import fr.dila.ss.core.util.SSExcelUtil;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.services.SSSupervisionUIService;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.ui.th.bean.UsersListForm;
import fr.dila.st.ui.th.model.SpecificContext;
import java.io.File;
import java.util.Calendar;
import java.util.List;
import javax.activation.DataSource;

public class SSSupervisionUIServiceImpl implements SSSupervisionUIService {
    public static final STLogger LOGGER = STLogFactory.getLog(SSSupervisionUIService.class);

    @Override
    public List<SupervisionUserDTO> getAllUserConnected(SpecificContext context) {
        UsersListForm form = context.getFromContextData(SSContextDataKey.LIST_USERS_FORM);
        return getSSUserService()
            .getAllUserConnected(form.getSortInfos())
            .stream()
            .map(this::mapUserToDto)
            .collect(toList());
    }

    private SupervisionUserDTO mapUserToDto(STUser user) {
        SupervisionUserDTO dto = new SupervisionUserDTO();
        dto.setUtilisateur(user.getUsername());
        dto.setNom(user.getLastName());
        dto.setPrenom(user.getFirstName());
        dto.setDateConnexion(user.getDateDerniereConnexion());
        return dto;
    }

    @Override
    public List<SupervisionUserDTO> getAllUserNotConnectedSince(SpecificContext context) {
        Calendar filterNotConnected = context.getFromContextData(SSContextDataKey.DATE_CONNEXION);
        if (filterNotConnected == null) {
            return emptyList();
        }

        UsersListForm form = context.getFromContextData(SSContextDataKey.LIST_USERS_FORM);
        return getSSUserService()
            .getListUserNotConnectedSince(filterNotConnected.getTime(), form.getSortInfos())
            .stream()
            .map(this::mapUserToDto)
            .collect(toList());
    }

    @Override
    public File getUsersExport(SpecificContext context) {
        List<SupervisionUserDTO> users;
        if (context.getFromContextData(SSContextDataKey.DATE_CONNEXION) == null) {
            users = getAllUserConnected(context);
        } else {
            users = getAllUserNotConnectedSince(context);
        }
        boolean isPdf = BirtOutputFormat.PDF == context.getFromContextData(SSContextDataKey.BIRT_OUTPUT_FORMAT);
        DataSource data = SSExcelUtil.exportSupervision(context.getSession(), users, isPdf);
        return ExportUtils.createXlsOrPdfFromDataSource(data, isPdf);
    }
}
