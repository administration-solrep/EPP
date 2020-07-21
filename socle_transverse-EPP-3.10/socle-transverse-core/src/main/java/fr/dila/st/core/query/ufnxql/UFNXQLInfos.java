package fr.dila.st.core.query.ufnxql;

/**
 * Une classe utilisée pour passer des informations du maker au builder.
 * 
 * @author jgomez
 * 
 */
public class UFNXQLInfos {

	private Boolean	readAclOptimisation;

	public UFNXQLInfos() {
		this.readAclOptimisation = true;
	}

	public Boolean getReadAclOptimisation() {
		return readAclOptimisation;
	}

	public void setReadAclOptimisation(Boolean readAclOptimisation) {
		this.readAclOptimisation = readAclOptimisation;
	}

}
