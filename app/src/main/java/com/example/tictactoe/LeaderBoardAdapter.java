package com.example.tictactoe;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class LeaderBoardAdapter extends RecyclerView.Adapter<LeaderBoardAdapter.ViewHolder> {

    private ArrayList<PlayerStatictics> playerList = new ArrayList<PlayerStatictics>();
    private Context mContext;

    public LeaderBoardAdapter(ArrayList playerList, Context mContext) {
        this.playerList = playerList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.usernameTextView.setText(playerList.get(position).getUsername());
        holder.winTextView.setText(String.valueOf(playerList.get(position).getWinCount()));
        holder.lossTextView.setText(String.valueOf(playerList.get(position).getLossCount()));
        holder.drawTextView.setText(String.valueOf(playerList.get(position).getDrawCount()));
        float a, b, c, d;
        a = Float.valueOf(String.valueOf(playerList.get(position).getWinCount()));
        b = Float.valueOf(String.valueOf(playerList.get(position).getLossCount()));
        c = Float.valueOf(String.valueOf(playerList.get(position).getDrawCount()));
        d = 100*((a-b)/(a+b+c));
        d = d - (d%(0.01f));
        if(d<0){
            d=0;
        }
        holder.scoreTextView.setText(String.valueOf(d));
    }

    @Override
    public int getItemCount() {
        return playerList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView usernameTextView ;
        TextView winTextView ;
        TextView lossTextView ;
        TextView drawTextView ;
        TextView scoreTextView ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.playerUsername);
            winTextView = itemView.findViewById(R.id.winNumber);
            lossTextView = itemView.findViewById(R.id.lossNumber);
            drawTextView = itemView.findViewById(R.id.drawNumber);
            scoreTextView = itemView.findViewById(R.id.tScore);
        }
    }
}
