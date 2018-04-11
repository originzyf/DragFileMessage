package file.zyf.com.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.File;

/**
 * Created by 0 on 2018/3/5.
 */

public class entity implements MultiItemEntity {
    private int integer;
    public Boolean b = true;
    private int itemType;
    private File file;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getInteger() {
        return integer;
    }

    public void setInteger(int integer) {
        this.integer = integer;
    }


    public entity(int itemType) {
        this.itemType = itemType;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }


    @Override
    public int getItemType() {
        return itemType;
    }
}
