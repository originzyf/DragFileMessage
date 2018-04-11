package file.zyf.com.drag;

public interface DragListener {

    //没有id的默认值
    public static final int NO_ID = -1;

    //更新item
    void notifyItemChange(int position, int dragItemId);

    //获取position
    int getPosition(Object object);

    //选中之后的操作
    void onDragEnd(int fromPosition, int toPosition);

    //设置item的背景
    void setItemColor(int fromPosition, int toPosition);

    //设置item的背景
    void setAllColor();

}
