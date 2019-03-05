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

public class PendingRequestsAdapter extends RecyclerView.Adapter<PendingRequestsAdapter.PendingRequestViewHolder> {

    private List<Friend> dataList;
    private LayoutInflater inflater;
    private PendingRequestsAdapterListener onClickListener;

    public PendingRequestsAdapter(Context context, List<Friend> data, PendingRequestsAdapterListener onClickListener) {
        inflater = LayoutInflater.from(context);
        dataList = data;
        this.onClickListener = onClickListener;
    }

    public interface PendingRequestsAdapterListener {
        void acceptBtnOnClick(View v, int position);
        void rejectBtnOnClick(View v, int position);
    }

    // stores and recycles views as they are scrolled off screen
    public class PendingRequestViewHolder extends RecyclerView.ViewHolder {
        ImageButton acceptBtn;
        ImageButton rejectBtn;
        TextView tvPendingEmail;

        PendingRequestViewHolder(View itemView) {
            super(itemView);
            acceptBtn = itemView.findViewById(R.id.btn_pending_approve);
            rejectBtn = itemView.findViewById(R.id.btn_pending_ignore);
            tvPendingEmail = itemView.findViewById(R.id.tv_pending_email);

            acceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.acceptBtnOnClick(v, getAdapterPosition());
                }
            });
            rejectBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.rejectBtnOnClick(v, getAdapterPosition());
                }
            });
        }
    }

    @NonNull
    @Override
    public PendingRequestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item_pending_request, viewGroup, false);
        return new PendingRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PendingRequestViewHolder pendingRequestViewHolder, int i) {
        String email = dataList.get(i).getEmail();
        pendingRequestViewHolder.tvPendingEmail.setText(email);
    }

    public Friend getItem(int id) {
        return dataList.get(id);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
