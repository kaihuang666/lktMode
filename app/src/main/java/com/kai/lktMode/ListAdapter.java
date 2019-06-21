package com.kai.lktMode;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    private OnItemClick onItemClick=null;
    private List<Item> items;
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView subtitle;
        public ViewHolder(View v){
            super(v);
            title=v.findViewById(R.id.list_title);
            subtitle=v.findViewById(R.id.list_subtitle);
        }
    }
    public ListAdapter(List<Item> items){
        this.items=items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_all,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }
    public void setOnItemClick(OnItemClick click){
        onItemClick=click;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Item item=items.get(position);
        holder.title.setText(item.getTitle());
        holder.subtitle.setText(item.getSubtitle());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClick.onClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    interface OnItemClick{
        public void onClick(int i);
    }
}

