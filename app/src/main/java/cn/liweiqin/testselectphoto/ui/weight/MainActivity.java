package cn.liweiqin.testselectphoto.ui.weight;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.liweiqin.testselectphoto.BasePhotoActivity;
import cn.liweiqin.testselectphoto.core.FunctionConfig;
import cn.liweiqin.testselectphoto.core.PhotoFinal;
import cn.liweiqin.testselectphoto.R;
import cn.liweiqin.testselectphoto.model.PhotoInfo;
import cn.liweiqin.testselectphoto.service.UpLoadIntentService;
import cn.liweiqin.testselectphoto.ui.adpater.PhotoShowListAdpater;

/**
 * 用于显示选择图片
 */
public class MainActivity extends BasePhotoActivity implements AdapterView.OnItemClickListener, UpLoadIntentService.UpDateUI {

    private Context mContext = this;
    /**
     * 存放选择的照片
     */
    private ArrayList<String> sekectList = new ArrayList<String>();
    /**
     * 上传到服务器后，图片的服务器地址
     */
    private ArrayList<String> pic_List = new ArrayList<>();

    private GridView selectView;
    private PhotoShowListAdpater listAdpater;
    private ProgressDialog mDialog;
    private int i = 0;//表示已上传图片个数
    //handler对消息进行处理
    private Handler mUIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mDialog.setProgress(msg.arg1);
                    break;
                case 1:
                    i++;//表示已上传数量+1
                    if (i <= sekectList.size()) {
                        Log.i("tag", "handleMessage:---------------> 看看你的i" + i);
                        pic_List.add(msg.obj.toString() + "");
                        mDialog.setProgress(0);
                        mDialog.setMessage("已上传图片(" + i + "/" + sekectList.size() + ")");
                    }
                    Log.i("tag", "handleMessage: ---------->" + i + "-----" + sekectList.size());
                    if (i >= sekectList.size()) {
                        mDialog.dismiss();
                        sekectList.clear();
                        listAdpater.notifyDataSetChanged();
                        //将上传完毕的图片网络地址传到显示朋友圈的界面
                        if (null != pic_List && 0 != pic_List.size()) {
                            Log.i("tag", "handleMessage: ----------->==0=0=0=" + pic_List.size());
                            Intent intent = new Intent(mContext, LoadImageActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putStringArrayList("image", pic_List);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                        i = 0;
                    }

                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        selectView = (GridView) findViewById(R.id.gv_selected);
        listAdpater = new PhotoShowListAdpater(MainActivity.this, sekectList, mScreenWidth);
        selectView.setAdapter(listAdpater);
        selectView.setOnItemClickListener(this);
    }


    /**
     * 加载配置的信息
     */
    private FunctionConfig initConfig() {
        //对 选择图片进行配置
         FunctionConfig.Builder functionBuilder = new FunctionConfig.Builder();
         FunctionConfig functionConfig = functionBuilder.setMaxSize(9)//设置最大选择数
                .setSelected(sekectList)//设置选泽的照片集
                .setContext(this)//设置上下文对象
                .setTakePhotoFolder(null)//设置拍照存放地址 默认为null
                .build();
        PhotoFinal.init(functionConfig);
        return functionConfig;
    }

    /**
     * 选择好图片的回调
     */
    private PhotoFinal.OnHanlderResultCallback mOnHanlderResultCallback = new PhotoFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            if (reqeustCode == PhotoFinal.REQUEST_CODE_MUTI) {
                //是选择图片回来的照片
                sekectList.clear();
                for (PhotoInfo info : resultList) {
                    sekectList.add(info.getPhotoPath());
                }
                listAdpater.notifyDataSetChanged();
                // Toast.makeText(getApplicationContext(), "size:" + resultList.size(), Toast.LENGTH_LONG).show();
            } else if (reqeustCode == PhotoFinal.REQUEST_CODE_CAMERA) {
                //是拍照带回来的照片
                sekectList.add(resultList.get(0).getPhotoPath());
                listAdpater.notifyDataSetChanged();
            }

        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PhotoShowListAdpater.PhotoViewHolder vh = (PhotoShowListAdpater.PhotoViewHolder) view.getTag();
        //最后一个才处理点击事件
        if (position == sekectList.size() && vh.iv_thumb.getVisibility() != View.GONE) {
            //相片浏览配置
            FunctionConfig functionConfig = initConfig();
            //打开图片 选择界面
            PhotoFinal.openMuti(functionConfig, mOnHanlderResultCallback);
        }
    }

    /**
     * 点击上传事件
     *
     * @param view
     */
    public void clickUpLoad(View view) {
        if (sekectList.size() == 0 || sekectList == null) return;
        //创建跳转到服务的意图
        final Intent intent = new Intent(MainActivity.this, UpLoadIntentService.class);
        intent.setFlags(sekectList.size());
        Bundle bundle = new Bundle();
        for (int i = 0; i < sekectList.size(); i++) {
            bundle.putString(i + "", sekectList.get(i));
        }
        intent.putExtras(bundle);
        startService(intent);
        //初始化更新UI所要用的接口对象
        UpLoadIntentService.setUpdateUI(this);

        //显示进度条
        mDialog = new ProgressDialog(mContext);
        mDialog.setTitle("提示");
        mDialog.setMessage("已上传图片(0/" + sekectList.size() + ")");
        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDialog.setMax(100);
        mDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        mDialog.show();
    }

    @Override
    public void upDateToUI(Message message) {
        mUIHandler.sendMessage(message);
    }
}
