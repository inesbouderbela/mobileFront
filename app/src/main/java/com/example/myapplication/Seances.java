package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.Seance;
import com.example.myapplication.Model.Spectacle;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Seances extends AppCompatActivity {
    private EditText searchEditText;
    private ImageView searchIcon;
    private RecyclerView spectaclesRecycler, searchResultsRecycler;
    private LinearLayout searchView, notFoundView;
    private Button backButton;
    private boolean isInSearchMode = false;
    private List<Seance> originalSeanceList = new ArrayList<>();
    private SeancesAdapter mainAdapter, searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seances);

        // Initialisation des vues
        searchEditText = findViewById(R.id.searchEditText);
        searchIcon = findViewById(R.id.searchIcon);
        spectaclesRecycler = findViewById(R.id.spectaclesByname);
        searchResultsRecycler = findViewById(R.id.searchResultsRecycler);
        searchView = findViewById(R.id.searchView);
        notFoundView = findViewById(R.id.notFoundView);
        backButton = findViewById(R.id.backButton);

        // Récupérer les données initiales
        originalSeanceList = (List<Seance>) getIntent().getSerializableExtra("seances_list");
        if (originalSeanceList == null) {
            originalSeanceList = new ArrayList<>();
        }

        // Configurer les RecyclerView
        setupRecyclerView(spectaclesRecycler, originalSeanceList);
        setupRecyclerView(searchResultsRecycler, new ArrayList<>());

        // Configurer la recherche
        setupSearchFunctionality();

        backButton.setOnClickListener(v -> exitSearchMode());
    }

    private void setupRecyclerView(RecyclerView recyclerView, List<Seance> data) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SeancesAdapter adapter = new SeancesAdapter(data, this);
        recyclerView.setAdapter(adapter);

        if (recyclerView == spectaclesRecycler) {
            mainAdapter = adapter;
        } else {
            searchAdapter = adapter;
        }
    }

    private void setupSearchFunctionality() {
        searchIcon.setOnClickListener(v -> {
            String query = searchEditText.getText().toString().trim();
            if (!query.isEmpty()) {
                performServerSearch(query);
            } else {
                enterSearchMode();
            }
        });

        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = searchEditText.getText().toString().trim();
                if (!query.isEmpty()) {
                    performServerSearch(query);
                }
                return true;
            }
            return false;
        });

        searchEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && !isInSearchMode) {
                enterSearchMode();
            }
        });
    }

    private void enterSearchMode() {
        isInSearchMode = true;
        searchView.setVisibility(View.VISIBLE);
        searchEditText.requestFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT);
    }

    private void exitSearchMode() {
        isInSearchMode = false;
        searchView.setVisibility(View.GONE);
        searchEditText.clearFocus();
        notFoundView.setVisibility(View.GONE);

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
    }

    private void performServerSearch(String query) {
        // Afficher un indicateur de chargement
        notFoundView.setVisibility(View.GONE);
        searchResultsRecycler.setVisibility(View.GONE);
        // Ici vous pourriez ajouter une ProgressBar

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<List<Seance>> call = apiService.searchSeancesByTitle(query);

        call.enqueue(new Callback<List<Seance>>() {
            @Override
            public void onResponse(Call<List<Seance>> call, Response<List<Seance>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Seance> results = response.body();
                    updateSearchResults(results);
                } else {
                    showSearchError();
                }
            }

            @Override
            public void onFailure(Call<List<Seance>> call, Throwable t) {
                showSearchError();
            }
        });
        // Masquer le clavier
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
    }

    private void updateSearchResults(List<Seance> results) {
        if (results == null || results.isEmpty()) {
            searchResultsRecycler.setVisibility(View.GONE);
            notFoundView.setVisibility(View.VISIBLE);
        } else {
            notFoundView.setVisibility(View.GONE);
            searchResultsRecycler.setVisibility(View.VISIBLE);
            searchAdapter.updateList(results);
        }
    }

    private void showSearchError() {
        searchResultsRecycler.setVisibility(View.GONE);
        notFoundView.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Erreur lors de la recherche", Toast.LENGTH_SHORT).show();
    }
}