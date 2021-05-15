package com.htech.ureward.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;
import com.htech.ureward.R;
import com.htech.ureward.holder.VideoPlayerViewHolder;
import com.htech.ureward.model.VideoModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VideoPlayerRecyclerAdapter extends RecyclerView.Adapter<VideoPlayerViewHolder> {

    private ArrayList<VideoModel> mediaObjects;
    private RequestManager requestManager;
    Context context;

    public VideoPlayerRecyclerAdapter(ArrayList<VideoModel> mediaObjects, RequestManager requestManager, Context context) {
        this.mediaObjects = mediaObjects;
        this.requestManager = requestManager;
        this.context=context;
    }

    @NonNull
    @Override
    public VideoPlayerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new VideoPlayerViewHolder(
                LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.video_card, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VideoPlayerViewHolder viewHolder, int i) {
        ((VideoPlayerViewHolder)viewHolder).onBind(mediaObjects.get(i), requestManager,context);
    }

    @Override
    public int getItemCount() {
        return mediaObjects.size();
    }
}
