package fr.dila.st.api.organigramme;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Type contenu dans le champs ou des noeud de l'organigramme.
 *
 * @author Fabio Esposito
 */
public enum OrganigrammeType {
    BASE("BASE"),
    INSTITUTION("INS", "organigramme.type.institution"),
    GOUVERNEMENT("GVT"),
    MINISTERE("MIN", "organigramme.type.ministere"),
    UNITE_STRUCTURELLE("UST", "organigramme.type.uniteStructurelle"),
    DIRECTION("DIR"),
    POSTE("PST", "organigramme.type.poste"),
    USER("USR"),
    OTHER("OTHER");

    private final String value;
    private final String messageCode;
    private final List<OrganigrammeType> lstPotentialParent = new ArrayList<>();

    static {
        MINISTERE.lstPotentialParent.add(GOUVERNEMENT);
        UNITE_STRUCTURELLE.lstPotentialParent.addAll(
            Arrays.asList(MINISTERE, INSTITUTION, UNITE_STRUCTURELLE, DIRECTION)
        );
        DIRECTION.lstPotentialParent.addAll(Arrays.asList(MINISTERE, INSTITUTION, UNITE_STRUCTURELLE, DIRECTION));
        POSTE.lstPotentialParent.addAll(Arrays.asList(MINISTERE, INSTITUTION, UNITE_STRUCTURELLE, DIRECTION));
        USER.lstPotentialParent.add(POSTE);
    }

    OrganigrammeType(String type) {
        this(type, null);
    }

    OrganigrammeType(String type, String messageCode) {
        this.value = type;
        this.messageCode = messageCode;
    }

    public String getValue() {
        return value;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public static OrganigrammeType getEnum(String enumValue) {
        return Stream.of(values()).filter(type -> Objects.equals(type.value, enumValue)).findFirst().orElse(null);
    }

    public List<OrganigrammeType> getLstPotentialParent() {
        return new ArrayList<>(lstPotentialParent);
    }

    public static boolean isPoste(String nodeType) {
        return POSTE.getValue().equals(nodeType);
    }

    public static boolean isUniteStructurelle(String nodeType) {
        return UNITE_STRUCTURELLE.getValue().equals(nodeType);
    }

    public static boolean isMinistere(String nodeType) {
        return MINISTERE.getValue().equals(nodeType);
    }

    public static boolean isDirection(String nodeType) {
        return DIRECTION.getValue().equals(nodeType);
    }
}
