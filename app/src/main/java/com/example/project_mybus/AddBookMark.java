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
import android.os.AsyncTask;
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class AddBookMark extends AppCompatActivity implements View.OnClickListener{

    DBManager dbmanager;
    SQLiteDatabase sqlitedb;
    String serviceKey = "%2FnU0vVe9yEqaJ2vRtCPpJZHv%2Bef81aaG8G2pMXgYpYhJGqpcVzsFP2pqQ62JPlcfY54It2FZeXgN3p8nItuu9Q%3D%3D";
    Document doc;
    Document doc2;
    int cityCode;
    String nodeId;
    String routeId;
    LinearLayout dynamicLayout;
    LinearLayout dynamicHori;
    int idc = 0;

    String busnum;
    String stopname;
    String arrvcnt;
    String endnm;
    String startnm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book_mark);

        Intent it = getIntent();
        cityCode = it.getIntExtra("cityCode", 0);
        routeId = it.getStringExtra("routeId");
        nodeId = it.getStringExtra("nodeId");

        System.out.println(routeId);
        System.out.println(nodeId);

        new GetXMLTask();
        new GetXMLTask2();

        idc = 0;
        try {
            dbmanager = new DBManager(this);
            sqlitedb = dbmanager.getReadableDatabase();
            Cursor cursor = sqlitedb.query("folders", null, null, null, null, null, null);
            while (cursor.moveToNext()) {
                String foldnm = cursor.getString(cursor.getColumnIndex("name"));
                idc++;
                AddFolder(foldnm, idc);
            }
            cursor = sqlitedb.query("BusBookMark", null, null, null, null, null, null);
            while (cursor.moveToNext()) {
                if(cursor.getString(cursor.getColumnIndex("folder")) == "null") {
                    String busnm = cursor.getString(cursor.getColumnIndex("busnum"));
                    String stopnm  = cursor.getString((cursor.getColumnIndex("stopname")));
                    String arrvct  = cursor.getString((cursor.getColumnIndex("arrvcnt")));
                    String stnm  = cursor.getString((cursor.getColumnIndex("startnm")));
                    String ednm  = cursor.getString((cursor.getColumnIndex("endnm")));
                    AddText(busnm, stopnm, arrvct, stnm, ednm);
                }
            }
            cursor.close();
            sqlitedb.close();
            dbmanager.close();
        } catch (SQLiteException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private class GetXMLTask extends AsyncTask<String, Void, Document> {
        @Override
        protected Document doInBackground(String... urls) {
            URL url;
            try {
                url = new URL("http://openapi.tago.go.kr/openapi/service/ArvlInfoInqireService/getSttnAcctoSpcifyRouteBusArvlPrearngeInfoList"+
                        "?serviceKey=" + serviceKey +
                        "&cityCode="+ cityCode +
                        "&nodeId=" + nodeId +
                        "&routeId=" + routeId);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                doc = db.parse(new InputSource(url.openStream()));
                doc.getDocumentElement().normalize();

            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "Parsing Error", Toast.LENGTH_SHORT).show();
            }
            return doc;
        }

        @Override
        protected void onPostExecute(Document doc) {

            NodeList nodeList = doc.getElementsByTagName("item");

            for(int i = 0; i< nodeList.getLength(); i++){

                Node node = nodeList.item(i);
                Element fstElmnt = (Element) node;

                NodeList routeno = fstElmnt.getElementsByTagName("routeno");
                busnum = routeno.item(0).getChildNodes().item(0).getNodeValue();

                NodeList nodenm = fstElmnt.getElementsByTagName("nodenm");
                stopname = nodenm.item(0).getChildNodes().item(0).getNodeValue();

                NodeList arrprevstationcnt = fstElmnt.getElementsByTagName("arrprevstationcnt");
                arrvcnt = arrprevstationcnt.item(0).getChildNodes().item(0).getNodeValue();
            }

            super.onPostExecute(doc);
        }
    }

    private class GetXMLTask2 extends AsyncTask<String, Void, Document> {
        @Override
        protected Document doInBackground(String... urls) {
            URL url;
            try {
                url = new URL("http://openapi.tago.go.kr/openapi/service/BusRouteInfoInqireService/getRouteInfoIem"+
                        "?serviceKey=" + serviceKey +
                        "&cityCode="+ cityCode +
                        "&routeId=" + routeId);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                doc2 = db.parse(new InputSource(url.openStream()));
                doc2.getDocumentElement().normalize();

            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "Parsing Error", Toast.LENGTH_SHORT).show();
            }
            return doc2;
        }

        @Override
        protected void onPostExecute(Document doc2) {

            NodeList nodeList = doc2.getElementsByTagName("item");

            for(int i = 0; i< nodeList.getLength(); i++){

                Node node = nodeList.item(i);
                Element fstElmnt = (Element) node;

                NodeList startnodenm = fstElmnt.getElementsByTagName("startnodenm");
                startnm = startnodenm.item(0).getChildNodes().item(0).getNodeValue();

                NodeList endnodenm = fstElmnt.getElementsByTagName("endnodenm");
                endnm = endnodenm.item(0).getChildNodes().item(0).getNodeValue();
            }

            super.onPostExecute(doc2);
        }
    }

    public void AddText(String busnum, String stopname, String arrvcnt, String startnm, String endnm) {
        dynamicLayout = (LinearLayout)findViewById(R.id.dynamicLayout);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT, 2.0f);
        param.width = MATCH_PARENT;
        param.height = 250;
        param.leftMargin = 50;
        param.rightMargin = 50;
        param.topMargin = 40;
        param.bottomMargin = 10;
        ImageView busicon_v = new ImageView(this);
        TextView busnum_tv = new TextView(this);
        TextView arrvcnt_tv = new TextView(this);
        TextView stopnm_tv = new TextView(this);
        TextView startnm_tv = new TextView(this);
        TextView endnm_tv = new TextView(this);
        LinearLayout layout_tv2 = new LinearLayout(this);
        layout_tv2.setOrientation(LinearLayout.HORIZONTAL);
        layout_tv2.setGravity(Gravity.CENTER);
        LinearLayout layout_tv3 = new LinearLayout(this);
        layout_tv3.setOrientation(LinearLayout.HORIZONTAL);
        layout_tv3.setGravity(Gravity.CENTER);
        busnum_tv.setText(busnum);
        busnum_tv.setTextSize(25);
        busnum_tv.setEllipsize(TextUtils.TruncateAt.END);
        busnum_tv.setSingleLine(true);
        arrvcnt_tv.setText("(남은 정류장 수 : " + arrvcnt + ")");
        arrvcnt_tv.setTextSize(10);
        arrvcnt_tv.setEllipsize(TextUtils.TruncateAt.END);
        arrvcnt_tv.setSingleLine(true);
        layout_tv2.addView(busnum_tv);
        layout_tv2.addView(arrvcnt_tv);
        stopnm_tv.setText(stopname);
        stopnm_tv.setTextSize(10);
        stopnm_tv.setEllipsize(TextUtils.TruncateAt.END);
        stopnm_tv.setSingleLine(true);

        startnm_tv.setText(startnm + " -> ");
        startnm_tv.setTextSize(10);
        startnm_tv.setEllipsize(TextUtils.TruncateAt.END);
        startnm_tv.setSingleLine(true);
        endnm_tv.setText(endnm);
        endnm_tv.setTextSize(10);
        endnm_tv.setEllipsize(TextUtils.TruncateAt.END);
        endnm_tv.setSingleLine(true);
        layout_tv3.addView(startnm_tv);
        layout_tv3.addView(endnm_tv);

        busicon_v.setImageResource(R.drawable.bus_icon3);
        busicon_v.setAdjustViewBounds(true);
        busicon_v.setScaleType(ImageView.ScaleType.FIT_CENTER);
        busicon_v.setPadding(60, 30, 0, 30);
        LinearLayout layout_tv = new LinearLayout(this);
        layout_tv.setOrientation(LinearLayout.VERTICAL);
        layout_tv.setPadding(50, 30,0,0);
        layout_tv.setGravity(Gravity.CENTER);
        layout_tv.addView(layout_tv2);
        layout_tv.addView(stopnm_tv);
        layout_tv.addView(layout_tv3);
        dynamicHori = new LinearLayout(this);
        dynamicHori.setBackgroundResource(R.drawable.search_menu_shape);
        dynamicHori.setLayoutParams(param);
        dynamicHori.addView(busicon_v);
        dynamicHori.addView(layout_tv);
        dynamicLayout.addView(dynamicHori);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        for(int j = 1; j <= idc; j++){
            if(id == j){
                Toast.makeText(this, j + "클릭 됨", Toast.LENGTH_LONG).show();
            }
        }

    }

    public void AddClick(View v) {

        try {
            dbmanager = new DBManager(this);
            sqlitedb = dbmanager.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("folder", "null");
            values.put("busnum", busnum);
            values.put("stopname", stopname);
            values.put("arrvcnt", arrvcnt);
            values.put("startnm", startnm);
            values.put("endnm", endnm);
            long  newRowId = sqlitedb.insert("BusBookMark", null, values);
            sqlitedb.close();
            dbmanager.close();
            AddText(busnum, stopname, arrvcnt, startnm, endnm);
        } catch (SQLiteException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
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

                dialog.dismiss();   //닫기
                AddFolderData(input);

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

    public void AddFolderData(String name) {
        try{
            dbmanager = new DBManager(this);
            sqlitedb = dbmanager.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("name", name);
            long  newRowId = sqlitedb.insert("folders", null, values);
            sqlitedb.close();
            dbmanager.close();
            AddFolder(name, idc+1);
            idc++;
        } catch (SQLiteException e){
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
        ImageView plusicon_v = new ImageView(this);
        ImageView foldicon_v = new ImageView(this);
        foldname_tv.setText(fold_name);
        foldname_tv.setTextSize(25);
        foldname_tv.setEllipsize(TextUtils.TruncateAt.END);
        foldname_tv.setSingleLine(true);
        foldname_tv.setPadding(50, 30, 0, 0);
        plusicon_v.setImageResource(R.drawable.plus_icon);
        plusicon_v.setAdjustViewBounds(true);
        plusicon_v.setScaleType(ImageView.ScaleType.FIT_CENTER);
        plusicon_v.setPadding(60, 30, 0, 30);
        plusicon_v.setId(id);
        plusicon_v.setOnClickListener(this);
        foldicon_v.setImageResource(R.drawable.folder);
        foldicon_v.setAdjustViewBounds(true);
        foldicon_v.setScaleType(ImageView.ScaleType.FIT_CENTER);
        foldicon_v.setPadding(60, 30, 0, 30);
        dynamicHori = new LinearLayout(this);
        dynamicHori.setBackgroundResource(R.drawable.search_menu_shape);
        dynamicHori.setLayoutParams(param);
        dynamicHori.addView(plusicon_v);
        dynamicHori.addView(foldicon_v);
        dynamicHori.addView(foldname_tv);
        dynamicLayout.addView(dynamicHori);
    }

}