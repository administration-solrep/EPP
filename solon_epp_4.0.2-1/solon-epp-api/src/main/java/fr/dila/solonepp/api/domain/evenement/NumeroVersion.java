package fr.dila.solonepp.api.domain.evenement;

/**
 * Numéro de version.
 *
 * @author jtremeaux
 */
public class NumeroVersion {
    /**
     * Numéro de version majeur.
     */
    private Long majorVersion;

    /**
     * Numéro de version mineur.
     */
    private Long minorVersion;

    /**
     * Constructeur de NumeroVersion.
     *
     * @param majorVersion Numéro de version majeur
     * @param minorVersion Numéro de version mineur
     */
    public NumeroVersion(Long majorVersion, Long minorVersion) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
    }

    /**
     * Retourne le numéro de version majeur.
     *
     * @return Numéro de version majeur
     */
    public Long getMajorVersion() {
        return majorVersion;
    }

    /**
     * Renseigne le numéro de version majeur.
     *
     * @param objet Numéro de version majeur
     */
    public void setMajorVersion(Long majorVersion) {
        this.majorVersion = majorVersion;
    }

    /**
     * Retourne le numéro de version mineur.
     *
     * @return Numéro de version mineur
     */
    public Long getMinorVersion() {
        return minorVersion;
    }

    /**
     * Renseigne le numéro de version mineur.
     *
     * @param objet Numéro de version mineur
     */
    public void setMinorVersion(Long minorVersion) {
        this.minorVersion = minorVersion;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder().append(majorVersion).append(".").append(minorVersion);
        return sb.toString();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((majorVersion == null) ? 0 : majorVersion.hashCode());
        result = prime * result + ((minorVersion == null) ? 0 : minorVersion.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        NumeroVersion other = (NumeroVersion) obj;
        if (majorVersion == null) {
            if (other.majorVersion != null) {
                return false;
            }
        } else if (!majorVersion.equals(other.majorVersion)) {
            return false;
        }
        if (minorVersion == null) {
            if (other.minorVersion != null) {
                return false;
            }
        } else if (!minorVersion.equals(other.minorVersion)) {
            return false;
        }
        return true;
    }
}
