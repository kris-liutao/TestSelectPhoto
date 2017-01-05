package cn.liweiqin.testselectphoto.ui.weight;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

import cn.liweiqin.testselectphoto.R;
import cn.liweiqin.testselectphoto.ui.adpater.MyBaseAdapter;

public class LoadImageActivity extends AppCompatActivity {
    private Context mContext = this;
    private GridView gridView_show;
    private ArrayList<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_image);
        gridView_show = (GridView) findViewById(R.id.gridview_show);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        list = bundle.getStringArrayList("image");
        int screenWidthPx = mContext.getResources().getDisplayMetrics().widthPixels - 30;
        ViewGroup.LayoutParams params = gridView_show.getLayoutParams();
        if (list.size() >= 3 && list.size() != 4) {
            gridView_show.setNumColumns(3);
            params.width = screenWidthPx;
        } else if (list.size() == 2 || list.size() == 4) {
            gridView_show.setNumColumns(2);
            params.width = screenWidthPx / 3 * 2;
        } else if (list.size() == 1) {
            gridView_show.setNumColumns(1);
            params.width = screenWidthPx / 3 * 2;
        }
        gridView_show.setLayoutParams(params);

        MyBaseAdapter adapter = new MyBaseAdapter(mContext, list);
        gridView_show.setAdapter(adapter);
    }
}
