package fr.sword.naiad.nuxeo.commons.core.helper.querybuilder;

public class SubQueryElement extends AbstractAliasElement {

	private final UFNXQLQueryBuilder queryBuilder;
	
	public SubQueryElement(UFNXQLQueryBuilder queryBuilder, String alias){
		super(alias);
		this.queryBuilder = queryBuilder;
	}
	
	@Override
	public String toString(){
		return "(" + queryBuilder.query() + ") AS " + getAlias();
	}
	
	@Override
	public Object[] getParams(){
		return queryBuilder.getParams();
	}
	
}
