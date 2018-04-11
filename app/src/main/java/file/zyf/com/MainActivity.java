package file.zyf.com;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.crypto.Crypto;
import com.facebook.crypto.Entity;
import com.gcssloop.encrypt.unsymmetric.RSAUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import file.zyf.com.adapter.contentAdapter;
import file.zyf.com.adapter.myAdapter;
import file.zyf.com.drag.DragManager;
import file.zyf.com.drag.DragShadowBuilder;
import file.zyf.com.drag.DragState;
import file.zyf.com.drag.TextListener;
import file.zyf.com.entity.entity;
import file.zyf.com.utils.DisplayUtil;
import file.zyf.com.utils.FileUtils;

public class MainActivity extends AppCompatActivity implements MainContract.View, TextListener, View.OnClickListener {

    private ArrayList<entity> arrayList = new ArrayList<>();
    private ArrayList<entity> contentList = new ArrayList<>();
    private String SDCarePath = Environment.getExternalStorageDirectory().toString() + "/";
    private String path = "";
    private String twoPath = "";
    private Activity mActivity;
    private File backFile;
    private myAdapter mAdapter;
    private TextView tv;
    private contentAdapter contentAdapter;
    private MainPresenter mPresenter;
    private final PointF dragTouchPoint = new PointF();
    private ImageView rename, remove;

    //手指按下的点距item的边框距离
    private float x;
    private float y;
    //手指按下时距离手机边框的距离
    private float rawX;
    private float rawY;
    //屏幕的宽高
    private int width, hight;
    //记录长按的item坐标
    private int onLongPosition = -1;
    //判断点击事件是不是remove
    private int removeCount = -1;
    //判断点击事件是不是rename
    private int renameCount = -1;
    private int copyCount = -1;
    //获取item相对于父Framelayout的坐标
    private float dragX;
    private float dragY;
    private RecyclerView rv, rv_content;
    //记录选中过的item的position
    private int lastPosition = -1;
    private String publicKey;
    private String privateKey;
    private Crypto crypto;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActivity = this;

        rv = findViewById(R.id.rv);
        rv_content = findViewById(R.id.rv_content);
        FrameLayout container = findViewById(R.id.container);
        tv = findViewById(R.id.tv);
        remove = findViewById(R.id.remove_tv);
        rename = findViewById(R.id.rename_tv);
        ImageView back = findViewById(R.id.back);

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        hight = dm.heightPixels;

        container.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                //获取view 的坐标
                int maxWidth = remove.getWidth();
                int maxHight = remove.getHeight();

                dragX = dragEvent.getX();
                dragY = dragEvent.getY();

