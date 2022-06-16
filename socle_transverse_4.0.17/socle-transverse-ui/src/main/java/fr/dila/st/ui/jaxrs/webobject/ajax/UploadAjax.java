package fr.dila.st.ui.jaxrs.webobject.ajax;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.restapi.server.jaxrs.BatchUploadObject;
import org.nuxeo.ecm.webengine.forms.FormData;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.runtime.api.Framework;

@WebObject(type = "UploadAjax")
public class UploadAjax extends BatchUploadObject {
    private static final String MIME_TYPE_WHITE_LIST_DEF_VAL =
        "application/msword,application/vnd.ms-excel,application/vnd.ms-powerpoint,application/vnd.oasis.opendocument.text,application/vnd.oasis.opendocument.spreadsheet,application/vnd.oasis.opendocument.presentation,application/vnd.openxmlformats-officedocument.wordprocessingml.document,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,application/vnd.openxmlformats-officedocument.presentationml.presentation,application/pdf,text/plain,text/rtf";
    private static final String MIME_TYPE_WHITE_LIST_PROP = "solon.file.upload.extension.accepted";
    private static final String MIME_TYPE_VERIFICATION_ENABLED = "solon.file.upload.extension.verification.enabled";

    @POST
    @Path("{batchId}/{fileIdx}")
    @Override
    public Response upload(
        @Context HttpServletRequest request,
        @PathParam(REQUEST_BATCH_ID) String batchId,
        @PathParam(REQUEST_FILE_IDX) String fileIdx
    )
        throws IOException {
        boolean mimeTypeVerificationEnabled = BooleanUtils.toBoolean(
            Framework.getProperty(MIME_TYPE_VERIFICATION_ENABLED, "false")
        );

        if (mimeTypeVerificationEnabled) {
            String mimeType = request.getHeader("X-File-Type");
            if (StringUtils.isBlank(mimeType)) {
                FormData formData = new FormData(request);
                Blob blob = formData.getFirstBlob();
                if (blob == null) {
                    throw new NuxeoException("Impossible de réaliser l'upload en multipart sans aucun blob attaché");
                }

                mimeType = blob.getMimeType();
            }

            String whiteList = Framework.getProperty(MIME_TYPE_WHITE_LIST_PROP, MIME_TYPE_WHITE_LIST_DEF_VAL);

            if (StringUtils.isEmpty(mimeType) || !whiteList.contains(mimeType)) {
                Map<String, Object> result = new HashMap<>();
                result.put("uploaded", "false");

                return buildEmptyResponse(Response.Status.UNSUPPORTED_MEDIA_TYPE);
            }
        }

        return super.upload(request, batchId, fileIdx);
    }
}
