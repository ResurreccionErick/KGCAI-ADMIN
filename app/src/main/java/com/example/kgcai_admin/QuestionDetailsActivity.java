package com.example.kgcai_admin;

import static com.example.kgcai_admin.CategoryActivity.catList;
import static com.example.kgcai_admin.CategoryActivity.selected_cat_index;
import static com.example.kgcai_admin.QuestionActivity.quesList;
import static com.example.kgcai_admin.SetsActivity.selected_set_index;
import static com.example.kgcai_admin.SetsActivity.setsIDs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class QuestionDetailsActivity extends AppCompatActivity {

    private EditText txtQues, txtOptionA, txtOptionB, txtOptionC, txtOptionD,txtAnswer;
    private Button btnAddQuestion;
    private Toolbar toolbar;
    private Dialog loadingDialog;
    private FirebaseFirestore firestore;
    private String qStr, aStr, bStr,cStr,dStr;
    int ansStr;
    private String ansString = String.valueOf(ansStr);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_details);

        firestore = FirebaseFirestore.getInstance();

        toolbar = findViewById(R.id.qdetails_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Question #: " + String.valueOf(quesList.size()+1));

        loadingDialog = new Dialog(QuestionDetailsActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_bar); //initialize the loading dialog
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        txtQues = findViewById(R.id.question);
        txtOptionA = findViewById(R.id.optionA);
        txtOptionB = findViewById(R.id.optionB);
        txtOptionC = findViewById(R.id.optionC);
        txtOptionD = findViewById(R.id.optionD);
        txtAnswer = findViewById(R.id.answer);
        btnAddQuestion = findViewById(R.id.addQB);

        btnAddQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 qStr = txtQues.getText().toString();
                 aStr = txtOptionA.getText().toString();
                 bStr = txtOptionB.getText().toString();
                 cStr = txtOptionC.getText().toString();
                 dStr = txtOptionD.getText().toString();
                 ansString = txtAnswer.getText().toString();

                if(qStr.isEmpty()||aStr.isEmpty()||bStr.isEmpty()||cStr.isEmpty()||dStr.isEmpty()||ansString.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please enter all fields", Toast.LENGTH_SHORT).show();
                }else{
                    addNewQuestion();
                }
            }
        });


    }

    private void addNewQuestion() {
        loadingDialog.show();

        Map<String, Object> quesData = new ArrayMap<>();

        quesData.put("QUESTION",qStr);
        quesData.put("A", aStr);
        quesData.put("B", bStr);
        quesData.put("C", cStr);
        quesData.put("D", dStr);
        quesData.put("ANSWER", ansStr);

        String doc_id = firestore.collection("QUIZ").document(catList.get(selected_cat_index).getId())
                .collection(setsIDs.get(selected_set_index)).document().getId();

        firestore.collection("QUIZ").document(catList.get(selected_cat_index).getId()) //add the quesData arrayMap
            .collection(setsIDs.get(selected_set_index)).document(doc_id).set(quesData)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {

                    Map<String, Object> quesDoc = new ArrayMap<>();
                    quesDoc.put("Q" + String.valueOf(quesList.size()+1)+"_ID", doc_id); //add new in q_id with random id
                    quesDoc.put("COUNT" + String.valueOf(setsIDs.size()+1), doc_id); //count will increment

                    firestore.collection("QUIZ").document(catList.get(selected_cat_index).getId())
                            .collection(setsIDs.get(selected_set_index)).document("QUESTIONS_LIST")
                            .update(quesDoc).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(), "Question Added Successfully", Toast.LENGTH_SHORT).show();

                            quesList.add(new QuestionModel( //add the variable into quesList
                                    doc_id,
                                    qStr,aStr,bStr,cStr,dStr,Integer.valueOf(ansStr)
                            ));

                            loadingDialog.dismiss();

                            QuestionDetailsActivity.this.finish();
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

    @Override
    protected void onResume() {
        super.onResume();
    }
}