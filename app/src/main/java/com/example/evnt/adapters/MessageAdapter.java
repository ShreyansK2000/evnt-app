package com.example.evnt.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.evnt.IdentProvider;
import com.example.evnt.Message;
import com.example.evnt.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MsgViewHolder> {

    public static final int MSG_OTHER = 0;
    public static final int MSG_ME = 1;

    private Context context;
    private IdentProvider ident;
    private List<Message> messages;
    public MessageAdapter (Context context, List messages){
        this.context = context;
        this.messages = messages;
        this.ident = new IdentProvider(context.getApplicationContext());
    }

    @NonNull
    @Override
    public MsgViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_ME) {
            View view = LayoutInflater.from(context).inflate(R.layout.messages_me, parent, false);
            return new MessageAdapter.MsgViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.messages_others, parent, false);
            return new MessageAdapter.MsgViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MsgViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.message_tv.setText(message.getMessage());



    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class MsgViewHolder extends RecyclerView.ViewHolder {

        private TextView message_tv;

        public MsgViewHolder(@NonNull View itemView) {
            super(itemView);

            message_tv = itemView.findViewById(R.id.message_delivered);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).getSender().equals(ident.getValue(context.getString(R.string.user_id)))) {
            return MSG_ME;
        } else {
            return MSG_OTHER;
        }
    }
}
