package fr.dila.st.api.dao.pagination;

/**
 * Informations de pagination pour les DAO.
 *
 * @author jtremeaux
 */
public class PageInfo {
    /**
     * Taille d'une page.
     */
    private long pageSize;

    /**
     * Numéro de la page.
     */
    private long pageNumber;

    /**
     * Constructeur par défaut de PageInfo.
     */
    public PageInfo() {
        // do nothing
    }

    /**
     * Constructeur de PageInfo.
     *
     * @param pageSize
     *            Taille d'une page
     * @param pageNumber
     *            Numéro de la page
     */
    public PageInfo(long pageSize, long pageNumber) {
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
    }

    /**
     * Getter de pageSize.
     *
     * @return pageSize
     */
    public long getPageSize() {
        return pageSize;
    }

    /**
     * Setter de pageSize.
     *
     * @param pageSize
     *            pageSize
     */
    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * Getter de pageNumber.
     *
     * @return pageNumber
     */
    public long getPageNumber() {
        return pageNumber;
    }

    /**
     * Setter de pageNumber.
     *
     * @param pageNumber
     *            pageNumber
     */
    public void setPageNumber(long pageNumber) {
        this.pageNumber = pageNumber;
    }

    /**
     * Getter de offset.
     *
     * @return offset
     */
    public long getOffset() {
        return pageSize * pageNumber;
    }
}
