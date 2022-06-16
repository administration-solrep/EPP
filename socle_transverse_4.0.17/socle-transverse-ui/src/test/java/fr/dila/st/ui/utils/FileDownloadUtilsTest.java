package fr.dila.st.ui.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import javax.ws.rs.core.Response;
import org.junit.Test;

public class FileDownloadUtilsTest {

    @Test
    public void testBuildResponse_ok() {
        String filename = "filename.txt";
        File folder = new File("/tmp");
        Response response = FileDownloadUtils.getResponse(folder, filename, "text/plain");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertThat(getResponseContentDisposition(response)).contains(filename);
    }

    @Test
    public void testBuildResponse_forbidden() {
        String filename = "../filename.txt";
        File folder = new File("/tmp");

        Response response = FileDownloadUtils.getResponse(folder, filename);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void testIsFilenameParamSecure() {
        assertTrue(FileDownloadUtils.isSecuredFilename("filename"));
        assertTrue(FileDownloadUtils.isSecuredFilename("filename.txt"));
        assertTrue(FileDownloadUtils.isSecuredFilename("filename01.txt"));
        assertTrue(FileDownloadUtils.isSecuredFilename("filename_.txt"));
        assertTrue(FileDownloadUtils.isSecuredFilename("_filename.txt"));
        assertTrue(FileDownloadUtils.isSecuredFilename("filename_test.txt"));
        assertTrue(FileDownloadUtils.isSecuredFilename("filename test.txt"));
        assertTrue(FileDownloadUtils.isSecuredFilename("filename_éàèù.txt"));
        assertTrue(FileDownloadUtils.isSecuredFilename("filename.txttxt"));
        assertFalse(FileDownloadUtils.isSecuredFilename("test/filename.txt"));
        assertFalse(FileDownloadUtils.isSecuredFilename("/test/filename.txt"));
        assertFalse(FileDownloadUtils.isSecuredFilename("../filename.txt"));
    }

    @Test
    public void testGetResponse() {
        File file = mock(File.class);
        when(file.isFile()).thenReturn(true);
        String filename = "file n@me.txt";
        String mimetype = "text/plain";

        Response response = FileDownloadUtils.getResponse(file, filename, mimetype);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertThat(getResponseContentDisposition(response)).contains(filename);
    }

    private static String getResponseContentDisposition(Response response) {
        return (String) response.getMetadata().get(FileDownloadUtils.HDR_CONTENT_DISPOSITION).get(0);
    }
}
