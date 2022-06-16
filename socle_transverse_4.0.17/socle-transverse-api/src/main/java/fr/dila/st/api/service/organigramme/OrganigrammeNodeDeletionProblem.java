package fr.dila.st.api.service.organigramme;

/**
 * Représente un problème repéré lors de la scrutation d'un élément de
 * l'organigramme en vue de sa suppression.
 *
 * @author tlombard
 */
public class OrganigrammeNodeDeletionProblem {
    /** Type de problème rencontré */
    private ProblemType problemType;

    /** Elément d'identification de l'objet bloqué dans sa suppression */
    private String blockedObjIdentification;

    /** Type d'objet qui bloque la suppression */
    private ProblemScope blockingObjType;

    /** Elément d'identification de l'objet qui bloque la suppression. */
    private String blockingObjIdentification;

    /**
     * Dans le cas où l'objet qui bloque la suppression d'une direction est un
     * dossier (EPG), ministère de rattachement + direction de rattachement + statut
     * du dossier.
     */
    private String blockingDossierInfo;

    /**
     * Lorsque la suppression concerne un poste :
     * <ul>
     * <li>les directions et ministères de rattachement du poste (REP)</li>
     * <li>les ministères de rattachement du poste (EPG)</li>
     * </ul>
     */
    private String posteInfo;

    private OrganigrammeNodeDeletionProblem() {
        // Default constructor not accessible
        super();
    }

    public OrganigrammeNodeDeletionProblem(ProblemType problemType, String blockedObjIdentification) {
        this(problemType, blockedObjIdentification, null, null);
    }

    public OrganigrammeNodeDeletionProblem(
        ProblemType problemType,
        String blockedObjIdentification,
        String blockingObjIdentification,
        String posteInfo
    ) {
        this();
        this.problemType = problemType;
        blockingObjType = problemType.scope;
        this.blockedObjIdentification = blockedObjIdentification;
        this.blockingObjIdentification = blockingObjIdentification;
        this.posteInfo = posteInfo;
    }

    public ProblemType getProblemType() {
        return problemType;
    }

    public void setProblemType(ProblemType problemType) {
        this.problemType = problemType;
    }

    public ProblemScope getBlockingObjType() {
        return blockingObjType;
    }

    public void setBlockingObjType(ProblemScope blockingObjType) {
        this.blockingObjType = blockingObjType;
    }

    public String getBlockingObjIdentification() {
        return blockingObjIdentification;
    }

    public void setBlockingObjIdentification(String blockingObjId) {
        this.blockingObjIdentification = blockingObjId;
    }

    public String getBlockingDossierInfo() {
        return blockingDossierInfo;
    }

    public void setBlockingDossierInfo(String blockingDossierInfo) {
        this.blockingDossierInfo = blockingDossierInfo;
    }

    public String getPosteInfo() {
        return posteInfo;
    }

    public void setPosteInfo(String posteInfo) {
        this.posteInfo = posteInfo;
    }

    public enum ProblemScope {
        MODELE_FDR("organigramme.error.delete.scope.modele.fdr"),
        UTILISATEUR("organigramme.error.delete.scope.utilisateur"),
        DOSSIER("organigramme.error.delete.scope.dossier"),
        SQUELETTE("organigramme.error.delete.scope.squelette"),
        BULLETIN_OFFICIEL("organigramme.error.delete.scope.bulletin.officiel");

        /** Property associée à ce scope */
        private String propertyKey;

        ProblemScope(String propertyKey) {
            this.propertyKey = propertyKey;
        }

        public String getPropertyKey() {
            return propertyKey;
        }
    }

