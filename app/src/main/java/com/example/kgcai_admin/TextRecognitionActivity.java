package com.example.kgcai_admin;


import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArrayMap;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class TextRecognitionActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference mReference;
    FirebaseStorage firebaseStorage;

    private Button btnAddNewTextRecogDialog;
    private FloatingActionButton btnAddText;
    private EditText txtVideoTitle, txtTextRecogDialog, txtTextAnsDialog;
    private ImageView imgAddTextDialog;

    private static final int Gallery_Code = 1;
    Uri imageUrl = null;
    private Dialog addNewTextDialog;
    ProgressDialog progressDialog;

    String instruction, correctAns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_recognition);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        mReference = firebaseDatabase.getReference().child("TextRecognition");
        progressDialog = new ProgressDialog(this);


        addNewTextDialog = new Dialog(TextRecognitionActivity.this);
        addNewTextDialog.setContentView(R.layout.add_textrecog_dialog); //initialize the loading dialog
        addNewTextDialog.setCancelable(true);
        addNewTextDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        txtTextRecogDialog = addNewTextDialog.findViewById(R.id.txtTextRecogDialog);
        txtTextAnsDialog = addNewTextDialog.findViewById(R.id.txtTextAnsDialog);
        btnAddNewTextRecogDialog = addNewTextDialog.findViewById(R.id.btnAddNewTextRecogDialog);
        imgAddTextDialog = addNewTextDialog.findViewById(R.id.imgAddTextDialog);

        btnAddText = findViewById(R.id.btnAddText);

        btnAddText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewTextDialog.show();
            }
        });


        imgAddTextDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, Gallery_Code);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==Gallery_Code && resultCode==RESULT_OK){
            imageUrl = data.getData();
            imgAddTextDialog.setImageURI(imageUrl);
        }
        btnAddNewTextRecogDialog.setOnClickListener(new View.OnClickListener() { //this is btn on dialog
            @Override
            public void onClick(View v) {
                if (txtTextRecogDialog.getText().toString().isEmpty()) {
                    txtTextRecogDialog.setError("Please Enter Instruction");
                    txtTextRecogDialog.requestFocus();
                    return;
                }else if(txtTextAnsDialog.getText().toString().isEmpty()) {
                    txtTextAnsDialog.setError("Please Enter the Correct Answer");
                    txtTextAnsDialog.requestFocus();
                    return;
                }
                else if(imageUrl==null) {
                    Toast.makeText(getApplicationContext(), "Please pick a picture", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.setTitle("Uploading...");
                    progressDialog.show();
                    instruction = txtTextRecogDialog.getText().toString().trim();
                    correctAns = txtTextAnsDialog.getText().toString().trim();

                    StorageReference storageReference = firebaseStorage.getReference().child("TextRecog").child(imageUrl.getLastPathSegment());
                    storageReference.putFile(imageUrl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> downloadUri = taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    DatabaseReference reference = mReference.push();

                                    reference.child("Instructions").setValue(instruction);
                                    reference.child("CorrectAns").setValue(correctAns);
                                    reference.child("img").setValue(task.getResult().toString());
                                    progressDialog.dismiss();
                                    addNewTextDialog.dismiss();
                                }
                            });
                            txtTextRecogDialog.setText("");
                            txtTextAnsDialog.setText("");
                        }
                    });
                }
            }
        });

    }
}