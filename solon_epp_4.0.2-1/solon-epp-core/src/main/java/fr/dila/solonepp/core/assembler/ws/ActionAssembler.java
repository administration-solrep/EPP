package fr.dila.solonepp.core.assembler.ws;

import com.google.common.collect.ImmutableBiMap;
import fr.dila.solonepp.api.constant.SolonEppConstant;
import fr.sword.xsd.solon.epp.Action;
import org.nuxeo.ecm.core.api.NuxeoException;

/**
 * Assembleur des actions objet métier <-> Web service.
 *
 * @author jtremeaux
 */
public class ActionAssembler {
    private static final ImmutableBiMap<Action, String> actionMap = new ImmutableBiMap.Builder<Action, String>()
        .put(Action.ABANDONNER, SolonEppConstant.VERSION_ACTION_ABANDONNER)
        .put(Action.ACCEPTER, SolonEppConstant.VERSION_ACTION_ACCEPTER)
        .put(Action.ACCUSER_RECEPTION, SolonEppConstant.VERSION_ACTION_ACCUSER_RECEPTION)
        .put(Action.ANNULER, SolonEppConstant.VERSION_ACTION_ANNULER)
        .put(Action.COMPLETER, SolonEppConstant.VERSION_ACTION_COMPLETER)
        .put(Action.CREER_ALERTE, SolonEppConstant.VERSION_ACTION_CREER_ALERTE)
        .put(Action.CREER_EVENEMENT, SolonEppConstant.VERSION_ACTION_CREER_EVENEMENT)
        .put(Action.LEVER_ALERTE, SolonEppConstant.VERSION_ACTION_LEVER_ALERTE)
        .put(Action.MARQUAGE_MESSAGE_TRAITE, SolonEppConstant.VERSION_ACTION_PASSER_MESSAGE_TRAITE)
        .put(Action.MODIFIER, SolonEppConstant.VERSION_ACTION_MODIFIER)
        .put(
            Action.PASSAGE_MESSAGE_EN_COURS_DE_TRAITEMENT,
            SolonEppConstant.VERSION_ACTION_PASSER_MESSAGE_EN_COURS_DE_TRAITEMENT
        )
        .put(Action.PUBLIER, SolonEppConstant.VERSION_ACTION_PUBLIER)
        .put(Action.RECTIFIER, SolonEppConstant.VERSION_ACTION_RECTIFIER)
        .put(Action.REJETER, SolonEppConstant.VERSION_ACTION_REJETER)
        .put(Action.SUPPRIMER, SolonEppConstant.VERSION_ACTION_SUPPRIMER)
        .put(Action.TRANSMETTRE_MEL, SolonEppConstant.VERSION_ACTION_TRANSMETTRE_MEL)
        .put(Action.VISUALISER_VERSION, SolonEppConstant.VERSION_ACTION_VISUALISER_VERSION)
        .build();

    /**
     * Assemble l'objet web service -> métier.
     *
     * @param xsd Objet web service
     * @return Objet métier
     */
    public static String assembleXsdToAction(Action xsd) {
        String action = actionMap.get(xsd);
        if (action == null) {
            throw new NuxeoException("Action inconnue : " + xsd);
        }
        return action;
    }

    /**
     * Assemble l'objet métier ->Evt22Assemblerweb service.
     *
     * @param action Objet métier
     * @return Objet web service
     */
    public static Action assembleActionToXsd(String action) {
        Action xsd = actionMap.inverse().get(action);
        if (xsd == null) {
            throw new NuxeoException("Action inconnue : " + action);
        }
        return xsd;
    }
}
