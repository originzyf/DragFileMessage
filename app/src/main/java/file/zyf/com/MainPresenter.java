package file.zyf.com;

import java.io.File;
import java.util.ArrayList;

import file.zyf.com.entity.entity;

public class MainPresenter implements MainContract.Presenter {
    protected MainContract.View view;

    public MainPresenter() {
    }


    public void attachView(MainContract.View view) {
        this.view = view;
    }

    public void detachView() {
        this.view = null;
    }

    @Override
    public void getPathDate(ArrayList<entity> list, String path, Boolean b) {
        File f = new File(path);
        File[] files = f.listFiles();
        list.clear();
        for (int i = 0; i < files.length; i++) {
            entity entity = new entity(1);
            entity.setFile(files[i]);
            entity.setName(files[i].getName());
            entity.setInteger(i);
            if (files[i].isDirectory()) {
                list.add(entity);
            } else {
                if (!b)
                    list.add(entity);
            }
        }
    }


}
