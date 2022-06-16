package fr.dila.ss.ui.services.impl;

import static java.util.stream.Collectors.toList;

import fr.dila.ss.core.dto.admin.ExportJournalTechniqueListingDTO;
import fr.dila.ss.core.util.ExportUtils;
import fr.dila.ss.core.util.SSExcelUtil;
import fr.dila.ss.ui.bean.JournalTechniqueListingDTO;
import fr.dila.ss.ui.bean.JournalTechniqueResultList;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.query.pageprovider.SSJournalAdminPageProvider;
import fr.dila.ss.ui.services.SSJournalAdminUIService;
import fr.dila.st.core.util.ObjectHelper;
import fr.dila.st.ui.th.model.SpecificContext;
import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.activation.DataSource;
import org.nuxeo.ecm.platform.query.api.PageProvider;

public class SSJournalAdminUIServiceImpl
    extends AbstractSSJournalUIServiceImpl<JournalTechniqueResultList, JournalTechniqueListingDTO>
    implements SSJournalAdminUIService {

    public SSJournalAdminUIServiceImpl() {
        super(JournalTechniqueResultList.class, JournalTechniqueListingDTO.class);
    }

    @Override
    protected JournalTechniqueListingDTO toListingDTO(Map<String, Serializable> log) {
        JournalTechniqueListingDTO dto = super.toListingDTO(log);
        dto.setReferenceDossier((String) log.get("dossierRef"));
        return dto;
    }

    @Override
    protected String getProviderName() {
        // provider should be contributed in higher level applications and
        // should implement SSJournalAdminPageProvider
        return "ADMIN_JOURNAL_DOSSIER";
    }

    @Override
    public JournalTechniqueResultList getJournalDTO(SpecificContext context) {
        SSJournalAdminPageProvider pp = (SSJournalAdminPageProvider) getJournalPageProvider(context);
        List<Map<String, Serializable>> page = pp.getCurrentPage();
        List<JournalTechniqueListingDTO> dtos = page.stream().map(this::toListingDTO).collect(toList());

        JournalTechniqueResultList resultDTO = new JournalTechniqueResultList();
        resultDTO.setListe(dtos);
        resultDTO.setNbTotal(Math.toIntExact(pp.getResultsCount()));

        return resultDTO;
    }

    @Override
    protected PageProvider<Map<String, Serializable>> getJournalPageProvider(SpecificContext context) {
        SSJournalAdminPageProvider journalPageProvider = (SSJournalAdminPageProvider) super.getJournalPageProvider(
            context
        );
        Boolean isExport = ObjectHelper.requireNonNullElse(
            context.getFromContextData(SSContextDataKey.IS_EXPORT),
            false
        );
        if (isExport) {
            journalPageProvider.setForceNonPaginatedForExcel();
        }
        return journalPageProvider;
    }

    @Override
    public File exportJournal(SpecificContext context) {
        context.putInContextData(SSContextDataKey.IS_EXPORT, true);
        List<ExportJournalTechniqueListingDTO> liste = getJournalDTO(context)
            .getListe()
            .stream()
            .map(JournalTechniqueListingDTO.class::cast)
            .map(
                dto ->
                    new ExportJournalTechniqueListingDTO(
                        dto.getDate(),
                        dto.getUtilisateur(),
                        dto.getPoste(),
                        dto.getCategorie(),
                        dto.getCommentaire(),
                        dto.getReferenceDossier()
                    )
            )
            .collect(Collectors.toList());

        DataSource data = SSExcelUtil.exportJournalTechnique(context.getSession(), liste);
        return ExportUtils.createXlsOrPdfFromDataSource(data, false);
    }
}
