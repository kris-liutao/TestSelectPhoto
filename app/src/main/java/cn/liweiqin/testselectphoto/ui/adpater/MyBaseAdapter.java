package cn.liweiqin.testselectphoto.ui.adpater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import cn.liweiqin.testselectphoto.R;

/**
 * Created by Administrator on 2016/12/30.
 */

public class MyBaseAdapter extends BaseAdapter{
    private ArrayList<String> mList;
    private Context mContext;

    public MyBaseAdapter(Context mContext, ArrayList<String> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder myViewHolder;
        if (convertView==null){
            convertView= LayoutInflater.from(mContext).inflate(R.layout.item_gridview_show,parent,false);
            myViewHolder=new MyViewHolder(convertView);
            convertView.setTag(myViewHolder);
        }else{
            myViewHolder= (MyViewHolder) convertView.getTag();
        }

        Glide.with(mContext).load(mList.get(position)).into(myViewHolder.imageView_show);

        return convertView;
    }
    public class MyViewHolder{
        private ImageView imageView_show;
        public MyViewHolder(View view){
            int screenWidthPx=mContext.getResources().getDisplayMetrics().widthPixels-30;
            imageView_show= (ImageView) view.findViewById(R.id.imageview_item_show);
            ViewGroup.LayoutParams params = imageView_show.getLayoutParams();

            if (mList.size()>=3&&mList.size()!=4){
                params.width=screenWidthPx/3;
                params.height=screenWidthPx/3;
            }else if(mList.size()==2||mList.size()==4){
                params.width=screenWidthPx/3;
                params.height=screenWidthPx/3;
            }else if (mList.size()==1){
                params.width=screenWidthPx/3*2;
                params.height=screenWidthPx;
            }
            imageView_show.setLayoutParams(params);
        }
    }
}
