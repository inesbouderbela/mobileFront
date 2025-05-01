package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.Model.DateWithCount;
import com.example.myapplication.Model.Seance;
import com.example.myapplication.Model.Spectacle_Detail;
import com.example.myapplication.Model.TopSpectacle;
import com.example.myapplication.Model.categorie;
import com.example.myapplication.SliderAdapter; // Assuming SliderAdapter is in a separate package
import com.example.myapplication.Model.Spectacle; // Assuming Spectacle class is in a 'models' package
import com.google.gson.GsonBuilder;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class MainActivity extends AppCompatActivity  implements CategorieSpectacleAdapter.OnCategoryClickListener {

    private ViewPager2 viewPager2;
    private ProgressBar progressBarSlider;
    private boolean isInSearchMode = false;
    private ConstraintLayout eventsContainer;
    private List<Seance> seancesList ;


    private ProgressBar progressBarUpcoming;
    private ProgressBar progressBarTopMovie;
    private RecyclerView recyclerViewTopMovie;
    private RecyclerView RecycleViewUpcoming;
    private RecyclerView CatgoryListView;


    private RecyclerView eventsRecyclerView; // RecyclerView vertical pour les événements
    private ProgressBar progressBar;
    private RecyclerView datesRecyclerView;
    private DateAdapter dateAdapter;
    private EventsAdapter eventsAdapter;

    private List<DateWithCount> datesWithCount ;


    private SliderAdapter sliderAdapter;
    EditText searchEditText;
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
        recyclerViewTopMovie = findViewById(R.id.recyclerViewTopMovie);
        RecycleViewUpcoming = findViewById(R.id.RecycleViewUpcoming);
        progressBarUpcoming = findViewById(R.id.progressBarUpcoming);
        CatgoryListView = findViewById(R.id.CatgoryListView);
        fetchSpectacles();
        initTopMoving("ALL");
        initSpecacles("ALL");
        initCategorie();
        bottomNav = findViewById(R.id.bottom_nav);
        Log.d("MAIN_DEBUG bonjour", "Données reçues pour le slider: ");

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

        ScrollView normalView = findViewById(R.id.scrollView2);
        LinearLayout searchView = findViewById(R.id.searchView);
        RecyclerView searchResultsRecycler = findViewById(R.id.searchResultsRecycler);
        LinearLayout notFoundView = findViewById(R.id.notFoundView);
         searchEditText = findViewById(R.id.searchEditText);
        Button backButton = findViewById(R.id.backButton);
        ImageView searchIcon = findViewById(R.id.searchIcon);// ID de l'ImageView ajoutée dans le XML
        searchIcon.setOnClickListener(v -> {
            String query = searchEditText.getText().toString().trim();
            performSearch(query, searchResultsRecycler, notFoundView);
        });

        setupSearchFunctionality(searchEditText, searchIcon, normalView, searchView,
                searchResultsRecycler, notFoundView, backButton);

        sliderRunnable = () -> viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);

        datesWithCount = new ArrayList<>();
        seancesList = new ArrayList<>();
        eventsContainer = findViewById(R.id.eventsContainer);
        eventsContainer.setVisibility(View.GONE);



        datesRecyclerView = findViewById(R.id.DateListView);
        progressBar = findViewById(R.id.progressBarDate);
        datesRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        dateAdapter = new DateAdapter(datesWithCount, (date, position, isSelected) -> {
            if (isSelected) {
                loadEventsForDate(date.getDate());
                eventsContainer.setVisibility(View.VISIBLE);
            } else {
                eventsContainer.setVisibility(View.GONE);
            }
        });
        datesRecyclerView.setAdapter(dateAdapter);


        RecyclerView eventsRecyclerView = findViewById(R.id.RecycleViewDate);
        eventsRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        eventsAdapter = new EventsAdapter(seancesList, this, seance -> {

        });
        eventsRecyclerView.setAdapter(eventsAdapter);


        loadDatesWithCount();
    }

    private void loadDatesWithCount() {
        progressBar.setVisibility(View.VISIBLE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<List<DateWithCount>> call = apiService.getDatesWithEventCount();

        call.enqueue(new Callback<List<DateWithCount>>() {
            @Override
            public void onResponse(Call<List<DateWithCount>> call, Response<List<DateWithCount>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    datesWithCount = response.body();
                    dateAdapter.updateData(datesWithCount);
                }
            }

            @Override
            public void onFailure(Call<List<DateWithCount>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Failed to load dates", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadEventsForDate(String date) {
        progressBar.setVisibility(View.VISIBLE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<List<Seance>> call = apiService.getSeancesByDate(date);

        call.enqueue(new Callback<List<Seance>>() {
            @Override
            public void onResponse(Call<List<Seance>> call, Response<List<Seance>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("SEANCES", "Received " + response.body().size() + " seances");
                    seancesList.clear();
                    seancesList.addAll(response.body());
                    eventsAdapter.notifyDataSetChanged();
                } else {
                    Log.e("SEANCES", "Response is empty or not successful");
                }
            }

            @Override
            public void onFailure(Call<List<Seance>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Failed to load events", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void setupSearchFunctionality(EditText searchEditText, ImageView searchIcon,
                                          ScrollView normalView, LinearLayout searchView,
                                          RecyclerView searchResultsRecycler, LinearLayout notFoundView,
                                          Button backButton) {

        // Gestion du clic sur l'icône de recherche
        searchIcon.setOnClickListener(v -> {
            String query = searchEditText.getText().toString().trim();
            if (!query.isEmpty()) {
                enterSearchMode(normalView, searchView);
                performSearch(query, searchResultsRecycler, notFoundView);
            }
        });

        // Gestion du focus
        searchEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && !isInSearchMode) {
                enterSearchMode(normalView, searchView);
            }
        });

        // Gestion de la recherche avec le clavier
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = searchEditText.getText().toString().trim();
                if (!query.isEmpty()) {
                    performSearch(query, searchResultsRecycler, notFoundView);
                }
                return true;
            }
            return false;
        });

        // Bouton retour
        backButton.setOnClickListener(v -> exitSearchMode(normalView, searchView, searchEditText));
    }

    private void enterSearchMode(ScrollView normalView, LinearLayout searchView) {
        isInSearchMode = true;
        normalView.setVisibility(View.GONE);
        searchView.setVisibility(View.VISIBLE);

        // Masquer le clavier si nécessaire
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
    }

    private void exitSearchMode(ScrollView normalView, LinearLayout searchView, EditText searchEditText) {
        isInSearchMode = false;
        normalView.setVisibility(View.VISIBLE);
        searchView.setVisibility(View.GONE);
        searchEditText.clearFocus();
    }

    private void performSearch(String query, RecyclerView searchResultsRecycler, LinearLayout notFoundView) {
        // Masquer le clavier
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);

        searchSeancesBySpectacleName(query, searchResultsRecycler, notFoundView);
    }

    private void searchSeancesBySpectacleName(String spectacleName, RecyclerView recyclerView, LinearLayout notFoundView) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<List<Seance>> call = apiService.searchSeancesByTitle(spectacleName);

        call.enqueue(new Callback<List<Seance>>() {
            @Override
            public void onResponse(Call<List<Seance>> call, Response<List<Seance>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Seance> seances = response.body();

                    if (seances != null && !seances.isEmpty()) {

                        notFoundView.setVisibility(View.GONE);


                        ArrayList<Seance> serializableList = new ArrayList<>(seances);

                        Intent intent = new Intent(MainActivity.this, Seances.class);
                        intent.putExtra("spectacle_name", spectacleName);
                        intent.putExtra("seances_list", serializableList); // Pas besoin de cast
                        startActivity(intent);
                    } else {
                        // Aucun résultat trouvé
                        recyclerView.setVisibility(View.GONE);
                        notFoundView.setVisibility(View.VISIBLE);
                    }
                } else {
                    // Gérer l'erreur de réponse
                    recyclerView.setVisibility(View.GONE);
                    notFoundView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<Seance>> call, Throwable t) {

                recyclerView.setVisibility(View.GONE);
                notFoundView.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, "Erreur de connexion", Toast.LENGTH_SHORT).show();
            }
        });



    }



    private void initCategorie() {
        List<categorie> categories = new ArrayList<>();
        categories.add(new categorie("ALL", R.drawable.all));
        categories.add(new categorie("THEATRE", R.drawable.theater));
        categories.add(new categorie("MUSIQUE", R.drawable.music));
        categories.add(new categorie("DANSE", R.drawable.dancing));

        CategorieSpectacleAdapter castAdapter = new CategorieSpectacleAdapter(
                categories, this, this);
        CatgoryListView.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false));
        CatgoryListView.setAdapter(castAdapter);


    }

    @Override
    public void onCategoryClick(String categoryName) {

        initSpecacles(categoryName);
        initTopMoving(categoryName);

    }

    private void initSpecacles(String category) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<List<TopSpectacle>> call;
        if (category.equals("ALL")) {
            call = apiService.getAllSpectacles();
        } else {

            call = apiService.getSpectaclesByCategory(category);
        }

        call.enqueue(new Callback<List<TopSpectacle>>() {
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

    private void initTopMoving(String category) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<List<TopSpectacle>> call;
        if (category.equals("ALL")) {
            call = apiService.getTopSpectacles();
        } else {

            call = apiService.getTopSpectaclesByCategory(category);
        }

        call.enqueue(new Callback<List<TopSpectacle>>() {
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
        Log.d("MAIN_DEBUG", "Données reçues pour le slider: " + spectacles.size());
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