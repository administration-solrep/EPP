package fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.parser;

import java.util.ArrayList;
import java.util.List;

public class SFromKeyList {

	public static final String FACET = "facet:";
	public static final String STRICT_TYPE = "strict:";
	
	public enum FromKeyOperator{
		AND, OR, NOT, NONE;
	};
	
	public static class SFromKey{
		private final String name;
		private final FromKeyOperator operator;
		private final boolean facet;
		
		/**
		 *  indique si on ne doit pas inclure les types enfants
		 */
		private final boolean strict;
		
		public SFromKey(String name, FromKeyOperator operator){
			if(name.startsWith(FACET)){
				this.name = name.substring(FACET.length());
				facet = true;
				strict = false;
			} else if(name.startsWith(STRICT_TYPE)){
				this.name = name.substring(STRICT_TYPE.length());
				facet = false;
				strict = true;
			} else {
				this.name = name;
				facet = false;
				strict = false;
			}			
			this.operator = operator;
		}
		
		public SFromKey(String name){
			this(name, FromKeyOperator.NONE);
		}

		public String getName() {
			return name;
		}

		public FromKeyOperator getOperator() {
			return operator;
		}

		public boolean isFacet() {
			return facet;
		}
		
		public boolean isStrict() {
			return strict;
		}
		
		public StringBuilder toString(StringBuilder strBuilder){
			if(strBuilder == null){
				strBuilder = new StringBuilder();
			}
			switch(getOperator()){
			case OR:
				strBuilder.append('|');
				break;
			case AND:
				strBuilder.append('&');
				break;
			case NOT:
				strBuilder.append("!");
				break;
			default:
			// NONE : add nothing
			}
			if(isFacet()){
				strBuilder.append(FACET);
			}
			strBuilder.append(getName());
			return strBuilder;
		}
		
		@Override
		public String toString(){
			return toString(null).toString();
		}
	}
		
	private final List<SFromKey> keylist;
	
	public SFromKeyList(){
		keylist = new ArrayList<SFromKey>();
	}
	
	public void add(String name){
		keylist.add(new SFromKey(name));
	}
	
	public void add(FromKeyOperator op, String name){
		keylist.add(new SFromKey(name, op));
	}

	public List<SFromKey> getKeylist() {
		return keylist;
	}
	
	public int count(){
		return keylist.size();
	}
	
	@Override
	public String toString(){
		StringBuilder strBuilder = new StringBuilder();
		for(SFromKey key : keylist){
			strBuilder = key.toString(strBuilder);
		}
		return strBuilder.toString();
	}
	
	

	
}
