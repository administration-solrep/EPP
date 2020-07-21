/**
 * 
 */
package dila.spellchecker;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.impl.DefaultObject;

import dila.spellchecker.engine.JMySpellChecker;

/**
 * @author bby
 * 
 */
@WebObject(type = "JMySpellChecker")
public class JMySpellCheckerWebEngine extends DefaultObject {

	@POST
	@Produces("application/json")
	public String spellChecked(String requestJSON, @Context HttpServletResponse response) {
		JMySpellChecker servlet = new JMySpellChecker();
		try {
			return servlet.doPost(requestJSON, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "Not words !!!";
	}

}
