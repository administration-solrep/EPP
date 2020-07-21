/*
 * (C) Copyright 2009 Nuxeo SA (http://nuxeo.com/) and contributors.
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
 *     arussel
 *     Nicolas Ulrich
 */
package fr.dila.cm.caselink;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.cm.cases.Case;
import fr.dila.cm.cases.HasParticipants;
import fr.dila.cm.mailbox.Mailbox;

/**
 * A CorrespondencePost represent a sent {@link Case} in a {@link Mailbox}.
 * <p>
 * When a Mailbox send a MailEnvelope, a CorrespondencePost is created in its Mailbox and a
 * {@link CorrespondenceMessage} is created in each of the recipients Mailbox.
 * 
 * @author <a href="mailto:arussel@nuxeo.com">Alexandre Russel</a>
 */
public interface CaseLink extends HasParticipants, Serializable {

	enum CaseLinkState {
		draft, project, todo, done
	}

	enum CaseLinkTransistion {
		toProject, toDone
	}

	/**
	 * Gets the document model.
	 */
	DocumentModel getDocument();

	/**
	 * Gets the id of the post.
	 */
	String getId();

	/**
	 * Gets the subject.
	 */
	String getSubject();

	/**
	 * Gets the comment.
	 */
	String getComment();

	/**
	 * Gets the date.
	 */
	Calendar getDate();

	/**
	 * Gets the sender.
	 */
	String getSender();

	/**
	 * Gets the sender mailbox id.
	 */
	String getSenderMailboxId();

	/**
	 * The mail envelope sent.
	 */
	Case getCase(CoreSession session);

	/**
	 * Gets the send date of the post.
	 */
	Date getSentDate();

	/**
	 * Gets the type of the message.
	 */
	String getType();

	/**
	 * Returns true if this message has been read.
	 */
	boolean isRead();

	/**
	 * Persists the post.
	 */
	void save(CoreSession session);

	/**
	 * Is it a draft?
	 */
	boolean isDraft();

	void setActionnable(boolean actionnable);

	boolean isActionnable();

	Calendar getDateDebutValidation();

	void setDateDebutValidation(Calendar dateDebutValidation);

}
