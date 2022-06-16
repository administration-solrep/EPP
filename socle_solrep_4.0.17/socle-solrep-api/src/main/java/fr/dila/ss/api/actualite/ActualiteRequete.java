package fr.dila.ss.api.actualite;

import java.time.LocalDate;

public interface ActualiteRequete {
    LocalDate getDateEmissionDebut();

    void setDateEmissionDebut(LocalDate dateEmissionDebut);

    LocalDate getDateEmissionFin();

    void setDateEmissionFin(LocalDate dateEmissionFin);

    LocalDate getDateValiditeDebut();

    void setDateValiditeDebut(LocalDate dateValiditeDebut);

    LocalDate getDateValiditeFin();

    void setDateValiditeFin(LocalDate dateValiditeFin);

    String getObjet();

    void setObjet(String objet);

    boolean getIsInHistorique();

    void setDansHistorique(boolean isInHistorique);

    boolean getHasPj();

    void setHasPj(boolean hasPj);
}
