package fr.dila.st.ui.th.bean;

import fr.dila.st.api.service.STUserService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.annot.SwBean;
import fr.dila.st.ui.validators.annot.SwNotEmpty;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.ws.rs.FormParam;
import org.apache.commons.lang3.StringUtils;

@SwBean(keepdefaultValue = true)
public class TransmettreParMelForm {
    @FormParam("idMessage")
    private String idMessage;

    @FormParam("objet")
    @SwNotEmpty
    private String objet;

    @FormParam("destinataires")
    private ArrayList<String> destinataires = new ArrayList<>();

    @FormParam("destinataires-externes")
    private String destinatairesExternes;

    @FormParam("message")
    @SwNotEmpty
    private String message;

    public TransmettreParMelForm() {
        super();
    }

    public TransmettreParMelForm(String idMessage, String objet) {
        this.idMessage = idMessage;
        this.objet = objet;
    }

    public String getIdMessage() {
        return idMessage;
    }

    public void setIdMessage(String idMessage) {
        this.idMessage = idMessage;
    }

    public String getObjet() {
        return objet;
    }

    public void setObjet(String objet) {
        this.objet = objet;
    }

    public List<String> getDestinataires() {
        return destinataires;
    }

    public void setDestinataires(ArrayList<String> destinataires) {
        this.destinataires = destinataires;
    }

    public String getDestinatairesExternes() {
        return destinatairesExternes;
    }

    public void setDestinatairesExternes(String destinatairesExternes) {
        this.destinatairesExternes = destinatairesExternes;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFullDestinataires() {
        STUserService userService = STServiceLocator.getSTUserService();
        return Stream
            .of(
                destinatairesExternes,
                destinataires
                    .stream()
                    .map(dest -> userService.getUser(dest).getEmail())
                    .collect(Collectors.joining(";"))
            )
            .filter(StringUtils::isNotEmpty)
            .collect(Collectors.joining(";"));
    }
}
