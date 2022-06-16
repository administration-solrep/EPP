package fr.dila.st.ui.th.model;

public class FragmentBlock implements Comparable<FragmentBlock> {
    private String filename = "";
    private String name = "";
    private int order = 0;
    private int level = 0;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((filename == null) ? 0 : filename.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        FragmentBlock other = (FragmentBlock) obj;
        if (filename == null) {
            if (other.filename != null) return false;
        } else if (!filename.equals(other.filename)) return false;
        if (name == null) {
            if (other.name != null) return false;
        } else if (!name.equals(other.name)) return false;
        return true;
    }

    @Override
    public int compareTo(FragmentBlock o) {
        if (o.getOrder() == this.getOrder()) {
            if (o.getLevel() == this.getLevel()) {
                return 0;
            } else if (o.getLevel() > this.getLevel()) {
                return -1;
            } else {
                return 1;
            }
        } else if (o.getOrder() > this.getOrder()) {
            return -1;
        } else {
            return 1;
        }
    }
}
