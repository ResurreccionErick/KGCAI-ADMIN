package com.example.kgcai_admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SetsAdapter extends RecyclerView.Adapter<SetsAdapter.ViewHolder> {

    private List<String> setIds;

    public SetsAdapter(List<String> setIds) {
        this.setIds = setIds;
    }

    @NonNull
    @Override
    public SetsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.set_item_layout,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SetsAdapter.ViewHolder holder, int position) {
        holder.setData(position); //pass the position into setData method
    }

    @Override
    public int getItemCount() {
        return setIds.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView setName;
        private ImageView deleteSet;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            setName = itemView.findViewById(R.id.tvSetsName);
            deleteSet = itemView.findViewById(R.id.btnSetsDelete);


        }

        public void setData(int position) {
            //setName.setText("SET " + String.valueOf(position + 1));
            setName.setText("SET " + String.valueOf(position + 1));
        }
    }
}
