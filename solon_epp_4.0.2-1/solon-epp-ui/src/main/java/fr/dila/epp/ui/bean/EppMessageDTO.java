package fr.dila.epp.ui.bean;

import fr.dila.st.ui.bean.parlement.STMessageDTO;

public class EppMessageDTO extends STMessageDTO {
    private static final long serialVersionUID = 1L;

    private static final String LECTURE = "lecture";

    private static final String VERSION = "version";

    private static final String MODE_CREATION_VERSION = "modeCreationVersion";

    public EppMessageDTO() {
        super();
    }

    public EppMessageDTO(
        String idDossier,
        String objetDossier,
        String lecture,
        String emetteur,
        String destinataire,
        String copie,
        String communication,
        String version,
        String date
    ) {
        super(idDossier, objetDossier, emetteur, destinataire, copie, communication, date);
        setLecture(lecture);
        setVersion(version);
    }

    public String getLecture() {
        return getString(LECTURE);
    }

    public void setLecture(String lecture) {
        put(LECTURE, lecture);
    }

    public String getVersion() {
        return getString(VERSION);
    }

    public void setVersion(String version) {
        put(VERSION, version);
    }

    public void setModeCreationVersion(String modeCreationVersion) {
        put(MODE_CREATION_VERSION, modeCreationVersion);
    }

    public String getModeCreationVersion() {
        return getString(MODE_CREATION_VERSION);
    }
}
