package com.cse110.personalbest;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ChatMessagesListAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_MESSAGE_SENT = 12345;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 54321;
    private List<ChatMessage> dataList;
    private LayoutInflater inflater;

    public ChatMessagesListAdapter(Context context, List<ChatMessage> data) {
        inflater = LayoutInflater.from(context);
        dataList = data;
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, emailText;

        ReceivedMessageHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.tv_chat_message);
            timeText = itemView.findViewById(R.id.tv_msg_timestamp);
            emailText = itemView.findViewById(R.id.tv_chat_email);
        }

        void bind(ChatMessage message) {
            messageText.setText(message.getChatText());
            timeText.setText(message.getTime());
            emailText.setText(message.getEmail());
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;

        SentMessageHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.tv_chat_message);
            timeText = itemView.findViewById(R.id.tv_msg_timestamp);
        }

        void bind(ChatMessage message) {
            messageText.setText(message.getChatText());
            timeText.setText(message.getTime());
        }
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = dataList.get(position);
        if (message.getMsgType() == ChatMessage.MSG_TYPE.FROM_FRIEND) {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
        return VIEW_TYPE_MESSAGE_SENT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = inflater.inflate(R.layout.item_message_sent, viewGroup, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = inflater.inflate(R.layout.item_message_received, viewGroup, false);
            return new ReceivedMessageHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ChatMessage message = (ChatMessage) dataList.get(i);

        switch (viewHolder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) viewHolder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) viewHolder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
