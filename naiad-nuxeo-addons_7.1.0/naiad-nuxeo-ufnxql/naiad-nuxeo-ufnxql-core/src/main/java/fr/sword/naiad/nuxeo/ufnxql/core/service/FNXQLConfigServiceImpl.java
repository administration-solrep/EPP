package fr.sword.naiad.nuxeo.ufnxql.core.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.functions.UfnxqlFunction;

public class FNXQLConfigServiceImpl implements FNXQLConfigService {
	
	private static final Log LOG = LogFactory.getLog(FNXQLConfigServiceImpl.class);
	
	private final Map<String, String> mapTypeSchema = new HashMap<String, String>();
	
	private final Set<String> mixinTypeNotPerInstance = new HashSet<String>();
	
	private final List<UfnxqlFunction> functions = new ArrayList<UfnxqlFunction>();
	
	/**
	 * Default constructor
	 */
	public FNXQLConfigServiceImpl(){
		super();
	}
	
	@Override
	public Map<String, String> getMappingTypeSchema() {
		return mapTypeSchema;
	}
	
	@Override
	public String getSchemaForType(String type){
		return mapTypeSchema.get(type);
	}
	
	@Override
	public Set<String> getMixinTypeGlobalToTypes() {
		return mixinTypeNotPerInstance;
	}
	
	@Override
	public boolean isMixinTypeGlobalToTypes(String mixinType){
		return mixinTypeNotPerInstance.contains(mixinType);
	}
	
	@Override 
	public Set<String> extractMixinTypePerInstance(Set<String> mixinTypes) {
		Set<String> result = new HashSet<String>();
		for(String mixintype : mixinTypes){
			if(!isMixinTypeGlobalToTypes(mixintype)){
				result.add(mixintype);
			}
		}
		return result;
	}

	@Override
	public List<UfnxqlFunction> getUFNXQLFunctions() {				
		return functions;
	}
	
	public void registerMixinTypeNotPerInstance(String mixintype){
		if(StringUtils.isNotBlank(mixintype)){
			mixinTypeNotPerInstance.add(mixintype);
		}
	}
	
	public void unregisterFunction(Class<? extends UfnxqlFunction> functionClazz){
		final List<UfnxqlFunction> toRemove = new ArrayList<UfnxqlFunction>(); 
		for(UfnxqlFunction func : functions){
			if(functionClazz.equals(func.getClass())){
				toRemove.add(func);
			}
		}
		functions.removeAll(toRemove);
	}
	
	public void registerFunction(Class<? extends UfnxqlFunction> function){
		if(function == null){
			LOG.error("Failed to register function : null class");
			return;
		}
		try {
			functions.add(function.newInstance());
		} catch(IllegalAccessException e){
			LOG.error(e, e);
		} catch(InstantiationException e){
			LOG.error(e, e);
		}
	}
	
	public void registerSchemaForType(String type, String schema){
		if(StringUtils.isNotBlank(type)){
			if(StringUtils.isBlank(schema)){
				mapTypeSchema.remove(type);
			} else {
				mapTypeSchema.put(type, schema);
			}
		}
	}
	

	
}
