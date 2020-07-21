package fr.dila.solonepp.page.communication.pg.complete.generique;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.pg.detail.generique.DetailCommPGGenerique;
import fr.sword.xsd.solon.epp.PieceJointeType;

/**
 * 
 * @author abianchi
 * @description page d'édition pour PG générique
 * 
 */
public class CompletionPGGenerique extends AbstractCreateComm {

	public static final String AUTRES = "Autres(s)";

	private static final String URL = "http://www.url.com";
	private static final String PJ = "/attachments/piece-jointe.doc";

	public DetailCommPGGenerique completePG_GEN01(String Commentaire) {

		getFlog().startAction("Complétion données générique pg 01");

		setCommentaire(Commentaire);
		addPieceJointe(PieceJointeType.AUTRE, URL, PJ);
		DetailCommPGGenerique temp = publier("La communication a été complétée");

		getFlog().endAction();
		return temp;
	}

	@Override
	protected Class<? extends AbstractDetailComm> getCreateResultPageClass() {
		return DetailCommPGGenerique.class;
	}

}
