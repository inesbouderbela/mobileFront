package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.Seat;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SeatListActivity extends AppCompatActivity {

    private RecyclerView seatsRecycler;
    private TextView numberSelectedTxt, priceTxt;
    private double pricePerSeat = 10.0; // À remplacer dynamiquement
    private int numberSelected = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_list);

        seatsRecycler = findViewById(R.id.seatsRecycle);
        numberSelectedTxt = findViewById(R.id.numeroSelected);
        priceTxt = findViewById(R.id.PriceTotle);



        initSeatsList();
    }

    private void initSeatsList() {
        List<Seat> seatList = new ArrayList<>();
        for (int i = 0; i < 81; i++) {
            Seat.SeatStatus status = (i == 2 || i == 20 || i == 33 || i == 41 || i == 50 || i == 72 || i == 73)
                    ? Seat.SeatStatus.UNAVAILABLE
                    : Seat.SeatStatus.AVAILABLE;
            seatList.add(new Seat("S" + (i + 1), status));
        }

        GridLayoutManager layoutManager = new GridLayoutManager(this, 7);
        seatsRecycler.setLayoutManager(layoutManager);

        SeatListAdapter adapter = new SeatListAdapter(seatList, this, (selectedNames, count) -> {
            numberSelected = count;
            numberSelectedTxt.setText(count + " Seat(s) Selected");
            double totalPrice = numberSelected * pricePerSeat;
            priceTxt.setText("$" + String.format("%.2f", totalPrice));
        });

        seatsRecycler.setAdapter(adapter);
        seatsRecycler.setNestedScrollingEnabled(false);
    }
}
