package com.example.group2_bigproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.PostViewHolder> {
    private List<PostItem> postList;
    public SharedPreferencesHelper pfHelper;
    public FirebaseHelper fbHelper;
    public Context context;

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        public ImageButton likeBtn, commentBtn, shareBtn, optionBtn;
        public TextView postDate;
        public TextView userName;
        public TextView postDescription;
        public ImageView avaUser;

        public PostViewHolder(View itemView) {
            super(itemView);
            avaUser = itemView.findViewById(R.id.avaUser);
            likeBtn = itemView.findViewById(R.id.btn_like);
            commentBtn = itemView.findViewById(R.id.cmtBtn);
            shareBtn = itemView.findViewById(R.id.shareBtn);
            optionBtn = itemView.findViewById(R.id.optionButton);
            userName = itemView.findViewById(R.id.textView_Username);
            postDescription = itemView.findViewById(R.id.textView_PostText);
            postDate = itemView.findViewById(R.id.textView_Date);
        }
    }

    public Adapter(List<PostItem> postList, Context context) {
        this.postList = postList;
        this.context = context;
        pfHelper = new SharedPreferencesHelper(context);
        fbHelper = new FirebaseHelper(context);
    }

    public void updatePostList(ArrayList<PostItem> postList){
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        PostItem currentItem = postList.get(position);
        holder.postDate.setText(currentItem.date);
        holder.postDescription.setText(currentItem.description);
        holder.userName.setText(currentItem.userName);

        holder.avaUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewProfilePageActivity.class);
                context.startActivity(intent);
            }
        });

        holder.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), PostActivity.class);
                fbHelper.getPostIDByPost(currentItem, postID -> {
                    intent.putExtra("postID", postID);
                    holder.itemView.getContext().startActivity(intent);
                });
            }
        });

        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(holder.itemView.getContext(), "Clicked like", Toast.LENGTH_SHORT).show();
            }
        });

//        holder.shareBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(holder.itemView.getContext(), "Clicked share", Toast.LENGTH_SHORT).show();
//            }
//        });

        holder.optionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(holder.itemView.getContext(), holder.optionBtn);
                popupMenu.inflate(R.menu.menu_options); // Load menu resource
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemId = item.getItemId();
                        if (itemId == R.id.saveRouteBtn) {
                            Toast.makeText(holder.itemView.getContext(), "Clicked save route", Toast.LENGTH_SHORT).show();
                            return true;
                        } else if (itemId == R.id.blockBtn) {
                            Toast.makeText(holder.itemView.getContext(), "Clicked block", Toast.LENGTH_SHORT).show();
                            return true;
                        } else if (itemId == R.id.reportBtn) {
                            Toast.makeText(holder.itemView.getContext(), "Clicked report", Toast.LENGTH_SHORT).show();

                            return true;
                        } else {
                            return false;
                        }
                    }
                });
                popupMenu.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }
}

