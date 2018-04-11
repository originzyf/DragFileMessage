package file.zyf.com.drag;

import android.graphics.PointF;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;

import file.zyf.com.R;

import static java.lang.Float.MIN_VALUE;

public class DragManager implements View.OnDragListener {

    private RecyclerView recyclerView;
    private DragListener dragListener;
    private TextListener textListener;

    private int fromPosition = -1;

    private final PointF nextMoveTouchPoint = new PointF(MIN_VALUE, MIN_VALUE);

    public DragManager(RecyclerView recyclerView, DragListener dragListener, TextListener textListener) {
        this.recyclerView = recyclerView;
        this.dragListener = dragListener;
        this.textListener = textListener;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        if (!(event.getLocalState() instanceof DragState)) {
            return false;
        }
        final DragState dragState = (DragState) event.getLocalState();
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                dragListener.notifyItemChange(dragListener.getPosition(dragState.getItem()), dragState.getItemId());
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                break;
            case DragEvent.ACTION_DRAG_LOCATION:
                if (fromPosition == -1) {
                    //获取源文件的position
                    fromPosition = dragListener.getPosition(dragState.getItem());
                }
                //更新item背景
                dragListener.setItemColor(fromPosition, getToPosition(event));
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                dragListener.setAllColor();
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                //重置一些参数
                dragListener.notifyItemChange(dragListener.getPosition(dragState.getItem()), DragListener.NO_ID);
                fromPosition = -1;
                nextMoveTouchPoint.set(MIN_VALUE, MIN_VALUE);
                break;
            case DragEvent.ACTION_DROP:
                //删除源文件的item 并移动文件
                dragListener.onDragEnd(fromPosition, getToPosition(event));
                textListener.setTextVisibility(View.GONE, R.color.white);
                break;
        }
        return true;
    }

    //获取目标文件夹的position
    private int getToPosition(DragEvent event) {
        float x = event.getX();
        float y = event.getY();
        int toPosition = RecyclerView.NO_POSITION;
        View child1 = recyclerView.findChildViewUnder(x, y);
        if (child1 != null) {
            toPosition = recyclerView.getChildViewHolder(child1).getAdapterPosition();
        }
        return toPosition;
    }
}
