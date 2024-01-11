package com.example.moneywise.quiz;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import com.example.moneywise.R;

import java.util.ArrayList;

public class ImageViewPagerAdapter extends PagerAdapter {
    private Context context;
    ArrayList<Uri> ImageUris;
    LayoutInflater layoutInflater;

    public ImageViewPagerAdapter(Context context, ArrayList<Uri> imageUris){
        this.context = context;
        ImageUris = new ArrayList<>(imageUris);
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return ImageUris.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = layoutInflater.inflate(R.layout.course_viewpager_image, container, false);
        ImageView imageView = view.findViewById(R.id.UploadImage);
        imageView.setImageURI(ImageUris.get(position));
        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

}