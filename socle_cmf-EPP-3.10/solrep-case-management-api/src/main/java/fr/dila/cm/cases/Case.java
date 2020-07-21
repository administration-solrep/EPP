/*
 * (C) Copyright 2006-2008 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 *     <a href="mailto:at@nuxeo.com">Anahide Tchertchian</a>
 *
 * $Id: MailEnvelope.java 57494 2008-09-11 17:17:23Z atchertchian $
 */

package fr.dila.cm.cases;

import java.io.Serializable;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Mail envelope
 *
 * @author <a href="mailto:at@nuxeo.com">Anahide Tchertchian</a>
 */
public interface Case extends HasParticipants, Serializable {

    /**
     * Gets the document model describing the envelope.
     */
    DocumentModel getDocument();

    /**
     * Persists the envelope.
     */
    void save(CoreSession session);

    /**
     * Is this a draft envelope?
     */
    boolean isDraft() throws ClientException;

    /**
     * Is this an empty envelope?
     */
    boolean isEmpty() throws ClientException;

}
