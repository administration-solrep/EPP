package fr.dila.st.ui.jaxrs.writer;

import fr.dila.st.ui.th.ThEngineService;
import fr.dila.st.ui.th.model.ThTemplate;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import org.nuxeo.runtime.api.Framework;

/**
 * Write data by using Thymeleaf rendering engine
 * @author SPL
 */
@Provider
@Produces("*/*")
public class ThTemplateWriter implements MessageBodyWriter<ThTemplate> {

    public ThTemplateWriter() {
        // do nothing
    }

    public void writeTo(
        ThTemplate tpl,
        Class<?> type,
        Type genericType,
        Annotation[] annotations,
        MediaType mediaType,
        MultivaluedMap<String, Object> httpHeaders,
        OutputStream entityStream
    )
        throws IOException {
        ThEngineService engineService = Framework.getService(ThEngineService.class);
        engineService.render(tpl, entityStream);
    }

    public long getSize(ThTemplate tpl, Class<?> clazz, Type type, Annotation[] annot, MediaType mediatype) {
        return -1;
    }

    public boolean isWriteable(Class<?> clazz, Type type, Annotation[] annot, MediaType mediatype) {
        return ThTemplate.class.isAssignableFrom(clazz);
    }
}
