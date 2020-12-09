package com.example.project_mybus;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class AddBookMark extends AppCompatActivity implements View.OnClickListener{

    DBManager dbmanager;
    SQLiteDatabase sqlitedb;

    LinearLayout dynamicLayout;
    LinearLayout dynamicHori;
    int idc = 0;
    int idc_fold = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book_mark);

        try {
            dbmanager = new DBManager(this);
            sqlitedb = dbmanager.getReadableDatabase();
            Cursor cursor = sqlitedb.query("folders", null, null, null, null, null, null);
            while (cursor.moveToNext()) {
                String nodeId = cursor.getString(cursor.getColumnIndex("nodeId"));
                String roudeId = cursor.getString((cursor.getColumnIndex("roudeId")));
                AddText(idc, nodeId, roudeId);
                idc++;
                idc_fold++;
                Toast.makeText(getBaseContext(),  "클릭 됨", Toast.LENGTH_SHORT).show();
            }
            cursor = sqlitedb.query("BusBookMark", null, null, null, null, null, null);
            while (cursor.moveToNext()) {
                if(cursor.getString(cursor.getColumnIndex("folder")) == "null") {
                    String nodeId = cursor.getString(cursor.getColumnIndex("nodeId"));
                    String roudeId = cursor.getString((cursor.getColumnIndex("roudeId")));
                    AddText(idc, nodeId, roudeId);
                    idc++;
                }
            }
        } catch (SQLiteException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void AddText(int id, String nodeId, String roudeId) {
        dynamicLayout = (LinearLayout)findViewById(R.id.dynamicLayout);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT, 2.0f);
        param.width = MATCH_PARENT;
        param.height = 150;
        param.leftMargin = 50;
        param.rightMargin = 50;
        param.topMargin = 40;
        param.bottomMargin = 10;
        TextView stopbus_tv = new TextView(this);
        ImageView plusicon_v = new ImageView(this);
        String s1 = nodeId + roudeId;
        stopbus_tv.setText(s1);
        stopbus_tv.setTextSize(25);
        stopbus_tv.setEllipsize(TextUtils.TruncateAt.END);
        stopbus_tv.setSingleLine(true);
        plusicon_v.setImageResource(R.drawable.plus_icon);
        plusicon_v.setAdjustViewBounds(true);
        plusicon_v.setScaleType(ImageView.ScaleType.FIT_CENTER);
        plusicon_v.setPadding(60, 30, 0, 30);
        plusicon_v.setId(id);
        LinearLayout layout_tv = new LinearLayout(this);
        layout_tv.setOrientation(LinearLayout.VERTICAL);
        layout_tv.setPadding(50, 30,0,0);
        layout_tv.setGravity(Gravity.CENTER);
        layout_tv.addView(stopbus_tv);
        dynamicHori = new LinearLayout(this);
        dynamicHori.setBackgroundResource(R.drawable.search_menu_shape);
        dynamicHori.setLayoutParams(param);
        dynamicHori.addView(plusicon_v);
        dynamicHori.addView(layout_tv);
        dynamicLayout.addView(dynamicHori);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        ImageView add_v;

        for(int j = 1; j <= idc; j++){
            if(id == j && id <= idc_fold){
                Toast.makeText(getBaseContext(), j + "클릭 됨", Toast.LENGTH_SHORT).show();
                add_v = (ImageView)findViewById(v.getId());
                Intent it = new Intent(this, AddBookMark.class);
                // event 폴더로 이동
                startActivity(it);

            }
        }

    }

    public void AddClick(View v) {
                Intent it = getIntent();
                String routeId = it.getStringExtra("routeId");
                String nodeId = it.getStringExtra("nodeId");

                try {
                    dbmanager = new DBManager(this);
                    sqlitedb = dbmanager.getWritableDatabase();

                    ContentValues values = new ContentValues();
                    values.put("folder", "null");
                    values.put("routeId", routeId);
                    values.put("nodeId", nodeId);
                    sqlitedb.insert("BusBookMark", null, values);
                    sqlitedb.close();
                    dbmanager.close();

                } catch (SQLiteException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                }

        finish();
    }

    public void FolderAddClick(View v) {

        AlertDialog.Builder ad = new AlertDialog.Builder(AddBookMark.this);

        ad.setTitle("폴더 추가");       // 제목 설정
        ad.setMessage("폴더 이름을 적어주세요");   // 내용 설정

// EditText 삽입하기
        final EditText et = new EditText(AddBookMark.this);
        ad.setView(et);

// 확인 버튼 설정
        ad.setPositiveButton("추가", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Text 값 받기
                String input = et.getText().toString();

                dialog.dismiss();     //닫기
                ContentValues values = new ContentValues();
                values.put("name", input);
                sqlitedb.insert("folders", null, values);
                // Event
            }
        });


// 취소 버튼 설정
        ad.setNegativeButton("닫기", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();     //닫기
                // Event
            }
        });

// 창 띄우기
        ad.show();

    }

}