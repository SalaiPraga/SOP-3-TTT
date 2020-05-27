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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.playerUsername);
            winTextView = itemView.findViewById(R.id.winNumber);
            lossTextView = itemView.findViewById(R.id.lossNumber);
            drawTextView = itemView.findViewById(R.id.drawNumber);
        }
    }









//    public LeaderBoardAdapter(Context context, int resource, List<PlayerStatictics> objects) {
//        super(context, resource, objects);
//    }
//
//    @NonNull
//    @Override
//    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        if (convertView == null) {
//            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_player, parent, false);
//        }
//
//        TextView usernameTextView = convertView.findViewById(R.id.playerUsername);
//        TextView winTextView = convertView.findViewById(R.id.winNumber);
//        TextView lossTextView = convertView.findViewById(R.id.lossNumber);
//        TextView drawTextView = convertView.findViewById(R.id.drawNumber);
//
//        PlayerStatictics statictics = getItem(position);
//        usernameTextView.setText(statictics.getUsername());
//        winTextView.setText(statictics.getWinCount());
//        lossTextView.setText(statictics.getLossCount());
//        drawTextView.setText(statictics.getDrawCount());
//
//        return convertView;
//    }
}
