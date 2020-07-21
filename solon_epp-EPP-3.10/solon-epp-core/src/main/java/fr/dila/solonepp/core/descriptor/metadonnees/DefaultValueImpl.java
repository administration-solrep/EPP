package fr.dila.solonepp.core.descriptor.metadonnees;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

import fr.dila.solonepp.api.descriptor.metadonnees.DefaultValue;
import fr.dila.solonepp.api.descriptor.metadonnees.PropertyDescriptor;

/**
 * descriptor des valeurs par defaut d'une {@link PropertyDescriptor}
 * 
 * @author asatre
 * 
 */
@XObject("defaultValue")
public class DefaultValueImpl implements DefaultValue {

	/**
	 * Type de la valeur par defaut (vocabulary, object, date...)
	 */
	@XNode("@type")
	private String type;

	/**
	 * d'ou vient la valeur (dépends du type)
	 * si type = objet, schema 
	 * 
	 * @default true
	 */
	@XNode("@source")
	private String source;

	/**
	 * ou va la valeur (dépends du type)
	 * si type = objet, schema
	 * 
	 * @default true
	 */
	@XNode("@destination")
	private String destination;
	
	/**
     * valeur conditionnelle, sous la forme AN:SENAT,SENAT:AN
     * si type = conditionnel
     */
    @XNode("@conditionnelValue")
    private String conditionnelValue;

	/**
	 * valeur (depends de source et type)
	 */
	@XNode("@value")
	private String value;

	public DefaultValueImpl() {
		type = null;
		source = null;
		value = null;
	}

	@Override
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public void setSource(String source) {
		this.source = source;
	}

	@Override
	public String getSource() {
		return source;
	}

	@Override
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String getValue() {
		return value;
	}
	
	@Override
	public String getConditionnelValue() {
        return conditionnelValue;
    }

	@Override
    public void setConditionnelValue(String conditionnelValue) {
        this.conditionnelValue = conditionnelValue;
    }

    @Override
	public String getDestination() {
		if (StringUtils.isEmpty(destination)) {
			// si pas de destination on prends la source
			return source;
		} else {
			return destination;
		}
	}

	@Override
	public void setDestination(String destination) {
		this.destination = destination;
	}
}
