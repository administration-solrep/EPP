/*
 * (C) Copyright 2006-2009 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more detail.
 *
 * Contributors:
 *     Anahide Tchertchian
 *
 * $Id$
 */

package fr.dila.cm.cases;

/**
 * @author Anahide Tchertchian
 *
 */
public class CaseConstants {
    public static final String CASE_TREE_TYPE = "Folder";

    public static final String CASE_ROOT_DOCUMENT_PATH = "/case-management/case-root";

    public static final String CASE_SCHEMA = "case";

    public static final String TITLE_PROPERTY_NAME = "dc:title";

    public static final String INITIAL_ACTION_INTERNAL_PARTICIPANTS_PROPERTY_NAME =
        "initial_action_internal_participant_mailboxes";
    public static final String INITIAL_ACTION_INTERNAL_PARTICIPANTS_PROPERTY_XPATH =
        "cmdist:" + INITIAL_ACTION_INTERNAL_PARTICIPANTS_PROPERTY_NAME;

    public static final String INITIAL_COPY_INTERNAL_PARTICIPANTS_PROPERTY_NAME =
        "cmdist:initial_copy_internal_participant_mailboxes";

    public static final String INITIAL_ACTION_EXTERNAL_PARTICIPANTS_PROPERTY_NAME =
        "cmdist:initial_action_external_participant_mailboxes";

    public static final String INITIAL_COPY_EXTERNAL_PARTICIPANTS_PROPERTY_NAME =
        "cmdist:initial_copy_external_participant_mailboxes";

    public static final String ALL_ACTION_PARTICIPANTS_PROPERTY_NAME = "all_action_participant_mailboxes";
    public static final String ALL_ACTION_PARTICIPANTS_PROPERTY_XPATH =
        "cmdist:" + ALL_ACTION_PARTICIPANTS_PROPERTY_NAME;

    public static final String ALL_COPY_PARTICIPANTS_PROPERTY_NAME = "cmdist:all_copy_participant_mailboxes";

    public static final String MAILBOX_DOCUMENTS_ID_TYPE = "documentsId";

    public static final String MAILBOX_DOCUMENTS_ID_PROPERTY_NAME = "case:documentsId";

    // operation
    public static final String OPERATION_CASE_LINKS_KEY = "operation.case.links.key";

    public static final String OPERATION_CASE_LINK_KEY = "operation.case.link.key";

    public static final String CASE_MANAGEMENT_OPERATION_CATEGORY = "CaseManagement";

    // Constant utility class
    private CaseConstants() {}
}
