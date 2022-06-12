package com.example.kgcai_admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.List;

public class SetsActivity extends AppCompatActivity {

    private FloatingActionButton btnAddSets;
    private RecyclerView setsView;
    private Dialog addSetsDialog;
    private EditText dialogSetsName;
    private Button btnAdd;
    private SetsAdapter adapter;
    private String input;
    public static List<String> setsIDs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sets);

        setsView = findViewById(R.id.setsRecyclerView);
        btnAddSets = findViewById(R.id.btnAddNewSets);

        addSetsDialog = new Dialog(SetsActivity.this);
        addSetsDialog.setContentView(R.layout.add_sets_dialog); //initialize the loading dialog
        addSetsDialog.setCancelable(true);
        addSetsDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        dialogSetsName = addSetsDialog.findViewById(R.id.txtAddSetsName);
        btnAdd = addSetsDialog.findViewById(R.id.btnAddSetsDialog);

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
                input = dialogSetsName.getText().toString(); //get from the dialog
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        setsView.setLayoutManager(layoutManager);

        loadSets(input);

    }

    private void loadSets(String input) {
        setsIDs.clear();

        //setsIDs.add(input);
        setsIDs.add("c");
        setsIDs.add("b");

        adapter = new SetsAdapter(setsIDs);
        setsView.setAdapter(adapter);
    }
}