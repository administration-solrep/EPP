package fr.dila.st.core.logger;

import fr.dila.st.api.logging.STLogEnum;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import java.security.Principal;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.slf4j.MDC;

/**
 * Logger pour le formalisme Surcouche au log4j
 */
public class STLoggerImpl implements STLogger {
    private static final String DEFAULT = " - ";

    private static final String SESSION_NULL = "Session null";
    private static final String PRINCIPAL_NULL = "Principal null";

    // Variables pour le MDC (Mapped Diagnostic Context)
    private static final String USERNAME_MDC = "userName";
    private static final String PATH_DOC_MDC = "pathDoc";
    private static final String UID_DOC_MDC = "uidDoc";
    private static final String CODE_MDC = "codeLog";
    private static final String TYPE_DOC_MDC = "typeDoc";
    private static final String TITLE_DOC_MDC = "titleDoc";
    private static final String PARENT_DOC_MDC = "parentDoc";
    private static final String STRING_MDC = "infoCompl";

    // Formattage datas
    private static final String DEBUT = "[";
    private static final String FIN = "]";

    private static final int TRACE_INT = 0;
    private static final int DEBUG_INT = 1;
    private static final int INFO_INT = 2;
    private static final int WARN_INT = 3;
    private static final int ERROR_INT = 4;
    private static final int FATAL_INT = 5;

    private final Logger logger;

    public STLoggerImpl(Class<?> classLoggante) {
        logger = LogManager.getLogger(classLoggante);
    }

    public STLoggerImpl(String classLoggante) {
        logger = LogManager.getLogger(classLoggante);
    }

    /**
     * récupère le nom utilisateur à partir de la session
     *
     * @param session
     *            CoreSession
     * @return le nom utilisateur entouré de crochets <br />
     *         ou SESSION_NULL si la session est null, <br />
     *         ou PRINCIPAL_NULL si le principal de la session est null
     */
    private String getUserName(final CoreSession session) {
        StringBuilder formattedName = new StringBuilder();
        formattedName.append(DEBUT);
        if (session == null) {
            formattedName.append(SESSION_NULL);
        } else {
            Principal prin = session.getPrincipal();
            if (prin == null) {
                formattedName.append(PRINCIPAL_NULL);
            } else {
                formattedName.append(prin.getName());
            }
        }
        formattedName.append(FIN);
        return formattedName.toString();
    }

    /**
     * récupère l'uid du document model passé en paramètre
     *
     * @param doc
     * @return uid du document entouré de crochets <br />
     *         ou DEFAULT_UID si le document est null
     */
    private String getUidDoc(final DocumentModel doc) {
        StringBuilder formattedUid = new StringBuilder();
        formattedUid.append(DEBUT);
        if (doc == null) {
            formattedUid.append(DEFAULT);
        } else {
            formattedUid.append(doc.getId());
        }
        formattedUid.append(FIN);
        return formattedUid.toString();
    }

    /**
     * récupère l'uid du document reference passé en paramètre
     *
     * @param doc
     * @return uid du document entouré de crochets <br />
     *         ou DEFAULT_UID si le document est null
     */
    private String getUidDoc(final DocumentRef doc) {
        StringBuilder formattedUid = new StringBuilder();
        formattedUid.append(DEBUT);
        if (doc == null) {
            formattedUid.append(DEFAULT);
        } else {
            formattedUid.append(doc.reference());
        }
        formattedUid.append(FIN);
        return formattedUid.toString();
    }

    /**
     * récupère l'id du noeud passé en paramètre
     *
     * @param node
     * @return uid du noeud entouré de crochets <br />
     *         ou DEFAULT_UID si le noeud est null
     */
    private String getIdNode(final OrganigrammeNode node) {
        StringBuilder formattedUid = new StringBuilder();
        formattedUid.append(DEBUT);
        if (node == null) {
            formattedUid.append(DEFAULT);
        } else {
            formattedUid.append(node.getId());
        }
        formattedUid.append(FIN);
        return formattedUid.toString();
    }

    /**
     * récupère le type du document model passé en paramètre
     *
     * @param doc
     * @return type du document entouré de crochets <br />
     *         ou DEFAULT_TYPE si le document est null
     */
    private String getTypeDoc(final DocumentModel doc) {
        StringBuilder formattedType = new StringBuilder();
        formattedType.append(DEBUT);
        if (doc == null) {
            formattedType.append(DEFAULT);
        } else {
            formattedType.append(doc.getType());
        }
        formattedType.append(FIN);
        return formattedType.toString();
    }

