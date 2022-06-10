package com.example.kgcai_admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List <String> cat_list;

    public CategoryAdapter(List<String> cat_list) {
        this.cat_list = cat_list;
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cat_item_layout,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        String title = cat_list.get(position);
        holder.setData(title);
    }

    @Override
    public int getItemCount() {
        return cat_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView catName;
        private ImageView btnDelete,btnEdit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            catName = itemView.findViewById(R.id.tvSubjName);
            btnDelete = itemView.findViewById(R.id.btnSubjDelete);
            btnEdit = itemView.findViewById(R.id.btnSubjEdit);
        }

        private void setData(String title){
            catName.setText(title);

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
}
