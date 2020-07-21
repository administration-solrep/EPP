package fr.dila.st.core.descriptor;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

import fr.dila.st.core.requeteur.RequeteurContributionBuilderImpl;

@XObject("contributionbuilder")
public class ContributionBuilderDescriptor {

	@XNode("@class")
	private Class<RequeteurContributionBuilderImpl>	klass;

	@XNode("@name")
	private String									name;

	@XNode("@componentName")
	private String									componentName;

	@XNode("@layoutName")
	private String									layoutName;

	@XNode("@templateName")
	private String									templateName;

	@XNode("@showCategories")
	private Boolean									showcategories;

	/**
	 * Default constructor
	 */
	public ContributionBuilderDescriptor() {
		// do nothing
	}

	public Class<RequeteurContributionBuilderImpl> getKlass() {
		return klass;
	}

	public String getName() {
		return name;
	}

	public String getComponentName() {
		return componentName;
	}

	public String getLayoutName() {
		return layoutName;
	}

	public Boolean getShowcategories() {
		return showcategories;
	}

	public String getTemplateName() {
		return templateName;
	}

}
