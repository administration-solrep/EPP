package fr.dila.ss.ui.jaxrs.weboject.model;

import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.function.Consumer;
import javax.ws.rs.core.StreamingOutput;

public interface WebObjectExportModel {
    default StreamingOutput getOutputStream(SpecificContext context, Consumer<SpecificContext> consumer) {
        return output -> {
            context.putInContextData(SSContextDataKey.OUTPUTSTREAM, output);
            consumer.accept(context);
        };
    }
}
