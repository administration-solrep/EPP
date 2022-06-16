package fr.dila.ss.ui.services;

import fr.dila.ss.core.dto.supervision.SupervisionUserDTO;
import fr.dila.st.ui.th.model.SpecificContext;
import java.io.File;
import java.util.List;

public interface SSSupervisionUIService {
    List<SupervisionUserDTO> getAllUserConnected(SpecificContext context);

    List<SupervisionUserDTO> getAllUserNotConnectedSince(SpecificContext context);

    File getUsersExport(SpecificContext context);
}
