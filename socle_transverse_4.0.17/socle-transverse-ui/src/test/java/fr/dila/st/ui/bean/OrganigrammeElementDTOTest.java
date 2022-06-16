package fr.dila.st.ui.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.UserNode;
import fr.dila.st.core.organigramme.EntiteNodeImpl;
import fr.dila.st.core.organigramme.UserNodeImpl;
import fr.dila.st.core.util.SolonDateConverter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.nuxeo.ecm.core.api.CoreSession;

public class OrganigrammeElementDTOTest {
    private EntiteNode entiteNode;
    private UserNode userNode;
    private CoreSession session;

    @Before
    public void settup() throws ParseException {
        entiteNode = buildMinistere();
        userNode = buildUser();
    }

    @Test
    public void testConstructors() {
        OrganigrammeElementDTO element1 = new OrganigrammeElementDTO(session, entiteNode);
        assertNotNull(element1);
        assertNotNull(element1.getChilds());
        assertEquals(0, element1.getChilds().size());
        assertEquals(entiteNode.getLabel(), element1.getLabel());
        assertEquals(entiteNode.getId(), element1.getKey());
        assertEquals(entiteNode.getId(), element1.getCompleteKey());
        assertEquals(entiteNode, element1.getOrganigrammeNode());
        assertNull(element1.getCurrentUser());
        assertNull(element1.getLstUserNode());
        assertNull(element1.getMinistereId());
        assertNull(element1.getParent());
        assertEquals(entiteNode.getType().getValue(), element1.getType());
        assertEquals(entiteNode.isActive(), element1.getIsActive());
        assertEquals(false, element1.getAllowAdd());

        OrganigrammeElementDTO element2 = new OrganigrammeElementDTO(session, entiteNode, "50000001");
        assertNotNull(element2);
        assertNotNull(element2.getChilds());
        assertEquals(0, element2.getChilds().size());
        assertEquals(entiteNode.getLabel(), element2.getLabel());
        assertEquals(entiteNode.getId(), element2.getKey());
        assertEquals(entiteNode.getId(), element2.getCompleteKey());
        assertEquals(entiteNode, element2.getOrganigrammeNode());
        assertNull(element2.getCurrentUser());
        assertNull(element2.getLstUserNode());
        assertEquals("50000001", element2.getMinistereId());
        assertNull(element2.getParent());
        assertEquals(entiteNode.getType().getValue(), element2.getType());
        assertEquals(entiteNode.isActive(), element2.getIsActive());
        assertEquals(false, element2.getAllowAdd());

        OrganigrammeElementDTO element3 = new OrganigrammeElementDTO(session, entiteNode, "50000001", element1);
        assertNotNull(element3);
        assertNotNull(element3.getChilds());
        assertEquals(0, element3.getChilds().size());
        assertEquals(entiteNode.getLabel(), element3.getLabel());
        assertEquals(entiteNode.getId(), element3.getKey());
        assertEquals(String.format("%s__%s", element1.getCompleteKey(), element3.getKey()), element3.getCompleteKey());
        assertEquals(entiteNode, element3.getOrganigrammeNode());
        assertNull(element3.getCurrentUser());
        assertNull(element3.getLstUserNode());
        assertEquals("50000001", element3.getMinistereId());
        assertEquals(element1, element3.getParent());
        assertEquals(entiteNode.getType().getValue(), element3.getType());
        assertEquals(entiteNode.isActive(), element3.getIsActive());
        assertEquals(false, element3.getAllowAdd());

        OrganigrammeElementDTO element4 = new OrganigrammeElementDTO(userNode);
        assertNotNull(element4);
        assertNotNull(element4.getChilds());
        assertEquals(0, element4.getChilds().size());
        assertEquals(userNode.getLabel(), element4.getLabel());
        assertEquals(userNode.getId(), element4.getKey());
        assertEquals(null, element4.getCompleteKey());
        assertEquals(true, element4.getIsLastLevel());
        assertNull(element4.getOrganigrammeNode());
        assertEquals(userNode, element4.getCurrentUser());
        assertNotNull(element4.getLstUserNode());
        assertEquals(1, element4.getLstUserNode().size());
        assertEquals(userNode, element4.getLstUserNode().get(0));
        assertNull(element4.getMinistereId());
        assertNull(element4.getParent());
        assertEquals(userNode.getType().getValue(), element4.getType());
        assertEquals(userNode.isActive(), element4.getIsActive());
        assertEquals(false, element4.getAllowAdd());
    }

