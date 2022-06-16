package fr.dila.solonepp.core.assembler.ws;

import fr.dila.solonepp.api.service.corbeilletype.CorbeilleNode;
import fr.sword.xsd.solon.epp.ChercherCorbeilleResponse;
import fr.sword.xsd.solon.epp.Corbeille;
import fr.sword.xsd.solon.epp.Section;
import java.util.List;

/**
 * Assembleur de la hiérarchie des corbeilles objet métier <-> Web service.
 *
 * @author jtremeaux
 */
public class CorbeilleNodeAssembler {

    /**
     * Assemble les données de l'arborescence des corbeilles pour le Web Service.
     *
     * @param corbeilleNodeList Arborescence des corbeilles à assembler
     * @return Objet assemblé
     */
    public ChercherCorbeilleResponse assembleCorbeilleNodeTree(List<CorbeilleNode> corbeilleNodeList) {
        ChercherCorbeilleResponse response = new ChercherCorbeilleResponse();
        List<Section> sectionList = response.getSection();
        List<Corbeille> corbeilleList = response.getCorbeille();
        for (CorbeilleNode corbeilleNode : corbeilleNodeList) {
            if (corbeilleNode.isTypeSection()) {
                Section section = new Section();
                sectionList.add(section);

                section.setIdSection(corbeilleNode.getName());
                section.setNom(corbeilleNode.getLabel());
                section.setDescription(corbeilleNode.getDescription());
                List<Corbeille> subCorbeilleList = section.getCorbeille();
                for (CorbeilleNode subCorbeilleNode : corbeilleNode.getCorbeilleNodeList()) {
                    if (corbeilleNode.isTypeSection()) {
                        Corbeille corbeille = assembleCorbeille(subCorbeilleNode);
                        subCorbeilleList.add(corbeille);
                    }
                }
            } else if (corbeilleNode.isTypeCorbeille()) {
                Corbeille corbeille = assembleCorbeille(corbeilleNode);
                corbeilleList.add(corbeille);
            }
        }

        return response;
    }

    /**
     * Assemble un noeud de type corbeille.
     *
     * @param corbeilleNode Noeud corbeille à assembler
     * @return Noeud corbeille assemblé
     */
    protected Corbeille assembleCorbeille(CorbeilleNode corbeilleNode) {
        Corbeille corbeille = new Corbeille();
        corbeille.setIdCorbeille(corbeilleNode.getName());
        corbeille.setNom(corbeilleNode.getLabel());
        corbeille.setDescription(corbeilleNode.getDescription());
        return corbeille;
    }
}
