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
    private ArrayList<Uri> imageUris;
    private LayoutInflater layoutInflater;

    public ImageViewPagerAdapter(Context context, ArrayList<Uri> imageUris) {
        this.context = context;
        this.imageUris = new ArrayList<>(imageUris);
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return imageUris.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        // Inflate the layout for a single image item in the ViewPager
        View view = layoutInflater.inflate(R.layout.course_viewpager_image, container, false);

        // Find the ImageView in the inflated layout
        ImageView imageView = view.findViewById(R.id.UploadImage);

        // Set the image URI to the ImageView
        imageView.setImageURI(imageUris.get(position));

        // Add the inflated layout to the ViewPager container
        container.addView(view);

        return view;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        // Remove the view from the container when it's no longer needed
        container.removeView((View) object);
    }
}
