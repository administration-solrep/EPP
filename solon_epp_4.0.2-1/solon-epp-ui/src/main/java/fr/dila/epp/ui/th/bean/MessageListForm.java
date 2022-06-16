package fr.dila.epp.ui.th.bean;

import com.google.gson.Gson;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.ui.annot.SwBean;
import fr.dila.st.ui.bean.FormSort;
import fr.dila.st.ui.enums.SortOrder;
import fr.dila.st.ui.th.bean.AbstractSortablePaginationForm;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.ws.rs.FormParam;
import javax.ws.rs.QueryParam;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.SortInfo;

@SwBean
public class MessageListForm extends AbstractSortablePaginationForm {
    public static final String ID_DOSSIER_TRI = "idDossierTri";
    public static final String OBJET_DOSSIER_TRI = "objetDossierTri";
    public static final String EMETTEUR_TRI = "emetteurTri";
    public static final String DESTINATAIRE_TRI = "destinataireTri";
    public static final String COPIE_TRI = "copieTri";
    public static final String COMMUNICATION_TRI = "communicationTri";
    public static final String DATE_TRI = "dateTri";

    private static final String XPATH_FORMAT = "%s.%s:%s";
    private static final String EVENEMENT_ALIAS = "e";
    private static final String VERSION_ALIAS = "v";
    private static final String MESSAGE_ALIAS = "m";

    @SuppressWarnings("unchecked")
    public MessageListForm(String json) {
        super();
        Gson gson = new Gson();
        Map<String, Object> map = gson.fromJson(json, Map.class);

        if (map != null) {
            idDossier = SortOrder.fromValue((String) map.get(ID_DOSSIER_TRI));
            objetDossier = SortOrder.fromValue((String) map.get(OBJET_DOSSIER_TRI));
            emetteur = SortOrder.fromValue((String) map.get(EMETTEUR_TRI));
            destinataire = SortOrder.fromValue((String) map.get(DESTINATAIRE_TRI));
            copie = SortOrder.fromValue((String) map.get(COPIE_TRI));
            communication = SortOrder.fromValue((String) map.get(COMMUNICATION_TRI));
            date = SortOrder.fromValue((String) map.get(DATE_TRI));
            if (StringUtils.isNotEmpty((String) map.get(PAGE_PARAM_NAME))) {
                setPage((String) map.get(PAGE_PARAM_NAME));
            }
            if (StringUtils.isNotEmpty((String) map.get(SIZE_PARAM_NAME))) {
                setSize((String) map.get(SIZE_PARAM_NAME));
            }
        }
    }

    @QueryParam(ID_DOSSIER_TRI)
    @FormParam(ID_DOSSIER_TRI)
    private SortOrder idDossier;

    @QueryParam(OBJET_DOSSIER_TRI)
    @FormParam(OBJET_DOSSIER_TRI)
    private SortOrder objetDossier;

    @QueryParam(EMETTEUR_TRI)
    @FormParam(EMETTEUR_TRI)
    private SortOrder emetteur;

    @QueryParam(DESTINATAIRE_TRI)
    @FormParam(DESTINATAIRE_TRI)
    private SortOrder destinataire;

    @QueryParam(COPIE_TRI)
    @FormParam(COPIE_TRI)
    private SortOrder copie;

    @QueryParam(COMMUNICATION_TRI)
    @FormParam(COMMUNICATION_TRI)
    private SortOrder communication;

    @QueryParam(DATE_TRI)
    @FormParam(DATE_TRI)
    private SortOrder date;

    public MessageListForm() {
        super();
    }

    public SortOrder getIdDossier() {
        return idDossier;
    }

    public void setIdDossier(SortOrder idDossier) {
        this.idDossier = idDossier;
    }

    public SortOrder getObjetDossier() {
        return objetDossier;
    }

    public void setObjetDossier(SortOrder objetDossier) {
        this.objetDossier = objetDossier;
    }

    public SortOrder getEmetteur() {
        return emetteur;
    }

