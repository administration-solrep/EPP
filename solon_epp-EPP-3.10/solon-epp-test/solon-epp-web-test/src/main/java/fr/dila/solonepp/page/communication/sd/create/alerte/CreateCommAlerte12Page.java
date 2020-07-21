package fr.dila.solonepp.page.communication.sd.create.alerte;

import fr.dila.solonepp.page.AlertePage;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.sd.detail.alerte.DetailCommAlerte12Page;

public class CreateCommAlerte12Page extends AlertePage {

    public static final String TYPE_COMM = "Alerte - Déclaration sur un sujet déterminé";
    
    public static final String COMMUNICATION = "nxw_metadonnees_evenement_libelle";
    
    @Override
    protected Class<? extends AbstractDetailComm> getCreateResultPageClass() {
        return DetailCommAlerte12Page.class;
    }

    public DetailCommAlerte12Page createCommAlerte12Page(String destinataire ) {
        checkValue(COMMUNICATION, TYPE_COMM);
        selectInOrganigramme(destinataire, DESTINATAIRE);
        publier();
        return getPage(DetailCommAlerte12Page.class);
    }
}