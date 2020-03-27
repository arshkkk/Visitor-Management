package com.example.visitormanagement;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.visitormanagement.Adapter.HostAdapter;
import com.example.visitormanagement.Person.Host;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

public class Setting_Up_Meeting extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HostAdapter adapter;
    private List<Host> employeeList;
    private ProgressBar progressBar;
    private MaterialSearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting__up__meeting);

        //Toolbar
        androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
        toolbar.setTitle("Select Your Host");
        setSupportActionBar(toolbar);

        //SearchView
        searchView = (MaterialSearchView)findViewById(R.id.search_view);

        recyclerView = findViewById(R.id.recyclerView);


        //Progress Bar
        progressBar= findViewById(R.id.progressBar1);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);



        //Attaching Adapter to Recycler View
        employeeList = new ArrayList<>();
        adapter = new HostAdapter(employeeList, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        initiateEmployeeList(" ");


        //SearchView Listener
        MaterialSearchView searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Do some magic
                employeeList.clear();
                adapter.notifyDataSetChanged();

                initiateEmployeeList(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic


                return false;
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_menu, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        adapter.notifyDataSetChanged();

        searchView.setMenuItem(item);

        return true;
    }



    public void initiateEmployeeList(String query){


        recyclerView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);


        Query queryForEmployees = FirebaseDatabase.getInstance().getReference().child("hosts")
                .orderByChild("fullName").startAt(query.toUpperCase()).endAt(query.toLowerCase()+"\uf8ff");

        if(query.equals(" "))
         queryForEmployees = FirebaseDatabase.getInstance().getReference().child("hosts")
                .orderByChild("fullName");

        queryForEmployees.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if(dataSnapshot.exists()){
                            employeeList.add(dataSnapshot.getValue(Host.class));

                            progressBar.setVisibility(View.GONE);

                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }




}