    public void setEmetteur(SortOrder emetteur) {
        this.emetteur = emetteur;
    }

    public SortOrder getDestinataire() {
        return destinataire;
    }

    public void setDestinataire(SortOrder destinataire) {
        this.destinataire = destinataire;
    }

    public SortOrder getCopie() {
        return copie;
    }

    public void setCopie(SortOrder copie) {
        this.copie = copie;
    }

    public SortOrder getCommunication() {
        return communication;
    }

    public void setCommunication(SortOrder communication) {
        this.communication = communication;
    }

    public SortOrder getDate() {
        return date;
    }

    public void setDate(SortOrder date) {
        this.date = date;
    }

    @Override
    public final List<SortInfo> getSortInfos() {
        List<SortInfo> sorting = new ArrayList<>();

        createSortInfo(
            getIdDossier(),
            String.format(
                XPATH_FORMAT,
                EVENEMENT_ALIAS,
                SolonEppSchemaConstant.EVENEMENT_SCHEMA_PREFIX,
                SolonEppSchemaConstant.EVENEMENT_DOSSIER_PROPERTY
            ),
            sorting
        );
        createSortInfo(
            getObjetDossier(),
            String.format(
                XPATH_FORMAT,
                VERSION_ALIAS,
                SolonEppSchemaConstant.VERSION_SCHEMA_PREFIX,
                SolonEppSchemaConstant.VERSION_OBJET_PROPERTY
            ),
            sorting
        );
        createSortInfo(
            getEmetteur(),
            String.format(
                XPATH_FORMAT,
                EVENEMENT_ALIAS,
                SolonEppSchemaConstant.EVENEMENT_SCHEMA_PREFIX,
                SolonEppSchemaConstant.EVENEMENT_EMETTEUR_PROPERTY
            ),
            sorting
        );
        createSortInfo(
            getDestinataire(),
            String.format(
                XPATH_FORMAT,
                EVENEMENT_ALIAS,
                SolonEppSchemaConstant.EVENEMENT_SCHEMA_PREFIX,
                SolonEppSchemaConstant.EVENEMENT_DESTINATAIRE_PROPERTY
            ),
            sorting
        );
        createSortInfo(
            getCopie(),
            String.format(
                XPATH_FORMAT,
                EVENEMENT_ALIAS,
                SolonEppSchemaConstant.EVENEMENT_SCHEMA_PREFIX,
                SolonEppSchemaConstant.EVENEMENT_DESTINATAIRE_COPIE_CONCAT_PROPERTY
            ),
            sorting
        );
        createSortInfo(
            getCommunication(),
            String.format(
                XPATH_FORMAT,
                EVENEMENT_ALIAS,
                SolonEppSchemaConstant.EVENEMENT_SCHEMA_PREFIX,
                SolonEppSchemaConstant.EVENEMENT_TYPE_EVENEMENT_PROPERTY
            ),
            sorting
        );
        createSortInfo(
            getDate(),
            String.format(
                XPATH_FORMAT,
                MESSAGE_ALIAS,
                SolonEppSchemaConstant.CASE_LINK_SCHEMA_PREFIX,
                STSchemaConstant.CASE_LINK_DATE_PROPERTY
            ),
            sorting
        );
        return sorting;
    }

    @Override
    protected void setDefaultSort() {
        date = SortOrder.DESC;
    }

    private static void createSortInfo(SortOrder sortOrder, String xpath, List<SortInfo> lstSorting) {
        if (sortOrder != null) {
            lstSorting.add(new SortInfo(xpath, SortOrder.isAscending(sortOrder)));
        }
    }

    @Override
    protected Map<String, FormSort> getSortForm() {
        return getSortInfos()
            .stream()
            .collect(
                Collectors.toMap(
                    SortInfo::getSortColumn,
                    sortInfo -> new FormSort(SortOrder.fromAscending(sortInfo.getSortAscending()))
                )
            );
    }
}
