package fr.dila.ss.core.pdf;

import static fr.dila.ss.api.constant.SSFondDeDossierConstants.FOND_DE_DOSSIER_FICHIER_DOCUMENT_TYPE;
import static fr.dila.ss.core.util.SSPdfUtil.FORMAT_A4_HEIGHT_PAYSAGE_SIZE;
import static fr.dila.ss.core.util.SSPdfUtil.FORMAT_A4_MARGIN_SIZE;
import static fr.dila.ss.core.util.SSPdfUtil.FORMAT_A4_WIDTH_PAYSAGE_SIZE;

import com.google.common.collect.ImmutableMap;
import fr.dila.ss.api.domain.feuilleroute.StepFolder;
import fr.dila.ss.api.feuilleroute.SSRouteStep;
import fr.dila.ss.api.service.MailboxPosteService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.constant.STVocabularyConstants;
import fr.dila.st.api.dossier.STDossier;
import fr.dila.st.api.service.MailboxService;
import fr.dila.st.api.service.VocabularyService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.ObjectHelper;
import fr.dila.st.core.util.SolonDateConverter;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.model.RouteFolderElement;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.model.RouteTableElement;
import java.io.File;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocument1;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation;

public abstract class AbstractPdfDossierService<T extends STDossier> {
    protected static final String ROUTE_SERIAL_VERTICAL_PNG = "pdf/route_serial_vertical.png";
    protected static final String ROUTE_PARALLEL_PNG = "pdf/route_parallel.png";

    protected static final String FOND_BLEU = "5c75a2";
    protected static final String FOND_BLANC = "ffffff";
    protected static final String FOND_HEADER = "cccccc";

    protected static final int SIZE_TITRE1 = 18;
    protected static final int SIZE_TITRE2 = 16;
    protected static final int SIZE_TITRE3 = 14;
    protected static final int SIZE_CONTENU = 12;
    protected static final int SIZE_SPACING = 20;

    private static final Map<Predicate<SSRouteStep>, String> VALIDATION_ICONS = new ImmutableMap.Builder<Predicate<SSRouteStep>, String>()
        .put(SSRouteStep::isEtapeAvenir, "pdf/icon_etape_a_venir.png")
        .put(SSRouteStep::isRunning, "pdf/icon_etape_en_cours.png")
        .put(SSRouteStep::isValideManuellement, "pdf/icon_valide_manuellement.png")
        .put(SSRouteStep::isInvalide, "pdf/icon_invalide.png")
        .put(SSRouteStep::isValidationAuto, "pdf/icon_validation_auto.png")
        .put(SSRouteStep::isNonConcerne, "pdf/icon_non_concerne.png")
        .build();

    protected static List<File> convertDocumentModelsBlobsToPdf(List<DocumentModel> files) {
        return files
            .stream()
            .filter(d -> FOND_DE_DOSSIER_FICHIER_DOCUMENT_TYPE.equals(d.getType()))
            .map(d -> d.getAdapter(fr.dila.st.api.domain.file.File.class))
            .map(fr.dila.st.api.domain.file.File::getContent)
            .filter(Objects::nonNull)
            .map(SSServiceLocator.getSSPdfService()::convertToPdf)
            .map(Blob::getFile)
            .collect(Collectors.toList());
    }

    protected File generatePdf(CoreSession session, T dossier, XWPFDocument document) {
        String filename = generateDossierFileName(session, dossier);
        return SSServiceLocator.getSSPdfService().generatePdf(filename, document);
    }

    protected abstract String generateDossierFileName(CoreSession session, T dossier);

    protected String getValidationIcon(SSRouteStep routeStep) {
        return VALIDATION_ICONS
            .entrySet()
            .stream()
            .filter(entry -> entry.getKey().test(routeStep))
            .findFirst()
            .map(Map.Entry::getValue)
            .orElse("");
    }

    protected String getObligatoireIcon(SSRouteStep routeStep) {
        String val = "";
        if (routeStep.isObligatoireMinistere() || routeStep.isObligatoireSGG()) {
            if (routeStep.isRunning()) {
                val = "pdf/icon_obligatoire_blanc.png";
            } else {
                val = "pdf/icon_obligatoire.png";
            }
        }

        return val;
    }