    /**
     * récupère le type du noeud passé en paramètre
     *
     * @param node
     * @return type du noeud entouré de crochets <br />
     *         ou DEFAULT_TYPE si le noeud est null
     */
    private String getTypeNode(final OrganigrammeNode node) {
        StringBuilder formattedType = new StringBuilder();
        formattedType.append(DEBUT);
        if (node == null) {
            formattedType.append(DEFAULT);
        } else {
            formattedType.append(node.getType().getValue());
        }
        formattedType.append(FIN);
        return formattedType.toString();
    }

    /**
     * récupère le path du document
     *
     * @param doc
     *            DocumentModel
     * @return le path du document entouré de crochets <br />
     *         ou DEFAULT_PATH si le document est null
     */
    private String getPathDoc(final DocumentModel doc) {
        StringBuilder formattedPath = new StringBuilder();
        formattedPath.append(DEBUT);
        if (doc == null) {
            formattedPath.append(DEFAULT);
        } else {
            formattedPath.append(doc.getPathAsString());
        }
        formattedPath.append(FIN);
        return formattedPath.toString();
    }

    /**
     * récupère le title du document
     *
     * @param doc
     *            DocumentModel
     * @return le title du document entouré de crochets <br />
     *         ou DEFAULT_TITLE si le document est null ou DEFAULT_TITLE si getTitle génère une exception
     */
    private String getTitleDoc(final DocumentModel doc) {
        StringBuilder formattedTitle = new StringBuilder();
        formattedTitle.append(DEBUT);
        if (doc == null) {
            formattedTitle.append(DEFAULT);
        } else {
            try {
                formattedTitle.append(doc.getTitle());
            } catch (NuxeoException e) {
                formattedTitle.append(DEFAULT);
            }
        }
        formattedTitle.append(FIN);
        return formattedTitle.toString();
    }

    /**
     * récupère le label du noeud
     *
     * @param node
     *            OrganigrammeNode
     * @return le label du noeud entouré de crochets <br />
     *         ou DEFAULT_TITLE si le noeud est null
     */
    private String getTitleNode(final OrganigrammeNode node) {
        StringBuilder formattedTitle = new StringBuilder();
        formattedTitle.append(DEBUT);
        if (node == null) {
            formattedTitle.append(DEFAULT);
        } else {
            formattedTitle.append(node.getLabel());
        }
        formattedTitle.append(FIN);
        return formattedTitle.toString();
    }

    /**
     * Formatte le code enuméré avec des crochets autour
     *
     * @param codeEnum
     * @return code de la forme [000_000_000]
     */
    private String getFormattedCode(final STLogEnum codeEnum) {
        StringBuilder formattedCode = new StringBuilder();
        formattedCode.append(DEBUT);
        formattedCode.append(codeEnum.getCode());
        formattedCode.append(FIN);
        return formattedCode.toString();
    }

    private String getParentUid(final DocumentModel doc) {
        StringBuilder formattedParentUid = new StringBuilder();
        formattedParentUid.append(DEBUT);
        if (doc == null) {
            formattedParentUid.append(DEFAULT);
        } else {
            DocumentRef parentRef = doc.getParentRef();
            if (parentRef == null) {
                formattedParentUid.append(DEFAULT);
            } else {
                formattedParentUid.append(parentRef.reference());
            }
        }
        formattedParentUid.append(FIN);
        return formattedParentUid.toString();
    }

    /**
     * Récupère le message du code. Si énumération null, une erreur est loggée indiquant l'erreur d'utilisation du
     * logger
     *
     * @param codeEnum
     *            STLogEnum l'enumération du code passée dans le logger
     *            @param message Texte transmis
     *            @param object Objet à décrire (via toString) si le message est empty
     * @return null si énumération null, codeEnum.getText() sinon
     */
    private String getTextCode(STLogEnum codeEnum, String message, Object object) {
        if (codeEnum == null) {
            logErrorUse();
            return null;
        } else {
            // Construction du message à logger
            StringBuilder text = new StringBuilder(codeEnum.getText());
            if (StringUtils.isNotBlank(message)) {
                // Si un message existe (ex: debug ou trace), on le concatene au texte à logger
                text.append(DEFAULT).append(message);
            } else if (object != null) {
                // Aucun message n'est passé en param, on concatène object.toString() s'il est non null.
                text.append(DEFAULT).append(object.toString());
            }
            return text.toString();
        }
    }

