package fr.dila.st.ui.services.impl;

import static fr.dila.st.ui.th.bean.mapper.MapFormToNode.posteWsFormToPosteNode;

import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.core.organigramme.PosteNodeImpl;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.ObjectHelper;
import fr.dila.st.core.util.SolonDateConverter;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.services.STPosteUIService;
import fr.dila.st.ui.th.bean.PosteForm;
import fr.dila.st.ui.th.bean.PosteWsForm;
import fr.dila.st.ui.th.bean.mapper.MapFormToNode;
import fr.dila.st.ui.th.model.SpecificContext;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.NuxeoException;

public class STPosteUIServiceImpl extends STOrganigrammeUIServiceImpl<PosteNode> implements STPosteUIService {

    @Override
    public PosteForm getPosteForm(SpecificContext context) {
        PosteForm posteForm = null;
        String identifiant = context.getFromContextData(STContextDataKey.ID);
        try {
            PosteNode posteNode = getOrganigrammeService().getOrganigrammeNodeById(identifiant, OrganigrammeType.POSTE);

            lockOrganigrammeNode(context, posteNode);

            posteForm = convertPosteNodeToPosteForm(posteNode);
        } catch (Exception e) {
            throw new NuxeoException("Une erreur est survenue lors de la récupération du poste : " + identifiant, e);
        }
        return posteForm;
    }

    @Override
    public void createPoste(SpecificContext context) {
        PosteForm posteForm = context.getFromContextData(STContextDataKey.POSTE_FORM);

        // convertir l'objet PosteForm -> PosteNode
        PosteNode posteNode = convertPosteFormToPosteNode(posteForm);
        createPoste(context, posteNode);
    }

    protected void createPoste(SpecificContext context, PosteNode posteNode) {
        // sauvegarder un ministère
        STServiceLocator.getSTPostesService().createPoste(context.getSession(), posteNode);
        context.getMessageQueue().addSuccessToQueue("organigramme.success.create.poste");
    }

    @Override
    public void updatePoste(SpecificContext context) {
        PosteForm posteForm = context.getFromContextData(STContextDataKey.POSTE_FORM);
        String identifiant = posteForm.getId();

        OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();
        // Récupérer le ministère à partir du param
        PosteNode posteNode = organigrammeService.getOrganigrammeNodeById(identifiant, OrganigrammeType.POSTE);
        // met à jour les données
        updatePosteNode(posteNode, posteForm);
        updatePoste(context, posteNode);
    }

    protected void updatePoste(SpecificContext context, PosteNode posteNode) {
        performNodeUpdate(
            context,
            posteNode,
            n -> STServiceLocator.getSTPostesService().updatePoste(context.getSession(), n),
            "organigramme.success.update.poste"
        );
    }

    /**
     * Converti un PosteNode en PosteForm
     *
     * @param node
     *            bean (back) to convert
     * @return
     */
    private PosteForm convertPosteNodeToPosteForm(PosteNode node) {
        PosteForm posteForm = new PosteForm();
        posteForm.setId(node.getId());
        posteForm.setLibelle(node.getLabel());

        posteForm.setConseiller(Boolean.toString(node.isConseillerPM()));
        posteForm.setSuperviseur(Boolean.toString(node.isSuperviseurSGG()));
        posteForm.setChargeMission(Boolean.toString(node.isChargeMissionSGG()));
        posteForm.setPosteBDC(Boolean.toString(node.isPosteBdc()));
        if (posteForm.getDateDebut() != null) {
            posteForm.setDateDebut(SolonDateConverter.DATE_SLASH.format(node.getDateDebut()));
        }
        if (node.getDateFin() != null) {
            posteForm.setDateFin(SolonDateConverter.DATE_SLASH.format(node.getDateFin()));
        }
        // Gestion des ministères de rattachement
        HashMap<String, String> mapUniteStructurelle = new HashMap<>();
        ArrayList<String> lstUniteStructurelles = new ArrayList<>();
        for (UniteStructurelleNode us : node.getUniteStructurelleParentList()) {
            mapUniteStructurelle.put(us.getId(), us.getLabel());
            lstUniteStructurelles.add(us.getId());
        }
        posteForm.setMapUnitesStructurellesRattachement(mapUniteStructurelle);
        posteForm.setUnitesStructurellesRattachement(lstUniteStructurelles);

        return posteForm;
    }

    /**
     * Converti le SwBean PosteForm en PosteNode
     *
     * @param form
     *            bean (front)
     * @return
     * @throws ParseException
     */
    private PosteNode convertPosteFormToPosteNode(PosteForm form) {
        PosteNodeImpl posteNode = new PosteNodeImpl();
        return updatePosteNode(posteNode, form);
    }

