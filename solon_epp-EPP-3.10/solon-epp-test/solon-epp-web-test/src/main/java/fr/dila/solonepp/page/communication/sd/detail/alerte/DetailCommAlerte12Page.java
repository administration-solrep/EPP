package fr.dila.solonepp.page.communication.sd.detail.alerte;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.sd.create.alerte.CreateCommAlerte12Page;

public class DetailCommAlerte12Page extends AbstractDetailComm {

	public final static String OBJET_ID = "meta_evt:nxl_metadonnees_evenement:nxw_metadonnees_evenement_objet";
	
    @Override
    protected Class<? extends AbstractCreateComm> getModifierResultPageClass() {
        return CreateCommAlerte12Page.class;
    }

}
