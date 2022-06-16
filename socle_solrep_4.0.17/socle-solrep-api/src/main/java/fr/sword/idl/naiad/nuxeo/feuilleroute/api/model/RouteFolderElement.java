package fr.sword.idl.naiad.nuxeo.feuilleroute.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteElement;

/**
 *
 */
public class RouteFolderElement {
    @JsonIgnore
    private final FeuilleRouteElement element;

    private final RouteTable table;

    private final boolean firstChild;

    @JsonIgnore
    private final RouteFolderElement parent;

    private int totalChildCount;

    private final int depth;

    public RouteFolderElement(
        FeuilleRouteElement element,
        RouteTable table,
        boolean isFirstChild,
        RouteFolderElement parent,
        int depth
    ) {
        this.table = table;
        this.element = element;
        this.firstChild = isFirstChild;
        this.parent = parent;
        this.depth = depth;
    }

    public int getTotalChildCount() {
        return totalChildCount;
    }

    public void increaseTotalChildCount() {
        if (parent != null) {
            parent.increaseTotalChildCount();
        } else {
            table.increaseTotalChildCount();
        }
        totalChildCount++;
    }

    public FeuilleRouteElement getRouteElement() {
        return element;
    }

    public int getDepth() {
        return depth;
    }

    public boolean isFirstChild() {
        return this.firstChild;
    }

    public RouteFolderElement getParent() {
        return parent;
    }
}