    @Test
    public void testSetter() {
        OrganigrammeElementDTO element = new OrganigrammeElementDTO(userNode);

        element.setOrganigrammeNode(session, null);
        assertEquals(null, element.getOrganigrammeNode());
        assertEquals("", element.getKey());
        assertEquals("", element.getCompleteKey());
        assertEquals("", element.getLabel());
        assertEquals("", element.getType());
        assertEquals(true, element.getIsActive());
        assertEquals(null, element.getLockDate());
        assertEquals("", element.getLockUserName());

        element.setOrganigrammeNode(session, entiteNode);
        assertEquals(entiteNode, element.getOrganigrammeNode());
        assertEquals(entiteNode.getId(), element.getKey());
        assertEquals(entiteNode.getId(), element.getCompleteKey());
        assertEquals(entiteNode.getLabel(), element.getLabel());
        assertEquals(entiteNode.getType().getValue(), element.getType());
        assertEquals(entiteNode.isActive(), element.getIsActive());
        assertEquals(entiteNode.getLockDate(), element.getLockDate());
        assertEquals(entiteNode.getLockUserName(), element.getLockUserName());

        element.setCurrentUser(null);
        assertEquals(null, element.getCurrentUser());
        assertEquals("", element.getKey());
        assertEquals("", element.getLabel());
        assertEquals("", element.getType());

        element.setCurrentUser(userNode);
        assertEquals(userNode, element.getCurrentUser());
        assertEquals(userNode.getId(), element.getKey());
        assertEquals(userNode.getLabel(), element.getLabel());
        assertEquals(userNode.getType().getValue(), element.getType());

        element.setLstUserNode(null);
        assertNull(element.getLstUserNode());

        List<UserNode> liste = new ArrayList<>();
        liste.add(new UserNodeImpl());
        liste.add(new UserNodeImpl());
        element.setLstUserNode(liste);
        assertNotNull(element.getLstUserNode());
        assertEquals(2, element.getLstUserNode().size());

        element.setMinistereId("50000000");
        assertEquals("50000000", element.getMinistereId());

        OrganigrammeElementDTO parentElement = new OrganigrammeElementDTO(session, entiteNode);
        element.setParent(parentElement);
        assertEquals(parentElement, element.getParent());
        assertEquals(
            String.format("%s__%s", parentElement.getCompleteKey(), element.getKey()),
            element.getCompleteKey()
        );

        element.setType("TestType");
        assertEquals("TestType", element.getType());

        element.setAllowAdd(true);
        assertEquals(true, element.getAllowAdd());
    }

    private EntiteNode buildMinistere() throws ParseException {
        EntiteNode entiteNode = new EntiteNodeImpl();
        entiteNode.setId("50000000");
        entiteNode.setLabel("Premier Ministre");
        entiteNode.setDateDebut(SolonDateConverter.DATE_SLASH.parseToDate("01/01/2000"));
        entiteNode.setDateFin(SolonDateConverter.DATE_SLASH.parseToDate("31/12/2999"));

        return entiteNode;
    }

    private UserNode buildUser() {
        UserNode userNode = new UserNodeImpl();
        userNode.setId("6000000");
        userNode.setLabel("Administrateur");
        userNode.setActive(false);

        return userNode;
    }
}
