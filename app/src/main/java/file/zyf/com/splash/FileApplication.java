package file.zyf.com.splash;

import android.app.Application;

import com.facebook.soloader.SoLoader;

/**
 * Created by 0 on 2018/4/10.
 */

public class FileApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SoLoader.init(this, false);
    }
}
