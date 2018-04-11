package file.zyf.com;

import android.app.Activity;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;

import file.zyf.com.entity.entity;

public interface MainContract {

    interface View {
        void setTitle(String s);

        void showToast(String s);

        void showDialog(Activity mActivity, String s, BaseQuickAdapter mAdapter, ArrayList<entity> list);

        void showDeleteDialog(Activity mActivity, String s, int pos, BaseQuickAdapter mAdapter);

        void showMoveDialog(List<entity> mData, int fromPosition, int toPosition);
    }

    interface Presenter {
        void getPathDate(ArrayList<entity> list, String path, Boolean b);
    }

}
