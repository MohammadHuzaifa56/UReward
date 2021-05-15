package com.htech.uadmin;

import android.net.Uri;
import android.os.Bundle;
import android.util.SparseArray;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.btnFetch)
    Button btnFetch;
    @BindView(R.id.edtUrl)
    EditText edtUrl;
    @BindView(R.id.exoPlayer)
    PlayerView mPlayer;
    @BindView(R.id.btnPost)
    Button btnPost;

    int type;
    SimpleExoPlayer exoPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        type=getIntent().getIntExtra("type",1);
    }
    @OnClick(R.id.btnFetch)
    void fetchVideo(){

        initPlayer();
    }

    private void initPlayer() {

        exoPlayer = new SimpleExoPlayer.Builder(this).build();
        mPlayer.setPlayer(exoPlayer);

        switch (type){
            case 1:
                playYouTubeVideo(edtUrl.getText().toString());
                break;
            case 2:
                playUrlVideo(edtUrl.getText().toString());
                break;
        }

    }

    private void playUrlVideo(String toString) {
                try {
            exoPlayer = (com.google.android.exoplayer2.SimpleExoPlayer) ExoPlayerFactory.newSimpleInstance(getApplicationContext());
            Uri video = Uri.parse(toString);

            com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("video");
            String userAgent = Util.getUserAgent(getApplicationContext(), String.valueOf(R.string.app_name));
            DefaultHttpDataSourceFactory mediaDataSourceFactory = new DefaultHttpDataSourceFactory(
                    Util.getUserAgent(getApplicationContext(), String.valueOf(R.string.app_name)));
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            //MediaSource mediaSource = new ExtractorMediaSource(video, dataSourceFactory, extractorsFactory, null, null);
            MediaSource mediaSource = new ExtractorMediaSource.Factory(new DefaultHttpDataSourceFactory("exoplayer-codelab"))
                    .createMediaSource(video);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(false);
            mPlayer.setPlayer(exoPlayer);

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void playYouTubeVideo(String youtubeUrl) {
        new YouTubeExtractor(this)
        {
            @Override
            protected void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta videoMeta) {
                if (ytFiles!=null) {
                    int videoTag = 134;
                    int audioTag = 140;
                    MediaSource audioSource=new ProgressiveMediaSource.Factory(new DefaultHttpDataSource.Factory())
                            .createMediaSource(MediaItem.fromUri(ytFiles.get(audioTag).getUrl()));
                    MediaSource videoSource=new ProgressiveMediaSource.Factory(new DefaultHttpDataSource.Factory())
                            .createMediaSource(MediaItem.fromUri(ytFiles.get(videoTag).getUrl()));
                    exoPlayer.setMediaSource(new MergingMediaSource(
                            true,
                            videoSource,
                            audioSource),
                            true
                    );
                    exoPlayer.prepare();
                    exoPlayer.setPlayWhenReady(true);
                    exoPlayer.seekTo(0,0);
                }

            }
        }.extract(youtubeUrl,false,true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (exoPlayer!=null){
            exoPlayer.release();
        }
    }
    @OnClick(R.id.btnPost)
    void postVideo(){
        FirebaseFirestore fbStore=FirebaseFirestore.getInstance();
        if (edtUrl.length()<2){
            edtUrl.setError("Please enter url");
            return;
        }else {
            UHelper.showDialog(MainActivity.this,"Uploading");
        VideoModel videoModel=new VideoModel(edtUrl.getText().toString(),null,type);
        fbStore.collection("Videos").add(videoModel).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
             if (task.isSuccessful()){
                 UHelper.hideDialog();
                 UHelper.showToast(getApplicationContext(),"Successfully Uploaded");
             }
             else {
                 UHelper.hideDialog();
                 UHelper.showToast(getApplicationContext(),task.getException().getMessage());
             }
            }
        });
        }
    }
}