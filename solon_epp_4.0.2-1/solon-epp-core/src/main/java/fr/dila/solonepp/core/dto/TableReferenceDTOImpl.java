package fr.dila.solonepp.core.dto;

import fr.dila.solonepp.api.dto.TableReferenceDTO;

/**
 * DTO pour la recherche sur les tables de references
 * @author asatre
 *
 */
public class TableReferenceDTOImpl implements TableReferenceDTO {
    private String id;
    private String title;

    private TableReferenceDTOImpl() {
        //default private constructor
    }

    public TableReferenceDTOImpl(String id, String title) {
        this();
        this.id = id;
        this.title = title;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }
}