                if ((dragX - x) < maxWidth && (dragY - y) > 0 && (dragY - y) < maxHight && dragX > 0) {
                    removeCount = 0;
                    remove.setBackgroundResource(R.color.blue);
                } else {
                    remove.setBackgroundResource(R.color.gray);
                    removeCount = -1;
                }
                if ((width - dragX) < maxWidth && (width - dragX) > 0 && dragY < 120) {
                    renameCount = 0;
                    rename.setBackgroundResource(R.color.blue);
                } else {
                    rename.setBackgroundResource(R.color.gray);
                    renameCount = -1;
                }
                if (dragY > 1000) {
                    View child1 = rv.findChildViewUnder(dragX, hight - dragY);
                    if (child1 != null) {
                        int toPosition = rv.getChildViewHolder(child1).getAdapterPosition();
                        if (lastPosition == -1)
                            lastPosition = toPosition;
                        else {
                            if (lastPosition != toPosition) {
                                setChildBackground(lastPosition, Color.WHITE, true);
                                lastPosition = toPosition;
                            }
                        }
                        setChildBackground(toPosition, Color.BLUE, false);
                        copyCount = 0;
                    }
                } else {
                    copyCount = -1;
                    if (lastPosition != -1)
                        setChildBackground(lastPosition, Color.WHITE, true);
                }
                if (dragEvent.getAction() == DragEvent.ACTION_DROP) {
                    if (onLongPosition == -1)
                        return true;
                    String absolutePath = contentAdapter.getData().get(onLongPosition).getFile().getAbsolutePath();
                    if (removeCount == 0)
                        showDeleteDialog(mActivity, absolutePath, onLongPosition, contentAdapter);
                    else if (renameCount == 0)
                        showRenameDialog(mActivity, absolutePath, contentAdapter, contentList);
                    else if (copyCount == 0) {
                        if (lastPosition == -1)
                            return true;
                        AlertDialog.Builder inputDialog = new AlertDialog.Builder(mActivity);
                        inputDialog.setTitle("是否复制").setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        File file1 = arrayList.get(lastPosition).getFile();
                                        entity entity = contentList.get(onLongPosition);
                                        File file2 = new File(file1.getAbsolutePath() + "/" + entity.getFile().getName());
                                        if (!file2.exists())
                                            file2.mkdirs();
                                        if (FileUtils.moveDirectory(entity.getFile().getAbsolutePath(), file2.getAbsolutePath())) {
                                            mPresenter.getPathDate(contentList, SDCarePath + path + "/", false);
                                            contentAdapter.notifyDataSetChanged();
                                            showToast("移动成功");
                                        } else
                                            showToast("移动失败");
                                        lastPosition = -1;
                                    }
                                }).show();
                    }
                    setTextVisibility(View.GONE, R.color.white);
                }
                return true;
            }
        });

        back.setOnClickListener(this);
        rename.setOnClickListener(this);
        remove.setOnClickListener(this);

        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mAdapter = new myAdapter(R.layout.item, arrayList);
        rv.setAdapter(mAdapter);

        rv_content.setLayoutManager(new GridLayoutManager(this, 5));
        contentAdapter = new contentAdapter(R.layout.contentitem, contentList, rv_content, this);
        rv_content.setAdapter(contentAdapter);
        DragManager dragManager = new DragManager(rv_content, contentAdapter, this);
        rv_content.setOnDragListener(dragManager);

        mPresenter = new MainPresenter();
        mPresenter.attachView(this);
        mPresenter.getPathDate(arrayList, SDCarePath, true);
        path = arrayList.get(0).getFile().getName();
        backFile = arrayList.get(0).getFile();
        mPresenter.getPathDate(contentList, SDCarePath + path + "/", false);
        setTitle(SDCarePath + path + "/");

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, final int position) {
                twoPath = "";
                entity item = (entity) adapter.getItem(position);
                backFile = item.getFile();
                path = item.getFile().getName();
                mPresenter.getPathDate(contentList, item.getFile().getAbsolutePath(), false);
                contentAdapter.notifyDataSetChanged();
                setTitle(item.getFile().getAbsolutePath());
            }
        });

        rv_content.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                dragTouchPoint.set(e.getX(), e.getY());
                rawX = e.getRawX();
                rawY = e.getRawY();
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        contentAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                entity item = (entity) adapter.getItem(position);
                if ("".equals(twoPath)) {
                    if (item.getFile().isDirectory()) {
                        twoPath = item.getFile().getName();
                        mPresenter.getPathDate(contentList, item.getFile().getAbsolutePath(), false);
                        contentAdapter.notifyDataSetChanged();
                        tv.setText(item.getFile().getAbsolutePath());
                    } else
                        showToast("不是文件夹" + item.getFile().getName());
                } else
                    showToast("不能往下走了");

            }
        });

        contentAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                onLongPosition = position;
                onStartDrag(view, position);
                setTextVisibility(View.VISIBLE, R.color.gray);
                return false;
            }
        });

        mAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                entity item = (entity) adapter.getItem(position);
                showDeleteDialog(mActivity, item.getFile().getAbsolutePath(), position, mAdapter);
                return false;
            }
        });

