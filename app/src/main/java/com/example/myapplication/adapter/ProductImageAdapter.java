package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.myapplication.R;
import java.util.List;

public class ProductImageAdapter extends BaseAdapter {
    private Context context;
    private List<ProductImage> imageList;

    public ProductImageAdapter(Context context, List<ProductImage> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @Override
    public int getCount() {
        return imageList != null ? imageList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return imageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    private View createView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.spinner_item_icon, parent, false);
        }

        ProductImage currentItem = imageList.get(position);

        ImageView imgIcon = convertView.findViewById(R.id.img_spinner_icon);
        TextView tvName = convertView.findViewById(R.id.tv_spinner_icon_name);

        if (currentItem != null) {
            imgIcon.setImageResource(currentItem.getResourceId());
            tvName.setText(currentItem.getName());
        }

        return convertView;
    }

    public static class ProductImage {
        private String name;
        private String resourceName;
        private int resourceId;

        public ProductImage(String name, String resourceName, int resourceId) {
            this.name = name;
            this.resourceName = resourceName;
            this.resourceId = resourceId;
        }

        public String getName() { return name; }
        public String getResourceName() { return resourceName; }
        public int getResourceId() { return resourceId; }
        
        @Override
        public String toString() {
            return name;
        }
    }
}
