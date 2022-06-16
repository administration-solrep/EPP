package fr.dila.st.core.domain.comment;

import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.domain.comment.STComment;
import fr.dila.st.core.domain.STDomainObjectImpl;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import java.util.Calendar;
import org.nuxeo.ecm.core.api.DocumentModel;

public class STCommentImpl extends STDomainObjectImpl implements STComment {
    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 5797200275376972922L;

    public STCommentImpl(DocumentModel doc) {
        super(doc);
    }

    @Override
    public String getAuthor() {
        return PropertyUtil.getStringProperty(
            document,
            STSchemaConstant.COMMENT_SCHEMA,
            STSchemaConstant.COMMENT_AUTHOR_PROPERTY
        );
    }

    @Override
    public void setAuthor(String author) {
        PropertyUtil.setProperty(
            getDocument(),
            STSchemaConstant.COMMENT_SCHEMA,
            STSchemaConstant.COMMENT_AUTHOR_PROPERTY,
            author
        );
    }

    @Override
    public String getTexte() {
        return PropertyUtil.getStringProperty(
            document,
            STSchemaConstant.COMMENT_SCHEMA,
            STSchemaConstant.COMMENT_TEXT_PROPERTY
        );
    }

    @Override
    public void setTexte(String text) {
        PropertyUtil.setProperty(
            getDocument(),
            STSchemaConstant.COMMENT_SCHEMA,
            STSchemaConstant.COMMENT_TEXT_PROPERTY,
            text
        );
    }

    @Override
    public Calendar getCreationDate() {
        return PropertyUtil.getCalendarProperty(
            document,
            STSchemaConstant.COMMENT_SCHEMA,
            STSchemaConstant.COMMENT_CREATION_DATE_PROPERTY
        );
    }

    @Override
    public void setCreationDate(Calendar creationDate) {
        PropertyUtil.setProperty(
            getDocument(),
            STSchemaConstant.COMMENT_SCHEMA,
            STSchemaConstant.COMMENT_CREATION_DATE_PROPERTY,
            creationDate
        );
    }

    @Override
    public String getParentCommentId() {
        return PropertyUtil.getStringProperty(
            document,
            STSchemaConstant.COMMENT_SCHEMA,
            STSchemaConstant.COMMENT_PARENT_COMMENT_ID_PROPERTY
        );
    }
}