    /**
     * prépare le contexte de log qui sera utilisé par log4j les infos de la map MDC sont à utiliser dans le fichier xml
     * de configuration log4j dans le paramètre de conversionPattern sous la forme %X{clé}
     *
     * @param session
     * @param codeEnum
     * @param object
     */
    private void setMDCParameter(final CoreSession session, final STLogEnum codeEnum, final Object object) {
        MDC.put(USERNAME_MDC, getUserName(session));
        MDC.put(CODE_MDC, getFormattedCode(codeEnum));
        if (object instanceof DocumentModel) {
            DocumentModel doc = (DocumentModel) object;
            MDC.put(UID_DOC_MDC, getUidDoc(doc));
            MDC.put(PATH_DOC_MDC, getPathDoc(doc));
            MDC.put(TYPE_DOC_MDC, getTypeDoc(doc));
            MDC.put(TITLE_DOC_MDC, getTitleDoc(doc));
            MDC.put(PARENT_DOC_MDC, getParentUid(doc));
        } else if (object instanceof DocumentRef) {
            DocumentRef doc = (DocumentRef) object;
            MDC.put(UID_DOC_MDC, getUidDoc(doc));
        } else if (object instanceof OrganigrammeNode) {
            OrganigrammeNode node = (OrganigrammeNode) object;
            MDC.put(UID_DOC_MDC, getIdNode(node));
            MDC.put(TYPE_DOC_MDC, getTypeNode(node));
            MDC.put(TITLE_DOC_MDC, getTitleNode(node));
        } else if (object instanceof String) {
            String str = (String) object;
            MDC.put(STRING_MDC, str);
        }
    }

    /**
     * Le contexte doit être "nettoyé" après utilisation pour éviter qu'un log venant après réutilise les infos
     */
    private void cleanMDCParameter() {
        MDC.remove(USERNAME_MDC);
        MDC.remove(CODE_MDC);
        MDC.remove(UID_DOC_MDC);
        MDC.remove(PATH_DOC_MDC);
        MDC.remove(TYPE_DOC_MDC);
        MDC.remove(TITLE_DOC_MDC);
        MDC.remove(PARENT_DOC_MDC);
        MDC.remove(STRING_MDC);
    }

    private void log(final String message, final Throwable exc, int typeLog) {
        switch (typeLog) {
            case TRACE_INT:
                if (exc == null) {
                    logger.trace(message);
                } else {
                    logger.trace(message, exc);
                }
                break;
            case DEBUG_INT:
                if (exc == null) {
                    logger.debug(message);
                } else {
                    logger.debug(message, exc);
                }
                break;
            case INFO_INT:
                if (exc == null) {
                    logger.info(message);
                } else {
                    logger.info(message, exc);
                }
                break;
            case WARN_INT:
                if (exc == null) {
                    logger.warn(message);
                } else {
                    logger.warn(message, exc);
                }
                break;
            case ERROR_INT:
                if (exc == null) {
                    logger.error(message);
                } else {
                    logger.error(message, exc);
                }
                break;
            case FATAL_INT:
                if (exc == null) {
                    logger.fatal(message);
                } else {
                    logger.fatal(message, exc);
                }
                break;
            default:
                logErrorUse();
                break;
        }
    }

    private void logErrorUse() {
        // code erreur echec utilisation logger
        STLogEnum codeEnum = STLogEnumImpl.FAIL_LOG_TEC;
        // reset du mdc
        cleanMDCParameter();
        MDC.put(CODE_MDC, getFormattedCode(codeEnum));
        log(getTextCode(codeEnum, null, null), null, ERROR_INT);
        cleanMDCParameter();
    }

    /**
     * On prépare le log à afficher : récupération du texte, initialisation de la map MDC log4j On log ensuite avec la
     * méthode log On nettoie la map MDC log4j
     *
     * @param session
     *            la session en cours (peut être null)
     * @param codeEnum
     *            le code de log (ne doit pas être null, sinon log erreur utilisation logger)
     * @param object
     *            Object concerné (peut être null)
     * @param message
     *            String message supplémentaire utilisé en débug ou trace (peut être null)
     * @param exc
     *            Throwable une exception potentielle qu'il faudrait logger (peut être null)
     * @param typeLog
     *            le niveau de log
     */
    private void simpleLog(
        final CoreSession session,
        final STLogEnum codeEnum,
        final Object object,
        final String message,
        final Throwable exc,
        int typeLog
    ) {
        String messageCode = getTextCode(codeEnum, message, object);
        if (messageCode != null) {
            setMDCParameter(session, codeEnum, object);
            log(messageCode, exc, typeLog);
            cleanMDCParameter();
        }
    }

