package fr.sword.idl.naiad.nuxeo.feuilleroute.api.model;

/**
 *
 */
public class RouteTable {
    private int totalChildCount = 0;

    private int maxDepth = 0;

    /**
     *
     */
    public RouteTable() {
        // do nothing
    }

    /**
     * @return
     */
    public int getMaxDepth() {
        return maxDepth;
    }

    public void increaseTotalChildCount() {
        totalChildCount++;
    }

    public int getTotalChildCount() {
        return totalChildCount;
    }

    /**
     * @param maxDepth
     */
    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }
}
