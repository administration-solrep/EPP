package fr.sword.idl.naiad.nuxeo.feuilleroute.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteElement;
import java.util.ArrayList;
import java.util.List;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Wraps a documentElement adding informations about the level where the
 * document is inside the container documentRoute
 *
 */
public class RouteTableElement {
    @JsonIgnore
    private final FeuilleRouteElement element;

    @JsonIgnore
    private final RouteTable table;

    private final int depth;

    @JsonIgnore
    private RouteFolderElement parent;

    @JsonIgnore
    private boolean isFirstChild;

    @JsonIgnore
    private final List<RouteFolderElement> firstChildList = new ArrayList<>();

    public RouteTableElement(
        FeuilleRouteElement element,
        RouteTable table,
        int depth,
        RouteFolderElement parent,
        boolean isFirstChild
    ) {
        this.table = table;
        this.depth = depth;
        this.element = element;
        this.parent = parent;
        this.isFirstChild = isFirstChild;
    }

    public RouteFolderElement getParent() {
        return parent;
    }

    public FeuilleRouteElement getElement() {
        return element;
    }

    public int getDepth() {
        return depth;
    }

    public RouteTable getRouteTable() {
        return table;
    }

    public List<RouteFolderElement> getFirstChildFolders() {
        return firstChildList;
    }

    public int getRouteMaxDepth() {
        return table.getMaxDepth();
    }

    public DocumentModel getDocument() {
        return element.getDocument();
    }

    public void computeFirstChildList() {
        RouteFolderElement currentParent = parent;
        boolean currentIsFirst = isFirstChild;
        while (currentIsFirst && currentParent != null) {
            currentIsFirst = currentParent.isFirstChild();
            firstChildList.add(0, currentParent);
            currentParent = currentParent.getParent();
        }
    }
}
