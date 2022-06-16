package fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.parser;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.ecm.core.query.sql.model.SQLQuery;

/**
 * List d'element de la partie FROM de la requete
 * 
 * @author SPL
 *
 */
public class SFromList  {

	public static class AliasElementList {
		private final String alias;
		public AliasElementList(String alias){
			this.alias = alias;
		}
		public String getAlias() {
			return alias;
		}
	}
	public static class AliasKeyList extends AliasElementList {
		
		private final SFromKeyList keylist;
		
		public AliasKeyList(String alias, SFromKeyList keylist){
			super(alias);
			this.keylist = keylist;
		}

		public SFromKeyList getKeylist() {
			return keylist;
		}
		
		
	}
	
	public static class AliasQueryList extends AliasElementList {
		
		private final SQLQuery query;
		
		public AliasQueryList(String alias, SQLQuery query){
			super(alias);
			this.query = query;
		}

		public SQLQuery getQuery() {
			return query;
		}
		
		
	}
	
	private final List<AliasElementList> fromList;
	
	public SFromList(){
		fromList = new ArrayList<AliasElementList>();
	}
	
	public void add(SFromKeyList keylist){
		fromList.add(new AliasKeyList(null, keylist));
	}
	
	public void add(String alias, SFromKeyList keylist){
		fromList.add(new AliasKeyList(alias, keylist));
	}
	
	public void add(String alias, SQLQuery query){
		fromList.add(new AliasQueryList(alias, query));
	}

	public List<AliasElementList> getFromList() {
		return fromList;
	}
	
	
}