//        try {
//          //  Log.e( "onCreate: ",SDCarePath+"SURE签名验证服务器客户端接口使用手册(Java版)_20160907.pdf" );
//            File file = new File(SDCarePath+"SURE签名验证服务器客户端接口使用手册(Java版)_20160907.pdf");
//            FileOutputStream fileOutputStream = openFileOutput("SURE签名验证服务器客户端接口使用手册(Java版)_20160907.pdf",
//                    Context.MODE_PRIVATE);
//            InputStream input = new FileInputStream(file);
//            byte[] byt = new byte[input.available()];
//            fileOutputStream.write(byt);
//            fileOutputStream.close();
//            File file2=new File(getFilesDir().getCanonicalPath()+"/model");
//            if (!file2.exists()){
//                file2.mkdirs();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        //获取序列号
//        try {
//            Class<?> c = Class.forName("android.os.SystemProperties");
//            Method get = c.getMethod("get", String.class);
//            String serial = (String) get.invoke(c, "ro.serialno");
//            Log.e("onCreate: ", serial);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                long time = System.currentTimeMillis();
//                Log.e("onCreate: ", SummaryUtils.getHash(SDCarePath + "SURE签名验证服务器客户端接口使用手册(Java版)_20160907.pdf", SummaryUtils.SHA256));
//                long time2 = System.currentTimeMillis() - time;
//                Log.e("onCreate: ", time2 + "==");
//            }
//        }).start();
    }


    private void setChildBackground(int pos, int color, Boolean isColor) {
        View child = rv.getLayoutManager().findViewByPosition(pos);
        if (null != child) {
            mAdapter.getData().get(pos).b = isColor;
            BaseViewHolder holder = (BaseViewHolder) rv.getChildViewHolder(child);
            holder.setBackgroundColor(R.id.item_line, color);
            mAdapter.notifyItemChanged(pos);
        }
    }

    public void onStartDrag(View view, Integer integer) {
        x = rawX - view.getLeft();
        y = rawY - view.getTop() - DisplayUtil.dip2px(this, rv_content.getTop());
        DragState dragState = new DragState(integer, integer.intValue());
        DragShadowBuilder dragShadowBuilder = new DragShadowBuilder(view,
                new Point(((int) (dragTouchPoint.x - view.getX())), (int) (dragTouchPoint.y - view.getY())));
        Point shadowSize = new Point();
        Point shadowTouchPoint = new Point();
        dragShadowBuilder.onProvideShadowMetrics(shadowSize, shadowTouchPoint);
        view.startDrag(null, dragShadowBuilder, dragState, 0);
    }


    @Override
    public void onClick(View view) {
        File infile = new File(SDCarePath+"加密后的图片.jpg");
        File outfile = new File(SDCarePath+"解密后的图片.jpg");
        try {
            FileOutputStream out = new FileOutputStream(outfile);
            BufferedOutputStream bos = new BufferedOutputStream(out);
            FileInputStream fileStream = new FileInputStream(infile);

            InputStream inputStream = crypto.getCipherInputStream(fileStream,Entity.create("entity_id"));

            int read;
            byte[] buffer = new byte[1024];
            while ((read = inputStream.read(buffer)) != -1) {
                bos.write(buffer, 0, read);
                bos.flush();
            }

            out.close();
            inputStream.close();
            fileStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int id = view.getId();
        if (id == R.id.back) {
            if (null != backFile) {
                mPresenter.getPathDate(contentList, backFile.getAbsolutePath(), false);
                contentAdapter.notifyDataSetChanged();
                tv.setText(backFile.getAbsolutePath());
                twoPath = "";
            }
        }
    }


    @Override
    public void setTitle(String s) {
        tv.setText(SDCarePath + path + "/");
    }

    @Override
    public void showToast(String s) {
        Toast.makeText(mActivity, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showDialog(final Activity mActivity, final String s, final BaseQuickAdapter mAdapter, final ArrayList<entity> list) {
        final EditText editText = new EditText(mActivity);
        AlertDialog.Builder inputDialog =
                new AlertDialog.Builder(mActivity);
        inputDialog.setTitle("输入文件名").setView(editText).setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!"".equals(editText.getText().toString())) {
                            File file = new File(s + editText.getText().toString());
                            if (!file.exists()) {
                                file.mkdir();
                                entity bean = new entity(1);
                                bean.setFile(file);
                                mAdapter.addData(list.size() - 1, bean);
                            } else
                                showToast("文件夹已存在");
                        } else
                            showToast("文件夹名称不能为空");
                    }
                }).show();
    }

    public void showRenameDialog(final Activity mActivity, final String s, final BaseQuickAdapter mAdapter, final ArrayList<entity> list) {
        final EditText editText = new EditText(mActivity);
        AlertDialog.Builder inputDialog =
                new AlertDialog.Builder(mActivity);
        inputDialog.setTitle("输入文件名")
                .setView(editText)
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!"".equals(editText.getText().toString())) {
                                    FileUtils.rename(s, editText.getText().toString());
                                    mPresenter.getPathDate(contentList, SDCarePath + path, false);
                                    contentAdapter.notifyDataSetChanged();
                                } else
                                    showToast("文件夹名称不能为空");
                            }
                        }).show();
    }

    @Override
    public void showDeleteDialog(final Activity mActivity, final String s, final int posi, final BaseQuickAdapter mAdapter) {
        AlertDialog.Builder normalDialog = new AlertDialog.Builder(mActivity);
        normalDialog.setTitle("是否删除")
                .setPositiveButton("是",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                boolean b = FileUtils.deleteDirectory(s);
                                Toast.makeText(mActivity, b + s, Toast.LENGTH_SHORT).show();
                                mAdapter.remove(posi);
                            }
                        })
                .setNeutralButton("否",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .show();
    }


    public void showMoveDialog(final List<entity> mData, final int fromPosition, final int toPosition) {
        AlertDialog.Builder inputDialog = new AlertDialog.Builder(mActivity);
        inputDialog.setTitle("是否移动");
        inputDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File file = mData.get(fromPosition).getFile();
                        File file1 = mData.get(toPosition).getFile();
                        File file2 = new File(file1.getAbsolutePath() + "/" + file.getName());
                        if (!file2.exists())
                            file2.mkdirs();
                        boolean b = FileUtils.moveDirectory(file.getAbsolutePath(), file2.getAbsolutePath());
                        if (b) {
                            contentAdapter.remove(fromPosition);
                            Toast.makeText(mActivity, "移动成功", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    @Override
    public void setTextVisibility(int i, int color) {
        rename.setVisibility(i);
        remove.setVisibility(i);
    }
}
