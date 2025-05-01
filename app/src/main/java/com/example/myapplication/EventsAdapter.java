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
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.myapplication.Model.Seance;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {
    private List<Seance> eventList;
    private Context context;
    private OnEventClickListener listener;

    public interface OnEventClickListener {
        void onEventClick(Seance seance);
    }

    public EventsAdapter(List<Seance> eventList, Context context, OnEventClickListener listener) {
        this.eventList = eventList; // PAS new ArrayList<>(eventList)
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_spectacle_search, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Seance seance = eventList.get(position);

        // Set text values
        holder.titleTxt.setText(seance.getTitle());
        holder.adressTxt.setText(seance.getNomLieu());
        holder.dateTxt.setText(formatDate(seance.getDateSeance()));
        holder.heurTxt.setText(seance.getDebFin());
        holder.costTxt.setText(String.format("%s DT","10"));

        // Load image with Glide
        String fullUrl = Constants.BASE_URL + seance.getCoverImage();

        int targetWidth = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                658,
                context.getResources().getDisplayMetrics());

        int targetHeight = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                249,
                context.getResources().getDisplayMetrics());

        Glide.with(context)
                .load(fullUrl)
                .apply(new RequestOptions()
                        .override(targetWidth, targetHeight)
                        .transform(new FitCenter(), new RoundedCorners(60))
                        .dontAnimate())
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource,
                                                @Nullable Transition<? super Drawable> transition) {
                        holder.spectaclePic.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        holder.spectaclePic.setImageDrawable(resource);
                        holder.spectaclePic.setBackgroundColor(Color.WHITE);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        holder.spectaclePic.setImageDrawable(placeholder);
                    }
                });

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEventClick(seance);
            }
        });
    }

    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            Date date = inputFormat.parse(dateStr);
            return outputFormat.format(date);
        } catch (Exception e) {
            return dateStr;
        }
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void updateEvents(List<Seance> newEvents) {
        eventList.clear();
        eventList.addAll(newEvents);
        notifyDataSetChanged();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView spectaclePic;
        TextView titleTxt, adressTxt, dateTxt, heurTxt, costTxt;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            spectaclePic = itemView.findViewById(R.id.spectaclepic);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            adressTxt = itemView.findViewById(R.id.adressTxt);
            dateTxt = itemView.findViewById(R.id.dateTxt);
            heurTxt = itemView.findViewById(R.id.heurTxt);
            costTxt = itemView.findViewById(R.id.costTxt);
        }
    }
}