package fr.dila.st.core.organigramme;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.core.util.DateUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;

/**
 * Représentation d'un noeud de l'organigramme.
 *
 * @author Fabio Esposito
 */

@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class OrganigrammeNodeImpl implements OrganigrammeNode, Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 7169384985444229951L;

    @Id
    @Column(name = "ID_ORGANIGRAMME", nullable = false)
    private String idOrganigramme;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_DEBUT")
    private Calendar dateDebut;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_FIN")
    private Calendar dateFin;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_VERROU")
    private Calendar dateLock;

    @Column(name = "LABEL")
    private String label;

    @Column(name = "DELETED")
    private Boolean deleted;

    @Column(name = "UTILISATEUR_VERROU")
    private String lockUser;

    @Column(name = "FUNCTION_READ")
    private String functionRead;

    public OrganigrammeNodeImpl() {
        deleted = false;
    }

    public OrganigrammeNodeImpl(OrganigrammeNode copy) {
        dateDebut = Calendar.getInstance();

        if (copy.getDateFin() != null) {
            dateFin = DateUtil.toCalendarFromNotNullDate(copy.getDateFin());
        }

        if (copy.getLockDate() != null) {
            dateLock = DateUtil.toCalendarFromNotNullDate(copy.getLockDate());
        }

        deleted = copy.getDeleted();
        label = copy.getLabel();
        lockUser = copy.getLockUserName();
    }

    @Override
    public boolean isReadGranted(CoreSession coreSession) {
        if (coreSession == null) {
            return true;
        }

        List<String> functionReadList = getFunctionRead();
        if (CollectionUtils.isEmpty(functionReadList)) {
            return true;
        }

        NuxeoPrincipal principal = coreSession.getPrincipal();
        return functionReadList.stream().anyMatch(function -> principal != null && principal.isMemberOf(function));
    }

    @Override
    public Date getDateDebut() {
        if (dateDebut == null) {
            dateDebut = Calendar.getInstance();
        }

        return dateDebut.getTime();
    }

    @Override
    public void setDateDebut(Date dateDebut) {
        if (this.dateDebut == null) {
            this.dateDebut = Calendar.getInstance();
        }

        this.dateDebut.setTime(dateDebut);
    }

    @Override
    public Date getDateFin() {
        return dateFin == null ? null : dateFin.getTime();
    }

    @Override
    public void setDateFin(Date dateFin) {
        this.dateFin = DateUtil.toCalendar(dateFin);
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public boolean getDeleted() {
        return deleted != null && deleted;
    }

    @Override
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public void setLockDate(Date lockDate) {
        if (dateLock == null) {
            dateLock = Calendar.getInstance();
        }

        dateLock.setTime(lockDate);
    }

    @Override
    public void setLockDate(Calendar lockDate) {
        dateLock = lockDate;
    }

    @Override
    public Date getLockDate() {
        return dateLock == null ? null : dateLock.getTime();
    }

    @Override
    public void setLockUserName(String lockUserName) {
        lockUser = lockUserName;
    }

    @Override
    public String getLockUserName() {
        return lockUser;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + idOrganigramme.hashCode();
        return result;
    }

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
        OrganigrammeNodeImpl other = (OrganigrammeNodeImpl) obj;
        if (idOrganigramme == null) {
            if (other.getId() != null) {
                return false;
            }
        } else if (!idOrganigramme.equals(other.getId())) {
            return false;
        }

        return true;
    }

    @Override
    public boolean isActive() {
        Date now = new Date();
        Date dateFin = getDateFin();
        return (
            (getDateDebut().compareTo(now) < 0) && /* test date debut */(dateFin == null || dateFin.compareTo(now) > 0)
        )/* teste date fin */;
    }

    @Override
    public boolean isNext() {
        Date now = new Date();
        Date dateFin = getDateFin();
        return getDateDebut().compareTo(now) > 0 && (dateFin == null || dateFin.compareTo(now) > 0);
    }

    @Override
    public String getId() {
        return idOrganigramme;
    }

    @Override
    public void setId(String id) {
        idOrganigramme = id;
    }

    @Override
    public List<String> getFunctionRead() {
        if (StringUtils.isNotBlank(functionRead)) {
            return Lists.newArrayList(functionRead.split(";"));
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void setFunctionRead(List<String> functionRead) {
        if (functionRead != null) {
            Joiner join = Joiner.on(";").skipNulls();
            this.functionRead = join.join(functionRead);
        }
    }

    protected List<String> extractIdsFromElement(List<? extends OrganigrammeNode> lstOrga) {
        return lstOrga.stream().map(OrganigrammeNode::getId).collect(Collectors.toList());
    }

    @Override
    public void setDateDebut(Calendar dateDebut) {
        this.dateDebut = dateDebut;
    }

    @Override
    public void setDateFin(Calendar dateFin) {
        this.dateFin = dateFin;
    }

    public void setDateLock(Calendar dateLock) {
        this.dateLock = dateLock;
    }

    @Override
    public String getLabelWithNor(String idMinistere) {
        return label;
    }
}
