package fr.dila.solonepp.api.assembler;

import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.sword.xsd.solon.epp.EppBaseEvenement;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EvenementType;
import fr.sword.xsd.solon.epp.PieceJointe;
import java.util.List;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Interface commune des assembler
 *
 * @author asatre
 *
 */
public interface Assembler {
    void buildObject(Evenement evenement, Version version);

    void buildPieceJointe(List<PieceJointe> pieceJointeList);

    EvenementType getEvenementType();

    EppBaseEvenement getEppBaseEvenement();

    void buildXsd(Evenement evenement, Version version);

    void buildPieceJointeXsd(List<DocumentModel> pieceJointeDocList, boolean addPjContent);

    void setEvtInContainer(EppEvtContainer eppEvtContainerResponse);
}
