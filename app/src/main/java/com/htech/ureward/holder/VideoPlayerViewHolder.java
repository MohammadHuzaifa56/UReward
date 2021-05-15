package com.htech.ureward.holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.RequestManager;
import com.google.android.exoplayer2.ui.PlayerView;
import com.htech.ureward.R;
import com.htech.ureward.model.VideoModel;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VideoPlayerViewHolder extends RecyclerView.ViewHolder {
    public PlayerView mPlayer;
    public LottieAnimationView lottieAnimationView;
    public ImageButton btnPlay;
    public TextView tvTime;
    public ImageView imageView;
    View parent;
    RequestManager requestManager;
    public VideoPlayerViewHolder(@NonNull View itemView) {
        super(itemView);
        mPlayer=itemView.findViewById(R.id.exoPlayer);
        lottieAnimationView=itemView.findViewById(R.id.vidLoading);
        btnPlay=itemView.findViewById(R.id.btnPlay);
        imageView=itemView.findViewById(R.id.imgVidThum);
        tvTime=itemView.findViewById(R.id.tvTime);
        parent=itemView;
    }
    public void onBind(VideoModel mediaObject, RequestManager requestManager, Context context) {
        this.requestManager = requestManager;
         parent.setTag(this);

        //title.setText(mediaObject.getTitle());
        this.requestManager
                .load("https://www.rd.com/wp-content/uploads/2020/04/GettyImages-694542042-e1586274805503.jpg")
                .into(imageView);
    }
}
