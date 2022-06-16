package fr.dila.st.ui.jaxrs.writer;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.dila.st.ui.bean.JsonResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

/**
 * Write data for JSON Object
 * @author SLE
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class JsonWriter implements MessageBodyWriter<JsonResponse> {

    public JsonWriter() {
        // do nothing
    }

    public void writeTo(
        JsonResponse json,
        Class<?> type,
        Type genericType,
        Annotation[] annotations,
        MediaType mediaType,
        MultivaluedMap<String, Object> httpHeaders,
        OutputStream entityStream
    )
        throws IOException {
        new ObjectMapper().writeValue(entityStream, json);
    }

    public long getSize(JsonResponse json, Class<?> clazz, Type type, Annotation[] annot, MediaType mediatype) {
        return -1;
    }

    public boolean isWriteable(Class<?> clazz, Type type, Annotation[] annot, MediaType mediatype) {
        return JsonResponse.class.isAssignableFrom(clazz) && MediaType.APPLICATION_JSON_TYPE.equals(mediatype);
    }
}
