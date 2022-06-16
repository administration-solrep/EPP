/*
 * (C) Copyright 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Florent Guillaume
 */

package org.nuxeo.ecm.core.storage.sql.coremodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.api.security.impl.ACLImpl;
import org.nuxeo.ecm.core.api.security.impl.ACPImpl;
import org.nuxeo.ecm.core.storage.sql.ACLRow;

/**
 * @author Florent Guillaume
 */
public class TestSQLSecurityManager {

    @Test
    public void testAclRowsToACP() {
        ACLRow acl1 = new ACLRow(0, "local", true, "Read", "bob", null);
        ACLRow acl2 = new ACLRow(1, "wf", false, "Write", "steve", null);
        ACLRow acl3 = new ACLRow(2, "wf", true, "Zap", "pete", null);
        ACP acp = SQLSession.aclRowsToACP(new ACLRow[] { acl1, acl2, acl3 });

        ACL[] acls = acp.getACLs();
        assertEquals(2, acls.length);

        ACL acl = acls[1];
        assertEquals("local", acl.getName());

        ACE[] aces = acl.getACEs();
        assertEquals(1, aces.length);

        ACE ace = aces[0];
        assertTrue(ace.isGranted());
        assertEquals("Read", ace.getPermission());
        assertEquals("bob", ace.getUsername());

        acl = acls[0];
        assertEquals("wf", acl.getName());

        aces = acl.getACEs();
        assertEquals(2, aces.length);

        ace = aces[0];
        assertFalse(ace.isGranted());
        assertEquals("Write", ace.getPermission());
        assertEquals("steve", ace.getUsername());

        ace = aces[1];
        assertTrue(ace.isGranted());
        assertEquals("Zap", ace.getPermission());
        assertEquals("pete", ace.getUsername());
    }

    @Test
    public void testAcpToAclRows() {
        ACPImpl acp = new ACPImpl();
        ACL acl = new ACLImpl("local");
        acp.addACL(acl);
        ACE ace = new ACE("bob", "Read", true);
        acl.add(ace);
        acl = new ACLImpl("wf");
        acp.addACL(acl);
        ace = new ACE("steve", "Write", false);
        acl.add(ace);
        ace = new ACE(null, "m", true); // null name skipped
        acl.add(ace);
        ace = new ACE("pete", "Zap", true);
        acl.add(ace);
        acl = new ACLImpl(ACL.INHERITED_ACL); // must be skipped
        ace = new ACE("x", "y", true);
        acl.add(ace);
        acp.addACL(acl);
        ACLRow[] aclrows = SQLSession.acpToAclRows(acp);
        assertEquals(3, aclrows.length);

        ACLRow aclrow = aclrows[2];
        assertEquals(2, aclrow.pos);
        assertEquals("local", aclrow.name);
        assertTrue(aclrow.grant);
        assertEquals("Read", aclrow.permission);
        assertEquals("bob", aclrow.user);
        assertNull(aclrow.group);

        aclrow = aclrows[0];
        assertEquals("steve", aclrow.user);

        aclrow = aclrows[1];
        assertEquals("pete", aclrow.user);
    }

    @Test
    public void testUpdateAclRows() {
        // existing:
        ACLRow acl1 = new ACLRow(0, "local", true, "Read", "bob", null);
        ACLRow acl2 = new ACLRow(1, "wf", false, "Write", "steve", null);
        ACLRow acl3 = new ACLRow(2, "wf", true, "Zap", "pete", null);
        // update with:
        ACPImpl acp = new ACPImpl();
        ACL acl = new ACLImpl("local");
        acp.addACL(acl);
        ACE ace = new ACE("bob", "Read", false); // change grant
        acl.add(ace);
        ace = new ACE("me", "All", true); // add other user
        acl.add(ace);
        acl = new ACLImpl("legal"); // add other acl
        acp.addACL(acl);
        ace = new ACE("all", "Write", false);
        acl.add(ace);
        acl = new ACLImpl(ACL.INHERITED_ACL); // must be skipped
        ace = new ACE("x", "y", true);
        acl.add(ace);
        acp.addACL(acl);
        ACLRow[] aclrows = SQLSession.updateAclRows(new ACLRow[] { acl1, acl2, acl3 }, acp);

        assertEquals(5, aclrows.length);

        ACLRow aclrow = aclrows[0];
        assertEquals(0, aclrow.pos);
        assertEquals("local", aclrow.name);
        assertFalse(aclrow.grant);
        assertEquals("Read", aclrow.permission);
        assertEquals("bob", aclrow.user);
        assertNull(aclrow.group);

        aclrow = aclrows[1];
        assertEquals(1, aclrow.pos);
        assertEquals("local", aclrow.name);
        assertTrue(aclrow.grant);
        assertEquals("All", aclrow.permission);
        assertEquals("me", aclrow.user);
        assertNull(aclrow.group);

        aclrow = aclrows[2];
        assertEquals(2, aclrow.pos);
        assertEquals("wf", aclrow.name);
        assertEquals("steve", aclrow.user);

        aclrow = aclrows[3];
        assertEquals(3, aclrow.pos);
        assertEquals("wf", aclrow.name);
        assertEquals("pete", aclrow.user);

        aclrow = aclrows[4];
        assertEquals(4, aclrow.pos);
        assertEquals("legal", aclrow.name);
        assertFalse(aclrow.grant);
        assertEquals("Write", aclrow.permission);
        assertEquals("all", aclrow.user);
        assertNull(aclrow.group);
    }

}
