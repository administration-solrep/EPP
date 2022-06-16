package fr.dila.ss.ui.services.impl;

import static fr.dila.ss.ui.jaxrs.webobject.ajax.journal.SsJournalAjax.JOURNAL_LIST_FORM;
import static fr.dila.ss.ui.jaxrs.webobject.ajax.journal.SsJournalAjax.JOURNAL_SEARCH_FORM;
import static fr.dila.ss.ui.query.pageprovider.SSJournalPageProvider.CURRENT_DOCUMENT_PROPERTY;
import static fr.dila.st.core.service.STServiceLocator.getSTUserService;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.SPACE;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.nuxeo.ecm.platform.audit.api.BuiltinLogEntryData.LOG_CATEGORY;
import static org.nuxeo.ecm.platform.audit.api.BuiltinLogEntryData.LOG_COMMENT;
import static org.nuxeo.ecm.platform.audit.api.BuiltinLogEntryData.LOG_DOC_PATH;
import static org.nuxeo.ecm.platform.audit.api.BuiltinLogEntryData.LOG_EVENT_DATE;
import static org.nuxeo.ecm.platform.audit.api.BuiltinLogEntryData.LOG_PRINCIPAL_NAME;

import fr.dila.ss.ui.bean.JournalDossierListingDTO;
import fr.dila.ss.ui.bean.JournalDossierResultList;
import fr.dila.ss.ui.services.SSJournalUIService;
import fr.dila.ss.ui.th.bean.JournalDossierForm;
import fr.dila.ss.ui.th.bean.JournalSearchForm;
import fr.dila.st.core.util.DateUtil;
import fr.dila.st.core.util.ObjectHelper;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.core.util.SolonDateConverter;
import fr.dila.st.ui.th.model.SpecificContext;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.platform.query.api.PageProvider;

public abstract class AbstractSSJournalUIServiceImpl<T extends JournalDossierResultList, U extends JournalDossierListingDTO>
    implements SSJournalUIService<T> {
    private Class<T> resultDTOClass;
    private Class<U> listingDTOClass;

    protected AbstractSSJournalUIServiceImpl(Class<T> t, Class<U> u) {
        this.resultDTOClass = t;
        this.listingDTOClass = u;
    }

    @Override
    public T getJournalDTO(SpecificContext context) {
        PageProvider<Map<String, Serializable>> pp = getJournalPageProvider(context);

        List<Map<String, Serializable>> page = pp.getCurrentPage();
        List<U> dtos = page.stream().map(this::toListingDTO).collect(toList());

        T resultDTO = getTypeInstance(resultDTOClass);
        resultDTO.setListe(dtos);
        resultDTO.setNbTotal(Math.toIntExact(pp.getResultsCount()));

        return resultDTO;
    }

    protected U toListingDTO(Map<String, Serializable> log) {
        U listingDTO = getTypeInstance(listingDTOClass);
        listingDTO.setCategorie(Objects.toString(log.get(LOG_CATEGORY), EMPTY));
        listingDTO.setCommentaire(
            ResourceHelper.translateKeysInString(Objects.toString(log.get(LOG_COMMENT), EMPTY), SPACE)
        );
        String date = DateUtil.convertFromTimestamp(Objects.toString(log.get(LOG_EVENT_DATE), EMPTY));
        listingDTO.setDate(date);
        listingDTO.setUtilisateur(Objects.toString(log.get(LOG_PRINCIPAL_NAME), EMPTY));
        listingDTO.setPoste(Objects.toString(log.get(LOG_DOC_PATH), EMPTY));
        return listingDTO;
    }

    protected PageProvider<Map<String, Serializable>> getJournalPageProvider(SpecificContext context) {
        JournalDossierForm form = ObjectHelper.requireNonNullElseGet(
            context.getFromContextData(JOURNAL_LIST_FORM),
            JournalDossierForm::new
        );
        PageProvider<Map<String, Serializable>> pp = form.getPageProvider(
            context.getSession(),
            getProviderName(),
            null,
            Arrays.asList(getJournalParameters(context))
        );

        pp.setProperties(getJournalProperties(context, pp));

        return pp;
    }

    private Object[] getJournalParameters(SpecificContext context) {
        JournalSearchForm searchForm = context.getFromContextData(JOURNAL_SEARCH_FORM);
        String cat = null;
        String user = null;
        Date debut = null;
        Date fin = null;
        String refDos = null;
        if (searchForm != null) {
            cat = searchForm.getCategorie();
            user = getSTUserService().getLegacyUserFullName(searchForm.getUtilisateurKey());
            String dateDeb = searchForm.getDateDebut();
            debut = isBlank(dateDeb) ? null : SolonDateConverter.DATE_SLASH.parseToDate(dateDeb);
            String dateFin = searchForm.getDateFin();
            fin = isBlank(dateFin) ? null : SolonDateConverter.DATE_SLASH.parseToDate(dateFin);
            refDos = searchForm.getReferenceDossier();
        }
        return new Object[] { cat, user, debut, fin, refDos };
    }

    private Map<String, Serializable> getJournalProperties(
        SpecificContext context,
        PageProvider<Map<String, Serializable>> pp
    ) {
        Map<String, Serializable> props = pp.getProperties();
        props.put(CURRENT_DOCUMENT_PROPERTY, context.getCurrentDocument());
        return props;
    }

    private <V> V getTypeInstance(Class<V> clazz) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (
            InstantiationException
            | IllegalAccessException
            | IllegalArgumentException
            | InvocationTargetException
            | NoSuchMethodException
            | SecurityException e
        ) {
            throw new NuxeoException(String.format("Could not instantiate [%s]", clazz.getName()), e);
        }
    }

    protected abstract String getProviderName();
}
