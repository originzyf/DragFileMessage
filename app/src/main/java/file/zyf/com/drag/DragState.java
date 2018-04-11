package file.zyf.com.drag;

public class DragState {
    private Object mItem;
    private int mItemId;

    public DragState(Object mItem, int mItemId) {
        this.mItem = mItem;
        this.mItemId = mItemId;
    }

    public Object getItem() {
        return this.mItem;
    }

    public int getItemId() {
        return this.mItemId;
    }
}
