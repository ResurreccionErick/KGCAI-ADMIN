package com.example.kgcai_admin;

import static com.example.kgcai_admin.CategoryActivity.catList;
import static com.example.kgcai_admin.CategoryActivity.selected_cat_index;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.collection.ArrayMap;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kgcai_admin.adapter.SetsAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SetsActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private FloatingActionButton btnAddSets;
    private RecyclerView setsView;
    private Dialog addSetsDialog,loadingDialog;
    private EditText dialogSetsName;
    private Button btnAdd;
    private SetsAdapter adapter;
    private String input;

    public static List<String> setsIDs = new ArrayList<>();
    public static int selected_set_index=0;

    private FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sets);

        toolbar = findViewById(R.id.toolbar_sets);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Sets");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        setsView = findViewById(R.id.setsRecyclerView);
        btnAddSets = findViewById(R.id.btnAddNewSets);

        loadingDialog = new Dialog(SetsActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_bar);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        btnAddSets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addNewSet();
            }
        });

        firestore = FirebaseFirestore.getInstance();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        setsView.setLayoutManager(layoutManager);

        loadSets();

    }


    private void loadSets()
    {
        setsIDs.clear();

        loadingDialog.show();

        firestore.collection("QUIZ").document(catList.get(selected_cat_index).getId())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                long noOfSets = (long)documentSnapshot.get("SETS");

                for(int i=1; i <= noOfSets; i++)
                {
                    setsIDs.add(documentSnapshot.getString("SET" + String.valueOf(i) + "_ID"));
                }

                catList.get(selected_cat_index).setSetCounter(documentSnapshot.getString("COUNTER"));
                catList.get(selected_cat_index).setNoOfSets(String.valueOf(noOfSets));

                adapter = new SetsAdapter(setsIDs);
                setsView.setAdapter(adapter);

                loadingDialog.dismiss();

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SetsActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                    }
                });

    }

    private void addNewSet()
    {
        loadingDialog.show();

        final String curr_cat_id = catList.get(selected_cat_index).getId();
        final String curr_counter = catList.get(selected_cat_index).getSetCounter();

        Map<String,Object> qData = new ArrayMap<>();
        qData.put("COUNT","0");

        firestore.collection("QUIZ").document(curr_cat_id)
                .collection(curr_counter).document("QUESTIONS_LIST")
                .set(qData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Map<String,Object> catDoc = new ArrayMap<>();
                        catDoc.put("COUNTER", String.valueOf(Integer.valueOf(curr_counter) + 1)  );
                        catDoc.put("SET" + String.valueOf(setsIDs.size() + 1) + "_ID", curr_counter);
                        catDoc.put("SETS", setsIDs.size() + 1);

                        firestore.collection("QUIZ").document(curr_cat_id)
                                .update(catDoc)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Toast.makeText(SetsActivity.this, " Set Added Successfully",Toast.LENGTH_SHORT).show();

                                        setsIDs.add(curr_counter);
                                        catList.get(selected_cat_index).setNoOfSets(String.valueOf(setsIDs.size()));
                                        catList.get(selected_cat_index).setSetCounter(String.valueOf(Integer.valueOf(curr_counter) + 1));

                                        adapter.notifyItemInserted(setsIDs.size());
                                        loadingDialog.dismiss();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SetsActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                        loadingDialog.dismiss();
                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SetsActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                    }
                });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { //this is for back button
        if (item.getItemId() == android.R.id.home) {
            SetsActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }


}