    /**
     * Méthode pour ne pas logger les infos sessions On prépare le log à afficher : récupération du texte,
     * initialisation de la map MDC log4j On log ensuite avec la méthode log On nettoie la map MDC log4j
     *
     * @param codeEnum
     *            le code de log (ne doit pas être null, sinon log erreur utilisation logger)
     * @param message
     *            String message supplémentaire utilisé en débug ou trace (peut être null)
     * @param exc
     *            Throwable une exception potentielle qu'il faudrait logger (peut être null)
     * @param typeLog
     *            le niveau de log
     */
    private void simpleLog(final STLogEnum codeEnum, final String message, final Throwable exc, int typeLog) {
        String messageCode = getTextCode(codeEnum, message, null);
        if (messageCode != null) {
            MDC.put(CODE_MDC, getFormattedCode(codeEnum));
            log(messageCode, exc, typeLog);
            cleanMDCParameter();
        }
    }

    private void simpleLog(
        final CoreSession session,
        final STLogEnum codeEnum,
        final Object object,
        final Throwable exc,
        int typeLog
    ) {
        simpleLog(session, codeEnum, object, null, exc, typeLog);
    }

    private void multipleLog(
        final CoreSession session,
        final STLogEnum codeEnum,
        final List<Object> objects,
        final Throwable exc,
        int typeLog
    ) {
        for (final Object object : objects) {
            simpleLog(session, codeEnum, object, exc, typeLog);
        }
    }

    private void multipleLog(
        final CoreSession session,
        final STLogEnum codeEnum,
        final Object[] objects,
        final Throwable exc,
        int typeLog
    ) {
        for (final Object objectRef : objects) {
            simpleLog(session, codeEnum, objectRef, exc, typeLog);
        }
    }

    /* **************************************************************************************************************************
     * *
     * METHODES LOG * *
     * **************************************************************************************************
     * ***********************
     */

    @Override
    public void trace(final CoreSession session, final STLogEnum codeEnum, final Object object, final String message) {
        if (logger.isTraceEnabled()) {
            simpleLog(session, codeEnum, object, message, null, TRACE_INT);
        }
    }

    @Override
    public void trace(
        final CoreSession session,
        final STLogEnum codeEnum,
        final Object object,
        final String message,
        final Throwable exc
    ) {
        if (logger.isTraceEnabled()) {
            simpleLog(session, codeEnum, object, message, exc, TRACE_INT);
        }
    }

    @Override
    public void trace(final CoreSession session, final STLogEnum codeEnum, final Object object, final Throwable exc) {
        if (logger.isTraceEnabled()) {
            simpleLog(session, codeEnum, object, exc, TRACE_INT);
        }
    }

    @Override
    public void trace(final CoreSession session, final STLogEnum codeEnum, final Object object) {
        trace(session, codeEnum, object, (Throwable) null);
    }

    @Override
    public void trace(final CoreSession session, final STLogEnum codeEnum) {
        trace(session, codeEnum, (DocumentRef) null, (Throwable) null);
    }

    @Override
    public void trace(final CoreSession session, final STLogEnum codeEnum, final Throwable exc) {
        trace(session, codeEnum, (DocumentRef) null, exc);
    }

    @Override
    public void trace(final CoreSession session, final STLogEnum codeEnum, final List<Object> objects) {
        if (logger.isTraceEnabled()) {
            multipleLog(session, codeEnum, objects, null, TRACE_INT);
        }
    }

    @Override
    public void trace(final CoreSession session, final STLogEnum codeEnum, final Object[] objects) {
        if (logger.isTraceEnabled()) {
            multipleLog(session, codeEnum, objects, null, TRACE_INT);
        }
    }

    @Override
    public void trace(STLogEnum codeEnum, String message) {
        simpleLog(codeEnum, message, null, TRACE_INT);
    }

    @Override
    public void trace(STLogEnum codeEnum) {
        trace(codeEnum, null);
    }

    @Override
    public void debug(final CoreSession session, final STLogEnum codeEnum, final Object object, final String message) {
        if (isDebugEnable()) {
            simpleLog(session, codeEnum, object, message, null, DEBUG_INT);
        }
    }

    @Override
    public void debug(
        final CoreSession session,
        final STLogEnum codeEnum,
        final Object object,
        final String message,
        final Throwable exc
    ) {
        if (isDebugEnable()) {
            simpleLog(session, codeEnum, object, message, exc, TRACE_INT);
        }
    }

