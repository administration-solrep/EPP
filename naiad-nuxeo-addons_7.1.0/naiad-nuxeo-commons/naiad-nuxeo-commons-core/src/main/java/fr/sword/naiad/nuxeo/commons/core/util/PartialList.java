package fr.sword.naiad.nuxeo.commons.core.util;

import java.util.List;

/**
 * Conteneur d'une liste contenant une sous-partie d'un ensemble de donnée
 * et du nombre total d'element
 * 
 * @author SPL
 *
 * @param <T>
 */
public class PartialList<T> {
	
	private final List<T> data;
	private final int total;
	
	public PartialList(List<T> data, int total){
		this.data = data;
		this.total = total;
	}
	
	public List<T> getData(){
		return data;
	}
	
	public int getTotal(){
		return total;
	}
	
}
