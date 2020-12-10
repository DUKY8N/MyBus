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
    LinearLayout dynamicLayout;
    LinearLayout dynamicHori;
    int idc = 0;

    Intent it;
    int cityCode;
    String routeId;

    Document doc;
    String serviceKey = "%2FnU0vVe9yEqaJ2vRtCPpJZHv%2Bef81aaG8G2pMXgYpYhJGqpcVzsFP2pqQ62JPlcfY54It2FZeXgN3p8nItuu9Q%3D%3D";
    String startnodenm;
    String endnodenm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book_mark);

        it = getIntent();

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
        } catch (SQLiteException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        //BusSearchClick에서 값 받아오기 getintent
        cityCode = it.getExtras().getInt("cityCode");
        routeId = it.getExtras().getString("routeId");

        new GetXMLTask().execute();
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        for(int j = 1; j <= idc; j++){
            if(id == j){
                Toast.makeText(this, j + "클릭 됨", Toast.LENGTH_LONG).show();
                //폴더에 +아이콘 클릭하면 인텐트값 넘기기
                try {
                    ContentValues values = new ContentValues();
                    values.put("folder", v.getTag().toString());
                    values.put("busnum", routeId);
                    values.put("cityid", cityCode);
                    values.put("startnm", startnodenm); // 시작점 종점 받아와야함.
                    values.put("endnm", endnodenm);

                    long newRowId = sqlitedb.insert("BusBookMark", null, values);
                    sqlitedb.close();
                    dbmanager.close();

                    finish();

                } catch(SQLiteException e) {
                    Toast.makeText(this,  e.getMessage(), Toast.LENGTH_LONG);
                }

            }
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
        plusicon_v.setTag("fold_name");
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

    public void AddClick(View view) {
        //화면아래 +아이콘 클릭하면 값 넘기기
        try {
            ContentValues values = new ContentValues();
            values.put("folder", "null");
            values.put("busnum", routeId);
            values.put("cityid", cityCode);
            values.put("startnm", startnodenm);
            values.put("endnm", endnodenm);

            long newRowId = sqlitedb.insert("BusBookMark", null, values);
            sqlitedb.close();
            dbmanager.close();

            finish();

        } catch(SQLiteException e) {
            Toast.makeText(this,  e.getMessage(), Toast.LENGTH_LONG);
        }
    }

    private class GetXMLTask extends AsyncTask<String, Void, Document> {
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
            Node node = nodeList.item(0);
            Element elmnt = (Element) node;

            NodeList snn = elmnt.getElementsByTagName("startnodenm");
            startnodenm = snn.item(0).getChildNodes().item(0).getNodeValue();
            NodeList enn = elmnt.getElementsByTagName("endnodenm");
            endnodenm = snn.item(0).getChildNodes().item(0).getNodeValue();

            super.onPostExecute(doc);

        }
    }
}
