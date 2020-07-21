package fr.dila.st.web.theme.fragments;

import org.nuxeo.theme.fragments.AbstractFragment;
import org.nuxeo.theme.models.Model;
import org.nuxeo.theme.models.ModelException;

import fr.dila.st.web.theme.models.DilaItem;

public class DilaFragment extends AbstractFragment {

	@Override
	public Model getModel() throws ModelException {
		return new DilaItem();
	}

}
