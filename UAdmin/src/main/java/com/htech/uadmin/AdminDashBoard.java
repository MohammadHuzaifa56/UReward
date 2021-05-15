package com.htech.uadmin;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AdminDashBoard extends AppCompatActivity {
    @BindView(R.id.addYoutube)
    CardView cardYoutube;
    @BindView(R.id.addUrl)
    CardView cardUrl;
    @BindView(R.id.addGallery)
    CardView cardGallery;
    @BindView(R.id.delVideo)
    CardView cardDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dash_board);
        ButterKnife.bind(this);

    }
    @OnClick(R.id.addYoutube)
    void youTubeVideo(){
        Intent it=new Intent(getApplicationContext(),MainActivity.class);
        it.putExtra("type",1);
        startActivity(it);
    }
    @OnClick(R.id.addUrl)
    void urlVideo(){
        Intent it=new Intent(getApplicationContext(),MainActivity.class);
        it.putExtra("type",2);
        startActivity(it);
    }

}