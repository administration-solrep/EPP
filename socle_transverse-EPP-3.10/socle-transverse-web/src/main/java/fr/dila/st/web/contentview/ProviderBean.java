package fr.dila.st.web.contentview;

import static org.jboss.seam.annotations.Install.FRAMEWORK;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.platform.contentview.seam.ContentViewActions;
import org.nuxeo.ecm.platform.query.api.PageProvider;

@Name("providerBean")
@Scope(ScopeType.CONVERSATION)
@Install(precedence = FRAMEWORK)
public class ProviderBean implements Serializable {

	private static final long		serialVersionUID	= -3176979187354080917L;

	@In(create = true)
	protected ContentViewActions	contentViewActions;

	public Boolean checkInstanceOfHiddenColumnPageProvider(final PageProvider<?> provider) {
		return provider instanceof HiddenColumnPageProvider;
	}

	public Boolean checkInstanceOfAbstractDTOPageProvider(final PageProvider<?> provider) {
		return provider instanceof AbstractDTOPageProvider;
	}

}
