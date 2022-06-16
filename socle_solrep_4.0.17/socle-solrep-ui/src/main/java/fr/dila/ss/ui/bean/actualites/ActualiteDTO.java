package fr.dila.ss.ui.bean.actualites;

import java.util.Calendar;

public interface ActualiteDTO {
    Calendar getDateEmission();

    void setDateEmission(Calendar dateEmission);

    Calendar getDateValidite();

    void setDateValidite(Calendar dateValidite);

    String getObjet();

    void setObjet(String objet);

    String getContenu();

    void setContenu(String contenu);
}
