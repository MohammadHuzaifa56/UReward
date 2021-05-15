package com.htech.ureward.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.htech.ureward.R;
import com.htech.ureward.helpers.UHelper;
import com.htech.ureward.utils.PreferencesManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SELECT_IMAGE =101 ;
    @BindView(R.id.edtImage)
    CircleImageView edtImage;
    @BindView(R.id.imgProfile)
    CircleImageView profileImage;
    @BindView(R.id.profileContainer)
    ConstraintLayout profileContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        showProfileImage();
    }

    @OnClick(R.id.edtImage)
    void selectImage(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE_SELECT_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_CODE_SELECT_IMAGE&&data!=null){
            UHelper.showDialog(ProfileActivity.this,"Uploading Image");
            Uri uri=data.getData();
            StorageReference fbStorage=FirebaseStorage.getInstance().getReference("Users");
            fbStorage.child(PreferencesManager.getInstance().getMail(getApplicationContext())).child("image").putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()){
                        fbStorage.child(PreferencesManager.getInstance().getMail(getApplicationContext())).child("image").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                             UHelper.hideDialog();
                             Snackbar.make(profileContainer,"Image Uploaded Successfully", BaseTransientBottomBar.LENGTH_SHORT).show();
                             PreferencesManager.getInstance().setImage(getApplicationContext(),uri.toString());
                             showProfileImage();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                UHelper.hideDialog();
                                Snackbar.make(profileContainer,e.toString(), BaseTransientBottomBar.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else {
                        UHelper.hideDialog();
                        Snackbar.make(profileContainer,task.getException().getMessage(), BaseTransientBottomBar.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    private void showProfileImage() {

        BitmapImageViewTarget target=new BitmapImageViewTarget(profileImage);
        Glide.with(this)
                .asBitmap()

                .load(PreferencesManager.getInstance().getImage(getApplicationContext()))
                .centerCrop()
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.icuser)
                .error(R.drawable.icuser)
                .override(500,500)
                .into(target);
    }

}