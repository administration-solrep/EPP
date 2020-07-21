package fr.dila.ecm.platform.routing.web;

import java.util.List;

import org.nuxeo.ecm.platform.ui.web.model.SelectDataModel;
import org.nuxeo.ecm.platform.ui.web.model.impl.SelectDataModelImpl;

public class DocumentRoutingSelectDataModelImpl extends SelectDataModelImpl implements SelectDataModel {

	private Boolean selected;

	@SuppressWarnings("rawtypes")
	public DocumentRoutingSelectDataModelImpl(String name, List data, List selectedData) {
		super(name, data, selectedData);
	}

	public void setSelected(Boolean selected) {
		this.selected = selected;
		this.selectedData = this.data;
		generateSelectRows();
	}

	public Boolean getSelected() {
		return selected;
	}
}
