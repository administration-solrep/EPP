package dila.spellchecker;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.exceptions.WebResourceNotFoundException;
import org.nuxeo.ecm.webengine.model.exceptions.WebSecurityException;
import org.nuxeo.ecm.webengine.model.impl.ModuleRoot;

/**
 *
 */
@Path("spellchecker")
@WebObject(type = "SpellCheckerWebEngine")
@Produces("text/html;charset=UTF-8")
public class SpellCheckerWebEngine extends ModuleRoot {

	@Path("jmyspell")
	public Object getJmySpellChecker() {
		return newObject("JMySpellChecker");
	}

	// handle errors
	public Object handleError(WebApplicationException e) {
		if (e instanceof WebSecurityException) {
			return Response.status(401).entity(getTemplate("error/error_401.ftl")).type("text/html").build();
		} else if (e instanceof WebResourceNotFoundException) {
			return Response.status(404).entity(getTemplate("error/error_404.ftl")).type("text/html").build();
		} else {
			return super.handleError(e);
		}
	}

}
