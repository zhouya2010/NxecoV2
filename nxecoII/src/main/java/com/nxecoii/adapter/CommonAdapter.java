package com.nxecoii.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nxecoii.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public abstract class CommonAdapter<T> extends BaseAdapter {
    private final String TAG = CommonAdapter.class.getSimpleName();
    private Context mContext;
    private List<T> mData;
    private int mItemResId;

    public CommonAdapter(Context context, List<T> data,int itemResId){
        this.mContext = context;
        this.mData = data;
        this.mItemResId = itemResId;
    }
    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public T getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder = ViewHolder.newInstance(mContext,view,parent,mItemResId);
        convert(holder, getItem(position),position);
        return holder.getConvertView();
    }

    /**
     * 清空全部数据
     */
    public void clear(){
        mData.clear();
        notifyDataSetChanged();
    }

    /**
     * 添加一个数据
     * @param data
     */
    public void add(T data){
        mData.add(data);
        notifyDataSetChanged();
    }

    /**
     * 添加一组数据
     */
    public void addAll(List<T> data){
        mData.addAll(data);
        notifyDataSetChanged();
    }

    /**
     * 需要重写的方法
     * @param holder
     * @param item 当前位置的的元素
     * @param position 当前的索引
     */
    public abstract void convert(ViewHolder holder, T item, int position);

    public static class ViewHolder {
        private final String TAG = ViewHolder.class.getSimpleName();
        private Context mContext;
        private SparseArray<View> mMaps;
        private View mConvertView;

        private ViewHolder(Context context, View convertView, ViewGroup parent, int itemResId){
            mMaps = new SparseArray<View>();
            mConvertView = LayoutInflater.from(context).inflate(itemResId,parent,false);
            mConvertView.setTag(this);
            convertView = mConvertView;
            this.mContext = context;
        }
        public static ViewHolder newInstance(Context context, View convertView, ViewGroup parent, int itemResId){
            ViewHolder holder = null;
            if(convertView == null){
                holder = new ViewHolder(context,convertView,parent,itemResId);
            }else{
                holder = (ViewHolder)convertView.getTag();
            }
            return holder;
        }

        public <T extends View> T getView(int id){
            View view = mMaps.get(id);
            if(view == null){
                view = mConvertView.findViewById(id);
                mMaps.put(id,view);
            }
            return (T)view;
        }

        public ViewHolder setText(int id,String txt){
            TextView textView = getView(id);
            textView.setText(txt);
            return this;
        }

        public ViewHolder setImage(int id, String url, Callback callback){
            ImageView imageView = getView(id);
            Picasso.with(mContext).load(url).error(R.drawable.default_zone).placeholder(R.drawable.default_zone).resize(109, 76).into(imageView, callback);
            return this;
        }

        public ViewHolder setTextColor(int id,int color){
            TextView textView = getView(id);
            textView.setTextColor(color);
            return this;
        }

        public ViewHolder setBackgroundColorr(int id,int color){
            View view = getView(id);
            view.setBackgroundColor(color);
            return this;
        }

        public ViewHolder setCircleImage(int id, String url, Callback callback){
            ImageView imageView = getView(id);
            Picasso.with(mContext).load(url).error(R.drawable.default_zone).placeholder(R.drawable.default_zone).resize(90, 90).centerCrop().transform(new CircleTransform()).into(imageView,callback);
            return this;
        }

        public ViewHolder setImage(int id,int resId, Callback callback){
            ImageView imageView = getView(id);
            Picasso.with(mContext).load(resId).placeholder(R.drawable.default_zone).resize(90, 90).into(imageView,callback);
            return this;
        }

        public ViewHolder setImage(int id,File file, Callback callback){
            ImageView imageView = getView(id);
            Picasso.with(mContext).load(file).placeholder(R.drawable.default_zone).resize(90, 90).into(imageView,callback);
            return this;
        }
        public View getConvertView(){
            return mConvertView;
        }
    }
}
