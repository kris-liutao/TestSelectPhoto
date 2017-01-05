package cn.liweiqin.testselectphoto.service;

import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import org.json.JSONObject;

import java.util.ArrayList;

import cn.liweiqin.testselectphoto.utils.BitmapUtils;
import cn.liweiqin.testselectphoto.utils.QiNiuUpHelper;

public class UpLoadIntentService extends IntentService {
    private Context mContext = this;
    public static UpDateUI updateUI;
    /**
     * 存放选择的照片
     */
    private ArrayList<String> sekectList = new ArrayList<String>();

    public static void setUpdateUI(UpDateUI updateUIInterface) {
        updateUI = updateUIInterface;
    }

    public UpLoadIntentService() {
        super("UpLoadIntentService");
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        if (intent != null) {
            //获取到要上传图片的信息
            final int flags = intent.getFlags();
            Bundle bundle = intent.getExtras();
            for (int i = 0; i < flags; i++) {
                sekectList.add(i, bundle.getString("" + i));
            }
            //开始压缩并上传图片
            final UpProgressHandler handler = new UpProgressHandler() {
                @Override
                public void progress(String key, double percent) {
                    Message msg = new Message();
                    msg.what = 0;
                    msg.arg1 = (int) (percent * 100);
                    //通知主线程去更新UI
                    if (updateUI != null) {
                        updateUI.upDateToUI(msg);
                    }
                }
            };
            final UploadOptions options = new UploadOptions(null, null, false, handler, null);
            final UploadManager uploadManager = QiNiuUpHelper.getUploadManager();
            final String token = QiNiuUpHelper.getUploadToken();
            for (int i = 0; i < sekectList.size(); i++) {
                String file = sekectList.get(i);
                String key = System.currentTimeMillis() + file.substring(file.lastIndexOf("/") + 1);
//                String key="img.jpg";
                byte[] bytes = compressImage(file);
                uploadManager.put(bytes, key, token, new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject response) {
                        if (info.isOK()) {
//                            Log.i("tag", "------>>>>>img" + "上传成功 " + key);
                            Message msg = new Message();
                            msg.what = 1;
                            msg.obj = "http://oh0vbg8a6.bkt.clouddn.com/" + key;
                            //通知主线程去更新UI
                            if (updateUI != null) {
                                updateUI.upDateToUI(msg);
                            }
                        } else {
                            Log.i("tag", "------>>>>>img" + "上传失败 ");
                        }
                    }
                }, options);
            }
        }
    }

    /**
     * @param path-->图片路径
     * @return 用来对图片进行压缩
     */
    public byte[] compressImage(String path) {
        Bitmap bitmap = BitmapUtils.createThumbnail(path, 100);
        Bitmap image = BitmapUtils.compressImage(bitmap);
        byte[] result = BitmapUtils.convertIconToByte(image);
        return result;
    }

    public interface UpDateUI {
        void upDateToUI(Message message);
    }
}
