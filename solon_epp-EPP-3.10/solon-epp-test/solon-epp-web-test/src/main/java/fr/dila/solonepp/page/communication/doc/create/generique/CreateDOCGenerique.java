package fr.dila.solonepp.page.communication.doc.create.generique;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.doc.detail.generique.DetailCommDOCGenerique;

public class CreateDOCGenerique extends AbstractCreateComm {
	
    public static final String TYPE_COMM = "Generique - Autres documents transmis aux assemblées";
	
	@Override
	protected Class<? extends AbstractDetailComm> getCreateResultPageClass() {
		return DetailCommDOCGenerique.class;
	}

	public DetailCommDOCGenerique createCommDOCGenerique(String destinataire, String objet) {
		getFlog().startAction("create communication DOC Generique");
		setDestinataire(destinataire);
		// setIdDossier(idDossier);
		setObjet(objet);
		// setDate(date);
		// addPieceJointe(PieceJointeType.LETTRE_PM, URL, PJ);
		getFlog().endAction();
		return this.publier();
	}
	
    public DetailCommDOCGenerique createCommGenerique15(final String destinataire ) {
        checkValue(COMMUNICATION, TYPE_COMM);
        selectInOrganigramme(destinataire, DESTINATAIRE);
        sleep(3);
        return publier();
    }

}
