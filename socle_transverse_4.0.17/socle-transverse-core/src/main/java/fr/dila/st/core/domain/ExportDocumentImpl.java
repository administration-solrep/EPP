package fr.dila.st.core.domain;

import fr.dila.st.api.constant.STExportConstants;
import fr.dila.st.api.constant.STLifeCycleConstant;
import fr.dila.st.api.domain.ExportDocument;
import java.util.Calendar;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Implémentation document ExportDocument
 */
public class ExportDocumentImpl extends STDomainObjectImpl implements ExportDocument {
    /**
     * Serial UID
     */
    private static final long serialVersionUID = 2217368619284178440L;

    public ExportDocumentImpl(DocumentModel doc) {
        super(doc);
    }

    @Override
    public String getOwner() {
        return getStringProperty(STExportConstants.EXPORT_DOC_SCHEMA, STExportConstants.EXPORT_DOC_OWNER_PROPERTY);
    }

    @Override
    public void setOwner(String owner) {
        setProperty(STExportConstants.EXPORT_DOC_SCHEMA, STExportConstants.EXPORT_DOC_OWNER_PROPERTY, owner);
    }

    @Override
    public Calendar getDateRequest() {
        return getDateProperty(STExportConstants.EXPORT_DOC_SCHEMA, STExportConstants.EXPORT_DOC_DATE_PROPERTY);
    }

    @Override
    public void setDateRequest(Calendar date) {
        setProperty(STExportConstants.EXPORT_DOC_SCHEMA, STExportConstants.EXPORT_DOC_DATE_PROPERTY, date);
    }

    @Override
    public boolean isExporting() {
        return STLifeCycleConstant.EXPORTING_STATE.equals(document.getCurrentLifeCycleState());
    }

    @Override
    public void setExporting(boolean exporting) {
        if (exporting) {
            document.followTransition(STLifeCycleConstant.TO_EXPORTING_TRANSITION);
        } else {
            document.followTransition(STLifeCycleConstant.TO_DONE_TRANSITION);
        }
    }

    @Override
    public void setFileContent(Blob content) {
        setProperty(STExportConstants.EXPORT_DOC_SCHEMA, STExportConstants.EXPORT_DOC_CONTENT_PROPERTY, content);
    }

    @Override
    public Blob getFileContent() {
        return getBlobProperty(STExportConstants.EXPORT_DOC_SCHEMA, STExportConstants.EXPORT_DOC_CONTENT_PROPERTY);
    }
}
