package fr.dila.ss.api.actualite;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface Actualite {
    LocalDate getDateEmission();

    void setDateEmission(LocalDate dateEmission);

    LocalDate getDateValidite();

    void setDateValidite(LocalDate dateValidite);

    String getObjet();

    void setObjet(String objet);

    String getContenu();

    void setContenu(String contenu);

    boolean getHasPj();

    void setHasPj(boolean hasPj);

    boolean getIsInHistorique();

    void setIsInHistorique(boolean historiser);

    List<Map<String, Serializable>> getPiecesJointes();

    void setPiecesJointes(List<Map<String, Serializable>> fichiers);

    List<String> getLecteurs();

    void setLecteurs(List<String> lecteurs);
}
