package fr.dila.st.core.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataSource;
import org.nuxeo.ecm.core.api.Blob;

/**
 * Classe encapsule Blob et String
 *
 */
public class BlobDataSource implements DataSource {
    private final Blob data;
    private final String name;

    /**
     * Creates a DataSource from an array of bytes
     *
     * @param data
     *            byte[] Array of bytes to convert into a DataSource
     * @param name
     *            String Name of the DataSource (ex: filename)
     */
    public BlobDataSource(Blob data, String name) {
        this.data = data;
        this.name = name;
    }

    /**
     * Returns the content-type information required by a DataSource application/octet-stream in this case
     */
    public String getContentType() {
        return "application/octet-stream";
    }

    /**
     * Returns an InputStream from the DataSource
     *
     * @returns InputStream Array of bytes converted into an InputStream
     */
    public InputStream getInputStream() throws IOException {
        return data.getStream();
    }

    /**
     * Returns the name of the DataSource
     *
     * @returns String Name of the DataSource
     */
    public String getName() {
        return name;
    }

    /**
     * Returns an OutputStream from the DataSource
     *
     * @returns OutputStream Array of bytes converted into an OutputStream
     */
    public OutputStream getOutputStream() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        out.write(data.getByteArray());
        return out;
    }
}