    public enum ProblemType {
        MODELE_FDR_ATTACHED_TO_DIRECTION(
            "organigramme.error.delete.direction.rattache.fdr.modele",
            ProblemScope.MODELE_FDR
        ),
        USER_IS_LINKED_TO_POSTE("organigramme.error.delete.user.has.only.one.poste", ProblemScope.UTILISATEUR),
        INSTANCE_FDR_ATTACHED_TO_POSTE("organigramme.error.delete.poste.linked.to.instance.fdr", ProblemScope.DOSSIER),
        MODELE_FDR_ATTACHED_TO_POSTE("organigramme.error.delete.poste.linked.to.modele.fdr", ProblemScope.MODELE_FDR),
        SQUELETTE_ATTACHED_TO_POSTE("organigramme.error.delete.poste.linked.to.squelette", ProblemScope.SQUELETTE),
        BULLETIN_ATTACHED_TO_MINISTERE(
            "organigramme.error.delete.ministere.rattache.bulletin",
            ProblemScope.BULLETIN_OFFICIEL
        ),
        MOTSCLES_ATTACHED_TO_MINISTERE(
            "organigramme.error.delete.ministere.rattache.indexation.motscles",
            ProblemScope.DOSSIER
        ),
        MODELE_FDR_ATTACHED_TO_MINISTERE(
            "organigramme.error.delete.ministere.rattache.fdr.modele",
            ProblemScope.MODELE_FDR
        ),
        DOSSIER_ATTACHED_TO_MINISTERE("organigramme.error.delete.ministere.rattache.dossier", ProblemScope.DOSSIER),
        DOSSIER_ATTACHED_TO_DIRECTION("organigramme.error.delete.direction.rattache.dossier", ProblemScope.DOSSIER);

        /** Property associée à ce type de problème */
        private String propertyKey;

        /** Type d'objet concerné par ce type de problème */
        private ProblemScope scope;

        ProblemType(String propertyKey, ProblemScope scope) {
            this.propertyKey = propertyKey;
            this.scope = scope;
        }

        public String getPropertyKey() {
            return propertyKey;
        }

        public ProblemScope getScope() {
            return scope;
        }
    }

    public String getBlockedObjIdentification() {
        return blockedObjIdentification;
    }

    public void setBlockedObjIdentification(String blockedObjIdentification) {
        this.blockedObjIdentification = blockedObjIdentification;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((blockedObjIdentification == null) ? 0 : blockedObjIdentification.hashCode());
        result = prime * result + ((blockingDossierInfo == null) ? 0 : blockingDossierInfo.hashCode());
        result = prime * result + ((blockingObjIdentification == null) ? 0 : blockingObjIdentification.hashCode());
        result = prime * result + ((blockingObjType == null) ? 0 : blockingObjType.hashCode());
        result = prime * result + ((posteInfo == null) ? 0 : posteInfo.hashCode());
        result = prime * result + ((problemType == null) ? 0 : problemType.hashCode());
        return result;
    }

    private boolean equalsBlockedObjIdentification(OrganigrammeNodeDeletionProblem other) {
        if (blockedObjIdentification == null) {
            if (other.blockedObjIdentification != null) {
                return false;
            }
        } else if (!blockedObjIdentification.equals(other.blockedObjIdentification)) {
            return false;
        }

        return true;
    }

    private boolean equalsBlockingDossierInfo(OrganigrammeNodeDeletionProblem other) {
        if (blockingDossierInfo == null) {
            if (other.blockingDossierInfo != null) {
                return false;
            }
        } else if (!blockingDossierInfo.equals(other.blockingDossierInfo)) {
            return false;
        }

        return true;
    }

    private boolean equalsBlockingObjIdentification(OrganigrammeNodeDeletionProblem other) {
        if (blockingObjIdentification == null) {
            if (other.blockingObjIdentification != null) {
                return false;
            }
        } else if (!blockingObjIdentification.equals(other.blockingObjIdentification)) {
            return false;
        }

        return true;
    }

    private boolean equalsPosteInfo(OrganigrammeNodeDeletionProblem other) {
        if (posteInfo == null) {
            if (other.posteInfo != null) {
                return false;
            }
        } else if (!posteInfo.equals(other.posteInfo)) {
            return false;
        }

        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        OrganigrammeNodeDeletionProblem other = (OrganigrammeNodeDeletionProblem) obj;

        return (
            equalsBlockedObjIdentification(other) &&
            equalsBlockingDossierInfo(other) &&
            equalsBlockingObjIdentification(other) &&
            (blockingObjType == other.blockingObjType) &&
            equalsPosteInfo(other) &&
            (problemType == other.problemType)
        );
    }
}
