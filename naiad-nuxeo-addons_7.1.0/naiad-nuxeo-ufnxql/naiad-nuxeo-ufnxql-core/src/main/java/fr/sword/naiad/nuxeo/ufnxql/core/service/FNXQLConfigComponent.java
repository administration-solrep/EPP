package fr.sword.naiad.nuxeo.ufnxql.core.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

import fr.sword.naiad.nuxeo.ufnxql.core.descriptor.FunctionDescriptor;
import fr.sword.naiad.nuxeo.ufnxql.core.descriptor.MixinTypeDescriptor;
import fr.sword.naiad.nuxeo.ufnxql.core.descriptor.TypeSchemaDescriptor;


public class FNXQLConfigComponent extends DefaultComponent {

	private static final Log LOG = LogFactory.getLog(FNXQLConfigComponent.class); 
	
	// ------------- POINT D'EXTENSION ----------
	private static final String GLOBAL_MIXINTYPES_EP = "globalMixinTypes";
	private static final String SCHEMA_TYPES_EP = "schemaTypes";
	private static final String FUNCTIONS_EP = "functions";
	
	private FNXQLConfigServiceImpl configService;
	
	public FNXQLConfigComponent(){
		super();		
	}
	
	@Override
	public <T> T getAdapter(Class<T> clazz){
		if(clazz.isAssignableFrom(FNXQLConfigService.class)){
			return clazz.cast(configService);
		}		
		return null;
	}
	
	@Override
    public void activate(ComponentContext context) {
		configService = new FNXQLConfigServiceImpl();          
    }

    @Override
    public void deactivate(ComponentContext context) {
    	configService = null;
    }
	
	@Override
	public void registerContribution(Object contribution,
            String extensionPoint, ComponentInstance contributor) {
		
		if(LOG.isDebugEnabled()){
			LOG.debug("register contribution on ["+extensionPoint+"] ("+contribution+")");
		}
		
		if(GLOBAL_MIXINTYPES_EP.equals(extensionPoint)){
			registerMixinTypes((MixinTypeDescriptor) contribution);
		} else if(SCHEMA_TYPES_EP.equals(extensionPoint)){
			registerTypeSchema((TypeSchemaDescriptor) contribution);
		} else if(FUNCTIONS_EP.equals(extensionPoint)){
			registerFunction((FunctionDescriptor) contribution);
		} else {
			LOG.error("Unknown extension point ["+extensionPoint+"]");
		}
	}
	
	protected void registerMixinTypes(MixinTypeDescriptor descr){
		configService.registerMixinTypeNotPerInstance(descr.getName());
	}
	
	protected void registerTypeSchema(TypeSchemaDescriptor descr){
		configService.registerSchemaForType(descr.getType(), descr.getSchema());
	}
	
	protected void registerFunction(FunctionDescriptor descr){
		if(descr.isEnabled()){
			configService.registerFunction(descr.getClazz());
		} else {
			configService.unregisterFunction(descr.getClazz());
		}
	}
	
}
