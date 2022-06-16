package fr.dila.st.ui.services.impl;

import fr.dila.st.api.organigramme.GouvernementNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.core.organigramme.GouvernementNodeImpl;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.core.util.SolonDateConverter;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.services.STGouvernementUIService;
import fr.dila.st.ui.th.bean.GouvernementForm;
import fr.dila.st.ui.th.model.SpecificContext;

public class STGouvernementUIServiceImpl implements STGouvernementUIService {

    @Override
    public GouvernementForm getGouvernementForm(SpecificContext context) {
        String identifiant = context.getFromContextData(STContextDataKey.ID);
        OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();
        GouvernementNode gouvernement = organigrammeService.getOrganigrammeNodeById(
            identifiant,
            OrganigrammeType.GOUVERNEMENT
        );
        return convertGvtNodeToGvtForm(gouvernement);
    }

    @Override
    public void createGouvernement(SpecificContext context) {
        GouvernementForm gouvernementForm = context.getFromContextData(STContextDataKey.GVT_FORM);

        // convertir l'objet GouvernementForm -> GouvernementNode
        GouvernementNode gouvernementNode = convertGvtFormToGvtNode(gouvernementForm);
        STServiceLocator.getSTGouvernementService().createGouvernement(gouvernementNode);
        context
            .getMessageQueue()
            .addSuccessToQueue(ResourceHelper.getString("organigramme.success.create.gouvernement"));
    }

    @Override
    public void updateGouvernement(SpecificContext context) {
        GouvernementForm gouvernementForm = context.getFromContextData(STContextDataKey.GVT_FORM);
        String identifiant = gouvernementForm.getId();

        OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();
        // Récupérer le ministère à partir du param
        GouvernementNode gouvernement = organigrammeService.getOrganigrammeNodeById(
            identifiant,
            OrganigrammeType.GOUVERNEMENT
        );
        // met à jour les données
        GouvernementNode gouvernementNode = updateGvtNode(gouvernement, gouvernementForm);
        STServiceLocator.getSTGouvernementService().updateGouvernement(gouvernementNode);
        context
            .getMessageQueue()
            .addSuccessToQueue(ResourceHelper.getString("organigramme.success.update.gouvernement"));
    }

    /**
     * Converti un GouvernementNode en GouvernementForm
     *
     * @param node
     *            bean (back) to convert
     * @param dto
     *            current node (ici correspond au gouvernement)
     * @return
     */
    private static GouvernementForm convertGvtNodeToGvtForm(GouvernementNode node) {
        GouvernementForm gouvernementForm = new GouvernementForm();
        gouvernementForm.setId(node.getId());
        gouvernementForm.setAppellation(node.getLabel());
        if (node.getDateDebut() != null) {
            gouvernementForm.setDateDebut(SolonDateConverter.DATE_SLASH.format(node.getDateDebut()));
        }
        if (node.getDateFin() != null) {
            gouvernementForm.setDateFin(SolonDateConverter.DATE_SLASH.format(node.getDateFin()));
        }

        return gouvernementForm;
    }

    /**
     * Converti le SwBean GouvernementForm en GouvernementNode
     *
     * @param form
     *            bean (front)
     * @return
     */
    private static GouvernementNode convertGvtFormToGvtNode(GouvernementForm form) {
        GouvernementNodeImpl gouvernementNode = new GouvernementNodeImpl();
        return updateGvtNode(gouvernementNode, form);
    }

    /**
     * Met à jour GouvernementNode à partir des données du formulaire
     *
     * @param node
     *            bean to update (back)
     * @param form
     *            bean (front)
     * @return
     */
    private static GouvernementNode updateGvtNode(GouvernementNode node, GouvernementForm form) {
        node.setLabel(form.getAppellation());
        if (form.getDateDebut() != null) {
            node.setDateDebut(SolonDateConverter.DATE_SLASH.parseToDateOrNull(form.getDateDebut()));
        }
        if (form.getDateFin() != null) {
            node.setDateFin(SolonDateConverter.DATE_SLASH.parseToDateOrNull(form.getDateFin()));
        }
        return node;
    }
}
