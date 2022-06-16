package fr.sword.naiad.nuxeo.commons.core.constant;

import org.nuxeo.ecm.core.schema.FacetNames;

import fr.sword.naiad.nuxeo.commons.core.util.SchemaUtil;

/**
 * Class utilitaire regroupant les schemas et propriétés des document NUXEO communément utilisé
 * @author SPL
 *
 */
public final class CommonSchemaConstant {

    // ----------- FACET -------------
    public static final String FACET_HIDDEN_IN_NAVIGATION = FacetNames.HIDDEN_IN_NAVIGATION;

    public static final String FACET_PICTURE = "Picture";
    
    public static final String FACET_THUMBNAIL = "Thumbnail";

    // ----------- ECM ----------------

    public static final String PREFIX_ECM = "ecm";

    public static final String XPATH_ECM_PARENTID = SchemaUtil.xpath(PREFIX_ECM, "parentId");

    public static final String XPATH_ECM_UUID = SchemaUtil.xpath(PREFIX_ECM, "uuid");

    public static final String XPATH_ECM_PRIMARYTYPE = SchemaUtil.xpath(PREFIX_ECM, "primaryType");

    public static final String XPATH_ECM_MIXINTYPE = SchemaUtil.xpath(PREFIX_ECM, "mixinType");

    public static final String XPATH_ECM_NAME = SchemaUtil.xpath(PREFIX_ECM, "name");

    public static final String XPATH_ECM_LIFECYCLE = SchemaUtil.xpath(PREFIX_ECM, "currentLifeCycleState");

    public static final String XPATH_ECM_POS = SchemaUtil.xpath(PREFIX_ECM, "pos");

    public static final String XPATH_ECM_FULLTEXT = SchemaUtil.xpath(PREFIX_ECM, "fulltext");

    // ----------- DUBLINCORE ----------------

    public static final String SCHEMA_DUBLINCORE = "dublincore";

    public static final String PREFIX_DUBLICORE = "dc";

    public static final String PROP_DUBLINCORE_TITLE = "title";

    public static final String PROP_DUBLINCORE_DESCRIPTION = "description";

    public static final String PROP_DUBLINCORE_MODIFIED = "modified";

    public static final String PROP_DUBLINCORE_SUBJECTS = "subjects";

    public static final String PROP_DUBLINCORE_LAST_CONTRIBUTOR = "lastContributor";

    public static final String PROP_DUBLINCORE_CREATED = "created";

    public static final String PROP_DUBLINCORE_CREATOR = "creator";

    public static final String PROP_DUBLINCORE_CONTRIBUTORS = "contributors";

    public static final String XPATH_DUBLINCORE_TITLE = SchemaUtil.xpath(PREFIX_DUBLICORE, PROP_DUBLINCORE_TITLE);

    public static final String XPATH_DUBLINCORE_CREATED = SchemaUtil.xpath(PREFIX_DUBLICORE, PROP_DUBLINCORE_CREATED);

    public static final String XPATH_DUBLINCORE_MODIFIED = SchemaUtil.xpath(PREFIX_DUBLICORE, PROP_DUBLINCORE_MODIFIED);

    public static final String XPATH_DUBLINCORE_SUBJECTS = SchemaUtil.xpath(PREFIX_DUBLICORE, PROP_DUBLINCORE_SUBJECTS);

    public static final String XPATH_DUBLINCORE_CREATOR = SchemaUtil.xpath(PREFIX_DUBLICORE, PROP_DUBLINCORE_CREATOR);

    // ----------- FILE ----------------

    public static final String SCHEMA_FILE = "file";

    public static final String PREFIX_FILE = "file";

    public static final String PROP_FILE_CONTENT = "content";

    public static final String XPATH_FILE_CONTENT = SchemaUtil.xpath(PREFIX_FILE, PROP_FILE_CONTENT);

    // ----------- FILES ----------------
    
    public static final String SCHEMA_FILES = "files";
    
    public static final String PROP_FILES_FILES = "files";

    // ----------- RELATION ----------------

    public static final String SCHEMA_RELATION = "relation";

    public static final String PREFIX_RELATION = "relation";

    public static final String PROP_RELATION_SOURCE = "source";

    public static final String PROP_RELATION_TARGET = "target";

    public static final String XPATH_RELATION_SOURCE = SchemaUtil.xpath(PREFIX_RELATION, PROP_RELATION_SOURCE);

    public static final String XPATH_RELATION_TARGET = SchemaUtil.xpath(PREFIX_RELATION, PROP_RELATION_TARGET);

    // ----------- PICTURE ----------------

    public static final String SCHEMA_PICTURE = "picture";

