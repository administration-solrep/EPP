package fr.dila.st.core.smartquery;

import java.util.LinkedList;

/**
 * Linked list with a capacity to handle undo/redo actions.
 * <p>
 * The method {@link #addLast(Object)} will remove the first object of the list
 * when at full capacity.
 *
 * @since 5.4
 * @author Anahide Tchertchian
 */
public class HistoryList<E> extends LinkedList<E> {
    private static final long serialVersionUID = 1L;

    protected int capacity;

    public HistoryList(int capacity) {
        super();
        this.capacity = capacity;
    }

    @Override
    public void addLast(E o) {
        if (size() >= capacity) {
            removeFirst();
        }
        super.addLast(o);
    }
}
