package com.example.myapplication;

import android.content.Context;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.myapplication.Model.Seance;

import java.util.ArrayList;
import java.util.List;

public class SeancesAdapter extends RecyclerView.Adapter<SeancesAdapter.SeanceViewHolder> {
    private List<Seance> seanceList;
    private Context context;

    public SeancesAdapter(List<Seance> seanceList, Context context) {
        this.seanceList = seanceList;
        this.context = context;
    }

    @Override
    public SeanceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_spectacle_search, parent, false);
        return new SeanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SeanceViewHolder holder, int position) {
        Seance seance = seanceList.get(position);




        holder.titleTxt.setText(seance.getTitle());
        holder.adressTxt.setText(seance.getNomLieu());
        holder.dateTxt.setText(seance.getDateSeance());
        holder.heurTxt.setText(seance.getDebFin());

        String fullUrl = Constants.BASE_URL + seance.getCoverImage();


        int targetWidth = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                658,
                context.getResources().getDisplayMetrics()
        );
        int targetHeight = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                249,
                context.getResources().getDisplayMetrics()
        );



        Glide.with(context)
                .load(fullUrl)
                .apply(new RequestOptions()
                        .override(targetWidth, targetHeight)
                        .transform(
                                new FitCenter(),  // Affiche TOUTE l'image (zoom arrière)
                                new RoundedCorners(60)
                        )
                        .dontAnimate()
                )
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource,
                                                @Nullable Transition<? super Drawable> transition) {
                        // Applique un scaleType programmatique
                        holder.spectaclePic.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        holder.spectaclePic.setImageDrawable(resource);

                        // Option: Ajouter un fond si l'image est plus petite
                        holder.spectaclePic.setBackgroundColor(Color.WHITE);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        holder.spectaclePic.setImageDrawable(placeholder);
                    }
                });


    }

    @Override
    public int getItemCount() {
        return seanceList.size();
    }

    static class SeanceViewHolder extends RecyclerView.ViewHolder {

        ImageView spectaclePic;
        TextView titleTxt, adressTxt, dateTxt, heurTxt, costTxt;

        public SeanceViewHolder(View itemView) {
            super(itemView);
            spectaclePic = itemView.findViewById(R.id.spectaclepic);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            adressTxt = itemView.findViewById(R.id.adressTxt);
            dateTxt = itemView.findViewById(R.id.dateTxt);
            heurTxt = itemView.findViewById(R.id.heurTxt);
            costTxt = itemView.findViewById(R.id.costTxt);
        }
    }
    public void updateList(List<Seance> newList) {
        seanceList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }
}
