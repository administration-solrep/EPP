package fr.dila.solonepp.api.service.rechercherMessage;

import java.util.List;

import fr.dila.solonepp.api.dao.criteria.MessageCriteria;
import fr.dila.st.api.dao.pagination.PageInfo;

/**
 * Données de la requête pour rechercher un message.
 * 
 * @author sly
 * @author jtremeaux
 */
public class RechercherMessageRequest {
    /**
     * Recherche par critères de recherche.
     */
    private MessageCriteria messageCriteria;
    
    /**
     * Recherche "documentaire".
     */
    private String queryString;
    
    /**
     * requete de la recherche "xsd".
     */
    private String parametrizedQuery;

    /**
     * parametre de la recherche "xsd".
     */
    private List<Object> paramList;
    
    /**
     * Données de pagination.
     */
    private PageInfo pageInfo;

    /**
     * Getter de messageCriteria.
     *
     * @return messageCriteria
     */
    public MessageCriteria getMessageCriteria() {
        return messageCriteria;
    }

    /**
     * Setter de messageCriteria.
     *
     * @param messageCriteria messageCriteria
     */
    public void setMessageCriteria(MessageCriteria messageCriteria) {
        this.messageCriteria = messageCriteria;
    }

    /**
     * Getter de pageInfo.
     *
     * @return pageInfo
     */
    public PageInfo getPageInfo() {
        return pageInfo;
    }

    /**
     * Setter de pageInfo.
     *
     * @param pageInfo pageInfo
     */
    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    /**
     * Getter de queryString.
     *
     * @return queryString
     */
    public String getQueryString() {
        return queryString;
    }

    /**
     * Setter de queryString.
     *
     * @param queryString queryString
     */
    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public void setParamList(List<Object> paramList) {
        this.paramList = paramList;
    }

    public List<Object> getParamList() {
        return paramList;
    }

    public void setParametrizedQuery(String parametrizedQuery) {
        this.parametrizedQuery = parametrizedQuery;
    }

    public String getParametrizedQuery() {
        return parametrizedQuery;
    }
}
