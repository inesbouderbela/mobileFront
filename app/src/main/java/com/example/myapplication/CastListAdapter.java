package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Model.Acteur;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

public class CastListAdapter extends RecyclerView.Adapter<CastListAdapter.ViewHolder> {

    private List<Acteur> castList;
    private Context context;

    public CastListAdapter(List<Acteur> castList, Context context) {
        this.castList = castList;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView actorImg;
        TextView nameTxt;

        public ViewHolder(View itemView) {
            super(itemView);
            actorImg = itemView.findViewById(R.id.actorimg);
            nameTxt = itemView.findViewById(R.id.nameTxt);
        }

        public void bind(Acteur cast, Context context) {
            String fullUrl = Constants.BASE_URL + cast.getPhoto();
            Glide.with(context)
                    .load(fullUrl)
                    .into(actorImg);
            nameTxt.setText(cast.getNom());
        }
    }

    @NonNull
    @Override
    public CastListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_cast, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CastListAdapter.ViewHolder holder, int position) {
        holder.bind(castList.get(position), context);
    }

    @Override
    public int getItemCount() {
        return castList.size();
    }
}
