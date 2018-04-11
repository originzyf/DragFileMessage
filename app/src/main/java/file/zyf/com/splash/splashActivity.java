package file.zyf.com.splash;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;

import java.util.List;

import file.zyf.com.MainActivity;
import file.zyf.com.R;

/**
 * Created by 0 on 2018/3/6.
 */

public class splashActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        //初始化读写文件夹权限
        myPermission();

    }

    private void myPermission() {
        AndPermission.with(this)
                .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .onGranted(new Action() {
                    @Override
                    public void onAction(List<String> permissions) {
                        startActivity(new Intent(splashActivity.this, MainActivity.class));
                        finish();
                    }
                }).onDenied(new Action() {
            @Override
            public void onAction(List<String> permissions) {

            }
        })
                .start();
    }
}
