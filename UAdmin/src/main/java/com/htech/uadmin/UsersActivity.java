package com.htech.uadmin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {
   @BindView(R.id.adUsrRecycle)
    RecyclerView recUsers;

   FirebaseFirestore firebaseFirestore;
   FirestoreRecyclerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        firebaseFirestore=FirebaseFirestore.getInstance();

        showUsers();

        recUsers.setHasFixedSize(true);
        recUsers.setLayoutManager(new LinearLayoutManager(this));
        recUsers.setAdapter(mAdapter);

    }

    private void showUsers() {
        Query query = firebaseFirestore.collection("Users");
        final FirestoreRecyclerOptions<Users> options = new FirestoreRecyclerOptions.Builder<Users>()
                .setQuery(query, Users.class)
                .build();
        mAdapter = new FirestoreRecyclerAdapter<Users, UsersViewHolder>(options) {
            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_user_layout, parent, false);
                return new UsersViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final UsersViewHolder holder, int position, @NonNull final Users model) {
                holder.tvName.setText(model.getName());
                //holder.tvCity.setText(model.getCity());
                holder.tvNumber.setText(model.getMail());

                showImage(model.getImageUrl(),holder);

            }
        };
    }

    private void showImage(String imageUrl,UsersViewHolder holder) {
        BitmapImageViewTarget target=new BitmapImageViewTarget(holder.imgProfile);
        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .centerCrop()
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.ic_users)
                .error(R.drawable.ic_users)
                .override(500,500)
                .into(target);
    }

    private class UsersViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvName,tvCity,tvNumber;
        CircleImageView imgProfile;
        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName=itemView.findViewById(R.id.textView23);
            tvCity=itemView.findViewById(R.id.textView24);
            tvNumber=itemView.findViewById(R.id.usPhone);
            cardView=itemView.findViewById(R.id.cardUsers);
            imgProfile=itemView.findViewById(R.id.circleImageView2);

        }}
}