package com.example.aiplant.utility_classes;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.aiplant.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class GridImageAdapter extends ArrayAdapter<Bitmap> {
    private static final String TAG = "GridImageAdapter";
    private Context mContext;
    private LayoutInflater mInflater;
    private int layoutResource;
    private ArrayList<Bitmap> imgURLs;
    public GridImageAdapter(Context mContext, int layoutResource, ArrayList<Bitmap> imgURLs) {
        super(mContext, layoutResource, imgURLs);
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mContext = mContext;
        this.layoutResource = layoutResource;
        this.imgURLs = imgURLs;
    }
    private static class ViewHolder {
        CircleImageView image;
        ProgressBar mProgressBar;
    }
    @Override
    public View getView(int position, @Nullable View convertView, @Nullable ViewGroup parent) {
        final ViewHolder holder;
        /**
         * ViewHolder build pattern (Similar to RecyclerView)
         */
        if (convertView == null) {
            convertView = mInflater.inflate(layoutResource, parent, false);
            holder = new ViewHolder();
            holder.mProgressBar = convertView.findViewById(R.id.grid_image_progress_bar);
            holder.image = convertView.findViewById(R.id.gridImageView);
            holder.mProgressBar.setVisibility(View.VISIBLE);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            holder.mProgressBar.setVisibility(View.VISIBLE);
        }
        holder.mProgressBar.setVisibility(View.VISIBLE);
        Bitmap bitmap = imgURLs.get(position);
        Glide.with(convertView).load(bitmap).centerCrop().into(holder.image);
        holder.mProgressBar.setVisibility(View.INVISIBLE);
        return convertView;
    }



//        MyModifications
//        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(mContext);
//        ImageLoader imageLoader = ImageLoader.getInstance();
//        imageLoader.init(configuration);
//        Uri uri = getImageUri(mContext,bitmap);
//        imageLoader.loadImage(String.valueOf(uri), new ImageLoadingListener() {
//            @Override
//            public void onLoadingStarted(String imageUri, View view) {
//                if (holder.mProgressBar != null) {
////                    //TODO make drawable !!!
//                    holder.mProgressBar.setBackgroundResource(R.mipmap.eye_logo);
//                    holder.mProgressBar.setVisibility(View.VISIBLE);
//
//                }
//            }
//
//            @Override
//            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//                if (holder.mProgressBar != null) {
//                    Toast.makeText(getContext(), "Error loading: " + failReason.getCause().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//                    holder.mProgressBar.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                if (holder.mProgressBar != null) {
//                    holder.mProgressBar.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onLoadingCancelled(String imageUri, View view) {
//                if (holder.mProgressBar != null) {
//                    holder.mProgressBar.setVisibility(View.GONE);
//                }
//            }
//        });

//    }


}
