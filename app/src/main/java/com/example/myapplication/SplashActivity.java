package com.example.myapplication;



import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_splash);

        VideoView videoView = findViewById(R.id.splashVideo);
        Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splash1);
        videoView.setVideoURI(video);

        videoView.setOnCompletionListener(mp -> {

            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        });

        videoView.start();
    }
}
