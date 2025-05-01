package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.DateWithCount;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.DateViewHolder> {
    private List<DateWithCount> dates;
    private Integer selectedPosition = null;
    private OnDateClickListener listener;

    public interface OnDateClickListener {
        void onDateClick(DateWithCount date, int position, boolean isSelected);
    }

    public DateAdapter(List<DateWithCount> dates, OnDateClickListener listener) {
        this.dates = dates;
        this.listener = listener;

    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_date, parent, false);
        return new DateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
        DateWithCount date = dates.get(position);

        String formattedDate = formatDate(date.getDate());
        holder.dateText.setText(formattedDate);
        holder.countText.setText(String.valueOf(date.getEventCount()));

        // Mise à jour de l'apparence selon la sélection
        boolean isSelected = selectedPosition != null && selectedPosition == position;
        holder.topLayout.setBackgroundResource(
                isSelected ? R.drawable.rounded_top_selected : R.drawable.rounded_top_corners
        );

        holder.itemView.setOnClickListener(v -> {
            int clickedPosition = holder.getAdapterPosition();
            if (clickedPosition != RecyclerView.NO_POSITION) {
                // Toggle selection
                Integer newSelectedPosition = clickedPosition;
                if (selectedPosition != null && selectedPosition == clickedPosition) {
                    newSelectedPosition = null; // Désélection si on clique sur le même item
                }

                Integer previousSelected = selectedPosition;
                selectedPosition = newSelectedPosition;

                // Notifier les changements
                if (previousSelected != null) {
                    notifyItemChanged(previousSelected);
                }
                if (selectedPosition != null) {
                    notifyItemChanged(selectedPosition);
                }

                if (listener != null) {
                    listener.onDateClick(date, clickedPosition, selectedPosition != null);
                }
            }
        });
    }

    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());
            Date date = inputFormat.parse(dateStr);
            return outputFormat.format(date);
        } catch (Exception e) {
            return dateStr;
        }
    }

    @Override
    public int getItemCount() {
        return dates != null ? dates.size() : 0;
    }

    public void updateData(List<DateWithCount> newDates) {
        this.dates = newDates;
        // Réinitialiser la sélection
        if (!newDates.isEmpty()) {
            selectedPosition = 0;
        } else {
            selectedPosition = -1;
        }
        notifyDataSetChanged();
    }

    static class DateViewHolder extends RecyclerView.ViewHolder {
        LinearLayout topLayout;
        TextView dateText, countText;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            topLayout = itemView.findViewById(R.id.topLayout);
            dateText = itemView.findViewById(R.id.dateText);
            countText = itemView.findViewById(R.id.countText);
        }
    }
}