package com.example.project_mybus;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class FolderClick extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    DBManager dbManager;
    SQLiteDatabase sqlitedb;
    LinearLayout dynamicLayout;
    LinearLayout dynamicHori;
    String str_folder = "";
    ArrayList busnums = new ArrayList();
    ArrayList busids = new ArrayList();
    ArrayList citys = new ArrayList();
    int idc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_click);
        overridePendingTransition(0,0);

        Intent it = getIntent();
        str_folder = it.getStringExtra("it_foldnm");

        TextView foldnm_tv = (TextView)findViewById(R.id.foldername);

        foldnm_tv.setText(str_folder);

        Refrsh();
    }

    public void AddText(String busnum, String cityid, String startnm, String endnm, int id) {
        dynamicLayout = (LinearLayout)findViewById(R.id.dynamicLayout);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT, 2.0f);
        param.width = MATCH_PARENT;
        param.height = 200;
        param.leftMargin = 50;
        param.rightMargin = 50;
        param.topMargin = 40;
        param.bottomMargin = 10;
        TextView busnum_tv = new TextView(this);
        TextView startnm_tv = new TextView(this);
        TextView endnm_tv = new TextView(this);
        ImageView busicon_v = new ImageView(this);
        if((cityid.equals("37020")) || (cityid.equals("31230"))){
            busnum_tv.setText(busnum + "번 버스");
            busnums.add(busnum+ "번 버스");
        }
        else {
            busnum_tv.setText(busnum);
            busnums.add(busnum);
        }
        busnum_tv.setTextSize(25);
        busnum_tv.setEllipsize(TextUtils.TruncateAt.END);
        busnum_tv.setSingleLine(true);
        busnum_tv.setPadding(50, 30, 0, 0);

        startnm_tv.setText(startnm + " -> ");
        startnm_tv.setTextSize(15);
        endnm_tv.setText(endnm);
        endnm_tv.setTextSize(15);
        endnm_tv.setEllipsize(TextUtils.TruncateAt.END);
        endnm_tv.setSingleLine(true);

        LinearLayout layout_tv2 = new LinearLayout(this);
        layout_tv2.setOrientation(LinearLayout.HORIZONTAL);
        layout_tv2.setGravity(Gravity.CENTER);
        layout_tv2.setPadding(30, 0, 0, 0);
        layout_tv2.addView(startnm_tv);
        layout_tv2.addView(endnm_tv);

        LinearLayout layout_tv = new LinearLayout(this);
        layout_tv.setOrientation(LinearLayout.VERTICAL);
        layout_tv.setGravity(Gravity.CENTER);
        layout_tv.addView(busnum_tv);
        layout_tv.addView(layout_tv2);

        busicon_v.setImageResource(R.drawable.bus_icon3);
        busicon_v.setAdjustViewBounds(true);
        busicon_v.setScaleType(ImageView.ScaleType.FIT_CENTER);
        busicon_v.setPadding(60, 30, 0, 30);
        dynamicHori = new LinearLayout(this);
        dynamicHori.setBackgroundResource(R.drawable.search_menu_shape);
        dynamicHori.setLayoutParams(param);
        dynamicHori.addView(busicon_v);
        dynamicHori.addView(layout_tv);
        dynamicHori.setId(id);
        dynamicHori.setOnClickListener(this);
        dynamicHori.setOnLongClickListener(this);
        dynamicLayout.addView(dynamicHori);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        for(int k = 1; k <= idc; k++){
            if(id == k){
                Intent intent5 = new Intent(this, BusSearchClick.class);
                intent5.putExtra("it_busid", busids.get(k-1).toString());
                intent5.putExtra("it_busno", busnums.get(k-1).toString());
                intent5.putExtra("it_citycode", Integer.parseInt(citys.get(k-1).toString()));
                startActivity(intent5);
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        int id = v.getId();

        for(int j = 1; j <= idc; j++){
            if(id == j){
                AlertDialog.Builder ad = new AlertDialog.Builder(FolderClick.this);

                ad.setTitle("삭제");
                ad.setMessage("삭제 하시겠습니까?");

                final int finalJ = j;
                ad.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        // Event
                        DBdel(str_folder, busids.get(finalJ-1).toString());
                        dynamicLayout = (LinearLayout)findViewById(R.id.dynamicLayout);
                        dynamicLayout.removeAllViews();
                        Refrsh();
                    }
                });

                ad.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // Event
                    }
                });
                ad.show();
            }
        }
        return false;
    }

    public void DBdel(String foldnm, String busid) {
        try{
            dbManager = new DBManager(this);
            sqlitedb = dbManager.getReadableDatabase();
            sqlitedb.execSQL("DELETE FROM BusBookMark WHERE (folder = '" + foldnm + "') AND (busid = " + "'" + busid + "'" + ")");
            sqlitedb.close();
            dbManager.close();
        } catch (SQLiteException e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        dynamicLayout = (LinearLayout)findViewById(R.id.dynamicLayout);
        dynamicLayout.removeAllViews();
        Refrsh();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        overridePendingTransition(0,0);
    }

    public void Refrsh() {
        busnums = new ArrayList();
        busids = new ArrayList();
        citys = new ArrayList();
        dynamicLayout = (LinearLayout)findViewById(R.id.dynamicLayout);
        dynamicLayout.removeAllViews();
        try {
            idc = 0;
            dbManager = new DBManager(this);
            sqlitedb = dbManager.getReadableDatabase();
            Cursor cursor = sqlitedb.query("BusBookMark", null, "folder = ?", new String[]{str_folder}, null, null, null);
            while (cursor.moveToNext()) {
                String busnum = cursor.getString(cursor.getColumnIndex("busnum"));
                String cityid = cursor.getString(cursor.getColumnIndex("cityid"));
                String startnm = cursor.getString(cursor.getColumnIndex("startnm"));
                String endnm = cursor.getString(cursor.getColumnIndex("endnm"));
                String routeId = cursor.getString(cursor.getColumnIndex("busid"));

                busids.add(routeId);
                citys.add(cityid);

                idc++;
                AddText(busnum, cityid, startnm, endnm, idc);
            }
            cursor.close();
            sqlitedb.close();
            dbManager.close();
        } catch (SQLiteException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}