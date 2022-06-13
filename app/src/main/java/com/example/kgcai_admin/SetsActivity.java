package com.example.kgcai_admin;

import static com.example.kgcai_admin.CategoryActivity.catList;
import static com.example.kgcai_admin.CategoryActivity.selected_cat_index;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArrayMap;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.collect.Sets;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SetsActivity extends AppCompatActivity {

    private FloatingActionButton btnAddSets;
    private RecyclerView setsView;
    private Dialog addSetsDialog,loadingDialog;
    private EditText dialogSetsName;
    private Button btnAdd;
    private SetsAdapter adapter;
    private String input;
    public static List<String> setsIDs = new ArrayList<>();
    private FirebaseFirestore firestore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sets);

        setsView = findViewById(R.id.setsRecyclerView);
        btnAddSets = findViewById(R.id.btnAddNewSets);

        loadingDialog = new Dialog(SetsActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_bar); //initialize the loading dialog
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        addSetsDialog = new Dialog(SetsActivity.this);
        addSetsDialog.setContentView(R.layout.add_sets_dialog); //initialize the loading dialog
        addSetsDialog.setCancelable(true);
        addSetsDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        dialogSetsName = addSetsDialog.findViewById(R.id.txtAddSetsName);
        btnAdd = addSetsDialog.findViewById(R.id.btnAddSetsDialog);

        firestore = FirebaseFirestore.getInstance();

        btnAddSets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSetsDialog.show();

//                btnAdd.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        input = dialogSetsName.getText().toString(); //get from the dialog
//                    }
//                });
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewSets();
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        setsView.setLayoutManager(layoutManager);

        loadSets(input);

    }

    private void addNewSets() {
        loadingDialog.show();

        input = dialogSetsName.getText().toString(); //get from the dialog

        String current_cat_id = catList.get(selected_cat_index).getId();
        String current_counter = catList.get(selected_cat_index).getSetCounter();

        Map<String,Object> qData = new ArrayMap<>();
        qData.put("SET_NAME","0");
        //qData.put("COUNT", input);

        firestore.collection("QUIZ").document(current_cat_id)
        .collection(current_counter).document("QUESTIONS_LIST").set(qData) //set the qData into the count into questions_list in firebase
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Map<String, Object> catDoc = new ArrayMap<>();
                catDoc.put("COUNTER", String.valueOf(Integer.valueOf(current_counter)+1)); //after adding new sets. the Counter was incremented
                catDoc.put("SET"+String.valueOf(setsIDs.size()+1)+"_ID", current_counter); //add another SET_ID
                catDoc.put("SETS", setsIDs.size()+1); //increment value of SETS

                firestore.collection("QUIZ").document(current_cat_id).update(catDoc).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(), "Set was added successfully", Toast.LENGTH_SHORT).show();
                        setsIDs.add(current_counter);
                        catList.get(selected_cat_index).setNoOfSets(String.valueOf(setsIDs.size())); //get the noOfSets of that selected category
                        catList.get(selected_cat_index).setSetCounter(String.valueOf(Integer.valueOf(current_counter) +1));

                        adapter.notifyItemInserted(setsIDs.size());
                        loadingDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();

                        loadingDialog.dismiss();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();

                loadingDialog.dismiss();
            }
        });

    }

    private void loadSets(String input) {
        setsIDs.clear();

        //setsIDs.add(input);
        loadingDialog.show();

        firestore.collection("QUIZ").document(catList.get(selected_cat_index).getId())
            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {

                    long noOfSets = (long) documentSnapshot.get("SETS"); //get the sets from the firestore

                    for(int i = 1; i <= noOfSets; i++){
                        setsIDs.add(documentSnapshot.getString("SET" + String.valueOf(i) + "_ID")); //get the set with _ID from Categories
                    }

                    catList.get(selected_cat_index).setSetCounter(documentSnapshot.getString("COUNTER")); //Fetch the counter value from the firestore and store it in catList
                    catList.get(selected_cat_index).setNoOfSets(String.valueOf(noOfSets)); //from the noOfSets

                    adapter = new SetsAdapter(setsIDs);
                    setsView.setAdapter(adapter);

                    loadingDialog.dismiss();

                }
            }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();

                loadingDialog.dismiss();
                }
            });

    }
}