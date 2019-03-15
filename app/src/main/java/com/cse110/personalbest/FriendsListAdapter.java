package com.cse110.personalbest;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.FriendsListViewHolder> {

    private List<Friend> dataList;
    private LayoutInflater inflater;
    private FriendsListAdapterListener onClickListener;

    public FriendsListAdapter(Context context, List<Friend> data, FriendsListAdapterListener onClickListener) {
        inflater = LayoutInflater.from(context);
        dataList = data;
        this.onClickListener = onClickListener;
    }

    public interface FriendsListAdapterListener {
        void friendListItemOnClick(View v, int position);
        void removeFriendBtnOnClick(View v, int position);
    }

    // stores and recycles views as they are scrolled off screen
    public class FriendsListViewHolder extends RecyclerView.ViewHolder {
        ImageButton removeBtn;
        TextView tvFriendEmail;

        FriendsListViewHolder(View itemView) {
            super(itemView);
            removeBtn = itemView.findViewById(R.id.btn_delete_friend);
            tvFriendEmail = itemView.findViewById(R.id.tv_friend_email);

            tvFriendEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.friendListItemOnClick(v, getAdapterPosition());
                }
            });

            removeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.removeFriendBtnOnClick(v, getAdapterPosition());
                }
            });
        }
    }

    @NonNull
    @Override
    public FriendsListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item_friend, viewGroup, false);
        return new FriendsListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsListViewHolder pendingRequestViewHolder, int i) {
        String email = dataList.get(i).getEmail();
        pendingRequestViewHolder.tvFriendEmail.setText(email);
    }

    public Friend getItem(int id) {
        return dataList.get(id);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
