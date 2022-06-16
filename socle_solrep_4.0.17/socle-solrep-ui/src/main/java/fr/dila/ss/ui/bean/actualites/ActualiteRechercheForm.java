package fr.dila.ss.ui.bean.actualites;

import fr.dila.st.ui.enums.SortOrder;
import java.util.Calendar;
import java.util.List;
import org.nuxeo.ecm.core.api.SortInfo;

public interface ActualiteRechercheForm {
    Integer getPage();

    void setPage(Integer page);

    int getSize();

    void setSize(int size);

    Calendar getDateEmissionDebut();

    void setDateEmissionDebut(Calendar dateEmissionDebut);

    Calendar getDateEmissionFin();

    void setDateEmissionFin(Calendar dateEmissionFin);

    String getObjet();

    void setObjet(String objet);

    Boolean getHasPj();

    void setHasPj(Boolean sansPj);

    SortOrder getDateEmissionSort();

    void setDateEmissionSort(SortOrder dateEmissionSort);

    SortOrder getObjetSort();

    void setObjetSort(SortOrder objetSort);

    SortOrder getHasPjSort();

    void setHasPjSort(SortOrder objetSort);

    List<SortInfo> getSortInfos();

    Boolean getArchivee();
}
