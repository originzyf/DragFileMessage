package file.zyf.com.adapter;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import file.zyf.com.MainContract;
import file.zyf.com.R;
import file.zyf.com.drag.DragListener;
import file.zyf.com.entity.entity;

public class contentAdapter extends BaseQuickAdapter<entity, BaseViewHolder> implements DragListener {
    private int dragItemId = DragListener.NO_ID;
    private RecyclerView rv;
    private int lastPosition = -1;
    private MainContract.View mainView;

    public contentAdapter(int layoutResId, @Nullable List<entity> data, RecyclerView rv, MainContract.View mainView) {
        super(layoutResId, data);
        this.rv = rv;
        this.mainView = mainView;
    }

    @Override
    protected void convert(BaseViewHolder helper, entity item) {
        helper.itemView.setVisibility(dragItemId == item.getInteger() ? View.GONE : View.VISIBLE);
        helper.setText(R.id.tv, item.getFile().getName());
        if (item.getFile().isDirectory()) {
            helper.setVisible(R.id.tv_num, true);
            helper.setText(R.id.tv_num, item.getFile().listFiles().length + "项");
            helper.setBackgroundRes(R.id.imageView, R.mipmap.wjj);
        } else {
            helper.setVisible(R.id.tv_num, false);
            helper.setBackgroundRes(R.id.imageView, R.mipmap.wj);
        }
        if (item.b)
            helper.setBackgroundColor(R.id.line, Color.WHITE);
        else {
            if (item.getFile().isDirectory())
                helper.setBackgroundColor(R.id.line, Color.BLUE);
            else
                helper.setBackgroundColor(R.id.line, Color.RED);
        }
    }

    @Override
    public void notifyItemChange(int position, int dragItemId) {
        this.dragItemId = dragItemId;
        notifyItemChanged(position);
    }

    @Override
    public int getPosition(Object object) {
        int a = -1;
        for (int i = 0; i < mData.size(); i++) {
            entity entity = mData.get(i);
            if (entity.getInteger() == (int) object) {
                a = i;
            }
        }
        return a;
    }

    @Override
    public void onDragEnd(final int fromPosition, final int toPosition) {
        if (fromPosition != toPosition && fromPosition > -1 && toPosition > -1) {
            setChildBackground(toPosition, Color.WHITE, true);
            if (mData.get(toPosition).getFile().isDirectory()) {
                mainView.showMoveDialog(mData, fromPosition, toPosition);
            } else
                Toast.makeText(mContext, "这是文件不能挪动", Toast.LENGTH_SHORT).show();
            lastPosition = -1;
        } else {
            if (lastPosition != -1 && lastPosition != toPosition) {
                setChildBackground(lastPosition, Color.WHITE, true);
                lastPosition = toPosition;
            }
            lastPosition = -1;
        }
    }

    @Override
    public void setItemColor(int fromPosition, int toPosition) {
        if (lastPosition == -1)
            lastPosition = toPosition;
        else {
            if (lastPosition != toPosition) {
                setChildBackground(lastPosition, Color.WHITE, true);
                lastPosition = toPosition;
            }
        }
        if (toPosition != fromPosition && toPosition > -1) {
            if (mData.get(toPosition).getFile().isDirectory())
                setChildBackground(toPosition, Color.BLUE, false);
            else
                setChildBackground(toPosition, Color.RED, false);
        }
    }

    @Override
    public void setAllColor() {
        for (int j = 0; j < mData.size(); j++) {
            entity entity = mData.get(j);
            if (!entity.b) {
                setChildBackground(j, Color.WHITE, true);
            }
        }
    }


    private void setChildBackground(int pos, int color, Boolean isColor) {
        View child = rv.getLayoutManager().findViewByPosition(pos);
        mData.get(pos).b = isColor;
        if (null != child) {
            BaseViewHolder holder = (BaseViewHolder) rv.getChildViewHolder(child);
            holder.setBackgroundColor(R.id.line, color);
            notifyItemChanged(pos);
        }
    }
}