    protected Map<String, String> setFeuilleDeRouteVal(SSRouteStep routeStep, CoreSession session) {
        String typeAction = routeStep.getType();
        if (StringUtils.isNotEmpty(typeAction)) {
            final VocabularyService vocabularyService = STServiceLocator.getVocabularyService();
            typeAction =
                vocabularyService.getEntryLabel(STVocabularyConstants.ROUTING_TASK_TYPE_VOCABULARY, typeAction);
        }

        String distributionMailBoxId = routeStep.getDistributionMailboxId();
        String minLabel = routeStep.getMinistereLabel();
        if (StringUtils.isNotEmpty(distributionMailBoxId) && StringUtils.isEmpty(minLabel)) {
            final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
            minLabel = mailboxPosteService.getMinisteresEditionFromMailboxId(routeStep.getDistributionMailboxId());
        }

        String posteLabel = routeStep.getPosteLabel();
        if (StringUtils.isNotEmpty(distributionMailBoxId) && StringUtils.isEmpty(posteLabel)) {
            final MailboxService mailboxService = STServiceLocator.getMailboxService();
            posteLabel = mailboxService.getMailboxTitle(session, distributionMailBoxId);
        }

        String user = routeStep.getValidationUserLabel();

        Map<String, String> feuilleRouteValMap = new HashMap<>();

        feuilleRouteValMap.put("etat", getValidationIcon(routeStep));
        feuilleRouteValMap.put("action", typeAction);
        feuilleRouteValMap.put("ministere", minLabel);
        feuilleRouteValMap.put("poste", posteLabel);
        feuilleRouteValMap.put("utilisateur", user);
        feuilleRouteValMap.put("echeance", SolonDateConverter.DATE_SLASH.format(routeStep.getDueDate()));
        feuilleRouteValMap.put("traitement", SolonDateConverter.DATE_SLASH.format(routeStep.getDateFinEtape()));
        feuilleRouteValMap.put("obligatoire", getObligatoireIcon(routeStep));

        return feuilleRouteValMap;
    }

    protected String getEtapeParallelIcon(
        RouteTableElement docRouteTableElement,
        int position,
        List<SpanPosition> rowSpanPositionList
    ) {
        List<RouteFolderElement> firtChildList = docRouteTableElement.getFirstChildFolders();
        String val = "";
        List<StepFolder> stepFolders = SSServiceLocator.getSSFeuilleRouteService().getStepFolders(docRouteTableElement);
        for (StepFolder stepFolder : stepFolders) {
            if (stepFolder.isParallel()) {
                val = ROUTE_PARALLEL_PNG;
                for (RouteFolderElement firtChild : firtChildList) {
                    if (!firtChild.isFirstChild()) {
                        rowSpanPositionList.add(
                            new SpanPosition(position, position + firtChild.getTotalChildCount() - 1)
                        );
                    }
                }
            }
        }
        return val;
    }

    protected String getEtapeSerialIcon(RouteTableElement docRouteTableElement) {
        List<StepFolder> stepFolders = SSServiceLocator.getSSFeuilleRouteService().getStepFolders(docRouteTableElement);
        return stepFolders.stream().anyMatch(StepFolder::isSerial) ? ROUTE_SERIAL_VERTICAL_PNG : "";
    }

    protected void setOrientationPaysage(XWPFDocument document) {
        CTDocument1 doc = document.getDocument();
        CTBody body = doc.getBody();

        if (!body.isSetSectPr()) {
            body.addNewSectPr();
        }
        CTSectPr section = body.getSectPr();

        if (!section.isSetPgSz()) {
            section.addNewPgSz();
        }
        CTPageSz pageSize = section.getPgSz();
        pageSize.setOrient(STPageOrientation.LANDSCAPE);
        pageSize.setW(BigInteger.valueOf(FORMAT_A4_WIDTH_PAYSAGE_SIZE));
        pageSize.setH(BigInteger.valueOf(FORMAT_A4_HEIGHT_PAYSAGE_SIZE));

        CTPageMar pgMar = ObjectHelper.requireNonNullElseGet(section.getPgMar(), section::addNewPgMar);
        pgMar.setLeft(BigInteger.valueOf(FORMAT_A4_MARGIN_SIZE));
        pgMar.setRight(BigInteger.valueOf(FORMAT_A4_MARGIN_SIZE));
        pgMar.setTop(BigInteger.valueOf(FORMAT_A4_MARGIN_SIZE));
        pgMar.setBottom(BigInteger.valueOf(FORMAT_A4_MARGIN_SIZE));
    }

    protected String setCouleurLigneEtape(SSRouteStep routeStep) {
        if (routeStep.isRunning()) {
            return FOND_BLEU;
        }
        return FOND_BLANC;
    }
}
