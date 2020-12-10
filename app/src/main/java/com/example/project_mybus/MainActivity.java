package com.example.project_mybus;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    DBManager dbManager;
    SQLiteDatabase sqlitedb;
    LinearLayout dynamicLayout;
    LinearLayout dynamicHori;
    int idc;
    int idc_tv;
    ArrayList foldnms = new ArrayList();
    ArrayList busnums = new ArrayList();
    ArrayList busids = new ArrayList();
    ArrayList citys = new ArrayList();

    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, fab1, fab2, fab3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        overridePendingTransition(0,0);

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab3);

        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        fab3.setOnClickListener(this);

        try {
            idc = 0;
            dbManager = new DBManager(this);
            sqlitedb = dbManager.getReadableDatabase();
            Cursor cursor = sqlitedb.query("folders", null, null, null, null, null, null);
            while (cursor.moveToNext()) {
                String foldnm = cursor.getString(cursor.getColumnIndex("name"));
                foldnms.add(foldnm);
                idc++;
                AddFolder(foldnm, idc);
            }
            cursor.close();
            sqlitedb.close();
            dbManager.close();
        } catch (SQLiteException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        try {
            idc_tv = 200;
            dbManager = new DBManager(this);
            sqlitedb = dbManager.getReadableDatabase();
            String str_folder = "empty";
            Cursor cursor = sqlitedb.query("BusBookMark", null, "folder = ?", new String[]{str_folder}, null, null, null);
            while (cursor.moveToNext()) {
                String busnum = cursor.getString(cursor.getColumnIndex("busnum"));
                String cityid = cursor.getString(cursor.getColumnIndex("cityid"));
                String startnm = cursor.getString(cursor.getColumnIndex("startnm"));
                String endnm = cursor.getString(cursor.getColumnIndex("endnm"));
                String routeId = cursor.getString(cursor.getColumnIndex("busid"));

                busids.add(routeId);
                citys.add(cityid);


                idc_tv++;
                AddText(busnum, cityid, startnm, endnm, idc_tv);
            }
            cursor.close();
            sqlitedb.close();
            dbManager.close();
        } catch (SQLiteException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    public void AddFolder(String fold_name, int id) {
        dynamicLayout = (LinearLayout)findViewById(R.id.dynamicLayout);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT, 2.0f);
        param.width = MATCH_PARENT;
        param.height = 150;
        param.leftMargin = 50;
        param.rightMargin = 50;
        param.topMargin = 40;
        param.bottomMargin = 10;
        TextView foldname_tv = new TextView(this);
        ImageView foldicon_v = new ImageView(this);
        foldname_tv.setText(fold_name);
        foldname_tv.setTextSize(25);
        foldname_tv.setEllipsize(TextUtils.TruncateAt.END);
        foldname_tv.setSingleLine(true);
        foldname_tv.setPadding(50, 30, 0, 0);
        foldicon_v.setImageResource(R.drawable.folder);
        foldicon_v.setAdjustViewBounds(true);
        foldicon_v.setScaleType(ImageView.ScaleType.FIT_CENTER);
        foldicon_v.setPadding(60, 30, 0, 30);
        dynamicHori = new LinearLayout(this);
        dynamicHori.setBackgroundResource(R.drawable.search_menu_shape);
        dynamicHori.setLayoutParams(param);
        dynamicHori.addView(foldicon_v);
        dynamicHori.addView(foldname_tv);
        dynamicHori.setId(id);
        dynamicHori.setOnClickListener(this);
        dynamicHori.setOnLongClickListener(this);
        dynamicLayout.addView(dynamicHori);
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
        switch (id) {
            case R.id.fab:
                anim();
                break;
            case R.id.fab1:
                anim();
                Toast.makeText(getApplicationContext(), "현재화면 입니다.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.fab2:
                anim();
                Intent intent2 = new Intent(this, BusSearch.class);
                intent2.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent2);
                finish();
                break;
            case R.id.fab3:
                anim();
                Intent intent3 = new Intent(this, BusStopSearch.class);
                intent3.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent3);
                finish();
                break;
        }

        for(int j = 1; j <= idc; j++){
            if(id == j){
                Intent intent4 = new Intent(this, FolderClick.class);
                intent4.putExtra("it_foldnm", foldnms.get(j-1).toString());
                startActivity(intent4);
            }
        }
        for(int k = 201; k <= idc_tv; k++){
            if(id == k){
                Intent intent5 = new Intent(this, BusSearchClick.class);
                intent5.putExtra("it_busid", busids.get(k-201).toString());
                intent5.putExtra("it_busno", busnums.get(k-201).toString());
                intent5.putExtra("it_citycode", Integer.parseInt(citys.get(k-201).toString()));
                startActivity(intent5);
            }
        }
    }

    private void anim() {
        if (isFabOpen) {
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab3.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            fab3.setClickable(false);
            isFabOpen = false;
        } else {
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab3.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            fab3.setClickable(true);
            isFabOpen = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        overridePendingTransition(0,0);
    }

    @Override
    public boolean onLongClick(View v) {
        int id = v.getId();

        for(int j = 1; j <= idc; j++){
            if(id == j){
                AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);

                ad.setTitle("삭제");
                ad.setMessage("삭제 하시겠습니까?");

                ad.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        // Event
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
        for(int k = 201; k <= idc_tv; k++){
            if(id == k){


            }
        }

        return false;
    }
}
