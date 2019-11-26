package com.example.evnt.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.evnt.R;

import java.util.List;

public class TagChipAdapter extends RecyclerView.Adapter<TagChipAdapter.TagTextViewHolder> {

    private List<String> tagList;
    private Context context;

    public TagChipAdapter(Context context, List<String> tags){
        this.context = context;
        this.tagList = tags;
    }

    @NonNull
    @Override
    public TagTextViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.tag_text, parent, false);
        return new TagTextViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagTextViewHolder holder, int position) {
        String tagText = tagList.get(position);
        holder.tagText_tv.setText(tagText);
    }

    @Override
    public int getItemCount() {
        return tagList.size();
    }

    class TagTextViewHolder extends RecyclerView.ViewHolder {

        private TextView tagText_tv;
        public TagTextViewHolder(@NonNull View itemView) {
            super(itemView);
            tagText_tv = itemView.findViewById(R.id.tag_text);
        }
    }
}
