package file.zyf.com.adapter;

import android.graphics.Color;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import file.zyf.com.R;
import file.zyf.com.entity.entity;

public class myAdapter extends BaseQuickAdapter<entity, BaseViewHolder> {


    public myAdapter(int layoutResId, @Nullable List<entity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, entity item) {
            helper.setText(R.id.tv, item.getFile().getName());
            helper.setBackgroundRes(R.id.imageView, R.mipmap.wjj);
            if (item.b)
                helper.setBackgroundColor(R.id.item_line, Color.WHITE);
            else
                helper.setBackgroundColor(R.id.item_line, Color.BLUE);

    }

}