    /**
     * Met à jour PosteNode à partir des données du formulaire
     *
     * @param node
     *            bean to update (back)
     * @param form
     *            bean (front)
     * @return
     * @throws ParseException
     */
    public static PosteNode updatePosteNode(PosteNode node, PosteForm form) {
        OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();

        node.setLabel(form.getLibelle());
        node.setConseillerPM(Boolean.parseBoolean(form.getConseiller()));
        node.setSuperviseurSGG(Boolean.parseBoolean(form.getSuperviseur()));
        node.setChargeMissionSGG(Boolean.parseBoolean(form.getChargeMission()));
        node.setPosteBdc(Boolean.parseBoolean(form.getPosteBDC()));
        if (form.getDateDebut() != null) {
            node.setDateDebut(SolonDateConverter.DATE_SLASH.parseToDateOrNull(form.getDateDebut()));
        }
        if (form.getDateFin() != null) {
            node.setDateFin(SolonDateConverter.DATE_SLASH.parseToDateOrNull(form.getDateFin()));
        }

        // Gestion des US de rattachement
        if (form.getUnitesStructurellesRattachement() != null) {
            List<OrganigrammeNode> lstUniteStructurelleParent = new ArrayList<>();
            for (String id : form.getUnitesStructurellesRattachement()) {
                OrganigrammeNode oN = organigrammeService.getOrganigrammeNodeById(
                    id,
                    OrganigrammeType.UNITE_STRUCTURELLE
                );
                lstUniteStructurelleParent.add(oN);
            }
            node.setParentList(lstUniteStructurelleParent);
            node.setParentUnitIds(form.getUnitesStructurellesRattachement());
        }
        return node;
    }

    @Override
    public void createPosteWs(SpecificContext context) {
        PosteWsForm posteWsForm = context.getFromContextData(STContextDataKey.POSTE_WS_FORM);

        try {
            // convertir l'objet PosteForm -> PosteNode
            PosteNode posteNode = new PosteNodeImpl();
            posteWsFormToPosteNode(posteNode, posteWsForm);
            createPoste(context, posteNode);
        } catch (ParseException e) {
            throw new NuxeoException(
                "Erreur lors de la conversion de l'objet PosteWsForm : " + posteWsForm.getLibelle(),
                e
            );
        }
    }

    @Override
    public void updatePosteWs(SpecificContext context) {
        PosteWsForm posteFormWs = context.getFromContextData(STContextDataKey.POSTE_WS_FORM);
        String identifiant = posteFormWs.getId();
        try {
            OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();
            // Récupérer le ministère à partir du param
            PosteNode posteNode = organigrammeService.getOrganigrammeNodeById(identifiant, OrganigrammeType.POSTE);
            // met à jour les données
            posteWsFormToPosteNode(posteNode, posteFormWs);
            updatePoste(context, posteNode);
        } catch (ParseException e) {
            context.getMessageQueue().addErrorToQueue("organigramme.error.technique");
            throw new NuxeoException("Erreur lors de la mise à jour du poste : " + posteFormWs.getLibelle(), e);
        }
    }

    @Override
    public PosteWsForm getPosteWsForm(SpecificContext context) {
        PosteWsForm posteWsForm = new PosteWsForm();
        String identifiant = context.getFromContextData(STContextDataKey.ID);
        try {
            OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();
            PosteNode posteNode = organigrammeService.getOrganigrammeNodeById(identifiant, OrganigrammeType.POSTE);

            MapFormToNode.convertPosteNodeToPosteWsForm(posteNode, posteWsForm);

            lockOrganigrammeNode(context, posteNode);
        } catch (Exception e) {
            throw new NuxeoException("Une erreur est survenue lors de la récupération du poste : " + identifiant, e);
        }
        return posteWsForm;
    }

    @Override
    public boolean checkUniqueLabelPosteWs(SpecificContext context) {
        PosteWsForm posteWsForm = context.getFromContextData(STContextDataKey.POSTE_WS_FORM);
        PosteNode posteNode = ObjectHelper.requireNonNullElseGet(getPosteNode(posteWsForm.getId()), PosteNodeImpl::new);

        try {
            posteWsFormToPosteNode(posteNode, posteWsForm);
        } catch (ParseException e) {
            context.getMessageQueue().addErrorToQueue("organigramme.error.technique");
            throw new NuxeoException("Erreur lors de la mise à jour du poste : " + posteWsForm.getLibelle(), e);
        }
        return getOrganigrammeService().checkUniqueLabel(posteNode);
    }

    private static PosteNode getPosteNode(String identifiant) {
        PosteNode posteNode = null;
        if (StringUtils.isNotBlank(identifiant)) {
            OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();
            // Récupérer le ministère à partir du param
            posteNode = organigrammeService.getOrganigrammeNodeById(identifiant, OrganigrammeType.POSTE);
        }
        return posteNode;
    }

    @Override
    public boolean checkUniqueLabelPoste(SpecificContext context) {
        PosteForm posteForm = context.getFromContextData(STContextDataKey.POSTE_FORM);
        PosteNode posteNode = getPosteNode(posteForm.getId());
        if (posteNode != null) {
            // met à jour les données
            updatePosteNode(posteNode, posteForm);
        } else {
            // convertir l'objet PosteForm -> PosteNode
            posteNode = convertPosteFormToPosteNode(posteForm);
        }

        return getOrganigrammeService().checkUniqueLabel(posteNode);
    }
}
