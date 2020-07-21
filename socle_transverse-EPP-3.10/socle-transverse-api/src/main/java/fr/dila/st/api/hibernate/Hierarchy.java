package fr.dila.st.api.hibernate;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "HIERARCHY")
public class Hierarchy implements Serializable {

	/**
	 * Serial version UID
	 */
	private static final long	serialVersionUID	= 6280617405143962852L;

	@Id
	@Column(name = "ID", nullable = false, updatable = false, insertable = false)
	private String				id;

	@Column(name = "PARENTID")
	private String				parentId;

	@Column(name = "POS")
	private Integer				pos;

	@Column(name = "NAME")
	private String				name;

	@Column(name = "ISPROPERTY")
	private Boolean				isProperty;

	@Column(name = "PRIMARYTYPE")
	private String				primaryType;

	@Column(name = "MIXINTYPES")
	private String				mixinTypes;

	@Column(name = "ISCHECKEDIN")
	private Boolean				isCheckedIn;

	@Column(name = "BASEVERSIONID")
	private String				baseVersionId;

	@Column(name = "MAJORVERSION")
	private Integer				majorVersion;

	@Column(name = "MINORVERSION")
	private Integer				minorVersion;

	@Column(name = "ISVERSION")
	private Boolean				isVersion;

	public Hierarchy() {
		super();
	}

	/**
	 * @param id
	 * @param parentId
	 * @param pos
	 * @param name
	 * @param isProperty
	 * @param primaryType
	 * @param mixinTypes
	 * @param isCheckedIn
	 * @param baseVersionId
	 * @param majorVersion
	 * @param minorVersion
	 * @param isVersion
	 */
	public Hierarchy(String id, String parentId, Integer pos, String name, Boolean isProperty, String primaryType,
			String mixinTypes, Boolean isCheckedIn, String baseVersionId, Integer majorVersion, Integer minorVersion,
			Boolean isVersion) {
		super();
		this.id = id;
		this.parentId = parentId;
		this.pos = pos;
		this.name = name;
		this.isProperty = isProperty;
		this.primaryType = primaryType;
		this.mixinTypes = mixinTypes;
		this.isCheckedIn = isCheckedIn;
		this.baseVersionId = baseVersionId;
		this.majorVersion = majorVersion;
		this.minorVersion = minorVersion;
		this.isVersion = isVersion;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public Integer getPos() {
		return pos;
	}

	public void setPos(Integer pos) {
		this.pos = pos;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getIsProperty() {
		return isProperty;
	}

	public void setIsProperty(Boolean isProperty) {
		this.isProperty = isProperty;
	}

	public String getPrimaryType() {
		return primaryType;
	}

	public void setPrimaryType(String primaryType) {
		this.primaryType = primaryType;
	}

	public String getMixinTypes() {
		return mixinTypes;
	}

	public void setMixinTypes(String mixinTypes) {
		this.mixinTypes = mixinTypes;
	}

	public Boolean getIsCheckedIn() {
		return isCheckedIn;
	}

	public void setIsCheckedIn(Boolean isCheckedIn) {
		this.isCheckedIn = isCheckedIn;
	}

	public String getBaseVersionId() {
		return baseVersionId;
	}

	public void setBaseVersionId(String baseVersionId) {
		this.baseVersionId = baseVersionId;
	}

	public Integer getMajorVersion() {
		return majorVersion;
	}

	public void setMajorVersion(Integer majorVersion) {
		this.majorVersion = majorVersion;
	}

	public Integer getMinorVersion() {
		return minorVersion;
	}

	public void setMinorVersion(Integer minorVersion) {
		this.minorVersion = minorVersion;
	}

	public Boolean getIsVersion() {
		return isVersion;
	}

	public void setIsVersion(Boolean isVersion) {
		this.isVersion = isVersion;
	}

	public String getId() {
		return id;
	}
}
