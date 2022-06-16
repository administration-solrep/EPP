package fr.dila.solonepp.rest.api;

import fr.dila.reponses.rest.helper.VersionHelper;
import fr.dila.solonepp.api.logging.enumerationCodes.EppLogEnumImpl;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.core.exception.EppNuxeoException;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.solonepp.rest.management.EvenementDelegate;
import fr.dila.st.api.constant.STProfilUtilisateurConstants;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.logger.AbstractLogger;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.StringHelper;
import fr.dila.st.rest.helper.JaxBHelper;
import fr.sword.xsd.commons.TraitementStatut;
import fr.sword.xsd.commons.VersionResponse;
import fr.sword.xsd.solon.epp.AccuserReceptionRequest;
import fr.sword.xsd.solon.epp.AccuserReceptionResponse;
import fr.sword.xsd.solon.epp.AnnulerEvenementRequest;
import fr.sword.xsd.solon.epp.AnnulerEvenementResponse;
import fr.sword.xsd.solon.epp.ChercherEvenementRequest;
import fr.sword.xsd.solon.epp.ChercherEvenementResponse;
import fr.sword.xsd.solon.epp.ChercherPieceJointeRequest;
import fr.sword.xsd.solon.epp.ChercherPieceJointeResponse;
import fr.sword.xsd.solon.epp.CreerVersionDeltaRequest;
import fr.sword.xsd.solon.epp.CreerVersionDeltaResponse;
import fr.sword.xsd.solon.epp.CreerVersionRequest;
import fr.sword.xsd.solon.epp.CreerVersionResponse;
import fr.sword.xsd.solon.epp.EnvoyerMelRequest;
import fr.sword.xsd.solon.epp.EnvoyerMelResponse;
import fr.sword.xsd.solon.epp.InitialiserEvenementRequest;
import fr.sword.xsd.solon.epp.InitialiserEvenementResponse;
import fr.sword.xsd.solon.epp.MajInterneRequest;
import fr.sword.xsd.solon.epp.MajInterneResponse;
import fr.sword.xsd.solon.epp.RechercherEvenementRequest;
import fr.sword.xsd.solon.epp.RechercherEvenementResponse;
import fr.sword.xsd.solon.epp.SupprimerVersionRequest;
import fr.sword.xsd.solon.epp.SupprimerVersionResponse;
import fr.sword.xsd.solon.epp.ValiderVersionRequest;
import fr.sword.xsd.solon.epp.ValiderVersionResponse;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.platform.usermanager.NuxeoPrincipalImpl;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.impl.DefaultObject;
import org.nuxeo.runtime.transaction.TransactionHelper;

/**
 * Implémentation du service web des événements.
 *
 * @author sly
 */
