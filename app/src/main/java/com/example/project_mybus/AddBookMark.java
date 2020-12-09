package com.example.project_mybus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class AddBookMark extends AppCompatActivity implements View.OnClickListener {

    DBManager dbmanager;
    SQLiteDatabase sqlitedb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book_mark);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.add:
                Intent it = getIntent();
                String routeId = it.getStringExtra("routeId");
                String nodeId = it.getStringExtra("nodeId");

                try {
                    dbmanager = new DBManager(this);
                    sqlitedb = dbmanager.getWritableDatabase();

                    EditText nameEditText = (EditText)findViewById(R.id.name);
                    ContentValues values = new ContentValues();
                    values.put("name", nameEditText.getText().toString());
                    values.put("folder", "/");
                    values.put("routeId", routeId);
                    values.put("nodeId", nodeId);
                    sqlitedb.insert("BusBookMark", null, values);
                    sqlitedb.close();
                    dbmanager.close();
                    finish();

                } catch (SQLiteException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                }

                Toast.makeText(this, "hi", Toast.LENGTH_LONG).show();
                finish();
                break;
        }

    }

}