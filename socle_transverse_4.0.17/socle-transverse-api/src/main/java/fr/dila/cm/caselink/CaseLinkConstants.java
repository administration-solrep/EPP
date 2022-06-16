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
 *     nulrich
 */
package fr.dila.cm.caselink;

/**
 * @author <a href="mailto:arussel@nuxeo.com">Alexandre Russel</a>
 *
 */
public class CaseLinkConstants {
    /**
     * The he document type.
     */
    public static final String CASE_LINK_FACET = "CaseLink";

    public static final String CASE_LINK_SCHEMA = "case_link";

    /**
     * The xpath of the envelope document id.
     */
    public static final String CASE_DOCUMENT_ID_FIELD = "cslk:caseDocumentId";

    /**
     * The xpath of the subject.
     */
    public static final String SUBJECT_FIELD = "dc:title";

    /**
     * The xpath the sender mailbox id.
     */
    public static final String SENDER_MAILBOX_ID_FIELD = "cslk:senderMailboxId";

    /**
     * The xpath of the sender.
     */
    public static final String SENDER_FIELD = "cslk:sender";

    /**
     * The xpath the date.
     */
    public static final String DATE_FIELD = "cslk:date";

    /**
     * The xpath the comment.
     */
    public static final String COMMENT_FIELD = "cslk:comment";

    /**
     * The xpath of the sending date.
     */
    public static final String SENT_DATE_FIELD = "cslkt:sentDate";

    /**
     * The xpath of the isRead indicator.
     */
    public static final String IS_READ_FIELD = "cslk:isRead";

    // actionable fields

    public static final String IS_ACTIONABLE_FIELD = "cslk:isActionable";

    public static final String DATE_FIELD_DEBUT_VALIDATION = "cslk:dateFieldDebutValidation";

    public static final String STEP_DOCUMENT_ID_FIELD = "acslk:stepDocumentId";
}