@WebObject(type = WSEvenement.SERVICE_NAME)
@Produces("text/xml;charset=UTF-8")
public class WSEvenementImpl extends DefaultObject implements WSEvenement {
    /**
     * Logger surcouche socle de log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(WSEvenementImpl.class);

    @Inject
    protected NuxeoPrincipalImpl principal;

    public WSEvenementImpl() {}

    protected EvenementDelegate delegate;

    protected EvenementDelegate getEvenementDelegate(CoreSession session) {
        if (delegate == null && session != null) {
            delegate = new EvenementDelegate(session);
        }
        return delegate;
    }

    @Override
    @GET
    @Path(WSEvenement.METHOD_TEST)
    @Produces("text/plain")
    public String test() {
        return SERVICE_NAME;
    }

    @Override
    @GET
    @Path(WSEvenement.METHOD_VERSION)
    public VersionResponse version() throws Exception {
        return VersionHelper.getVersionForWSEvenement();
    }

    @Override
    @POST
    @Path(WSEvenement.METHOD_CHERCHER_EVENEMENT)
    public ChercherEvenementResponse chercherEvenement(ChercherEvenementRequest request) throws Exception {
        long startTime = System.nanoTime();
        ChercherEvenementResponse response = new ChercherEvenementResponse();
        if (isPasswordOutdated()) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_OUTDATED_INFO);
        } else if (isPasswordTemporary()) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_TEMPORARY_INFO);
        } else {
            try {
                delegate = getEvenementDelegate(ctx.getCoreSession());
                response = delegate.chercherEvenement(request);
            } catch (Throwable e) {
                TransactionHelper.setTransactionRollbackOnly();
                response.setMessageErreur(StringHelper.getStackTrace(e));
                response.setStatut(TraitementStatut.KO);
            }
        }
        if (LOGGER.isInfoEnable()) {
            LOGGER.info(
                ctx.getCoreSession(),
                EppLogEnumImpl.GET_WSEVENEMENT_TEC,
                JaxBHelper.logInWsTransaction(
                    WSEvenement.SERVICE_NAME,
                    WSEvenement.METHOD_CHERCHER_EVENEMENT,
                    ctx.getCoreSession().getPrincipal().getName(),
                    request,
                    ChercherEvenementRequest.class,
                    response,
                    ChercherEvenementResponse.class
                ) +
                "---DURATION : " +
                AbstractLogger.getDurationInMs(startTime) +
                "ms ---\n"
            );
        }
        return response;
    }

    @Override
    @POST
    @Path(WSEvenement.METHOD_CHERCHER_PIECE_JOINTE)
    public ChercherPieceJointeResponse chercherPieceJointe(ChercherPieceJointeRequest request) throws Exception {
        long startTime = System.nanoTime();
        ChercherPieceJointeResponse response = new ChercherPieceJointeResponse();
        if (isPasswordOutdated()) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_OUTDATED_INFO);
        } else if (isPasswordTemporary()) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_TEMPORARY_INFO);
        } else {
            try {
                delegate = getEvenementDelegate(ctx.getCoreSession());
                response = delegate.chercherPieceJointe(request);
            } catch (Throwable e) {
                TransactionHelper.setTransactionRollbackOnly();
                response.setMessageErreur(StringHelper.getStackTrace(e));
                response.setStatut(TraitementStatut.KO);
            }
        }
        if (LOGGER.isInfoEnable()) {
            LOGGER.info(
                ctx.getCoreSession(),
                EppLogEnumImpl.GET_WSEVENEMENT_TEC,
                JaxBHelper.logInWsTransaction(
                    WSEvenement.SERVICE_NAME,
                    WSEvenement.METHOD_CHERCHER_PIECE_JOINTE,
                    ctx.getCoreSession().getPrincipal().getName(),
                    request,
                    ChercherPieceJointeRequest.class,
                    response,
                    ChercherPieceJointeResponse.class
                ) +
                "---DURATION : " +
                AbstractLogger.getDurationInMs(startTime) +
                "ms ---\n"
            );
        }
        return response;
    }

    @Override
    @POST
    @Path(WSEvenement.METHOD_CREER_VERSION)
    public CreerVersionResponse creerVersion(CreerVersionRequest request) throws Exception {
        long startTime = System.nanoTime();
        CreerVersionResponse response = new CreerVersionResponse();
        if (isPasswordOutdated()) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_OUTDATED_INFO);
        } else if (isPasswordTemporary()) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_TEMPORARY_INFO);
        } else {
            try {
                delegate = getEvenementDelegate(ctx.getCoreSession());
                response = delegate.creerVersion(request);
            } catch (EppNuxeoException e) {
                TransactionHelper.setTransactionRollbackOnly();
                response.setMessageErreur(StringHelper.getStackTrace(e));
                response.setStatut(TraitementStatut.KO);
            } catch (Throwable e) {
                TransactionHelper.setTransactionRollbackOnly();
                response.setMessageErreur(StringHelper.getStackTrace(e));
                response.setStatut(TraitementStatut.KO);
            }
        }
        if (LOGGER.isInfoEnable()) {
            LOGGER.info(
                ctx.getCoreSession(),
                EppLogEnumImpl.GET_WSEVENEMENT_TEC,
                JaxBHelper.logInWsTransaction(
                    WSEvenement.SERVICE_NAME,
                    WSEvenement.METHOD_CREER_VERSION,
                    ctx.getCoreSession().getPrincipal().getName(),
                    request,
                    CreerVersionRequest.class,
                    response,
                    CreerVersionResponse.class
                ) +
                "---DURATION : " +
                AbstractLogger.getDurationInMs(startTime) +
                "ms ---\n"
            );
        }
        return response;
    }

    @Override
    @POST
    @Path(WSEvenement.METHOD_CREER_VERSION_DELTA)
    public CreerVersionDeltaResponse creerVersionDelta(CreerVersionDeltaRequest request) throws Exception {
        long startTime = System.nanoTime();
        CreerVersionDeltaResponse response = new CreerVersionDeltaResponse();
        if (isPasswordOutdated()) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_OUTDATED_INFO);
        } else if (isPasswordTemporary()) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_TEMPORARY_INFO);
        } else {
            try {
                delegate = getEvenementDelegate(ctx.getCoreSession());
                response = delegate.creerVersionDelta(request);
            } catch (Throwable e) {
                TransactionHelper.setTransactionRollbackOnly();
                response.setMessageErreur(StringHelper.getStackTrace(e));
                response.setStatut(TraitementStatut.KO);
            }
        }
        if (LOGGER.isInfoEnable()) {
            LOGGER.info(
                ctx.getCoreSession(),
                EppLogEnumImpl.GET_WSEVENEMENT_TEC,
                JaxBHelper.logInWsTransaction(
                    WSEvenement.SERVICE_NAME,
                    WSEvenement.METHOD_CREER_VERSION_DELTA,
                    ctx.getCoreSession().getPrincipal().getName(),
                    request,
                    CreerVersionDeltaRequest.class,
                    response,
                    CreerVersionDeltaResponse.class
                ) +
                "---DURATION : " +
                AbstractLogger.getDurationInMs(startTime) +
                "ms ---\n"
            );
        }
        return response;
    }

    @Override
    @POST
    @Path(WSEvenement.METHOD_VALIDER_VERSION)
    public ValiderVersionResponse validerVersion(ValiderVersionRequest request) throws Exception {
        long startTime = System.nanoTime();
        ValiderVersionResponse response = new ValiderVersionResponse();
        if (isPasswordOutdated()) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_OUTDATED_INFO);
        } else if (isPasswordTemporary()) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_TEMPORARY_INFO);
        } else {
            try {
                delegate = getEvenementDelegate(ctx.getCoreSession());
                response = delegate.validerVersion(request);
            } catch (Throwable e) {
                TransactionHelper.setTransactionRollbackOnly();
                response.setMessageErreur(StringHelper.getStackTrace(e));
                response.setStatut(TraitementStatut.KO);
            }
        }
        if (LOGGER.isInfoEnable()) {
            LOGGER.info(
                ctx.getCoreSession(),
                EppLogEnumImpl.GET_WSEVENEMENT_TEC,
                JaxBHelper.logInWsTransaction(
                    WSEvenement.SERVICE_NAME,
                    WSEvenement.METHOD_VALIDER_VERSION,
                    ctx.getCoreSession().getPrincipal().getName(),
                    request,
                    ValiderVersionRequest.class,
                    response,
                    ValiderVersionResponse.class
                ) +
                "---DURATION : " +
                AbstractLogger.getDurationInMs(startTime) +
                "ms ---\n"
            );
        }
        return response;
    }

    @Override
    @POST
    @Path(WSEvenement.METHOD_ANNULER_EVENEMENT)
    public AnnulerEvenementResponse annulerEvenement(AnnulerEvenementRequest request) throws Exception {
        long startTime = System.nanoTime();
        AnnulerEvenementResponse response = new AnnulerEvenementResponse();
        if (isPasswordOutdated()) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_OUTDATED_INFO);
        } else if (isPasswordTemporary()) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_TEMPORARY_INFO);
        } else {
            try {
                delegate = getEvenementDelegate(ctx.getCoreSession());
                response = delegate.annulerEvenement(request);
            } catch (Throwable e) {
                TransactionHelper.setTransactionRollbackOnly();
                response.setMessageErreur(StringHelper.getStackTrace(e));
                response.setStatut(TraitementStatut.KO);
            }
        }
        if (LOGGER.isInfoEnable()) {
            LOGGER.info(
                ctx.getCoreSession(),
                EppLogEnumImpl.GET_WSEVENEMENT_TEC,
                JaxBHelper.logInWsTransaction(
                    WSEvenement.SERVICE_NAME,
                    WSEvenement.METHOD_ANNULER_EVENEMENT,
                    ctx.getCoreSession().getPrincipal().getName(),
                    request,
                    AnnulerEvenementRequest.class,
                    response,
                    AnnulerEvenementResponse.class
                ) +
                "---DURATION : " +
                AbstractLogger.getDurationInMs(startTime) +
                "ms ---\n"
            );
        }
        return response;
    }

    @Override
    @POST
    @Path(WSEvenement.METHOD_SUPPRIMER_VERSION)
    public SupprimerVersionResponse supprimerVersion(SupprimerVersionRequest request) throws Exception {
        long startTime = System.nanoTime();
        SupprimerVersionResponse response = new SupprimerVersionResponse();
        if (isPasswordOutdated()) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_OUTDATED_INFO);
        } else if (isPasswordTemporary()) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_TEMPORARY_INFO);
        } else {
            try {
                delegate = getEvenementDelegate(ctx.getCoreSession());
                response = delegate.supprimerVersion(request);
            } catch (Throwable e) {
                response.setMessageErreur(StringHelper.getStackTrace(e));
                response.setStatut(TraitementStatut.KO);
            }
        }
        if (LOGGER.isInfoEnable()) {
            LOGGER.info(
                ctx.getCoreSession(),
                EppLogEnumImpl.GET_WSEVENEMENT_TEC,
                JaxBHelper.logInWsTransaction(
                    WSEvenement.SERVICE_NAME,
                    WSEvenement.METHOD_SUPPRIMER_VERSION,
                    ctx.getCoreSession().getPrincipal().getName(),
                    request,
                    SupprimerVersionRequest.class,
                    response,
                    SupprimerVersionResponse.class
                ) +
                "---DURATION : " +
                AbstractLogger.getDurationInMs(startTime) +
                "ms ---\n"
            );
        }
        return response;
    }

    @Override
    @POST
    @Path(WSEvenement.METHOD_ACCUSER_RECEPTION)
    public AccuserReceptionResponse accuserReception(AccuserReceptionRequest request) throws Exception {
        long startTime = System.nanoTime();
        AccuserReceptionResponse response = new AccuserReceptionResponse();
        if (isPasswordOutdated()) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_OUTDATED_INFO);
        } else if (isPasswordTemporary()) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_TEMPORARY_INFO);
        } else {
            try {
                delegate = getEvenementDelegate(ctx.getCoreSession());
                response = delegate.accuserReception(request);
            } catch (Throwable e) {
                response.setMessageErreur(StringHelper.getStackTrace(e));
                response.setStatut(TraitementStatut.KO);
            }
        }
        if (LOGGER.isInfoEnable()) {
            LOGGER.info(
                ctx.getCoreSession(),
                EppLogEnumImpl.GET_WSEVENEMENT_TEC,
                JaxBHelper.logInWsTransaction(
                    WSEvenement.SERVICE_NAME,
                    WSEvenement.METHOD_ACCUSER_RECEPTION,
                    ctx.getCoreSession().getPrincipal().getName(),
                    request,
                    AccuserReceptionRequest.class,
                    response,
                    AccuserReceptionResponse.class
                ) +
                "---DURATION : " +
                AbstractLogger.getDurationInMs(startTime) +
                "ms ---\n"
            );
        }
        return response;
    }

    @Override
    @POST
    @Path(WSEvenement.METHOD_INITIALISER_EVENEMENT)
    public InitialiserEvenementResponse initialiserEvenement(InitialiserEvenementRequest request) throws Exception {
        long startTime = System.nanoTime();
        InitialiserEvenementResponse response = new InitialiserEvenementResponse();
        if (isPasswordOutdated()) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_OUTDATED_INFO);
        } else if (isPasswordTemporary()) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_TEMPORARY_INFO);
        } else {
            try {
                delegate = getEvenementDelegate(ctx.getCoreSession());
                response = delegate.initialiserEvenement(request);
            } catch (Throwable e) {
                response.setMessageErreur(StringHelper.getStackTrace(e));
                response.setStatut(TraitementStatut.KO);
            }

            // on rollBack dans tous les cas rien ne doit etre créé
            TransactionHelper.setTransactionRollbackOnly();
        }
        if (LOGGER.isInfoEnable()) {
            LOGGER.info(
                ctx.getCoreSession(),
                EppLogEnumImpl.GET_WSEVENEMENT_TEC,
                JaxBHelper.logInWsTransaction(
                    WSEvenement.SERVICE_NAME,
                    WSEvenement.METHOD_INITIALISER_EVENEMENT,
                    ctx.getCoreSession().getPrincipal().getName(),
                    request,
                    InitialiserEvenementRequest.class,
                    response,
                    InitialiserEvenementResponse.class
                ) +
                "---DURATION : " +
                AbstractLogger.getDurationInMs(startTime) +
                "ms ---\n"
            );
        }
        return response;
    }

    @Override
    @POST
    @Path(WSEvenement.METHOD_ENVOYER_MEL)
    public EnvoyerMelResponse envoyerMel(EnvoyerMelRequest request) throws Exception {
        long startTime = System.nanoTime();
        EnvoyerMelResponse response = new EnvoyerMelResponse();
        if (isPasswordOutdated()) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_OUTDATED_INFO);
        } else if (isPasswordTemporary()) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_TEMPORARY_INFO);
        } else {
            try {
                delegate = getEvenementDelegate(ctx.getCoreSession());
                response = delegate.envoyerMel(request);
            } catch (Throwable e) {
                response.setMessageErreur(StringHelper.getStackTrace(e));
                response.setStatut(TraitementStatut.KO);
            }

            // on rollBack dans tous les cas rien ne doit etre créé
            TransactionHelper.setTransactionRollbackOnly();
        }
        if (LOGGER.isInfoEnable()) {
            LOGGER.info(
                ctx.getCoreSession(),
                EppLogEnumImpl.GET_WSEVENEMENT_TEC,
                JaxBHelper.logInWsTransaction(
                    WSEvenement.SERVICE_NAME,
                    WSEvenement.METHOD_ENVOYER_MEL,
                    ctx.getCoreSession().getPrincipal().getName(),
                    request,
                    EnvoyerMelRequest.class,
                    response,
                    EnvoyerMelResponse.class
                ) +
                "---DURATION : " +
                AbstractLogger.getDurationInMs(startTime) +
                "ms ---\n"
            );
        }
        return response;
    }

    @Override
    @POST
    @Path(WSEvenement.METHOD_RECHERCHER_EVENEMENT)
    public RechercherEvenementResponse rechercherEvenement(RechercherEvenementRequest request) throws Exception {
        long startTime = System.nanoTime();
        RechercherEvenementResponse response = new RechercherEvenementResponse();
        if (isPasswordOutdated()) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_OUTDATED_INFO);
        } else if (isPasswordTemporary()) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_TEMPORARY_INFO);
        } else {
            CoreSession session = ctx.getCoreSession();
            EppPrincipal eppPrincipal = (EppPrincipal) session.getPrincipal();

            try {
                // remove acl check
                eppPrincipal.setAdministrator(Boolean.TRUE);

                delegate = getEvenementDelegate(session);
                response = delegate.rechercherMessage(request);
            } catch (Throwable e) {
                response.setMessageErreur(StringHelper.getStackTrace(e));
                response.setStatut(TraitementStatut.KO);
            }

            // add acl check
            eppPrincipal.setAdministrator(Boolean.FALSE);
        }
        if (LOGGER.isInfoEnable()) {
            LOGGER.info(
                ctx.getCoreSession(),
                EppLogEnumImpl.GET_WSEVENEMENT_TEC,
                JaxBHelper.logInWsTransaction(
                    WSEvenement.SERVICE_NAME,
                    WSEvenement.METHOD_RECHERCHER_EVENEMENT,
                    ctx.getCoreSession().getPrincipal().getName(),
                    request,
                    RechercherEvenementRequest.class,
                    response,
                    RechercherEvenementResponse.class
                ) +
                "---DURATION : " +
                AbstractLogger.getDurationInMs(startTime) +
                "ms ---\n"
            );
        }
        return response;
    }

    @Override
    @POST
    @Path(WSEvenement.METHOD_MAJ_INTERNE)
    public MajInterneResponse majInterne(MajInterneRequest request) throws Exception {
        long startTime = System.nanoTime();
        MajInterneResponse response = new MajInterneResponse();
        if (isPasswordOutdated()) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_OUTDATED_INFO);
        } else if (isPasswordTemporary()) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_TEMPORARY_INFO);
        } else {
            try {
                delegate = getEvenementDelegate(ctx.getCoreSession());
                response = delegate.majInterne(request);
            } catch (Throwable e) {
                response.setMessageErreur(StringHelper.getStackTrace(e));
                response.setStatut(TraitementStatut.KO);
            }
        }
        if (LOGGER.isInfoEnable()) {
            LOGGER.info(
                ctx.getCoreSession(),
                EppLogEnumImpl.GET_WSEVENEMENT_TEC,
                JaxBHelper.logInWsTransaction(
                    WSEvenement.SERVICE_NAME,
                    WSEvenement.METHOD_MAJ_INTERNE,
                    ctx.getCoreSession().getPrincipal().getName(),
                    request,
                    MajInterneRequest.class,
                    response,
                    MajInterneResponse.class
                ) +
                "---DURATION : " +
                AbstractLogger.getDurationInMs(startTime) +
                "ms ---\n"
            );
        }
        return response;
    }

    private boolean isPasswordOutdated() {
        CoreSession session = ctx.getCoreSession();
        try {
            if (
                SolonEppServiceLocator
                    .getProfilUtilisateurService()
                    .isUserPasswordOutdated(session, session.getPrincipal().getName())
            ) {
                STServiceLocator.getSTUserService().forceChangeOutdatedPassword(session.getPrincipal().getName());
                return true;
            }
            return false;
        } catch (NuxeoException e) {
            LOGGER.warn(
                session,
                EppLogEnumImpl.GET_WSEPP_TEC,
                "Impossible de vérifier la validité de la date de changement de mot de passe",
                e
            );
            return false;
        }
    }

    private boolean isPasswordTemporary() {
        CoreSession session = ctx.getCoreSession();
        try {
            if (STServiceLocator.getSTUserService().isUserPasswordResetNeeded(session.getPrincipal().getName())) {
                return true;
            }
            return false;
        } catch (NuxeoException e) {
            LOGGER.warn(
                session,
                EppLogEnumImpl.GET_WSEPP_TEC,
                "Impossible de vérifier si le mot de passe est temporaire",
                e
            );
            return false;
        }
    }
}
