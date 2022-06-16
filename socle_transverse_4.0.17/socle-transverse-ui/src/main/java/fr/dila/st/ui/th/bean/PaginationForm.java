package fr.dila.st.ui.th.bean;

import com.google.common.collect.ImmutableList;
import fr.dila.st.core.util.STPageProviderHelper;
import fr.dila.st.ui.annot.SwBean;
import fr.dila.st.ui.validators.annot.SwListValues;
import fr.dila.st.ui.validators.annot.SwMin;
import fr.dila.st.ui.validators.annot.SwRegex;
import java.util.List;
import javax.ws.rs.FormParam;
import javax.ws.rs.QueryParam;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.platform.query.api.PageProvider;

@SwBean(keepdefaultValue = true)
public class PaginationForm {
    public static final String PAGE_PARAM_NAME = "page";
    public static final String SIZE_PARAM_NAME = "size";
    private static final String INDEX_PARAM_NAME = "index";
    public static final String TOTAL_PARAM_NAME = "nbTotal";

    protected static final int DEFAULT_PAGE = 1;

    private static final String STRING_SIZE_10 = "10";
    private static final String STRING_SIZE_20 = "20";
    private static final String STRING_SIZE_50 = "50";
    private static final List<String> STRING_SIZES = ImmutableList.of(STRING_SIZE_10, STRING_SIZE_20, STRING_SIZE_50);

    /** Le nombre d'élément par page par défaut */
    protected static final int DEFAULT_SIZE = Integer.parseInt(STRING_SIZE_10);
    protected static final int SIZE_20 = Integer.parseInt(STRING_SIZE_20);
    protected static final int SIZE_50 = Integer.parseInt(STRING_SIZE_50);
    private static final List<Integer> SIZES = ImmutableList.of(DEFAULT_SIZE, SIZE_20, SIZE_50);

    @SwMin(1)
    @QueryParam(PAGE_PARAM_NAME)
    @FormParam(PAGE_PARAM_NAME)
    private Integer page;

    @SwListValues({ STRING_SIZE_10, STRING_SIZE_20, STRING_SIZE_50 })
    @QueryParam(SIZE_PARAM_NAME)
    @FormParam(SIZE_PARAM_NAME)
    private int size;

    @SwRegex("\\p{Lu}")
    @QueryParam(INDEX_PARAM_NAME)
    @FormParam(INDEX_PARAM_NAME)
    private String index;

    @QueryParam(TOTAL_PARAM_NAME)
    @FormParam(TOTAL_PARAM_NAME)
    private Integer total;

    public PaginationForm() {
        this(DEFAULT_PAGE, DEFAULT_SIZE);
    }

    public PaginationForm(int size) {
        this(DEFAULT_PAGE, size);
    }

    public PaginationForm(int page, int size) {
        this.page = page;
        this.size = size;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        if (page == null || page > 0) {
            this.page = page;
        }
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        if (SIZES.contains(size)) {
            this.size = size;
        }
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    protected void setPage(String page) {
        Integer pageValue = null;
        if (page != null) {
            pageValue = Integer.parseInt(page);
        }
        setPage(pageValue);
    }

    protected void setSize(String size) {
        if (size != null) {
            setSize(Integer.parseInt(size));
        }
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public void noPagination() {
        size = 0;
    }

    public int getStartElement() {
        if (page != null && page > 0 && size > 0) {
            return (page - 1) * size;
        }
        return 0;
    }

    public int getNbPage(int nbTotal) {
        int nbPage = 0;

        if (nbTotal > 0 && size > 0) {
            if (nbTotal % size == 0) {
                nbPage = nbTotal / size;
            } else {
                nbPage = nbTotal / size + 1;
            }
        }
        return nbPage;
    }

    public int getDefaultSize() {
        return DEFAULT_SIZE;
    }

    public static List<String> getAllSizes() {
        return STRING_SIZES;
    }

    public <T extends PageProvider<?>> T getPageProvider(CoreSession session, String pageProviderName) {
        return getPageProvider(session, pageProviderName, null);
    }

    public <T extends PageProvider<?>> T getPageProvider(
        CoreSession session,
        String pageProviderName,
        List<Object> lstParams
    ) {
        T pageProvider = STPageProviderHelper.getPageProvider(pageProviderName, session);
        pageProvider.setPageSize(getSize());

        if (getPage() != null && getPage() > 0) {
            pageProvider.setCurrentPageIndex(getPage() - 1L);
        } else {
            pageProvider.setCurrentPageIndex(0);
        }
        pageProvider.setMaxPageSize(getSize());

        if (lstParams != null) {
            pageProvider.setParameters(lstParams.toArray());
        }

        return pageProvider;
    }
}
