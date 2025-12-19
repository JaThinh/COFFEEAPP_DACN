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

public class CategoryIconAdapter extends BaseAdapter {

    private Context context;
    private List<CategoryIcon> iconList;

    public CategoryIconAdapter(Context context, List<CategoryIcon> iconList) {
        this.context = context;
        this.iconList = iconList;
    }

    @Override
    public int getCount() {
        return iconList != null ? iconList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return iconList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.spinner_item_icon, parent, false);
        }

        CategoryIcon currentIcon = iconList.get(position);

        ImageView imgIcon = convertView.findViewById(R.id.img_spinner_icon);
        TextView tvName = convertView.findViewById(R.id.tv_spinner_icon_name);

        if (currentIcon != null) {
            imgIcon.setImageResource(currentIcon.getResourceId());
            tvName.setText(currentIcon.getName());
        }

        return convertView;
    }

    public static class CategoryIcon {
        private String name;
        private String resourceName; // Tên resource string (vd: "ic_coffee")
        private int resourceId;      // ID resource (vd: R.drawable.ic_coffee)

        public CategoryIcon(String name, String resourceName, int resourceId) {
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
