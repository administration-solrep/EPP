package fr.dila.st.ui.services.impl;

import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.core.organigramme.EntiteNodeImpl;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.SolonDateConverter;
import fr.dila.st.ui.bean.OrganigrammeElementDTO;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.services.STMinistereUIService;
import fr.dila.st.ui.th.bean.EntiteForm;
import fr.dila.st.ui.th.model.SpecificContext;
import java.text.ParseException;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoException;

public class STMinistereUIServiceImpl extends STOrganigrammeUIServiceImpl<EntiteNode> implements STMinistereUIService {
    public static final String ID_PARENT = "idParent";

    protected STMinisteresService getSTMinisteresService() {
        return STServiceLocator.getSTMinisteresService();
    }

    @Override
    public EntiteForm getEntiteForm(SpecificContext context) {
        EntiteForm entiteForm = null;
        String identifiant = context.getFromContextData(STContextDataKey.ID);
        try {
            EntiteNode ministere = getOrganigrammeService()
                .getOrganigrammeNodeById(identifiant, OrganigrammeType.MINISTERE);

            lockOrganigrammeNode(context, ministere);

            OrganigrammeNode gouvernement = getOrganigrammeService()
                .getOrganigrammeNodeById(ministere.getParentId(), OrganigrammeType.GOUVERNEMENT);
            OrganigrammeElementDTO dtoGouvernement = new OrganigrammeElementDTO(context.getSession(), gouvernement);
            entiteForm = convertEntiteNodeToEntiteForm(ministere, dtoGouvernement);
        } catch (Exception e) {
            throw new NuxeoException(
                "Une erreur est survenue lors de la récupération du ministère. ID du ministère : " + identifiant,
                e
            );
        }
        return entiteForm;
    }

    @Override
    public void createEntite(SpecificContext context) {
        EntiteForm entiteForm = context.getFromContextData(STContextDataKey.ENTITE_FORM);

        try {
            // convertir l'objet EntiteForm -> EntiteNode
            EntiteNode entiteNode = convertEntiteFormToEntiteNode(entiteForm);
            if (getOrganigrammeService().checkEntiteUniqueLabelInParent(context.getSession(), entiteNode)) {
                // sauvegarder un ministère
                STServiceLocator.getSTMinisteresService().createEntite(entiteNode);
                context.getMessageQueue().addSuccessToQueue("organigramme.success.create.ministere");
            } else {
                //Message erreur
                context.getMessageQueue().addErrorToQueue("organigramme.error.create.element.same.name");
            }
        } catch (ParseException e) {
            throw new NuxeoException(
                "Erreur lors de la conversion de l'objet entiteForm : " + entiteForm.getAppellation(),
                e
            );
        }
    }

    @Override
    public void updateEntite(SpecificContext context) {
        EntiteForm entiteForm = context.getFromContextData(STContextDataKey.ENTITE_FORM);
        String identifiant = entiteForm.getIdentifiant();
        // Récupérer le ministère à partir du param
        EntiteNode ministere = getOrganigrammeService()
            .getOrganigrammeNodeById(identifiant, OrganigrammeType.MINISTERE);
        try {
            // met à jour les données
            EntiteNode entiteNode = updateEntiteNode(ministere, entiteForm);
            CoreSession session = context.getSession();

            STMinisteresService stMinisteresService = getSTMinisteresService();

            if (getOrganigrammeService().checkEntiteUniqueLabelInParent(session, entiteNode)) {
                performNodeUpdate(
                    context,
                    entiteNode,
                    e -> {
                        stMinisteresService.updateEntite(session, e);
                    },
                    "organigramme.success.update.ministere"
                );
            } else {
                //Message erreur
                context.getMessageQueue().addErrorToQueue("organigramme.error.create.element.same.name");
            }
        } catch (ParseException e) {
            throw new NuxeoException("Erreur lors de la mise à jour du ministère : " + ministere.getLabel(), e);
        }
    }

    /**
     * Converti un EntiteNode en EntiteForm
     *
     * @param node
     *            bean (back) to convert
     * @param dto
     *            current node (ici correspond au gouvernement)
     * @return
     */
    public static EntiteForm convertEntiteNodeToEntiteForm(EntiteNode node, OrganigrammeElementDTO dto) {
        EntiteForm entiteForm = new EntiteForm();
        entiteForm.setIdentifiant(node.getId());
        entiteForm.setAppellation(node.getLabel());
        entiteForm.setEdition(node.getEdition());
        if (node.getDateDebut() != null) {
            entiteForm.setDateDebut(SolonDateConverter.DATE_SLASH.format(node.getDateDebut()));
        }
        if (node.getDateFin() != null) {
            entiteForm.setDateFin(SolonDateConverter.DATE_SLASH.format(node.getDateFin()));
        }

        entiteForm.setNor(node.getNorMinistere());

        entiteForm.setGouvernement(dto);
        entiteForm.setOrdreProtocolaire(node.getOrdre().toString());
        entiteForm.setFormulesEntetes(node.getFormule());
        entiteForm.setCivilite(node.getMembreGouvernementCivilite());
        entiteForm.setMembreGouvernementPrenom(node.getMembreGouvernementPrenom());
        entiteForm.setMembreGouvernementNom(node.getMembreGouvernementNom());
        entiteForm.setSuiviAN(node.isSuiviActiviteNormative());

        return entiteForm;
    }

    /**
     * Converti le SwBean EntiteForm en EntiteNode
     *
     * @param form
     *            bean (front)
     * @return
     * @throws ParseException
     */
    public static EntiteNode convertEntiteFormToEntiteNode(EntiteForm form) throws ParseException {
        EntiteNodeImpl entiteNode = new EntiteNodeImpl();
        return updateEntiteNode(entiteNode, form);
    }

    /**
     * Met à jour EntiteNode à partir des données du formulaire
     *
     * @param node
     *            bean to update (back)
     * @param form
     *            bean (front)
     * @return
     * @throws ParseException
     */
    public static EntiteNode updateEntiteNode(EntiteNode node, EntiteForm form) throws ParseException {
        node.setLabel(form.getAppellation());
        node.setMembreGouvernementCivilite(form.getCivilite());
        node.setEdition(form.getEdition());
        node.setFormule(form.getFormulesEntetes());
        node.setOrdre(Long.parseLong(form.getOrdreProtocolaire()));
        node.setMembreGouvernementNom(form.getMembreGouvernementNom());
        node.setMembreGouvernementPrenom(form.getMembreGouvernementPrenom());
        if (form.getDateDebut() != null) {
            node.setDateDebut(SolonDateConverter.DATE_SLASH.parseToDateOrNull(form.getDateDebut()));
        }
        if (form.getDateFin() != null) {
            node.setDateFin(SolonDateConverter.DATE_SLASH.parseToDateOrNull(form.getDateFin()));
        }
        if (StringUtils.isBlank(node.getNorMinistere())) {
            node.setNorMinistere(form.getNor());
        }

        node.setParentGouvernement(form.getIdGouvernement());
        node.setSuiviActiviteNormative(BooleanUtils.isTrue(form.getSuiviAN()));
        return node;
    }
}
