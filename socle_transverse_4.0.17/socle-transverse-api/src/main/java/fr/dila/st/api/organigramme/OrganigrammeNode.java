package fr.dila.st.api.organigramme;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Représentation d'un noeud de l'organigramme.
 *
 * @author Fabio Esposito
 * @author Adeline Hullot (refacto)
 */
public interface OrganigrammeNode extends Serializable {
    String getId();

    void setId(String anid);

    String getLabel();

    void setLabel(String label);

    String getLabelWithNor(String idMinistere);

    OrganigrammeType getType();

    Date getDateDebut();

    void setDateDebut(Date dateDebut);

    void setDateDebut(Calendar dateDebut);

    Date getDateFin();

    void setDateFin(Date dateFin);

    void setDateFin(Calendar dateFin);

    int getParentListSize();

    void setParentList(List<OrganigrammeNode> parentList);

    /**
     * Retourne vrai si le noeud de l'organigramme est supprimé (suppression logique).
     *
     * @return Vrai si le noeud de l'organigramme est supprimé (suppression logique)
     */
    boolean getDeleted();

    /**
     * Renseigne si le noeud de l'organigramme est supprimé (suppression logique).
     *
     * @param deleted
     *            Vrai si le noeud de l'organigramme est supprimé (suppression logique)
     */
    void setDeleted(boolean deleted);

    String getLockUserName();

    void setLockUserName(String lockUserName);

    Date getLockDate();

    void setLockDate(Date lockDate);

    void setLockDate(Calendar lockDate);

    /**
     * Retourne la liste des fonctions qui donnent le droit en lecture sur ce node
     *
     * @return
     */
    List<String> getFunctionRead();

    void setFunctionRead(List<String> functions);

    /**
     * Retourne la liste des fonctions qui donnent le droit en lecture sur ce node
     *
     * @param coreSession coreSession
     * @return
     */
    boolean isReadGranted(CoreSession coreSession);

    public boolean isActive();

    public boolean isNext();
}
