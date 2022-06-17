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
import android.view.MenuItem;
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
    private String qStr, aStr, bStr,cStr,dStr, ansStr;

    private String action;
    private int qID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_details);

        Toolbar toolbar = findViewById(R.id.qdetails_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtQues = findViewById(R.id.question);
        txtOptionA = findViewById(R.id.optionA);
        txtOptionB = findViewById(R.id.optionB);
        txtOptionC = findViewById(R.id.optionC);
        txtOptionD = findViewById(R.id.optionD);
        txtAnswer = findViewById(R.id.answer);
        btnAddQuestion = findViewById(R.id.addQB);

        loadingDialog = new Dialog(QuestionDetailsActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_bar);  //initialize the loading dialog
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        firestore = FirebaseFirestore.getInstance();

        action = getIntent().getStringExtra("ACTION");

        if(action.compareTo("EDIT") == 0)  //if the action value is EDIT
        {
            qID = getIntent().getIntExtra("Q_ID",0);
            loadData(qID);
            getSupportActionBar().setTitle("Question " + String.valueOf(qID + 1));
            btnAddQuestion.setText("UPDATE"); //IF ITS EDIT THEN THE BUTTON TEXT IS UPDATE
        }
        else
        {
            getSupportActionBar().setTitle("Question " + String.valueOf(quesList.size() + 1));
            btnAddQuestion.setText("ADD"); //IF ITS ADD FEATURE. THE BUTTON TEXT BECAME ADD
        }

        btnAddQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                qStr = txtQues.getText().toString();  //set the list into updated value
                aStr = txtOptionA.getText().toString();
                bStr = txtOptionB.getText().toString();
                cStr = txtOptionC.getText().toString();
                dStr = txtOptionD.getText().toString();
                ansStr = txtAnswer.getText().toString();

//                if(qStr.isEmpty()) {
//                    ques.setError("Enter Question");
//                    return;
//                }
//
//                if(aStr.isEmpty()) {
//                    optionA.setError("Enter option A");
//                    return;
//                }
//
//                if(bStr.isEmpty()) {
//                    optionB.setError("Enter option B ");
//                    return;
//                }
//                if(cStr.isEmpty()) {
//                    optionC.setError("Enter option C");
//                    return;
//                }
//                if(dStr.isEmpty()) {
//                    optionD.setError("Enter option D");
//                    return;
//                }
//                if(ansStr.isEmpty()) {
//                    answer.setError("Enter correct answer");
//                    return;
//                }

                if(action.compareTo("EDIT") == 0) //If the action is edit... editQuestion method will call and if not addNewQuestion method will call
                {
                    editQuestion();
                }
                else {
                    addNewQuestion();
                }

            }
        });
    }


    private void addNewQuestion()
    {
        loadingDialog.show();

        Map<String,Object> quesData = new ArrayMap<>();

        quesData.put("QUESTION",qStr);
        quesData.put("A",aStr);
        quesData.put("B",bStr);
        quesData.put("C",cStr);
        quesData.put("D",dStr);
        quesData.put("ANSWER",ansStr);


        final String doc_id = firestore.collection("QUIZ").document(catList.get(selected_cat_index).getId()) //add the quesData arrayMap
                .collection(setsIDs.get(selected_set_index)).document().getId();

        firestore.collection("QUIZ").document(catList.get(selected_cat_index).getId())
                .collection(setsIDs.get(selected_set_index)).document(doc_id)
                .set(quesData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Map<String,Object> quesDoc = new ArrayMap<>();
                        quesDoc.put("Q" + String.valueOf(quesList.size() + 1) + "_ID", doc_id); //add new in q_id with random id
                        quesDoc.put("COUNT",String.valueOf(quesList.size() + 1)); //count will increment

                        firestore.collection("QUIZ").document(catList.get(selected_cat_index).getId())
                                .collection(setsIDs.get(selected_set_index)).document("QUESTIONS_LIST")
                                .update(quesDoc)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(QuestionDetailsActivity.this, " Question Added Successfully", Toast.LENGTH_SHORT).show();

                                        quesList.add(new QuestionModel(
                                                doc_id,
                                                qStr,aStr,bStr,cStr,dStr, Integer.valueOf(ansStr)
                                        ));

                                        loadingDialog.dismiss();
                                        QuestionDetailsActivity.this.finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(QuestionDetailsActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                        loadingDialog.dismiss();
                                    }
                                });


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(QuestionDetailsActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                    }
                });


    }

    private void loadData(int id)
    {
        txtQues.setText(quesList.get(id).getQuestion());
        txtOptionA.setText(quesList.get(id).getOptionA());
        txtOptionB.setText(quesList.get(id).getOptionB());
        txtOptionC.setText(quesList.get(id).getOptionC());
        txtOptionD.setText(quesList.get(id).getOptionD());
        txtAnswer.setText(String.valueOf(quesList.get(id).getAnswer()));
    }


    private void editQuestion()
    {
        loadingDialog.show();

        Map<String,Object> quesData = new ArrayMap<>();
        quesData.put("QUESTION", qStr);
        quesData.put("A",aStr);
        quesData.put("B",bStr);
        quesData.put("C",cStr);
        quesData.put("D",dStr);
        quesData.put("ANSWER",ansStr);


        firestore.collection("QUIZ").document(catList.get(selected_cat_index).getId())
                .collection(setsIDs.get(selected_set_index)).document(quesList.get(qID).getQuesID())
                .set(quesData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(QuestionDetailsActivity.this,"Question updated successfully",Toast.LENGTH_SHORT).show();

                        quesList.get(qID).setQuestion(qStr);
                        quesList.get(qID).setOptionA(aStr);
                        quesList.get(qID).setOptionB(bStr);
                        quesList.get(qID).setOptionC(cStr);
                        quesList.get(qID).setOptionD(dStr);
                        quesList.get(qID).setAnswer(Integer.valueOf(ansStr));

                        loadingDialog.dismiss();
                        QuestionDetailsActivity.this.finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(QuestionDetailsActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                    }
                });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)
        {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