    @Override
    public void debug(final CoreSession session, final STLogEnum codeEnum, final Object object, final Throwable exc) {
        if (isDebugEnable()) {
            simpleLog(session, codeEnum, object, exc, DEBUG_INT);
        }
    }

    @Override
    public void debug(final CoreSession session, final STLogEnum codeEnum, final Object object) {
        if (isDebugEnable()) {
            debug(session, codeEnum, object, (Throwable) null);
        }
    }

    @Override
    public void debug(final CoreSession session, final STLogEnum codeEnum) {
        if (isDebugEnable()) {
            debug(session, codeEnum, (DocumentRef) null, (Throwable) null);
        }
    }

    @Override
    public void debug(final CoreSession session, final STLogEnum codeEnum, final Throwable exc) {
        if (isDebugEnable()) {
            debug(session, codeEnum, (DocumentRef) null, exc);
        }
    }

    @Override
    public void debug(final CoreSession session, final STLogEnum codeEnum, final List<Object> objects) {
        if (isDebugEnable()) {
            multipleLog(session, codeEnum, objects, null, DEBUG_INT);
        }
    }

    @Override
    public void debug(final CoreSession session, final STLogEnum codeEnum, final Object[] objects) {
        if (isDebugEnable()) {
            multipleLog(session, codeEnum, objects, null, DEBUG_INT);
        }
    }

    @Override
    public void debug(STLogEnum codeEnum, String message) {
        if (isDebugEnable()) {
            simpleLog(codeEnum, message, null, DEBUG_INT);
        }
    }

    @Override
    public void debug(STLogEnum codeEnum, Throwable exc) {
        if (isDebugEnable()) {
            simpleLog(codeEnum, null, exc, DEBUG_INT);
        }
    }

    @Override
    public void debug(STLogEnum codeEnum) {
        if (isDebugEnable()) {
            debug(codeEnum, (String) null);
        }
    }

    @Override
    public void info(final CoreSession session, final STLogEnum codeEnum, final Object object, final Throwable exc) {
        if (isInfoEnable()) {
            simpleLog(session, codeEnum, object, exc, INFO_INT);
        }
    }

    @Override
    public void info(final CoreSession session, final STLogEnum codeEnum, final Object object) {
        if (isInfoEnable()) {
            info(session, codeEnum, object, null);
        }
    }

    @Override
    public void info(final CoreSession session, final STLogEnum codeEnum) {
        if (isInfoEnable()) {
            info(session, codeEnum, (DocumentRef) null, null);
        }
    }

    @Override
    public void info(final CoreSession session, final STLogEnum codeEnum, final Throwable exc) {
        if (isInfoEnable()) {
            info(session, codeEnum, (DocumentRef) null, exc);
        }
    }

    @Override
    public void info(final CoreSession session, final STLogEnum codeEnum, final List<Object> objects) {
        if (isInfoEnable()) {
            multipleLog(session, codeEnum, objects, null, INFO_INT);
        }
    }

    @Override
    public void info(final CoreSession session, final STLogEnum codeEnum, final Object[] objects) {
        if (isInfoEnable()) {
            multipleLog(session, codeEnum, objects, null, INFO_INT);
        }
    }

    @Override
    public void info(final STLogEnum codeEnum) {
        if (isInfoEnable()) {
            simpleLog(codeEnum, null, null, INFO_INT);
        }
    }

    @Override
    public void info(final STLogEnum codeEnum, final String message) {
        if (isInfoEnable()) {
            simpleLog(codeEnum, message, null, INFO_INT);
        }
    }

    @Override
    public void info(final CoreSession session, final STLogEnum codeEnum, final String message) {
        if (isInfoEnable()) {
            simpleLog(session, codeEnum, null, message, null, INFO_INT);
        }
    }

    @Override
    public void info(final STLogEnum codeEnum, final Throwable exc) {
        if (isInfoEnable()) {
            simpleLog(codeEnum, null, exc, INFO_INT);
        }
    }

    @Override
    public void warn(final CoreSession session, final STLogEnum codeEnum, final Object object, final Throwable exc) {
        simpleLog(session, codeEnum, object, exc, WARN_INT);
    }

    @Override
    public void warn(final CoreSession session, final STLogEnum codeEnum, final Object object) {
        warn(session, codeEnum, object, null);
    }

    @Override
    public void warn(final CoreSession session, final String message) {
        simpleLog(session, STLogEnumImpl.DEFAULT, null, null, null, WARN_INT);
    }

