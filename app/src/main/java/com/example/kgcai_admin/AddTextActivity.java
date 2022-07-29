package com.example.kgcai_admin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AddTextActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private ImageView textImgView;
    private EditText txtTitle;
    private Button btnUploadVid;
    private FloatingActionButton btnPickImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_text);

        txtTitle = findViewById(R.id.txtVideoTitle);
        textImgView = findViewById(R.id.textImgView);
        btnUploadVid = findViewById(R.id.btnUploadVid);
        btnPickImg = findViewById(R.id.btnPickImg);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setMessage("Uploading...");
        progressDialog.setCanceledOnTouchOutside(false);

    }
}