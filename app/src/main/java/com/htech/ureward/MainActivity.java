package com.htech.ureward;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.htech.ureward.Adapter.VerticalSpacingItemDecorator;
import com.htech.ureward.Adapter.VideoPlayerRecyclerAdapter;
import com.htech.ureward.activities.ProfileActivity;
import com.htech.ureward.helpers.UHelper;
import com.htech.ureward.model.VideoModel;
import com.htech.ureward.utils.PreferencesManager;
import com.htech.ureward.views.VideoPlayerRecyclerView;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.imgUser)
    CircleImageView imgUser;
    @BindView(R.id.tvDailyReward)
    TextView tvDaily;
    @BindView(R.id.btnnav)
    ImageView btnNav;
    @BindView(R.id.btnNoti)
    ImageView btnNoti;
    @BindView(R.id.recVideos)
    VideoPlayerRecyclerView recVideos;
    FirebaseFirestore fbStore;
    FirestoreRecyclerAdapter mAdapter;
    SimpleExoPlayer exoPlayer;
    private long playbackPosition=0;
    private int currentWindow=0;
    private boolean playWhenReady;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        showProfileImage();
        fbStore = FirebaseFirestore.getInstance();
        //showVideos();

        initRecycler();

      //  recVideos.setHasFixedSize(true);

    }

    private void initRecycler() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recVideos.setLayoutManager(layoutManager);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(10);
        recVideos.addItemDecoration(itemDecorator);

 /*       ArrayList<MediaObject> mediaObjects = new ArrayList<MediaObject>(Arrays.asList(Resources.MEDIA_OBJECTS));
        recVideos.setMediaObjects(mediaObjects);
        VideoPlayerRecyclerAdapter adapter = new VideoPlayerRecyclerAdapter(mediaObjects, initGlide());
        recVideos.setAdapter(adapter);*/

        fbStore.collection("Videos").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
              if (task.isSuccessful()){
                  ArrayList<VideoModel> vidList=new ArrayList<>();
                  for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                  VideoModel videoModel=documentSnapshot.toObject(VideoModel.class);
                  vidList.add(videoModel);
                  UHelper.LogCat("list url "+videoModel.getUrl());
                  }
                  UHelper.LogCat("list size "+vidList.size());
                  recVideos.setMediaObjects(vidList);
                  VideoPlayerRecyclerAdapter adapter = new VideoPlayerRecyclerAdapter(vidList,initGlide(),getApplicationContext());
                  recVideos.setAdapter(adapter);
              }
              else {
                  UHelper.LogCat("error "+task.getException().getMessage());
              }
            }
        });
    }

    private RequestManager initGlide(){
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.white_background)
                .error(R.drawable.white_background);

        return Glide.with(this)
                .setDefaultRequestOptions(options);
    }
    private void showVideos() {
        UHelper.LogCat("ShowVideo Called");
        Query query = fbStore.collection("Videos");
        FirestoreRecyclerOptions<VideoModel> options = new FirestoreRecyclerOptions.Builder<VideoModel>()
                .setQuery(query, VideoModel.class)
                .build();
        mAdapter = new FirestoreRecyclerAdapter<VideoModel, VideoViewHolder>(options) {

            @NonNull
            @Override
            public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                UHelper.LogCat("OnCreateView Called");
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_card, parent, false);
                return new VideoViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull VideoViewHolder holder, int position, @NonNull VideoModel model) {
                UHelper.LogCat("BindView Called");
                if (isPlaying()){
                    releasePlayer();
                    initPlayer(model.getType(), model.getUrl(),holder);
                }
                else {
                    initPlayer(model.getType(), model.getUrl(),holder);
                }
                VidThumbs vidThumbs=new VidThumbs(holder);
                vidThumbs.execute(model.getUrl());

                CountDownTimer countDownTimer=new CountDownTimer(80 * 1000, 1000) {
                    @Override
                    public void onTick(long l) {

                    }

                    @Override
                    public void onFinish() {

                    }
                };
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (holder.btnPlay.getVisibility()==View.VISIBLE){
                            holder.btnPlay.setVisibility(View.GONE);
                        }
                        else {
                            holder.btnPlay.setVisibility(View.VISIBLE);
                        }
                    }
                });
                holder.btnPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (exoPlayer.isPlaying()){
                            view.setBackgroundResource(R.drawable.exo_controls_play);
                           // exoPlayer.pause();
                        }
                        else {
                            view.setBackgroundResource(R.drawable.exo_controls_pause);
                            // exoPlayer.play();
                        }
                    }
                });
            }
        };
    }

  /*  private void showYouTubeVideo(String url,VideoViewHolder holder) {
        exoPlayer = new SimpleExoPlayer.Builder(getApplicationContext()).build();
        exoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, int reason) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                UHelper.LogCat("PlayerState yt called");
                if (playbackState== ExoPlayer.STATE_BUFFERING){
                    holder.lottieAnimationView.setVisibility(View.VISIBLE);
                }
                else if (playbackState==ExoPlayer.STATE_READY){
                    holder.lottieAnimationView.setVisibility(View.GONE);
                    holder.btnPlay.setVisibility(View.GONE);
                    holder.imageView.setVisibility(View.GONE);

                    int timeMs=(int) exoPlayer.getDuration();
                    int totalSeconds = timeMs / 1000;
                    String time=stringForTime(totalSeconds);
                    holder.tvTime.setText("time");
                }
            }
        });
        holder.mPlayer.setPlayer(exoPlayer);
    }*/

    private String stringForTime(int timeMs) {
        StringBuilder mFormatBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        int totalSeconds = timeMs / 1000;
        //  videoDurationInSeconds = totalSeconds % 60;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

   /* private void showOtherVideo(String url,VideoViewHolder holder) {
        try {
            Uri video = Uri.parse(url);
            exoPlayer = (com.google.android.exoplayer2.SimpleExoPlayer) ExoPlayerFactory.newSimpleInstance(getApplicationContext());
            exoPlayer.addListener(new Player.EventListener() {
                @Override
                public void onTimelineChanged(Timeline timeline, int reason) {

                }

                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    UHelper.LogCat("PlayerState other called");
                    if (playbackState== ExoPlayer.STATE_BUFFERING){
                        holder.lottieAnimationView.setVisibility(View.VISIBLE);
                    }
                    else if (playbackState==ExoPlayer.STATE_READY){
                        holder.lottieAnimationView.setVisibility(View.GONE);
                        holder.btnPlay.setVisibility(View.GONE);
                        holder.imageView.setVisibility(View.GONE);
                    }
                }
            });
            com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("video");
            String userAgent = Util.getUserAgent(getApplicationContext(), String.valueOf(R.string.app_name));
            DefaultHttpDataSourceFactory mediaDataSourceFactory = new DefaultHttpDataSourceFactory(
                    Util.getUserAgent(getApplicationContext(), String.valueOf(R.string.app_name)));
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            //MediaSource mediaSource = new ExtractorMediaSource(video, dataSourceFactory, extractorsFactory, null, null);
            MediaSource mediaSource = new ExtractorMediaSource.Factory(new DefaultHttpDataSourceFactory("exoplayer-codelab"))
                    .createMediaSource(video);
          //  exoPlayer.setMediaSource(mediaSource);
           // exoPlayer.prepare();
            exoPlayer.setPlayWhenReady(false);
            holder.mPlayer.setPlayer(exoPlayer);

        }
        catch (Exception e){
            e.printStackTrace();
            UHelper.LogCat("VideoEx "+e.getMessage());
        }
    }*/

    private void showProfileImage() {
        BitmapImageViewTarget target = new BitmapImageViewTarget(imgUser);
        Glide.with(this)
                .asBitmap()

                .load(PreferencesManager.getInstance().getImage(getApplicationContext()))
                .centerCrop()
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.icuser)
                .error(R.drawable.icuser)
                .override(500, 500)
                .into(target);
    }

    @OnClick(R.id.imgUser)
    public void userProfile() {
        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));

    }

    class VideoViewHolder extends RecyclerView.ViewHolder{
        PlayerView mPlayer;
        LottieAnimationView lottieAnimationView;
        ImageButton btnPlay;
        TextView tvTime;
        ImageView imageView;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            mPlayer=itemView.findViewById(R.id.exoPlayer);
            lottieAnimationView=itemView.findViewById(R.id.vidLoading);
            btnPlay=itemView.findViewById(R.id.btnPlay);
            imageView=itemView.findViewById(R.id.imgVidThum);
            tvTime=itemView.findViewById(R.id.tvTime);
        }
    }

    private void initPlayer(int type, String url,VideoViewHolder holder) {
        switch (type) {
            case 1:
                //showYouTubeVideo(url,holder);
                break;
            case 2:
                //showOtherVideo(url,holder);
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
      /*  mAdapter.stopListening();
        if (exoPlayer!=null){
            exoPlayer.release();
            exoPlayer.stop(true);
        }*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        //mAdapter.startListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        exoPlayer.release();
//        exoPlayer.stop();
    }
    class VidThumbs extends AsyncTask<String,Void, Bitmap> {

        VideoViewHolder viewHolder;

        public VidThumbs(VideoViewHolder viewHolder) {
            this.viewHolder = viewHolder;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {

            String vidurl=strings[0];
            Bitmap bitmap = null;
            MediaMetadataRetriever mediaMetadataRetriever = null;
            try
            {
                mediaMetadataRetriever = new MediaMetadataRetriever();
                if (Build.VERSION.SDK_INT >= 14)
                    mediaMetadataRetriever.setDataSource(vidurl, new HashMap<String, String>());
                else
                    mediaMetadataRetriever.setDataSource(vidurl);
                //   mediaMetadataRetriever.setDataSource(videoPath);
                bitmap = mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                try {
                    throw new Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)"+ e.getMessage());
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
            finally
            {
                if (mediaMetadataRetriever != null)
                {
                    mediaMetadataRetriever.release();
                }
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            viewHolder.imageView.setImageBitmap(bitmap);
        }
    }
    private void releasePlayer() {
        if (exoPlayer != null) {
            playbackPosition = exoPlayer.getCurrentPosition();
            currentWindow = exoPlayer.getCurrentWindowIndex();
            playWhenReady = exoPlayer.getPlayWhenReady();
            exoPlayer.release();
            exoPlayer = null;
        }
    }
    private boolean isPlaying() {
        return exoPlayer != null
                && exoPlayer.getPlaybackState() != Player.STATE_ENDED
                && exoPlayer.getPlaybackState() != Player.STATE_IDLE
                && exoPlayer.getPlayWhenReady();
    }
}