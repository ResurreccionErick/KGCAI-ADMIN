package com.example.kgcai_admin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kgcai_admin.R;
import com.example.kgcai_admin.ScoreDataModel;

import java.util.List;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ScoreViewAdapter> {

    List<ScoreDataModel> list;
    Context context;

    //for rank
    int i = 1;

    public ScoreAdapter(List<ScoreDataModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ScoreViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.score_item_list,parent,false);
        return new ScoreViewAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreViewAdapter holder, int position) {
        //get it from ScoreDataModel and set data's in score_item_list
        ScoreDataModel scoreDataModel = list.get(position);

        holder.name.setText(scoreDataModel.getName());
        holder.score.setText("Score: "+String.valueOf(scoreDataModel.getScore()));
        holder.rank.setText(String.valueOf(i));
        Glide.with(context).load(scoreDataModel.getImage()).into(holder.scoreImg);

        i++; //increment the value of i
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ScoreViewAdapter extends RecyclerView.ViewHolder {

        ImageView scoreImg;
        TextView name, score, rank;

        public ScoreViewAdapter(@NonNull View itemView) {
            super(itemView);

            scoreImg = itemView.findViewById(R.id.imgScore);
            name = itemView.findViewById(R.id.scoreUserName);
            score = itemView.findViewById(R.id.scoreUserScore);
            rank = itemView.findViewById(R.id.scoreRank);
        }
    }

}