    public static final String PREFIX_PICTURE = "picture";

    public static final String PROP_PICTURE_VIEWS = "views";

    public static final String XPATH_PICTURE_VIEWS = SchemaUtil.xpath(PREFIX_PICTURE, PROP_PICTURE_VIEWS);

    public static final String KEY_PICTURE_VIEWS_TITLE = "title";

    public static final String KEY_PICTURE_VIEWS_DESCRIPTION = "description";

    public static final String KEY_PICTURE_VIEWS_TAG = "tag";

    public static final String KEY_PICTURE_VIEWS_FILENAME = "filename";

    public static final String KEY_PICTURE_VIEWS_WIDTH = "width";

    public static final String KEY_PICTURE_VIEWS_HEIGHT = "height";

    // ----------- PICTUREBOOK ----------------

    public static final String SCHEMA_PICTUREBOOK = "picturebook";

    public static final String PREFIX_PICTUREBOOK = "picturebook";

    public static final String PROP_PICTUREBOOK_PICTURETEMPLATES = "picturetemplates";

    public static final String XPATH_PICTUREBOOK_PICTURETEMPLATES = SchemaUtil.xpath(PREFIX_PICTUREBOOK,
            PROP_PICTUREBOOK_PICTURETEMPLATES);

    public static final String KEY_PICTUREBOOK_PICTURETEMPLATES_TITLE = "title";

    public static final String KEY_PICTUREBOOK_PICTURETEMPLATES_DESCRIPTION = "description";

    public static final String KEY_PICTUREBOOK_PICTURETEMPLATES_TAG = "tag";

    public static final String KEY_PICTUREBOOK_PICTURETEMPLATES_MAXSIZE = "maxsize";

    // ----------- GROUP ----------------

    public static final String SCHEMA_GROUP = "group";

    public static final String PREFIX_GROUP = "group";

    public static final String PROP_GROUP_NAME = "groupname";

    public static final String PROP_GROUP_LABEL = "grouplabel";

    public static final String PROP_GROUP_DESCRIPTION = "description";

    public static final String PROP_GROUP_MEMBERS = "members";

    public static final String PROP_GROUP_SUBGROUPS = "subGroups";

    // ----------- USER ----------------

    public static final String SCHEMA_USER = "user";

    public static final String PREFIX_USER = "user";

    public static final String PROP_USER_USERNAME = "username";

    public static final String PROP_USER_FIRSTNAME = "firstName";

    public static final String PROP_USER_LASTNAME = "lastName";

    public static final String PROP_USER_EMAIL = "email";

    public static final String PROP_USER_PHONE = "phone";

    public static final String PROP_USER_GROUPS = "groups";

    public static final String PROP_USER_COMPANY = "company";

    public static final String PROP_USER_PASSWORD = "password";

    // ------------- TASK ------------------------

    public static final String SCHEMA_TASK = "task";

    public static final String PREFIX_TASK = "nt";

    public static final String PROP_TASK_TYPE = "type";

    public static final String PROP_TASK_DUEDATE = "dueDate";

    public static final String PROP_TASK_TARGETDOCIDS = "targetDocumentsIds";

    public static final String PROP_TASK_TARGETDOCID = "targetDocumentId";

    public static final String PROP_TASK_VARIABLES = "task_variables";

    public static final String PROP_TASK_DELEGATED = "delegatedActors";

    public static final String PROP_TASK_ACTORS = "actors";

    public static final String PROP_TASK_NAME = "name";

    public static final String PROP_TASK_INITIATOR = "initiator";

    public static final String PROP_TASK_PROCESSID = "processId";

    public static final String XPATH_TASK_TYPE = SchemaUtil.xpath(PREFIX_TASK, PROP_TASK_TYPE);

    public static final String XPATH_TASK_DUEDATE = SchemaUtil.xpath(PREFIX_TASK, PROP_TASK_DUEDATE);

    public static final String XPATH_TASK_PROCESSID = SchemaUtil.xpath(PREFIX_TASK, PROP_TASK_PROCESSID);

    public static final String XPATH_TASK_TARGETDOCIDS = SchemaUtil.xpath(PREFIX_TASK, PROP_TASK_TARGETDOCIDS);

    public static final String XPATH_TASK_TARGETDOCID = SchemaUtil.xpath(PREFIX_TASK, PROP_TASK_TARGETDOCID);

    public static final String XPATH_TASK_NAME = SchemaUtil.xpath(PREFIX_TASK, PROP_TASK_NAME);

    /**
     * Constructeur privé : class utilitaire
     */
    private CommonSchemaConstant() {

    }

}
