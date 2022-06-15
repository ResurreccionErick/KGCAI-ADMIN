package com.example.kgcai_admin;

import static com.example.kgcai_admin.CategoryActivity.catList;
import static com.example.kgcai_admin.CategoryActivity.selected_cat_index;
import static com.example.kgcai_admin.QuestionActivity.quesList;
import static com.example.kgcai_admin.SetsActivity.selected_set_index;
import static com.example.kgcai_admin.SetsActivity.setsIDs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {

    private List<QuestionModel> ques_list;

    public QuestionAdapter(List<QuestionModel> ques_list) {
        this.ques_list = ques_list;
    }

    @NonNull
    @Override
    public QuestionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_item_layout, parent,false); //use the same layout in category_item_layout

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionAdapter.ViewHolder holder, int position) {
        holder.setData(position, this);
    }

    @Override
    public int getItemCount() {
        return ques_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private ImageView btnDelete, btnEdit;
        private Dialog loadingDialog;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.tvQuestionName);
            btnDelete = itemView.findViewById(R.id.btnSubjDeleteQuestion);
            btnEdit = itemView.findViewById(R.id.btnSubjEditQuestion);

            loadingDialog = new Dialog(itemView.getContext());
            loadingDialog.setContentView(R.layout.loading_progress_bar); //initialize the loading dialog
            loadingDialog.setCancelable(false);
            loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        }

        private void setData(int pos, QuestionAdapter adapter){
            title.setText("QUESTION " + String.valueOf(pos+1));

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        AlertDialog dialog = new AlertDialog.Builder(itemView.getContext()).setTitle("Delete Question")
                                .setMessage("Do you want to delete this Question?").setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        deleteQuestion(pos, itemView.getContext(), adapter);
                                    }
                                }).setNegativeButton("Cancel", null)
                                .setIcon(android.R.drawable.ic_dialog_alert).show();

                        dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
                        dialog.getButton(dialog.BUTTON_POSITIVE).setBackgroundColor(Color.RED);
                        dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(Color.WHITE);
                        dialog.getButton(dialog.BUTTON_NEGATIVE).setBackgroundColor(Color.BLUE);

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(0,0,50,0); //cancel button has 50 margin to the right
                        dialog.getButton(dialog.BUTTON_NEGATIVE).setLayoutParams(params);
                    }
            });
        }

        private void deleteQuestion(int position, Context context, QuestionAdapter adapter){
            loadingDialog.show();

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            firestore.collection("QUIZ").document(catList.get(selected_cat_index).getId()).collection(setsIDs.get(selected_set_index))
                    .document(quesList.get(position).getQuesID())
                    .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {

                    Map<String , Object> quesDoc = new ArrayMap<>();
                    int index = 1;
                    for(int i = 0; i < quesList.size(); i++){
                        if(i != position){
                            quesDoc.put("Q" + String.valueOf(index) + "_ID" , quesList.get(i).getQuesID()); //store the question who was gonna be deleted
                            index++;
                        }
                    }
                    quesDoc.put("COUNT", String.valueOf(index -1)); //decrement the COUNT

                    firestore.collection("QUIZ").document(catList.get(selected_cat_index).getId())
                    .collection(setsIDs.get(selected_set_index)).document("QUESTIONS_LIST").set(quesDoc)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(context.getApplicationContext(), "Question was deleted", Toast.LENGTH_SHORT).show();

                            quesList.remove(position); //remove to the delete question into the questions list

                            adapter.notifyDataSetChanged(); //update the recycler view

                            loadingDialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context.getApplicationContext(), "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();

                            loadingDialog.dismiss();
                        }
                    });
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context.getApplicationContext(), "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();

                    loadingDialog.dismiss();
                }
            });
        }
    }
}
