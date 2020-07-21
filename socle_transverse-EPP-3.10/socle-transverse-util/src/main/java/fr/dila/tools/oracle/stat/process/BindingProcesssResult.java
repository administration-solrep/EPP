package fr.dila.tools.oracle.stat.process;

import java.sql.ResultSet;
import java.sql.SQLException;

import fr.dila.tools.oracle.connection.ProcessResult;
import fr.dila.tools.oracle.stat.elements.BindingSet;

public class BindingProcesssResult implements ProcessResult {
	private BindingSet	bindings;

	public BindingSet getBindings() {
		return bindings;
	}

	@Override
	public void process(ResultSet rset) throws SQLException {
		bindings = new BindingSet();
		while (rset.next()) {
			String child_address = rset.getString(1);
			String name = rset.getString(2);
			String value = rset.getString(3);
			bindings.getBinding(child_address).put(name, value);
		}

	}

}
