/*
 * (C) Copyright 2010 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 */
package fr.dila.ecm.platform.routing.test;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.IdRef;

import fr.dila.ecm.platform.routing.api.DocumentRouteStep;
import fr.dila.ecm.platform.routing.api.helper.ActionableValidator;

/**
 * A Test Helper class that simulate persistence of Step information. This persistence is transient to the JVM.
 * 
 * @author <a href="mailto:arussel@nuxeo.com">Alexandre Russel</a>
 * 
 */
public class WaitingStepRuntimePersister {
	protected static final List<String>	runningSteps	= new ArrayList<String>();

	protected static final List<String>	doneSteps		= new ArrayList<String>();

	static public void addStepId(String id) {
		if (runningSteps.contains(id)) {
			throw new RuntimeException("Asking twice to wait on the same step.");
		}
		runningSteps.add(id);
	}

	static public List<String> getRunningStepIds() {
		return runningSteps;
	}

	static public DocumentRouteStep getStep(String id, CoreSession session) throws ClientException {
		return session.getDocument(new IdRef(id)).getAdapter(DocumentRouteStep.class);
	}

	static public List<String> getDoneStepIds() {
		return doneSteps;
	}

	static public void resumeStep(final String id, CoreSession session) {
		if (!runningSteps.contains(id)) {
			throw new RuntimeException("Asking to resume a non peristed step.");
		}
		ActionableValidator validator = new ActionableValidator(new SimpleActionableObject(id), session);
		validator.validate();
		runningSteps.remove(id);
		doneSteps.add(id);
	}

	/**
	 * @param stepId
	 */
	public static void removeStepId(String stepId) {
		runningSteps.remove(stepId);
		doneSteps.remove(stepId);
	}

	public static void resetAll() {
		runningSteps.clear();
		doneSteps.clear();
	}
}
