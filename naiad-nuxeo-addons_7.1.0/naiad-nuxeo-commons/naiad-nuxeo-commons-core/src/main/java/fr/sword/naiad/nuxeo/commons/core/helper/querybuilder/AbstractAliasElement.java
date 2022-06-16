package fr.sword.naiad.nuxeo.commons.core.helper.querybuilder;

public abstract class AbstractAliasElement implements AliasElement {

	private final String alias;
	
	public AbstractAliasElement(String alias){
		this.alias = alias;
	}
	
	@Override
	public String getAlias(){
		return alias;
	}
	
}
