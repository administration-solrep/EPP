package fr.dila.st.core.service.organigramme;

import fr.dila.st.api.organigramme.GouvernementNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.api.service.organigramme.STGouvernementService;
import fr.dila.st.core.organigramme.GouvernementNodeImpl;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.DateUtil;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.nuxeo.ecm.core.api.NuxeoException;

/**
 *
 */
public class STGouvernementServiceImpl implements STGouvernementService {
    private static final String CUR_GOUVERNEMENT_QUERY =
        "SELECT g FROM GouvernementNode g WHERE g.dateFin is null OR g.dateFin > :curDate ORDER BY g.dateDebut ASC";
    private static final String ALL_GOUVERNEMENT_QUERY = "SELECT g FROM GouvernementNode g ORDER BY dateDebut ASC";
    private static final String ALL_ACTIVE_GOUVERNEMENT_QUERY =
        "SELECT g FROM GouvernementNode g WHERE g.dateFin IS NULL ORDER BY dateDebut ASC";

    public STGouvernementServiceImpl() {
        // do nothing
    }

    @Override
    public List<GouvernementNode> getGouvernementList() {
        try {
            return getOrganigrammeService().query(ALL_GOUVERNEMENT_QUERY, null);
        } catch (NuxeoException e) {
            throw new NuxeoException("Erreur de récupération de la liste des gouvernements", e);
        }
    }

    @Override
    public List<GouvernementNode> getActiveGouvernementList() {
        try {
            return getOrganigrammeService().query(ALL_ACTIVE_GOUVERNEMENT_QUERY, null);
        } catch (NuxeoException e) {
            throw new NuxeoException("Erreur de récupération de la liste des gouvernements", e);
        }
    }

    @Override
    public GouvernementNode getCurrentGouvernement() {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("curDate", Calendar.getInstance());
            List<GouvernementNode> nodes = getOrganigrammeService().query(CUR_GOUVERNEMENT_QUERY, params);
            return nodes.get(0);
        } catch (NuxeoException e) {
            throw new NuxeoException("Erreur de récupération du gouvernement courant", e);
        }
    }

    @Override
    public GouvernementNode getGouvernement(String gouvernementId) {
        return getOrganigrammeService().getOrganigrammeNodeById(gouvernementId, OrganigrammeType.GOUVERNEMENT);
    }

    @Override
    public GouvernementNode getBareGouvernementModel() {
        return new GouvernementNodeImpl();
    }

    @Override
    public void createGouvernement(GouvernementNode newGouvernement) {
        getOrganigrammeService().createNode(newGouvernement);
    }

    @Override
    public void updateGouvernement(GouvernementNode gouvernement) {
        getOrganigrammeService().updateNode(gouvernement, Boolean.TRUE);
    }

    @Override
    public void setDateNewGvt(String currentGouvernement, String nextGouvernement) {
        GouvernementNode currentGouvNode = getGouvernement(currentGouvernement);
        GouvernementNode nextGouvNode = getGouvernement(nextGouvernement);

        // mise à jour de la date fin à 23h59
        Calendar cal = DateUtil.toCalendarFromNotNullDate(nextGouvNode.getDateDebut());
        cal.add(Calendar.SECOND, -1);
        currentGouvNode.setDateFin(cal.getTime());

        List<OrganigrammeNode> nodesToUpdate = new ArrayList<>();
        nodesToUpdate.add(currentGouvNode);
        nodesToUpdate.add(nextGouvNode);
        getOrganigrammeService().updateNodes(nodesToUpdate, false);
    }

    private OrganigrammeService getOrganigrammeService() {
        return STServiceLocator.getOrganigrammeService();
    }
}
