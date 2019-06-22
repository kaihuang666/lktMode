package com.kai.lktMode;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListGameAdapter extends RecyclerView.Adapter<ListGameAdapter.ViewHolder> {
    private OnItemClick onItemClick=null;
    private OnItemRemoveClick removeClick=null;
    private List<Item> items;
    private Context context;
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        ImageView icon;
        Button remove;
        CheckBox checkBox;
        public ViewHolder(View v){
            super(v);
            name=v.findViewById(R.id.gameName);
            icon=v.findViewById(R.id.gameIcon);
            remove=v.findViewById(R.id.remove);
            checkBox=v.findViewById(R.id.checkbox);
        }
    }
    public ListGameAdapter(Context context,List<Item> items){
        this.items=items;
        this.context=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_game,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }
    public void setOnItemClick(OnItemClick click){
        onItemClick=click;
    }

    public void setRemoveClick(OnItemRemoveClick removeClick) {
        this.removeClick = removeClick;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        if (position==getItemCount()-1){
            holder.name.setText("添加游戏加速应用，仅限5个");
            holder.icon.setImageResource(R.mipmap.add_game);
            holder.remove.setVisibility(View.INVISIBLE);
            holder.itemView.setOnClickListener(null);
        }else {
            final Item item=items.get(position);
            holder.name.setText(AppUtils.getAppName(context, item.getTitle()));
            holder.icon.setImageBitmap(AppUtils.getBitmap(context, item.getTitle()));
            holder.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    notifyItemRemoved(position);
                    items.remove(position);
                    notifyItemRangeChanged(position, items.size() - position);

                }
            });
            if(item.getChecked()){
                holder.checkBox.setVisibility(View.VISIBLE);
                holder.remove.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return items.size()+1;
    }
    interface OnItemClick{
        public void onClick(int i);
    }
    interface OnItemRemoveClick{
        public void onRemoveClick(int i);
    }
}

