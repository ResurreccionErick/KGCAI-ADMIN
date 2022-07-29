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
import com.example.kgcai_admin.helper.SetsModelClass;
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
    private SetsAdapter adapter;

    private EditText setName;
    private Button btnAdd;
    String txtSetName;

    public static List<SetsModelClass> setsList = new ArrayList<SetsModelClass>();
    public static List<String> setsIDs = new ArrayList<>();
    public static int selected_set_index=0;

    private FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sets);

        toolbar = findViewById(R.id.toolbar_sets);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Quiz Sets");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        setsView = findViewById(R.id.setsRecyclerView);
        btnAddSets = findViewById(R.id.btnAddNewSets);

        loadingDialog = new Dialog(SetsActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_bar);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        addSetsDialog = new Dialog(SetsActivity.this);
        addSetsDialog.setContentView(R.layout.add_sets_dialog);
        addSetsDialog.setCancelable(true);
        addSetsDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        setName = addSetsDialog.findViewById(R.id.txtAddSetsName);
        btnAdd = addSetsDialog.findViewById(R.id.btnAddSetsDialog);


        btnAddSets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSetsDialog.show();

                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        txtSetName = setName.getText().toString().trim(); //from add new set dialog

                        if(txtSetName.isEmpty()){
                            Toast.makeText(getApplicationContext(), "Please enter the set name.", Toast.LENGTH_SHORT).show();
                        }else{
                            addNewSet(txtSetName);
                            addSetsDialog.dismiss();
                        }
                    }
                });
            }
        });

        firestore = FirebaseFirestore.getInstance();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        setsView.setLayoutManager(layoutManager);

       loadSets();

    }

    private void loadSets() {
        setsList.clear(); //Clear the arraylist of the sets

        firestore.collection("QUIZ").document(catList.get(selected_cat_index).getId())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot doc) {
                if (doc.exists()) {
                    long noOfSets = (long) doc.get("SETS");

                    for (int i = 1; i <= noOfSets; i++) {
                        String setName = doc.getString("SET" + String.valueOf(i) + "_NAME"); //getting the SETS NAME and loop it to get the CAT_NAME in firebase
                        String setID = doc.getString("SET" + String.valueOf(i) + "_ID"); //getting the SETS ID and loop it to get the CAT_ID in firebase

                        setsList.add(new SetsModelClass(setID, setName, "0", "1"));
                        setsIDs.add(doc.getString("SET" + String.valueOf(i) + "_ID"));

                    }

                    adapter = new SetsAdapter(setsList);
                    setsView.setAdapter(adapter);
                }
            }
        });
    }

    private void addNewSet(String title)
    {

        loadingDialog.show();

        final String curr_cat_id = catList.get(selected_cat_index).getId();
        final String curr_counter = catList.get(selected_cat_index).getSetCounter();

        Map<String,Object> qData = new ArrayMap<>();
        qData.put("COUNT","0");

        String doc_id = firestore.collection("QUIZ").document(curr_cat_id)
                .collection(curr_counter).document("QUESTIONS_LIST").getId(); //get all the ID from the QUIZ

        firestore.collection("QUIZ").document(curr_cat_id)
                .collection(curr_counter).document("QUESTIONS_LIST")
                .set(qData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Map<String,Object> catDoc = new ArrayMap<>();

                        catDoc.put("COUNTER", String.valueOf(Integer.valueOf(curr_counter) + 1)  );
                        catDoc.put("SET" + String.valueOf(setsList.size() + 1) + "_ID" , curr_counter);
                        catDoc.put("SET" + String.valueOf(setsList.size() + 1) + "_NAME" , txtSetName);
                        catDoc.put("SETS", setsList.size() + 1);

                        firestore.collection("QUIZ").document(curr_cat_id)
                                .update(catDoc)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Toast.makeText(SetsActivity.this, " Set Added Successfully",Toast.LENGTH_SHORT).show();

                                        setsList.add(new SetsModelClass(doc_id,title,"0","1")); //add id,title,and no of sets into catList arrayList
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