package com.example.kgcai_admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private FloatingActionButton btnAddSubj;
    private RecyclerView cat_recycler_view;
    public static List<String> catList = new ArrayList<>();
    private FirebaseFirestore firestore;
    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);


        loadingDialog = new Dialog(CategoryActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_bar); //initialize the loading dialog
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();

        firestore = FirebaseFirestore.getInstance();

        btnAddSubj = findViewById(R.id.btnAddNewSubj);
        cat_recycler_view = findViewById(R.id.subjRecyclerView);

        Toolbar toolbar = findViewById(R.id.subjToolbar);
        setSupportActionBar(toolbar);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        cat_recycler_view.setLayoutManager(layoutManager);

        loadData(); //calling the load data method

    }
    private void loadData() {
        loadingDialog.show();

        //catList.clear(); //Clear the arraylist of the subject

        firestore.collection("QUIZ").document("Categories")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if(doc.exists()){
                        long count = (long)doc.get("COUNT"); //get the COUNT of categories in firebase

                        for(int i = 1; i<=count; i++){
                            String catName = doc.getString("CAT"+String.valueOf(i)); //getting the CAT and loop it to get the CAT in firebase

                            catList.add(catName); //add it to the catList
                        }

                        CategoryAdapter adapter = new CategoryAdapter(catList);
                        cat_recycler_view.setAdapter(adapter);

                    }else{
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "No Subject found", Toast.LENGTH_SHORT).show();
                    finish();
                }
                loadingDialog.dismiss();
            }
        });
    }
}