package com.example.myapplication;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import com.example.myapplication.ApiClient;
import com.example.myapplication.ApiService;
import com.example.myapplication.CastListAdapter;
import com.example.myapplication.Constants;
import com.example.myapplication.Model.Seance;
import com.example.myapplication.R;
import com.google.android.material.imageview.ShapeableImageView;
import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private ImageView filmPic;
    private ImageView localisation;
    private TextView titleTxt, timeTxt, dateTxt, descriptionTxt, movieSummeryTxt;
    private RecyclerView castListView, genreView;
    private BlurView blurView;
    private Long seanceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Permet d'étendre le layout jusqu'aux bords
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_detail);

        initViews();
        setupBlur();
        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.bringToFront();

        seanceId = getIntent().getLongExtra("seanceId", 0);

        if (seanceId != 0) {
            fetchSeanceDetails(seanceId);
        } else {
            Toast.makeText(this, "ID séance invalide", Toast.LENGTH_SHORT).show();
        }
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Créer un Intent pour revenir à l'activité principale (MainActivity)
                Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        localisation = findViewById(R.id.localisation);
        localisation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, Maps.class); // lancer l'activité Maps
                startActivity(intent);
            }
        });

    }
    private void initViews() {
        filmPic = findViewById(R.id.filmpic);
        titleTxt = findViewById(R.id.titleTxt);
        timeTxt = findViewById(R.id.timeTxt);
        dateTxt = findViewById(R.id.date);
        descriptionTxt = findViewById(R.id.description);
        movieSummeryTxt = findViewById(R.id.movieSummeryTxt);
        castListView = findViewById(R.id.castListView);
        genreView = findViewById(R.id.genreView);
        blurView = findViewById(R.id.blurView);
    }

    private void setupBlur() {
        View decorView = getWindow().getDecorView();
        ViewGroup rootView = decorView.findViewById(android.R.id.content);
        Drawable windowBackground = decorView.getBackground();

        blurView.setupWith(rootView, new RenderScriptBlur(this))
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(10f);

        blurView.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        blurView.setClipToOutline(true);
    }

    private void fetchSeanceDetails(Long id) {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        Call<Seance> call = apiService.getSeanceDetails(id);

        call.enqueue(new Callback<Seance>() {
            @Override
            public void onResponse(Call<Seance> call, Response<Seance> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Seance dto = response.body();
                    updateUI(dto);
                } else {
                    try {
                        if (response.errorBody() != null) {
                            System.out.println(response.errorBody().string());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(DetailActivity.this, "Erreur de chargement (code " + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Seance> call, Throwable t) {
                Toast.makeText(DetailActivity.this, "Erreur: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(Seance dto) {
        titleTxt.setText(dto.getTitle());
        timeTxt.setText(dto.getHeureDebut() + " - " + dto.getHeureFin());
        dateTxt.setText(dto.getDateSeance());
        descriptionTxt.setText("Description");
        movieSummeryTxt.setText(dto.getDescription());

        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transform(new CenterCrop(), new GranularRoundedCorners(0, 0, 50, 0));

        String fullUrl = Constants.BASE_URL + dto.getSecondaryImage();
        Glide.with(this)
                .load(fullUrl)
                .apply(requestOptions)
                .into(filmPic);

        // Ajout des acteurs
        if (dto.getActeurs() != null && !dto.getActeurs().isEmpty()) {
            CastListAdapter castAdapter = new CastListAdapter(dto.getActeurs(), this);
            castListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            castListView.setAdapter(castAdapter);
        }

        if (dto.getMotsCles() != null && !dto.getMotsCles().isEmpty()) {

            String[] motsArray = dto.getMotsCles().split("\\|");
            List<String> motsList = Arrays.asList(motsArray);
            CategoryAdapter adapter = new CategoryAdapter(motsList, this);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            genreView.setLayoutManager(layoutManager);
            genreView.setAdapter(adapter);
        }
    }
}
