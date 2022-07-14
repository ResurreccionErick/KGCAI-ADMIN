package com.example.kgcai_admin;

import static java.lang.Thread.sleep;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


public class SplashActivity extends AppCompatActivity {

    private ImageView imgLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        imgLogo = findViewById(R.id.imgLogo);

        Animation anim = AnimationUtils.loadAnimation(this, R.anim.logoanim);

        imgLogo.setAnimation(anim);


        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    sleep(1500); //sleep 1.5 second to load data and open login activity
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();

                }catch (InterruptedException e){
                    e.printStackTrace();
                }

            }
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { //this is for back button
        if (item.getItemId() == android.R.id.home) {
            SplashActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

}