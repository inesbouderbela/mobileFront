package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.Model.Spectacle_Detail;
import com.example.myapplication.Model.TopSpectacle;
import com.example.myapplication.Model.categorie;
import com.example.myapplication.SliderAdapter; // Assuming SliderAdapter is in a separate package
import com.example.myapplication.Model.Spectacle; // Assuming Spectacle class is in a 'models' package
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager2;
    private ProgressBar progressBarSlider;
    private ProgressBar progressBarUpcoming;
    private ProgressBar progressBarTopMovie;
    private RecyclerView recyclerViewTopMovie;
    private RecyclerView RecycleViewUpcoming;
    private RecyclerView CatgoryListView;



    private SliderAdapter sliderAdapter;
    private Handler sliderHandler = new Handler();
    private Runnable sliderRunnable;
    ChipNavigationBar bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager2 = findViewById(R.id.viewPager2);
        progressBarSlider = findViewById(R.id.progressBarSlider);
        progressBarTopMovie = findViewById(R.id.progressBarTopMovie);
        recyclerViewTopMovie=findViewById(R.id.recyclerViewTopMovie);
        RecycleViewUpcoming = findViewById(R.id.RecycleViewUpcoming);
        progressBarUpcoming=findViewById(R.id.progressBarUpcoming);
        CatgoryListView=findViewById(R.id.CatgoryListView);
        fetchSpectacles();
        initTopMoving();
        initSpecacles();
        initCategorie();
        bottomNav = findViewById(R.id.bottom_nav);

        bottomNav.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {
                if (id == R.id.explorer) {
                    // action explorer
                } else if (id == R.id.favorite) {
                    // action favorite
                } else if (id == R.id.cart) {
                    // action cart
                } else if (id == R.id.profile) {
                    /*Intent intent = new Intent(MainActivity.this, profileactivity.class);
                    startActivity(intent);*/
                }
            }
        });



        sliderRunnable = () -> viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
    }
    private void initCategorie() {
        List<categorie> categories = new ArrayList<>();
        categories.add(new categorie("ALL", R.drawable.all));
        categories.add(new categorie("Théâtre", R.drawable.theater));
        categories.add(new categorie("Musique", R.drawable.music));
        categories.add(new categorie("Danse", R.drawable.dancing));

        CategorieSpectacleAdapter castAdapter = new CategorieSpectacleAdapter(categories, this);
        CatgoryListView.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false));
        CatgoryListView.setAdapter(castAdapter);


        castAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                categorie selected = castAdapter.getSelectedItem();
                if (selected != null) {

                }
            }
        });
    }
    private void initSpecacles() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        apiService.getAllSpectacles().enqueue(new Callback<List<TopSpectacle>>() {
            @Override
            public void onResponse(@NonNull Call<List<TopSpectacle>> call, @NonNull Response<List<TopSpectacle>> response) {
                progressBarUpcoming.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    List<TopSpectacle> topSpectacles = response.body();
                    Log.d("MainActivity", "Response code: " + response.code());

                    if (topSpectacles != null && !topSpectacles.isEmpty()) {
                        Log.d("MainActivity", "Top Spectacles received: " + topSpectacles.size());

                        RecycleViewUpcoming.setLayoutManager(
                                new LinearLayoutManager(MainActivity.this,
                                        LinearLayoutManager.HORIZONTAL, false));

                        RecycleViewUpcoming.setAdapter(new SpectacleListAdapter(
                                new ArrayList<>(topSpectacles)));
                    } else {
                        Log.d("MainActivity", "Empty top spectacles list");
                        Toast.makeText(MainActivity.this,
                                "Aucun spectacle disponible", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ?
                                response.errorBody().string() : "null";
                        Log.e("MainActivity", "Error: " + response.code() + " - " + errorBody);
                        Toast.makeText(MainActivity.this,
                                "Erreur serveur: " + response.code(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<TopSpectacle>> call, @NonNull Throwable t) {
                progressBarUpcoming.setVisibility(View.GONE);
                Log.e("MainActivity", "API call failed", t);
                Toast.makeText(MainActivity.this,
                        "Erreur réseau: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void initTopMoving() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        apiService.getTopSpectacles().enqueue(new Callback<List<TopSpectacle>>() {
            @Override
            public void onResponse(@NonNull Call<List<TopSpectacle>> call, @NonNull Response<List<TopSpectacle>> response) {
                progressBarTopMovie.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    List<TopSpectacle> topSpectacles = response.body();
                    Log.d("MainActivity", "Response code: " + response.code());

                    if (topSpectacles != null && !topSpectacles.isEmpty()) {
                        Log.d("MainActivity", "Top Spectacles received: " + topSpectacles.size());

                        recyclerViewTopMovie.setLayoutManager(
                                new LinearLayoutManager(MainActivity.this,
                                        LinearLayoutManager.HORIZONTAL, false));

                        recyclerViewTopMovie.setAdapter(new SpectacleListAdapter(
                                new ArrayList<>(topSpectacles)));
                    } else {
                        Log.d("MainActivity", "Empty top spectacles list");
                        Toast.makeText(MainActivity.this,
                                "Aucun spectacle disponible", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ?
                                response.errorBody().string() : "null";
                        Log.e("MainActivity", "Error: " + response.code() + " - " + errorBody);
                        Toast.makeText(MainActivity.this,
                                "Erreur serveur: " + response.code(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<TopSpectacle>> call, @NonNull Throwable t) {
                progressBarTopMovie.setVisibility(View.GONE);
                Log.e("MainActivity", "API call failed", t);
                Toast.makeText(MainActivity.this,
                        "Erreur réseau: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void fetchSpectacles() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        apiService.getSpectacles().enqueue(new Callback<List<Spectacle>>() {
            @Override
            public void onResponse(Call<List<Spectacle>> call, Response<List<Spectacle>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Spectacle> spectacles = response.body();

                    setupSlider(spectacles);
                    progressBarSlider.setVisibility(View.GONE);
                } else {
                    Toast.makeText(MainActivity.this, "Erreur de récupération", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Spectacle>> call, Throwable t) {


                Toast.makeText(MainActivity.this, "Échec : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSlider(List<Spectacle> spectacles) {
        sliderAdapter = new SliderAdapter(this, spectacles, viewPager2);
        viewPager2.setAdapter(sliderAdapter);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(40));
        transformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });

        viewPager2.setPageTransformer(transformer);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 3000); // slide every 3 sec
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 3000);
    }
}