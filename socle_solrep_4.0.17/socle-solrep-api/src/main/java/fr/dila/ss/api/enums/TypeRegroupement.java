package fr.dila.ss.api.enums;

import java.util.Objects;
import java.util.stream.Stream;

public enum TypeRegroupement {
    PAR_MINISTERE,
    PAR_POSTE,
    PAR_SIGNALE,
    PAR_TYPE_ETAPE,
    PAR_TYPE_ACTE;

    public static TypeRegroupement fromValue(String name) {
        return Stream.of(values()).filter(statut -> Objects.equals(statut.name(), name)).findFirst().orElse(null);
    }
}
