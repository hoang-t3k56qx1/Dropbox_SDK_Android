package com.hoangt3k56.dropbox.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hoangt3k56.dropbox.listener.ListenerInt;
import com.hoangt3k56.dropbox.R;
import com.hoangt3k56.dropbox.model.ItemNav;

import java.util.List;

public class NavAdapter extends RecyclerView.Adapter<NavAdapter.ViewHolder> {

    List<ItemNav> itemNavList;
    ListenerInt listenerInt;

    public NavAdapter(List<ItemNav> itemNavList, ListenerInt listenerInt) {
        this.itemNavList = itemNavList;
        this.listenerInt = listenerInt;


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.layout_nav_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("AAA",itemNavList.get(position).getName());
        holder.textView.setText(itemNavList.get(position).getName());
        holder.imageView.setImageResource(itemNavList.get(position).getSrc());
    }

    @Override
    public int getItemCount() {
        return itemNavList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView imageView;
        TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.imgCustom);
            textView=itemView.findViewById(R.id.tvCustom);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listenerInt.listener(getAdapterPosition());
                }
            });
        }
    }
}
