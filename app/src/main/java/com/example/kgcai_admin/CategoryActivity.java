package com.example.kgcai_admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CategoryActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private FloatingActionButton btnAddSubj;
    private RecyclerView cat_recycler_view;
    public static List<CategoryModelClass> catList = new ArrayList<CategoryModelClass>();
    public static int selected_cat_index = 0;
    private FirebaseFirestore firestore;
    private Dialog loadingDialog, addSubjDialog, editSubjDialog;
    private EditText dialogSubjName;
    private Button dialogBtnAddSubj;
    private CategoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        toolbar = findViewById(R.id.toolbar_subjects);
        btnAddSubj = findViewById(R.id.btnAddNewSubj);
        cat_recycler_view = findViewById(R.id.subjRecyclerView);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Subjects");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadingDialog = new Dialog(CategoryActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_bar); //initialize the loading dialog
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        addSubjDialog = new Dialog(CategoryActivity.this);
        addSubjDialog.setContentView(R.layout.add_category_dialog); //initialize the addSubj dialog
        addSubjDialog.setCancelable(true);
        addSubjDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        dialogSubjName = addSubjDialog.findViewById(R.id.txtAddSubjName);
        dialogBtnAddSubj = addSubjDialog.findViewById(R.id.btnAddSubjDialog);

        firestore = FirebaseFirestore.getInstance();


        btnAddSubj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSubjName.getText().clear();
                addSubjDialog.show();
            }
        });

        dialogBtnAddSubj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialogSubjName.getText().toString().isEmpty()){
                    dialogSubjName.setError("Please Enter Subject Name");
                    dialogSubjName.requestFocus();
                    return;
                }else{
                    addNewSubject(dialogSubjName.getText().toString()); //from add new subj Edit text... values will pass into addNewSubject method parameter
                }
            }
        });


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        cat_recycler_view.setLayoutManager(layoutManager);

        loadData(); //calling the load data method

    }

    private void addNewSubject(String title) {
        addSubjDialog.dismiss();
        loadingDialog.show();

        Map<String,Object> catData = new ArrayMap<>();
        catData.put("NAME",title); //this is from AddNewSubj EditText
        catData.put("SETS",0); //sets set as zero value
        catData.put("COUNTER","1");

        String doc_id = firestore.collection("QUIZ").document().getId(); //get all the ID from the QUIZ

        firestore.collection("QUIZ").document(doc_id).set(catData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Map<String,Object> catDoc = new ArrayMap<>(); //this ArrayMap will use to rewrite the information from Categories everytime addNewSubjects method called
                catDoc.put("CAT"+String.valueOf(catList.size()+1) + "_NAME",title);
                catDoc.put("CAT"+String.valueOf(catList.size()+1) + "_ID",doc_id);
                catDoc.put("COUNT", catList.size()+1); //Count will increment

                    firestore.collection("QUIZ").document("Categories") //Update data from Categories using catDoc arrayMap
                            .update(catDoc).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(), "Subject was added successfully", Toast.LENGTH_SHORT).show();

                            catList.add(new CategoryModelClass(doc_id,title,"0","1")); //add id,title,and no of sets into catList arrayList

                            adapter.notifyItemInserted(catList.size()); //display new subjects added into the end

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


    private void loadData() {
        loadingDialog.show();

        catList.clear(); //Clear the arraylist of the subject

        firestore.collection("QUIZ").document("Categories")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if(doc.exists()){
                        long count = (long)doc.get("COUNT"); //get the COUNT of categories in firebase

                        for(int i = 1; i<=count; i++){
                            String catName = doc.getString("CAT"+String.valueOf(i) +"_NAME"); //getting the CAT NAME and loop it to get the CAT_NAME in firebase
                            String catID = doc.getString("CAT"+String.valueOf(i) +"_ID"); //getting the CAT ID and loop it to get the CAT_ID in firebase

                            catList.add(new CategoryModelClass(catID,catName,"0","1")); //then catName and catId will add it to the catList
                        }

                        adapter = new CategoryAdapter(catList);
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { //this is for back button
        if (item.getItemId() == android.R.id.home) {
            CategoryActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}