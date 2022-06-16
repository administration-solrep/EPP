package fr.dila.st.ui.th.bean;

import fr.dila.st.ui.annot.SwBeanInit;
import fr.dila.st.ui.bean.FormSort;
import fr.dila.st.ui.enums.SortOrder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.ws.rs.FormParam;
import javax.ws.rs.QueryParam;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.SortInfo;
import org.nuxeo.ecm.platform.query.api.PageProvider;

public abstract class AbstractSortablePaginationForm extends PaginationForm {
    private static final String IS_TABLE_CHANGE_EVENT_NAME = "isTableChangeEvent";

    @QueryParam(IS_TABLE_CHANGE_EVENT_NAME)
    @FormParam(IS_TABLE_CHANGE_EVENT_NAME)
    protected boolean isTableChangeEvent;

    protected AbstractSortablePaginationForm() {
        super(DEFAULT_SIZE);
    }

    protected AbstractSortablePaginationForm(int size) {
        super(size);
    }

    public boolean getIsTableChangeEvent() {
        return isTableChangeEvent;
    }

    public void setIsTableChangeEvent(boolean isTableChangeEvent) {
        this.isTableChangeEvent = isTableChangeEvent;
    }

    public List<SortInfo> getSortInfos() {
        return getSortInfos(StringUtils.EMPTY);
    }

    protected abstract Map<String, FormSort> getSortForm();

    public final List<SortInfo> getSortInfos(String prefix) {
        Map<String, FormSort> sortForm = getSortForm();
        if (sortForm == null) {
            return new ArrayList<>();
        }
        return sortForm
            .entrySet()
            .stream()
            .filter(entry -> Objects.nonNull(entry.getValue().getSortOrder()))
            .sorted(
                Comparator.comparing(s -> s.getValue().getPriority(), Comparator.nullsLast(Comparator.naturalOrder()))
            )
            .map(entry -> toSortInfo(prefix, entry))
            .collect(Collectors.toList());
    }

    private SortInfo toSortInfo(String prefix, Entry<String, FormSort> entry) {
        return new SortInfo(
            StringUtils.defaultString(prefix) + entry.getKey(),
            SortOrder.ASC == entry.getValue().getSortOrder()
        );
    }

    @SwBeanInit
    public void initSort() {
        if (!isTableChangeEvent) {
            setDefaultSort();
        }
    }

    protected abstract void setDefaultSort();

    @Override
    public <T extends PageProvider<?>> T getPageProvider(
        CoreSession session,
        String pageProviderName,
        List<Object> lstParams
    ) {
        return getPageProvider(session, pageProviderName, null, lstParams);
    }

    public <T extends PageProvider<?>> T getPageProvider(CoreSession session, String pageProviderName, String prefix) {
        return getPageProvider(session, pageProviderName, prefix, null);
    }

    public <T extends PageProvider<?>> T getPageProvider(
        CoreSession session,
        String pageProviderName,
        String prefix,
        List<Object> lstParams
    ) {
        T pageProvider = super.getPageProvider(session, pageProviderName, lstParams);
        List<SortInfo> sortInfos = prefix != null ? getSortInfos(prefix) : getSortInfos();
        if (!sortInfos.isEmpty()) { // if empty use provider sort
            pageProvider.setSortInfos(sortInfos);
        }
        return pageProvider;
    }

    /**
     * Method used by form builder
     *
     * @param form AbstractSortablePaginationForm to build
     */
    protected static <T extends AbstractSortablePaginationForm> T initForm(T form) {
        form.initSort();
        return form;
    }
}
