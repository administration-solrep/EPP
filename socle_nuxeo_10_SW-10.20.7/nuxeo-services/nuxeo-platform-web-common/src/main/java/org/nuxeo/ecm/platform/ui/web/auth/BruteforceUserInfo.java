package org.nuxeo.ecm.platform.ui.web.auth;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity(name = BruteforceUserInfo.ENTITY)
@Table(name = BruteforceUserInfo.TABLE_NAME)
public class BruteforceUserInfo {
	public static final String ENTITY = "BruteforceUserInfo";
	public static final String TABLE_NAME = "TENTATIVES_CONNEXION";
	public static final String TABLE_LOGIN = "identifiant";
	public static final String TABLE_COMPTEUR = "compteur";
	public static final String TABLE_DATE = "date_debut_blocage";

	@Id
	@Column(name = TABLE_LOGIN)
	private String username;
	@Column(name = TABLE_COMPTEUR)
	private int attemptCount;

	@Column(name = TABLE_DATE)
	@Temporal(TemporalType.TIMESTAMP)
	private Calendar lastAttemptDate;

	public void setUsername(String username) {
		this.username = username;
	}

	public void setAttemptCount(int attemptCount) {
		this.attemptCount = attemptCount;
	}

	public String getUsername() {
		return username;
	}

	public int getAttemptCount() {
		return attemptCount;
	}

	public void setLastAttemptDate(Calendar lastAttemptDate) {
		this.lastAttemptDate = lastAttemptDate;
	}

	public Calendar getLastAttemptDate() {
		return lastAttemptDate;
	}

	@Transient
	public void newAttempt() {
		attemptCount++;
		lastAttemptDate = Calendar.getInstance();
	}

}
