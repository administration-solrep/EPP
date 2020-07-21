package fr.dila.solonepp.api.descriptor.metadonnees;

public interface DefaultValue {

	void setType(String type);

	String getType();

	void setSource(String source);

	String getSource();

	void setValue(String value);

	String getValue();
	
	void setDestination(String destination);

	String getDestination();

	String getConditionnelValue();

	void setConditionnelValue(String conditionnelValue);
}
