package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.Seat;

import java.util.ArrayList;
import java.util.List;

public class SeatListAdapter extends RecyclerView.Adapter<SeatListAdapter.SeatViewHolder> {

    private List<Seat> seatList;

    private Context context;
    private List<String> selectedSeatNames = new ArrayList<>();
    private SelectedSeat callback;

    public interface SelectedSeat {
        void onSeatSelected(String selectedNames, int count);
    }

    public SeatListAdapter(List<Seat> seatList, Context context, SelectedSeat callback) {
        this.seatList = seatList;
        this.context = context;
        this.callback = callback;
    }

    @NonNull
    @Override
    public SeatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.seat_item, parent, false);
        return new SeatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeatViewHolder holder, int position) {
        Seat seat = seatList.get(position);
        holder.seatText.setText(seat.getName());

        switch (seat.getStatus()) {
            case AVAILABLE:
                holder.seatText.setBackgroundResource(R.drawable.ic_seat_available);
                break;
            case SELECTED:
                holder.seatText.setBackgroundResource(R.drawable.ic_seat_selected);
                break;
            case UNAVAILABLE:
                holder.seatText.setBackgroundResource(R.drawable.ic_seat_unavailable);
                break;
        }

        holder.seatText.setOnClickListener(v -> {
            if (seat.getStatus() == Seat.SeatStatus.AVAILABLE) {
                seat.setStatus(Seat.SeatStatus.SELECTED);
                selectedSeatNames.add(seat.getName());
            } else if (seat.getStatus() == Seat.SeatStatus.SELECTED) {
                seat.setStatus(Seat.SeatStatus.AVAILABLE);
                selectedSeatNames.remove(seat.getName());
            }
            notifyItemChanged(position);
            callback.onSeatSelected(String.join(",", selectedSeatNames), selectedSeatNames.size());
        });
    }

    @Override
    public int getItemCount() {
        return seatList.size();
    }

    static class SeatViewHolder extends RecyclerView.ViewHolder {
        TextView seatText;

        SeatViewHolder(@NonNull View itemView) {
            super(itemView);
            seatText = itemView.findViewById(R.id.seat);
        }
    }
}

