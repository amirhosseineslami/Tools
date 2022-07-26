package com.example.qrscanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.Holder> {
    private File[] voices;

    public RecyclerAdapter(File[] voices) {
        this.voices = voices;
    }

    private RecyclerViewOnItemClick recyclerViewOnItemClick;

    interface RecyclerViewOnItemClick{
        void onItemClickListener(File file, int position);
    }


    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.nameTextView.setText(voices[position].getName());

        String voiceDate = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss", Locale.US).format(new Date(voices[position].lastModified()));

        holder.dateTextView.setText(voiceDate);

    }

    @Override
    public int getItemCount() {
        return voices.length;
    }

    public class Holder extends RecyclerView.ViewHolder{
        TextView nameTextView, dateTextView;

        public Holder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textView_item_name);
            dateTextView = itemView.findViewById(R.id.textView_item_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getAdapterPosition()!=RecyclerView.NO_POSITION) {
                        recyclerViewOnItemClick.onItemClickListener(voices[getAdapterPosition()], getAdapterPosition());
                    }
                }
            });
        }
    }
    void setRecyclerViewOnItemClick(RecyclerViewOnItemClick listener){
        this.recyclerViewOnItemClick = listener;
    }
}
