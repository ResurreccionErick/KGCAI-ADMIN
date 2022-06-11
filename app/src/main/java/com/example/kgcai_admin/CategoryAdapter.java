package com.example.kgcai_admin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List <CategoryModelClass> cat_list;
    private Dialog loadingDialog;

    public CategoryAdapter(List<CategoryModelClass> cat_list) {
        this.cat_list = cat_list;
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cat_item_layout,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        String title = cat_list.get(position).getName();
        holder.setData(title,position,this);
    }

    @Override
    public int getItemCount() {
        return cat_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView catName;
        private ImageView btnDelete,btnEdit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            catName = itemView.findViewById(R.id.tvSubjName);
            btnDelete = itemView.findViewById(R.id.btnSubjDelete);
            btnEdit = itemView.findViewById(R.id.btnSubjEdit);

            loadingDialog = new Dialog(itemView.getContext());
            loadingDialog.setContentView(R.layout.loading_progress_bar); //initialize the loading dialog
            loadingDialog.setCancelable(false);
            loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        private void setData(String title, int position, CategoryAdapter adapter){
            catName.setText(title);

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog dialog = new AlertDialog.Builder(itemView.getContext()).setTitle("Delete Subject")
                            .setMessage("Do you want to delete this subject?").setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteCategory(position, itemView.getContext(), adapter);
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
        private void deleteCategory(final int id, Context context, CategoryAdapter adapter) {
            loadingDialog.show();

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            Map<String,Object> catDoc = new ArrayMap<>();
            int index = 1;

            for(int i = 0; i < cat_list.size(); i++){
                if(i != id){
                    catDoc.put("CAT"+String.valueOf(index)+"_ID",cat_list.get(i).getId());
                    catDoc.put("CAT"+String.valueOf(index)+"_NAME",cat_list.get(i).getName());

                    index++;
                }
            }
            catDoc.put("COUNT", index-1); //decrement index

            firestore.collection("QUIZ").document("Categories") //delete old data and set another data based from catDoc
                    .set(catDoc).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(context.getApplicationContext(), "Successfully Deleted", Toast.LENGTH_SHORT).show();

                    CategoryActivity.catList.remove(id); //update the catList

                    adapter.notifyDataSetChanged(); //update the adapter display

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
    }
}
