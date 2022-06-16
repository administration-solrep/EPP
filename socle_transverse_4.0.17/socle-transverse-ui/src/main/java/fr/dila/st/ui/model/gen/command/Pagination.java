package fr.dila.st.ui.model.gen.command;

import fr.dila.st.ui.annot.Command;

@Command
public class Pagination {
    private int current;
    private int size;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String toString() {
        return String.format("Pagination(cur=%d, size=%d)", current, size);
    }
}
