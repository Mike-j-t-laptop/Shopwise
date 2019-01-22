package mjt.shopwise;

public class DBDependency {

    public static final int DEPENDENCY_INVALID = 0;
    public static final int DEPENDENCY_TABLE_INDEX = 1;
    public static final int DEPENDENCY_TABLE_TRIGGER = 2;
    public static final int DEPENDENCY_TABLE_REFERENCE = 3;

    private Object parent;
    private Object child;
    private int dependency_type = DEPENDENCY_INVALID;
    boolean parent_complete = false;
    boolean usable = false;

    public DBDependency(Object parent, Object child, int dependency_type) {
        // Just in case there are nulls
        if (parent == null || child == null) {
            return;
        }
        this.parent = parent;
        this.child = child;
        this.dependency_type = dependency_type;
        this.usable = true;
    }
    public Object getDependencyParentObject() {
        return this.parent.getClass();
    }
    public Object getDependencyChildObject() {
        return this.child.getClass();
    }

    public int getDependency_type() {
        return dependency_type;
    }

    public void setParent_complete(boolean parent_complete) {
        this.parent_complete = parent_complete;
    }

    public Object getParent() {
        return parent;
    }

    public Object getChild() {
        return child;
    }

    public void setUsable(boolean usable) {
        this.usable = usable;
    }
}
