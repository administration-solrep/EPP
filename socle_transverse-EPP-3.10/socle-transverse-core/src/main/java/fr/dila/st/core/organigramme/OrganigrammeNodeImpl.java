package fr.dila.st.core.organigramme;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mortbay.log.Log;
import org.nuxeo.ecm.core.api.CoreSession;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.security.principal.STPrincipal;
import fr.dila.st.core.util.StringUtil;

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
	private static final long	serialVersionUID	= 7169384985444229951L;

	@Id
	@Column(name = "ID_ORGANIGRAMME", nullable = false)
	private String				idOrganigramme;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATE_DEBUT")
	private Calendar			dateDebut;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATE_FIN")
	private Calendar			dateFin;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATE_VERROU")
	private Calendar			dateLock;

	@Column(name = "LABEL")
	private String				label;

	@Column(name = "DELETED")
	private Boolean				deleted;

	@Column(name = "UTILISATEUR_VERROU")
	private String				lockUser;

	@Column(name = "FUNCTION_READ")
	private String				functionRead;

	public OrganigrammeNodeImpl() {
		deleted = false;
	}

	public OrganigrammeNodeImpl(OrganigrammeNode copy) {
		this.dateDebut = Calendar.getInstance();

		if (copy.getDateFin() != null) {
			this.dateFin = Calendar.getInstance();
			dateFin.setTime(copy.getDateFin());
		}

		if (copy.getLockDate() != null) {
			this.dateLock = Calendar.getInstance();
			dateLock.setTime(copy.getLockDate());
		}

		this.deleted = copy.getDeleted();
		this.label = copy.getLabel();
		this.lockUser = copy.getLockUserName();
	}

	@Override
	public boolean isReadGranted(CoreSession coreSession) {

		if (coreSession == null) {
			return true;
		}
		STPrincipal principal = null;
		try {
			principal = (STPrincipal) coreSession.getPrincipal();
		} catch (Exception e) {
			Log.debug(e);
		}

		List<String> functionReadList = getFunctionRead();
		if (functionReadList == null || functionReadList.isEmpty()) {
			return true;
		}
		for (String functionRead : functionReadList) {
			if (principal != null && principal.isMemberOf(functionRead)) {
				return true;
			}
		}

		return false;
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
		if (dateFin != null) {
			this.dateFin = Calendar.getInstance();
			this.dateFin.setTime(dateFin);
		}

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
		return deleted == null ? false : deleted;
	}

	@Override
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	@Override
	public void setLockDate(Date lockDate) {
		if (this.dateLock == null) {
			this.dateLock = Calendar.getInstance();
		}

		this.dateLock.setTime(lockDate);
	}

	@Override
	public void setLockDate(Calendar lockDate) {

		this.dateLock = lockDate;
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
		Date dateFin = this.getDateFin();
		return (this.getDateDebut().compareTo(now) < 0) /* test date debut */
				&& (dateFin == null || dateFin.compareTo(now) > 0) /* teste date fin */;
	}

	@Override
	public boolean isNext() {
		Date now = new Date();
		Date dateFin = this.getDateFin();
		return this.getDateDebut().compareTo(now) > 0 && (dateFin == null || dateFin.compareTo(now) > 0);
	}

	@Override
	public String getId() {
		return idOrganigramme;
	}

	@Override
	public void setId(String id) {
		this.idOrganigramme = id;
	}

	@Override
	public List<String> getFunctionRead() {
		if (StringUtil.isNotBlank(functionRead)) {
			return Lists.newArrayList(functionRead.split(";"));
		} else {

			return new ArrayList<String>();
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
		List<String> lstIds = new ArrayList<String>();

		for (OrganigrammeNode orga : lstOrga) {
			lstIds.add(orga.getId().toString());
		}

		return lstIds;
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

}
