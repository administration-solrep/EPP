package fr.dila.solonepp.rest.api;

import fr.dila.reponses.rest.helper.VersionHelper;
import fr.dila.solonepp.api.logging.enumerationCodes.EppLogEnumImpl;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.solonepp.rest.management.EppDelegate;
import fr.dila.st.api.constant.STProfilUtilisateurConstants;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.logger.AbstractLogger;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.StringHelper;
import fr.dila.st.rest.helper.JaxBHelper;
import fr.sword.xsd.commons.TraitementStatut;
import fr.sword.xsd.commons.VersionResponse;
import fr.sword.xsd.solon.epp.ChercherCorbeilleRequest;
import fr.sword.xsd.solon.epp.ChercherCorbeilleResponse;
import fr.sword.xsd.solon.epp.ChercherDossierRequest;
import fr.sword.xsd.solon.epp.ChercherDossierResponse;
import fr.sword.xsd.solon.epp.ChercherIdentiteRequest;
import fr.sword.xsd.solon.epp.ChercherIdentiteResponse;
import fr.sword.xsd.solon.epp.ChercherMandatParNORRequest;
import fr.sword.xsd.solon.epp.ChercherMandatParNORResponse;
import fr.sword.xsd.solon.epp.ChercherTableDeReferenceRequest;
import fr.sword.xsd.solon.epp.ChercherTableDeReferenceResponse;
import fr.sword.xsd.solon.epp.HasCommunicationNonTraiteesRequest;
import fr.sword.xsd.solon.epp.HasCommunicationNonTraiteesResponse;
import fr.sword.xsd.solon.epp.MajTableRequest;
import fr.sword.xsd.solon.epp.MajTableResponse;
import fr.sword.xsd.solon.epp.NotifierTransitionRequest;
import fr.sword.xsd.solon.epp.NotifierTransitionResponse;
import fr.sword.xsd.solon.epp.NotifierVerrouRequest;
import fr.sword.xsd.solon.epp.NotifierVerrouResponse;
import fr.sword.xsd.solon.epp.TransmissionDatePublicationJORequest;
import fr.sword.xsd.solon.epp.TransmissionDatePublicationJOResponse;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.xml.bind.JAXBException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.platform.usermanager.NuxeoPrincipalImpl;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.impl.DefaultObject;
import org.nuxeo.runtime.transaction.TransactionHelper;

/**
 * Implémentation du service web de l'EPP.
 *
 * @author sly
 */
