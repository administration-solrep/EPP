package fr.dila.st.core.event.work;

import java.io.Serializable;

public class StepContext implements Serializable {
    private static final long serialVersionUID = 313245574635509386L;

    private final long parentBatchJobId;
    private final String parentEventName;
    private final int startIndex;
    private final int chunkSize;
    private final int nbItemsTotal;

    public StepContext(long parentBatchJobId, String parentEventName, int startIndex, int chunkSize, int nbItemsTotal) {
        this.parentBatchJobId = parentBatchJobId;
        this.parentEventName = parentEventName;
        this.startIndex = startIndex;
        this.chunkSize = chunkSize;
        this.nbItemsTotal = nbItemsTotal;
    }

    public long getParentBatchJobId() {
        return parentBatchJobId;
    }

    public String getParentEventName() {
        return parentEventName;
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getStepOneIndex() {
        return startIndex / chunkSize + 1;
    }

    public int getNbItemsTotal() {
        return nbItemsTotal;
    }
}