    @Override
    public void warn(final CoreSession session, final STLogEnum codeEnum) {
        warn(session, codeEnum, (DocumentRef) null, null);
    }

    @Override
    public void warn(final CoreSession session, final STLogEnum codeEnum, final Throwable exc) {
        warn(session, codeEnum, (DocumentRef) null, exc);
    }

    @Override
    public void warn(final CoreSession session, final STLogEnum codeEnum, final List<Object> objects) {
        multipleLog(session, codeEnum, objects, null, WARN_INT);
    }

    @Override
    public void warn(final CoreSession session, final STLogEnum codeEnum, final Object[] objects) {
        multipleLog(session, codeEnum, objects, null, WARN_INT);
    }

    @Override
    public void warn(STLogEnum codeEnum) {
        warn(codeEnum, (Throwable) null);
    }

    @Override
    public void warn(STLogEnum codeEnum, String message) {
        simpleLog(codeEnum, message, null, WARN_INT);
    }

    @Override
    public void warn(STLogEnum codeEnum, Throwable exc) {
        simpleLog(codeEnum, null, exc, WARN_INT);
    }

    @Override
    public void warn(STLogEnum codeEnum, Throwable exc, String message) {
        simpleLog(codeEnum, message, exc, WARN_INT);
    }

    @Override
    public void error(final CoreSession session, final STLogEnum codeEnum, final Object object, final Throwable exc) {
        simpleLog(session, codeEnum, object, exc, ERROR_INT);
    }

    @Override
    public void error(final CoreSession session, final STLogEnum codeEnum, final Object object) {
        error(session, codeEnum, object, null);
    }

    @Override
    public void error(final CoreSession session, final STLogEnum codeEnum) {
        error(session, codeEnum, (DocumentRef) null, null);
    }

    @Override
    public void error(final CoreSession session, final STLogEnum codeEnum, final Throwable exc) {
        error(session, codeEnum, (DocumentRef) null, exc);
    }

    @Override
    public void error(final CoreSession session, final STLogEnum codeEnum, final List<Object> objects) {
        multipleLog(session, codeEnum, objects, null, ERROR_INT);
    }

    @Override
    public void error(final CoreSession session, final STLogEnum codeEnum, final Object[] objects) {
        multipleLog(session, codeEnum, objects, null, ERROR_INT);
    }

    @Override
    public void error(STLogEnum codeEnum) {
        error(codeEnum, (Throwable) null);
    }

    @Override
    public void error(STLogEnum codeEnum, Throwable exc) {
        simpleLog(codeEnum, null, exc, ERROR_INT);
    }

    @Override
    public void error(STLogEnum codeEnum, Throwable exc, String msg) {
        simpleLog(codeEnum, msg, exc, ERROR_INT);
    }

    @Override
    public void error(STLogEnum codeEnum, String msg) {
        simpleLog(codeEnum, msg, null, ERROR_INT);
    }

    @Override
    public void fatal(final CoreSession session, final STLogEnum codeEnum) {
        fatal(session, codeEnum, (DocumentRef) null, null);
    }

    @Override
    public void fatal(final CoreSession session, final STLogEnum codeEnum, final Object object) {
        fatal(session, codeEnum, object, null);
    }

    @Override
    public void fatal(final CoreSession session, final STLogEnum codeEnum, final Throwable exc) {
        fatal(session, codeEnum, (DocumentRef) null, exc);
    }

    @Override
    public void fatal(final CoreSession session, final STLogEnum codeEnum, final List<Object> objects) {
        multipleLog(session, codeEnum, objects, null, FATAL_INT);
    }

    @Override
    public void fatal(final CoreSession session, final STLogEnum codeEnum, final Object[] objects) {
        multipleLog(session, codeEnum, objects, null, FATAL_INT);
    }

    @Override
    public void fatal(final CoreSession session, final STLogEnum codeEnum, final Object object, final Throwable exc) {
        simpleLog(session, codeEnum, object, exc, FATAL_INT);
    }

    @Override
    public void fatal(STLogEnum codeEnum) {
        fatal(codeEnum, null);
    }

    @Override
    public void fatal(STLogEnum codeEnum, Throwable exc) {
        simpleLog(codeEnum, null, exc, FATAL_INT);
    }

    @Override
    public boolean isDebugEnable() {
        return logger.isDebugEnabled();
    }

    @Override
    public boolean isInfoEnable() {
        return logger.isInfoEnabled();
    }
}