@WebObject(type = WSEpp.SERVICE_NAME)
@Produces("text/xml;charset=UTF-8")
public class WSEppImpl extends DefaultObject implements WSEpp {
    /**
     * Logger surcouche socle de log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(WSEppImpl.class);

    @Inject
    protected NuxeoPrincipalImpl principal;

    public WSEppImpl() {}

    protected EppDelegate delegate;

    protected EppDelegate getEppDelegate(CoreSession session) {
        if (delegate == null && session != null) {
            delegate = new EppDelegate(session);
        }
        return delegate;
    }

    @Override
    @GET
    @Path(WSEpp.METHOD_TEST)
    @Produces("text/plain")
    public String test() {
        return SERVICE_NAME;
    }

    @Override
    @GET
    @Path(WSEpp.METHOD_VERSION)
    public VersionResponse version() throws Exception {
        return VersionHelper.getVersionForWSEpp();
    }

    @Override
    @POST
    @Path(WSEpp.METHOD_CHERCHER_CORBEILLE)
    public ChercherCorbeilleResponse chercherCorbeille(ChercherCorbeilleRequest request) throws Exception {
        long startTime = System.nanoTime();
        ChercherCorbeilleResponse response = new ChercherCorbeilleResponse();
        if (isPasswordOutdated()) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_OUTDATED_INFO);
        } else if (isPasswordTemporary()) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_TEMPORARY_INFO);
        } else {
            try {
                EppDelegate delegate = new EppDelegate(ctx.getCoreSession());
                response = delegate.chercherCorbeille(request);
            } catch (Exception e) {
                TransactionHelper.setTransactionRollbackOnly();
                response.setStatut(TraitementStatut.KO);
                response.setMessageErreur(StringHelper.getStackTrace(e));
            }
        }
        if (LOGGER.isInfoEnable()) {
            LOGGER.info(
                ctx.getCoreSession(),
                EppLogEnumImpl.GET_WSEPP_TEC,
                JaxBHelper.logInWsTransaction(
                    WSEvenement.SERVICE_NAME,
                    WSEpp.METHOD_CHERCHER_CORBEILLE,
                    ctx.getCoreSession().getPrincipal().getName(),
                    request,
                    ChercherCorbeilleRequest.class,
                    response,
                    ChercherCorbeilleResponse.class
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
    @Path(WSEpp.METHOD_CHERCHER_DOSSIER)
    public ChercherDossierResponse chercherDossier(ChercherDossierRequest request) throws Exception {
        long startTime = System.nanoTime();
        ChercherDossierResponse response = new ChercherDossierResponse();
        if (isPasswordOutdated()) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_OUTDATED_INFO);
        } else if (isPasswordTemporary()) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_TEMPORARY_INFO);
        } else {
            try {
                EppDelegate delegate = new EppDelegate(ctx.getCoreSession());
                response = delegate.chercherDossier(request);
            } catch (Exception e) {
                TransactionHelper.setTransactionRollbackOnly();
                response.setStatut(TraitementStatut.KO);
                response.setMessageErreur(StringHelper.getStackTrace(e));
            }
        }
        if (LOGGER.isInfoEnable()) {
            LOGGER.info(
                ctx.getCoreSession(),
                EppLogEnumImpl.GET_WSEPP_TEC,
                JaxBHelper.logInWsTransaction(
                    WSEvenement.SERVICE_NAME,
                    WSEpp.METHOD_CHERCHER_DOSSIER,
                    ctx.getCoreSession().getPrincipal().getName(),
                    request,
                    ChercherDossierRequest.class,
                    response,
                    ChercherDossierResponse.class
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
    @Path(WSEpp.METHOD_NOTIFIER_TRANSITION)
    public NotifierTransitionResponse notifierTransition(NotifierTransitionRequest request) throws Exception {
        long startTime = System.nanoTime();
        NotifierTransitionResponse response = new NotifierTransitionResponse();
        if (isPasswordOutdated()) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_OUTDATED_INFO);
        } else if (isPasswordTemporary()) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_TEMPORARY_INFO);
        } else {
            try {
                EppDelegate delegate = new EppDelegate(ctx.getCoreSession());
                response = delegate.notifierTransition(request);
            } catch (Exception e) {
                TransactionHelper.setTransactionRollbackOnly();
                response.setStatut(TraitementStatut.KO);
                response.setMessageErreur(StringHelper.getStackTrace(e));
            }
        }
        if (LOGGER.isInfoEnable()) {
            LOGGER.info(
                ctx.getCoreSession(),
                EppLogEnumImpl.GET_WSEPP_TEC,
                JaxBHelper.logInWsTransaction(
                    WSEvenement.SERVICE_NAME,
                    WSEpp.METHOD_NOTIFIER_TRANSITION,
                    ctx.getCoreSession().getPrincipal().getName(),
                    request,
                    NotifierTransitionRequest.class,
                    response,
                    NotifierTransitionResponse.class
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
    @Path(WSEpp.METHOD_NOTIFIER_VERROU)
    public NotifierVerrouResponse notifierVerrou(NotifierVerrouRequest request) throws Exception {
        long startTime = System.nanoTime();
        NotifierVerrouResponse response = new NotifierVerrouResponse();
        if (isPasswordOutdated()) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_OUTDATED_INFO);
        } else if (isPasswordTemporary()) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_TEMPORARY_INFO);
        } else {
            try {
                EppDelegate delegate = new EppDelegate(ctx.getCoreSession());
                response = delegate.notifierVerrou(request);
            } catch (Exception e) {
                TransactionHelper.setTransactionRollbackOnly();
                response.setStatut(TraitementStatut.KO);
                response.setMessageErreur(StringHelper.getStackTrace(e));
            }
        }
        if (LOGGER.isInfoEnable()) {
            LOGGER.info(
                ctx.getCoreSession(),
                EppLogEnumImpl.GET_WSEPP_TEC,
                JaxBHelper.logInWsTransaction(
                    WSEvenement.SERVICE_NAME,
                    WSEpp.METHOD_NOTIFIER_VERROU,
                    ctx.getCoreSession().getPrincipal().getName(),
                    request,
                    NotifierVerrouRequest.class,
                    response,
                    NotifierVerrouResponse.class
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
    @Path(WSEpp.METHOD_CHERCHER_TABLE_REFERENCE)
    public ChercherTableDeReferenceResponse chercherTableDeReference(ChercherTableDeReferenceRequest request)
        throws Exception {
        long startTime = System.nanoTime();
        ChercherTableDeReferenceResponse response = new ChercherTableDeReferenceResponse();
        if (isPasswordOutdated()) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_OUTDATED_INFO);
        } else if (isPasswordTemporary()) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_TEMPORARY_INFO);
        } else {
            try {
                EppDelegate delegate = new EppDelegate(ctx.getCoreSession());
                response = delegate.chercherTableDeReference(request);
            } catch (Exception e) {
                TransactionHelper.setTransactionRollbackOnly();
                response.setStatut(TraitementStatut.KO);
                response.setMessageErreur(StringHelper.getStackTrace(e));
            }
        }
        if (LOGGER.isInfoEnable()) {
            LOGGER.info(
                ctx.getCoreSession(),
                EppLogEnumImpl.GET_WSEPP_TEC,
                JaxBHelper.logInWsTransaction(
                    WSEvenement.SERVICE_NAME,
                    WSEpp.METHOD_CHERCHER_TABLE_REFERENCE,
                    ctx.getCoreSession().getPrincipal().getName(),
                    request,
                    ChercherTableDeReferenceRequest.class,
                    response,
                    ChercherTableDeReferenceResponse.class
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
    @Path(WSEpp.METHOD_MAJ_TABLE)
    public MajTableResponse majTable(MajTableRequest request) throws Exception {
        long startTime = System.nanoTime();
        MajTableResponse response = new MajTableResponse();
        if (isPasswordOutdated()) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_OUTDATED_INFO);
        } else if (isPasswordTemporary()) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_TEMPORARY_INFO);
        } else {
            try {
                EppDelegate delegate = new EppDelegate(ctx.getCoreSession());
                response = delegate.majTable(request);
            } catch (Exception e) {
                TransactionHelper.setTransactionRollbackOnly();
                response.setStatut(TraitementStatut.KO);
                response.setMessageErreur(StringHelper.getStackTrace(e));
            }
        }
        if (LOGGER.isInfoEnable()) {
            LOGGER.info(
                ctx.getCoreSession(),
                EppLogEnumImpl.GET_WSEPP_TEC,
                JaxBHelper.logInWsTransaction(
                    WSEvenement.SERVICE_NAME,
                    WSEpp.METHOD_MAJ_TABLE,
                    ctx.getCoreSession().getPrincipal().getName(),
                    request,
                    MajTableRequest.class,
                    response,
                    MajTableResponse.class
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
    @Path(WSEpp.METHOD_CHERCHER_IDENTITE)
    public ChercherIdentiteResponse chercherIdentite(ChercherIdentiteRequest request) throws Exception {
        long startTime = System.nanoTime();
        ChercherIdentiteResponse response = new ChercherIdentiteResponse();
        if (isPasswordOutdated()) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_OUTDATED_INFO);
        } else if (isPasswordTemporary()) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_TEMPORARY_INFO);
        } else {
            try {
                EppDelegate delegate = new EppDelegate(ctx.getCoreSession());
                response = delegate.chercherIdentite(request);
            } catch (Exception e) {
                TransactionHelper.setTransactionRollbackOnly();
                response.setStatut(TraitementStatut.KO);
                response.setMessageErreur(StringHelper.getStackTrace(e));
            }
        }
        if (LOGGER.isInfoEnable()) {
            LOGGER.info(
                ctx.getCoreSession(),
                EppLogEnumImpl.GET_WSEPP_TEC,
                JaxBHelper.logInWsTransaction(
                    WSEvenement.SERVICE_NAME,
                    WSEpp.METHOD_CHERCHER_IDENTITE,
                    ctx.getCoreSession().getPrincipal().getName(),
                    request,
                    ChercherIdentiteRequest.class,
                    response,
                    ChercherIdentiteResponse.class
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
    @Path(WSEpp.METHOD_CHERCHER_MANDAT_PAR_NOR)
    public ChercherMandatParNORResponse chercherMandatParNor(ChercherMandatParNORRequest request) throws Exception {
        long startTime = System.nanoTime();
        ChercherMandatParNORResponse response = new ChercherMandatParNORResponse();
        if (isPasswordOutdated()) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_OUTDATED_INFO);
        } else if (isPasswordTemporary()) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_TEMPORARY_INFO);
        } else {
            try {
                EppDelegate delegate = new EppDelegate(ctx.getCoreSession());
                response = delegate.chercherMandatParNor(request);
            } catch (Exception e) {
                TransactionHelper.setTransactionRollbackOnly();
                response.setStatut(TraitementStatut.KO);
                response.setMessageErreur(StringHelper.getStackTrace(e));
            }
        }
        if (LOGGER.isInfoEnable()) {
            LOGGER.info(
                ctx.getCoreSession(),
                EppLogEnumImpl.GET_WSEPP_TEC,
                JaxBHelper.logInWsTransaction(
                    WSEvenement.SERVICE_NAME,
                    WSEpp.METHOD_CHERCHER_MANDAT_PAR_NOR,
                    ctx.getCoreSession().getPrincipal().getName(),
                    request,
                    ChercherMandatParNORRequest.class,
                    response,
                    ChercherMandatParNORResponse.class
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
    @Path(WSEpp.METHOD_HAS_COMMUNICATION)
    public HasCommunicationNonTraiteesResponse hasCommunicationNonTraitees(HasCommunicationNonTraiteesRequest request)
        throws JAXBException {
        long startTime = System.nanoTime();
        HasCommunicationNonTraiteesResponse response = new HasCommunicationNonTraiteesResponse();
        if (isPasswordOutdated()) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_OUTDATED_INFO);
        } else if (isPasswordTemporary()) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_TEMPORARY_INFO);
        } else {
            try {
                EppDelegate delegate = new EppDelegate(ctx.getCoreSession());
                response = delegate.hasCommunication(request);
            } catch (Exception e) {
                TransactionHelper.setTransactionRollbackOnly();
                response.setStatut(TraitementStatut.KO);
                response.setMessageErreur(StringHelper.getStackTrace(e));
            }
        }
        if (LOGGER.isInfoEnable()) {
            LOGGER.info(
                ctx.getCoreSession(),
                EppLogEnumImpl.GET_WSEPP_TEC,
                JaxBHelper.logInWsTransaction(
                    WSEvenement.SERVICE_NAME,
                    WSEpp.METHOD_HAS_COMMUNICATION,
                    ctx.getCoreSession().getPrincipal().getName(),
                    request,
                    HasCommunicationNonTraiteesRequest.class,
                    response,
                    HasCommunicationNonTraiteesResponse.class
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
    @Path(WSEpp.METHOD_TRANSMISSION_DATE_PUBLI_JO)
    public TransmissionDatePublicationJOResponse transmissionDatePublicationJO(
        TransmissionDatePublicationJORequest request
    )
        throws Exception {
        long startTime = System.nanoTime();
        TransmissionDatePublicationJOResponse response = new TransmissionDatePublicationJOResponse();
        if (isPasswordOutdated()) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_OUTDATED_INFO);
        } else if (isPasswordTemporary()) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(STProfilUtilisateurConstants.PASSWORD_IS_TEMPORARY_INFO);
        } else {
            EppDelegate delegate = new EppDelegate(ctx.getCoreSession());
            response = delegate.transmissionDatePublicationJO(request);
        }
        if (LOGGER.isInfoEnable()) {
            LOGGER.info(
                ctx.getCoreSession(),
                EppLogEnumImpl.GET_WSEPP_TEC,
                JaxBHelper.logInWsTransaction(
                    WSEvenement.SERVICE_NAME,
                    WSEpp.METHOD_HAS_COMMUNICATION,
                    ctx.getCoreSession().getPrincipal().getName(),
                    request,
                    TransmissionDatePublicationJORequest.class,
                    response,
                    TransmissionDatePublicationJOResponse.class
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
